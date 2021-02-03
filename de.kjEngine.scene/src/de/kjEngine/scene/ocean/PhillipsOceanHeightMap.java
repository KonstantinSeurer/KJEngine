/**
 * 
 */
package de.kjEngine.scene.ocean;

import java.util.ArrayList;

import de.kjEngine.graphics.BufferAccessor;
import de.kjEngine.graphics.Descriptor.Type;
import de.kjEngine.graphics.DescriptorSet;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.ShaderBuffer;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.graphics.shader.BufferSource;
import de.kjEngine.graphics.shader.BufferSource.Layout;
import de.kjEngine.graphics.shader.DescriptorSetSource;
import de.kjEngine.graphics.shader.ImageSource;
import de.kjEngine.graphics.shader.TextureSource;
import de.kjEngine.graphics.shader.VariableSource;
import de.kjEngine.io.RL;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec4;
import de.kjEngine.renderer.Renderable.RenderImplementation.ID;
import de.kjEngine.renderer.Renderable.RenderImplementation.Provider;
import de.kjEngine.renderer.Renderable.RenderImplementation.Registry;

/**
 * @author konst
 *
 */
public class PhillipsOceanHeightMap extends OceanHeightMap {
	
	public static class PhillipsOceanHeightMapPhillipsOceanHeightMapList implements RenderImplementation<PhillipsOceanHeightMap> {

		@Override
		public void dispose() {
		}

		@Override
		public void init(PhillipsOceanHeightMap c) {
		}

		@Override
		public void updateDescriptors(PhillipsOceanHeightMap c) {
		}

		@Override
		public void render(PhillipsOceanHeightMap c) {
			c.getContainer().getRenderImplementation(ScenePhillipsOceanHeightMapList.class).heightMaps.add(c);
		}
	}
	
	public static final ID LIST = new ID(PhillipsOceanHeightMap.class, "list");
	
	static {
		Registry.registerProvider(LIST, new Provider() {
			
			@Override
			public de.kjEngine.renderer.Renderable.RenderImplementation create() {
				return new PhillipsOceanHeightMapPhillipsOceanHeightMapList();
			}
		});
	}

	public static final BufferSource SETTINGS_UBO_SOURCE = new BufferSource("settings", Type.UNIFORM_BUFFER, Layout.STANDARD, new ArrayList<>());
	static {
		SETTINGS_UBO_SOURCE.getMembers().add(new VariableSource("float", "t"));
		SETTINGS_UBO_SOURCE.getMembers().add(new VariableSource("float", "N"));
		SETTINGS_UBO_SOURCE.getMembers().add(new VariableSource("float", "L"));
		SETTINGS_UBO_SOURCE.getMembers().add(new VariableSource("vec2", "w"));
		SETTINGS_UBO_SOURCE.getMembers().add(new VariableSource("float", "A"));
	}

	public static final DescriptorSetSource H0K_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("h0k", new ArrayList<>());
	static {
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("positive", 2, TextureFormat.RG16F, false, true));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("negative", 2, TextureFormat.RG16F, false, true));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("noise0", 2));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("noise1", 2));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("noise2", 2));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("noise3", 2));
		H0K_DESCRIPTOR_SET_SOURCE.getDescriptors().add(SETTINGS_UBO_SOURCE);
	}

	public static final DescriptorSetSource HTK_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("htk", new ArrayList<>());
	static {
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("dx", 2, TextureFormat.RG16F, false, true));
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("dy", 2, TextureFormat.RG16F, false, true));
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("dz", 2, TextureFormat.RG16F, false, true));
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("h0k", 2));
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("h0mk", 2));
		HTK_DESCRIPTOR_SET_SOURCE.getDescriptors().add(SETTINGS_UBO_SOURCE);
	}

	public static final DescriptorSetSource BUTTERFLY_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("butterfly", new ArrayList<>());
	static {
		BUTTERFLY_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("twiddleIndices", 2));
		BUTTERFLY_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("pingpong0", 2, TextureFormat.RG16F, true, true));
		BUTTERFLY_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("pingpong1", 2, TextureFormat.RG16F, true, true));
	}

	public static final DescriptorSetSource INVERSION_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("inversion", new ArrayList<>());
	static {
		INVERSION_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("pingpong0", 2));
		INVERSION_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("pingpong1", 2));
		INVERSION_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new ImageSource("displacement", 2, TextureFormat.R16F, false, true));
	}

	public static final DescriptorSetSource RENDER_DESCRIPTOR_SET_SOURCE = new DescriptorSetSource("ocean", new ArrayList<>());
	static {
		RENDER_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dx", 2));
		RENDER_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dy", 2));
		RENDER_DESCRIPTOR_SET_SOURCE.getDescriptors().add(new TextureSource("dz", 2));
	}

	private int N;
	private int L;
	private Vec2 w;
	private float A;
	private float t;

	private ShaderBuffer settingsUbo;

	private Texture2D h0k, h0mk;
	private Texture2D[] noises = new Texture2D[4];
	private DescriptorSet h0kDescriptorSet;

	private Texture2D dxSpectrum, dySpectrum, dzSpectrum;
	private DescriptorSet htkDescriptorSet;

	private Texture2D dx, dy, dz;
	private Texture2D pingpong;
	private DescriptorSet dxButterflyDescriptorSet, dyButterflyDescriptorSet, dzButterflyDescriptorSet;
	private DescriptorSet dxInversionDescriptorSet, dyInversionDescriptorSet, dzInversionDescriptorSet;

	private Texture2D twiddleIndices;

	private DescriptorSet renderDescriptorSet;

	private boolean updateH0k = true;

	public PhillipsOceanHeightMap(int resolution, int L, Vec2 wind, float amplitude, float t0) {
		N = resolution;
		this.L = L;
		w = wind;
		A = amplitude;
		t = t0;

		settingsUbo = Graphics.createUniformBuffer(SETTINGS_UBO_SOURCE, new ArrayList<>(), ShaderBuffer.FLAG_NONE);

		h0k = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.NEAREST, WrappingMode.REPEAT));
		h0mk = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.NEAREST, WrappingMode.REPEAT));

		for (int i = 0; i < 4; i++) {
			noises[i] = Graphics.loadTexture(RL.create("jar://scene/de/kjEngine/scene/ocean/noise" + i + ".jpg"), SamplingMode.NEAREST, WrappingMode.CLAMP, true);
		}

		h0kDescriptorSet = Graphics.createDescriptorSet(H0K_DESCRIPTOR_SET_SOURCE);
		h0kDescriptorSet.set("positive", h0k.getImage(0));
		h0kDescriptorSet.set("negative", h0mk.getImage(0));
		h0kDescriptorSet.set("noise0", noises[0]);
		h0kDescriptorSet.set("noise1", noises[1]);
		h0kDescriptorSet.set("noise2", noises[2]);
		h0kDescriptorSet.set("noise3", noises[3]);
		h0kDescriptorSet.set("settings", settingsUbo);
		h0kDescriptorSet.update();

		dxSpectrum = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.NEAREST, WrappingMode.REPEAT));
		dySpectrum = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.NEAREST, WrappingMode.REPEAT));
		dzSpectrum = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.NEAREST, WrappingMode.REPEAT));

		htkDescriptorSet = Graphics.createDescriptorSet(HTK_DESCRIPTOR_SET_SOURCE);
		htkDescriptorSet.set("dx", dxSpectrum.getImage(0));
		htkDescriptorSet.set("dy", dySpectrum.getImage(0));
		htkDescriptorSet.set("dz", dzSpectrum.getImage(0));
		htkDescriptorSet.set("h0k", h0k);
		htkDescriptorSet.set("h0mk", h0mk);
		htkDescriptorSet.set("settings", settingsUbo);
		htkDescriptorSet.update();

		int log2N = (int) (Math.log(N) / Math.log(2f));

		twiddleIndices = Graphics.createTexture2D(new Texture2DData(log2N, N, 1, new Texture2DDataProvider() {

			@Override
			public void get(int x, int y, Vec4 target) {
				float k = (y * (N / (int) Math.pow(2f, x + 1))) % N;
				float twiddleA = Real.TWO_PI * k / N;
				target.x = Real.cos(twiddleA);
				target.y = Real.sin(twiddleA);

				int butterflySpan = (int) Math.pow(2f, x);
				int butterflyWing = 0;
				if (y % (int) Math.pow(2f, x + 1) < butterflySpan) {
					butterflyWing = 1;
				}

				if (x == 0) {
					if (butterflyWing == 1) {
						target.z = reverseBits(y);
						target.w = reverseBits(y + 1);
					} else {
						target.z = reverseBits(y - 1);
						target.w = reverseBits(y);
					}
				} else {
					if (butterflyWing == 1) {
						target.z = y;
						target.w = y + butterflySpan;
					} else {
						target.z = y - butterflySpan;
						target.w = y;
					}
				}
			}

			private int reverseBits(int i) {
				return Integer.rotateLeft(Integer.reverse(i), log2N);
			}
		}, TextureFormat.RGBA32F, SamplingMode.NEAREST, WrappingMode.CLAMP));

		dx = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.R16F, SamplingMode.LINEAR, WrappingMode.REPEAT));
		dy = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.R16F, SamplingMode.LINEAR, WrappingMode.REPEAT));
		dz = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.R16F, SamplingMode.LINEAR, WrappingMode.REPEAT));

		pingpong = Graphics.createTexture2D(new Texture2DData(N, N, 1, null, TextureFormat.RG16F, SamplingMode.LINEAR, WrappingMode.REPEAT));

		dxButterflyDescriptorSet = Graphics.createDescriptorSet(BUTTERFLY_DESCRIPTOR_SET_SOURCE);
		dxButterflyDescriptorSet.set("twiddleIndices", twiddleIndices);
		dxButterflyDescriptorSet.set("pingpong0", dxSpectrum.getImage(0));
		dxButterflyDescriptorSet.set("pingpong1", pingpong.getImage(0));
		dxButterflyDescriptorSet.update();

		dyButterflyDescriptorSet = Graphics.createDescriptorSet(BUTTERFLY_DESCRIPTOR_SET_SOURCE);
		dyButterflyDescriptorSet.set("twiddleIndices", twiddleIndices);
		dyButterflyDescriptorSet.set("pingpong0", dySpectrum.getImage(0));
		dyButterflyDescriptorSet.set("pingpong1", pingpong.getImage(0));
		dyButterflyDescriptorSet.update();

		dzButterflyDescriptorSet = Graphics.createDescriptorSet(BUTTERFLY_DESCRIPTOR_SET_SOURCE);
		dzButterflyDescriptorSet.set("twiddleIndices", twiddleIndices);
		dzButterflyDescriptorSet.set("pingpong0", dzSpectrum.getImage(0));
		dzButterflyDescriptorSet.set("pingpong1", pingpong.getImage(0));
		dzButterflyDescriptorSet.update();

		dxInversionDescriptorSet = Graphics.createDescriptorSet(INVERSION_DESCRIPTOR_SET_SOURCE);
		dxInversionDescriptorSet.set("displacement", dx.getImage(0));
		dxInversionDescriptorSet.set("pingpong0", dxSpectrum);
		dxInversionDescriptorSet.set("pingpong1", pingpong);
		dxInversionDescriptorSet.update();

		dyInversionDescriptorSet = Graphics.createDescriptorSet(INVERSION_DESCRIPTOR_SET_SOURCE);
		dyInversionDescriptorSet.set("displacement", dy.getImage(0));
		dyInversionDescriptorSet.set("pingpong0", dySpectrum);
		dyInversionDescriptorSet.set("pingpong1", pingpong);
		dyInversionDescriptorSet.update();

		dzInversionDescriptorSet = Graphics.createDescriptorSet(INVERSION_DESCRIPTOR_SET_SOURCE);
		dzInversionDescriptorSet.set("displacement", dz.getImage(0));
		dzInversionDescriptorSet.set("pingpong0", dzSpectrum);
		dzInversionDescriptorSet.set("pingpong1", pingpong);
		dzInversionDescriptorSet.update();

		renderDescriptorSet = Graphics.createDescriptorSet(RENDER_DESCRIPTOR_SET_SOURCE);
		renderDescriptorSet.set("dx", dx);
		renderDescriptorSet.set("dy", dy);
		renderDescriptorSet.set("dz", dz);
		renderDescriptorSet.update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void updateDescriptors() {
		super.updateDescriptors();
		
		BufferAccessor accessor = settingsUbo.getAccessor();
		accessor.set("t", t);
		accessor.set("N", N);
		accessor.set("L", L);
		accessor.set("w", w);
		accessor.set("A", A);
		settingsUbo.update();
	}

	/**
	 * @return the h0kDescriptorSet
	 */
	public DescriptorSet getH0kDescriptorSet() {
		return h0kDescriptorSet;
	}

	/**
	 * @return the h0k
	 */
	public Texture2D getH0k() {
		return h0k;
	}

	/**
	 * @return the h0mk
	 */
	public Texture2D getH0mk() {
		return h0mk;
	}

	/**
	 * @return the dxSpectrum
	 */
	public Texture2D getDxSpectrum() {
		return dxSpectrum;
	}

	/**
	 * @return the dySpectrum
	 */
	public Texture2D getDySpectrum() {
		return dySpectrum;
	}

	/**
	 * @return the dzSpectrum
	 */
	public Texture2D getDzSpectrum() {
		return dzSpectrum;
	}

	/**
	 * @return the htkDescriptorSet
	 */
	public DescriptorSet getHtkDescriptorSet() {
		return htkDescriptorSet;
	}

	/**
	 * @return the dx
	 */
	public Texture2D getDx() {
		return dx;
	}

	/**
	 * @return the dy
	 */
	public Texture2D getDy() {
		return dy;
	}

	/**
	 * @return the dz
	 */
	public Texture2D getDz() {
		return dz;
	}

	/**
	 * @return the twiddleIndices
	 */
	public Texture2D getTwiddleIndices() {
		return twiddleIndices;
	}

	/**
	 * @return the pingpong
	 */
	public Texture2D getPingpong() {
		return pingpong;
	}

	/**
	 * @return the dxButterflyDescriptorSet
	 */
	public DescriptorSet getDxButterflyDescriptorSet() {
		return dxButterflyDescriptorSet;
	}

	/**
	 * @return the dyButterflyDescriptorSet
	 */
	public DescriptorSet getDyButterflyDescriptorSet() {
		return dyButterflyDescriptorSet;
	}

	/**
	 * @return the dzButterflyDescriptorSet
	 */
	public DescriptorSet getDzButterflyDescriptorSet() {
		return dzButterflyDescriptorSet;
	}

	/**
	 * @return the noises
	 */
	public Texture2D[] getNoises() {
		return noises;
	}

	/**
	 * @return the dxInversionDescriptorSet
	 */
	public DescriptorSet getDxInversionDescriptorSet() {
		return dxInversionDescriptorSet;
	}

	/**
	 * @return the dyInversionDescriptorSet
	 */
	public DescriptorSet getDyInversionDescriptorSet() {
		return dyInversionDescriptorSet;
	}

	/**
	 * @return the dzInversionDescriptorSet
	 */
	public DescriptorSet getDzInversionDescriptorSet() {
		return dzInversionDescriptorSet;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return N;
	}

	public boolean pollUpdateH0k() {
		boolean result = updateH0k;
		updateH0k = false;
		return result;
	}

	/**
	 * @return the l
	 */
	public int getL() {
		return L;
	}

	/**
	 * @return the w
	 */
	public Vec2 getW() {
		return w;
	}

	/**
	 * @return the a
	 */
	public float getA() {
		return A;
	}

	/**
	 * @return the settingsUbo
	 */
	public ShaderBuffer getSettingsUbo() {
		return settingsUbo;
	}

	/**
	 * @return the updateH0k
	 */
	public boolean isUpdateH0k() {
		return updateH0k;
	}

	/**
	 * @return the renderDescriptorSet
	 */
	public DescriptorSet getRenderDescriptorSet() {
		return renderDescriptorSet;
	}

	@Override
	public void update(float delta) {
		t += delta;
	}
}
