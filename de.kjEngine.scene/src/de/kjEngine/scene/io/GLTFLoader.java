/**
 * 
 */
package de.kjEngine.scene.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import de.kjEngine.graphics.Color;
import de.kjEngine.graphics.Graphics;
import de.kjEngine.graphics.SamplingMode;
import de.kjEngine.graphics.Texture2D;
import de.kjEngine.graphics.Texture2DData;
import de.kjEngine.graphics.Texture2DDataProvider;
import de.kjEngine.graphics.TextureFormat;
import de.kjEngine.graphics.WrappingMode;
import de.kjEngine.io.RL;
import de.kjEngine.io.ResourceNotFoundException;
import de.kjEngine.io.UnknownProtocolException;
import de.kjEngine.math.Vec4;
import de.kjEngine.scene.Entity;
import de.kjEngine.scene.Scene;
import de.kjEngine.scene.camera.PerspectiveCameraComponent;
import de.kjEngine.scene.material.Material;
import de.kjEngine.scene.material.PbrMaterial;
import de.kjEngine.scene.model.Model;
import de.kjEngine.scene.model.ModelComponent;
import de.kjEngine.ui.Window;

/**
 * @author konst
 *
 */
public class GLTFLoader {

	private static class Chunk {
		private static enum Type {
			JSON, BIN
		}

		int length;
		Type type;
		byte[] data;
	}

	private static class BufferView {
		int buffer, offset, length;
	}

	private static class Accessor {
		String type;
		int count;
		int view;
	}

	public static Scene load(RL rl) {
		try {
			InputStream in = rl.openInputStream();
			byte[] data = in.readAllBytes();
			in.close();

			long magic = readUint32(data, 0);
			if (magic != 0x46546c67) {
				return null;
			}

			List<Chunk> buffers = new ArrayList<>();
			Chunk jsonChunk = null;
			int offset = 12;
			while (offset < data.length) {
				Chunk chunk = readChunk(data, offset);
				if (chunk.type == Chunk.Type.BIN) {
					buffers.add(chunk);
				} else {
					jsonChunk = chunk;
				}
				offset += chunk.length + 8;
			}

			JSONObject jsonData = new JSONObject(new String(jsonChunk.data));

			JSONArray jsonBufferViews = jsonData.getJSONArray("bufferViews");
			BufferView[] bufferViews = new BufferView[jsonBufferViews.length()];
			for (int i = 0; i < bufferViews.length; i++) {
				BufferView v = new BufferView();
				JSONObject jsonView = jsonBufferViews.getJSONObject(i);
				v.offset = jsonView.getInt("byteOffset");
				v.length = jsonView.getInt("byteLength");
				v.buffer = jsonView.getInt("buffer");
				bufferViews[i] = v;
			}

			JSONArray jsonAccessors = jsonData.getJSONArray("accessors");
			Accessor[] accessors = new Accessor[jsonAccessors.length()];
			for (int i = 0; i < accessors.length; i++) {
				JSONObject jsonAccessor = jsonAccessors.getJSONObject(i);
				Accessor a = new Accessor();
				a.type = jsonAccessor.getString("type");
				a.count = jsonAccessor.getInt("count");
				a.view = jsonAccessor.getInt("bufferView");
				accessors[i] = a;
			}

			BufferedImage[] images;

			if (jsonData.has("images")) {
				JSONArray jsonImages = jsonData.getJSONArray("images");
				images = new BufferedImage[jsonImages.length()];
				for (int i = 0; i < images.length; i++) {
					JSONObject jsonImage = jsonImages.getJSONObject(i);
					if (jsonImage.has("bufferView")) {
						BufferView view = bufferViews[jsonImage.getInt("bufferView")];
						images[i] = loadImage(view, buffers.get(view.buffer));
					}
					if (jsonImage.has("uri")) {
						images[i] = ImageIO.read(rl.getParent().getChild(jsonImage.getString("uri")).openInputStream());
					}
				}
			} else {
				images = new BufferedImage[0];
			}

			Texture2D[] textures = new Texture2D[images.length];
			Texture2D[] roughnessTextures = new Texture2D[images.length];
			Texture2D[] metalnessTextures = new Texture2D[images.length];

			JSONArray jsonMaterials = jsonData.getJSONArray("materials");
			Material[] materials = new Material[jsonMaterials.length()];
			for (int i = 0; i < materials.length; i++) {
				JSONObject jsonMaterial = jsonMaterials.getJSONObject(i);
				JSONObject jsonMaterialData = jsonMaterial.getJSONObject("pbrMetallicRoughness");

				PbrMaterial material = new PbrMaterial();

				if (jsonMaterialData.has("baseColorFactor")) {
					JSONArray jsonBaseColor = jsonMaterialData.getJSONArray("baseColorFactor");
					material.setAlbedo(new Color(jsonBaseColor.getFloat(0), jsonBaseColor.getFloat(1), jsonBaseColor.getFloat(2), jsonBaseColor.getFloat(3)));
				}

				if (jsonMaterialData.has("baseColorTexture")) {
					JSONObject jsonTexture = jsonMaterialData.getJSONObject("baseColorTexture");
					int index = jsonTexture.getInt("index");
					if (textures[index] == null) {
						textures[index] = Graphics.createTexture2D(Texture2DData.create(images[index], SamplingMode.LINEAR, WrappingMode.REPEAT));
					}
					material.setAlbedo(textures[index]);
				}

				if (jsonMaterialData.has("metallicFactor")) {
					material.setMetalness(jsonMaterialData.getFloat("metallicFactor"));
				}

				if (jsonMaterialData.has("roughnessFactor")) {
					material.setRoughness(jsonMaterialData.getFloat("roughnessFactor"));
				}

				if (jsonMaterialData.has("metallicRoughnessTexture")) {
					JSONObject jsonTexture = jsonMaterialData.getJSONObject("metallicRoughnessTexture");
					int index = jsonTexture.getInt("index");
					if (roughnessTextures[index] == null) {
						BufferedImage image = images[index];

						int width = image.getWidth();
						int height = image.getHeight();

						int roughnessComponent = 16;
						int metalnessComponent = 24;
						switch (image.getType()) {
						case BufferedImage.TYPE_3BYTE_BGR:
							roughnessComponent = 8;
							metalnessComponent = 0;
							break;
						}

						final int finalRoughnessComponent = roughnessComponent;
						final int finalMetalnessComponent = metalnessComponent;

						roughnessTextures[index] = Graphics.createTexture2D(new Texture2DData(width, height, 1, new Texture2DDataProvider() {

							@Override
							public void get(int x, int y, Vec4 target) {
								target.x = ((image.getRGB(x, y) >> finalRoughnessComponent) & 0xff) / 255f;
							}
						}, TextureFormat.R8, SamplingMode.LINEAR, WrappingMode.REPEAT));

						metalnessTextures[index] = Graphics.createTexture2D(new Texture2DData(width, height, 1, new Texture2DDataProvider() {

							@Override
							public void get(int x, int y, Vec4 target) {
								target.x = ((image.getRGB(x, y) >> finalMetalnessComponent) & 0xff) / 255f;
							}
						}, TextureFormat.R8, SamplingMode.LINEAR, WrappingMode.REPEAT));
					}
					material.setRoughness(roughnessTextures[index]);
					material.setMetalness(metalnessTextures[index]);
				}

				if (jsonMaterial.has("normalTexture")) {
					JSONObject jsonTexture = jsonMaterial.getJSONObject("normalTexture");
					int index = jsonTexture.getInt("index");
					if (textures[index] == null) {
						textures[index] = Graphics.createTexture2D(Texture2DData.create(images[index], SamplingMode.LINEAR, WrappingMode.REPEAT));
					}
					material.setNormal(textures[index]);
				}

				materials[i] = material;
			}

			JSONArray jsonMeshes = jsonData.getJSONArray("meshes");
			Model[] models = new Model[jsonMeshes.length()];
			for (int i = 0; i < models.length; i++) {
				JSONObject jsonMesh = jsonMeshes.getJSONObject(i);
				JSONObject jsonPrimitive = jsonMesh.getJSONArray("primitives").getJSONObject(0);

				Accessor indicesAccessor = accessors[jsonPrimitive.getInt("indices")];
				int[] indices = parseIndices(indicesAccessor, bufferViews[indicesAccessor.view], buffers);
				int triangleCount = indicesAccessor.count / 3;
				for (int j = 0; j < triangleCount; j++) {
					int baseIndex = j * 3;
					int temp = indices[baseIndex];
					indices[baseIndex] = indices[baseIndex + 2];
					indices[baseIndex + 2] = temp;
				}

				JSONObject jsonAttributes = jsonPrimitive.getJSONObject("attributes");

				Accessor positionAccessor = accessors[jsonAttributes.getInt("POSITION")];
				float[] positions = parseFloats(positionAccessor, bufferViews[positionAccessor.view], buffers, 3);
				for (int j = 0; j < positionAccessor.count; j++) {
					positions[j * 3 + 2] *= -1f;
				}

				Accessor texcoordAccessor = accessors[jsonAttributes.getInt("TEXCOORD_0")];
				float[] texcoords = parseFloats(texcoordAccessor, bufferViews[texcoordAccessor.view], buffers, 2);

				Accessor normalAccessor = accessors[jsonAttributes.getInt("NORMAL")];
				float[] normals = parseFloats(normalAccessor, bufferViews[normalAccessor.view], buffers, 3);
				for (int j = 0; j < normalAccessor.count; j++) {
					normals[j * 3 + 2] *= -1f;
				}

				models[i] = new Model(positions, texcoords, normals, indices, materials[jsonPrimitive.getInt("material")]);
			}

			Scene scene = new Scene();
			JSONObject jsonScene = jsonData.getJSONArray("scenes").getJSONObject(0);
			JSONArray jsonSceneNodes = jsonScene.getJSONArray("nodes");
			JSONArray jsonNodes = jsonData.getJSONArray("nodes");
			for (int i = 0; i < jsonSceneNodes.length(); i++) {
				JSONObject jsonNode = jsonNodes.getJSONObject(jsonSceneNodes.getInt(i));
				scene.add(parseNode(jsonNode, jsonNodes, jsonData.getJSONArray("cameras"), models, scene));
			}

			return scene;
		} catch (UnknownProtocolException e) {
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedImage loadImage(BufferView view, Chunk buffer) {
		try {
			return ImageIO.read(new InputStream() {
				int i = view.offset;
				int end = view.offset + view.length;

				@Override
				public int read() throws IOException {
					if (i < end) {
						return Byte.toUnsignedInt(buffer.data[i++]);
					}
					return -1;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static float[] parseFloats(Accessor accessor, BufferView view, List<Chunk> buffers, int componens) {
		byte[] data = buffers.get(view.buffer).data;
		float[] floats = new float[accessor.count * componens];
		for (int i = 0; i < floats.length; i++) {
			floats[i] = readFloat32(data, view.offset + i * 4);
		}
		return floats;
	}

	private static int[] parseIndices(Accessor accessor, BufferView view, List<Chunk> buffers) {
		byte[] data = buffers.get(view.buffer).data;
		int[] indices = new int[accessor.count];
		for (int i = 0; i < indices.length; i++) {
			int offset = view.offset + i * 2;
			indices[i] = readUint16(data, offset);
		}
		return indices;
	}

	private static Entity parseNode(JSONObject jsonNode, JSONArray jsonNodes, JSONArray jsonCameras, Model[] models, Scene scene) {
		Entity e = new Entity(true);
		if (jsonNode.has("camera")) {
			JSONObject jsonCamera = jsonCameras.getJSONObject(jsonNode.getInt("camera"));
			if (jsonCamera.getString("type").equals("perspective")) {
				JSONObject jsonFrustum = jsonCamera.getJSONObject("perspective");
				PerspectiveCameraComponent cam = new PerspectiveCameraComponent();
				cam.getFrustum().setNear(jsonFrustum.getFloat("znear")).setFar(jsonFrustum.getFloat("zfar")).setFov(jsonFrustum.getFloat("yfov")).setAspect(Window.getAspect());
				e.add(cam);
			}
		}
		if (jsonNode.has("children")) {
			JSONArray jsonChildren = jsonNode.getJSONArray("children");
			for (int i = 0; i < jsonChildren.length(); i++) {
				e.add(parseNode(jsonNodes.getJSONObject(jsonChildren.getInt(i)), jsonNodes, jsonCameras, models, scene));
			}
		}
		if (jsonNode.has("rotation")) {
			JSONArray jsonRotation = jsonNode.getJSONArray("rotation");
			e.transform.rotation.set(jsonRotation.getFloat(0), jsonRotation.getFloat(1), -jsonRotation.getFloat(2), jsonRotation.getFloat(3)).normalise();
		}
		if (jsonNode.has("name")) {
			e.name = jsonNode.getString("name");
		}
		if (jsonNode.has("translation")) {
			JSONArray jsonTranslation = jsonNode.getJSONArray("translation");
			e.transform.position.set(jsonTranslation.getFloat(0), jsonTranslation.getFloat(1), -jsonTranslation.getFloat(2));
		}
		if (jsonNode.has("scale")) {
			JSONArray jsonScale = jsonNode.getJSONArray("scale");
			e.transform.scale.set(jsonScale.getFloat(0), jsonScale.getFloat(1), jsonScale.getFloat(2));
		}
		if (jsonNode.has("mesh")) {
			e.add(new ModelComponent(models[jsonNode.getInt("mesh")]));
		}
		return e;
	}

	private static Chunk readChunk(byte[] data, int off) {
		Chunk c = new Chunk();
		c.length = (int) readUint32(data, off);
		long type = readUint32(data, off + 4);
		if (type == 0x4e4f534a) {
			c.type = Chunk.Type.JSON;
		} else {
			c.type = Chunk.Type.BIN;
		}
		c.data = new byte[c.length];
		for (int i = 0; i < c.length; i++) {
			c.data[i] = data[off + 8 + i];
		}
		return c;
	}

	private static float readFloat32(byte[] data, int i) {
		return Float.intBitsToFloat((int) readUint32(data, i));
	}

	private static long readUint32(byte[] data, int i) {
		long l = 0;
		l += Byte.toUnsignedLong(data[i + 3]) << 24;
		l += Byte.toUnsignedLong(data[i + 2]) << 16;
		l += Byte.toUnsignedLong(data[i + 1]) << 8;
		l += Byte.toUnsignedLong(data[i]);
		return l;
	}

	private static int readUint16(byte[] data, int i) {
		int l = 0;
		l += Byte.toUnsignedInt(data[i + 1]) << 8;
		l += Byte.toUnsignedInt(data[i]);
		return l;
	}
}
