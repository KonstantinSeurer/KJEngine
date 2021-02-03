/**
 * 
 */
package de.kjEngine.ui;

import de.kjEngine.math.Mat4;
import de.kjEngine.math.Real;
import de.kjEngine.math.Vec2;
import de.kjEngine.math.Vec4;
import de.kjEngine.ui.transform.Offset;
import de.kjEngine.ui.transform.PixelOffset;
import de.kjEngine.ui.transform.Size;

/**
 * @author konst
 *
 */
public class UI extends UIComponent<UI, UI> {

	public final Offset x, y;
	public final Size width, height;
	public float rotation;
	public final Offset rotationPivotX, rotationPivotY;
	public final Mat4 localTransform = new Mat4(), globalTransform = new Mat4();

	public UI(Offset x, Offset y, Size width, Size height, float rotation, Offset rotationPivotX, Offset rotationPivotY) {
		super(LATE);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.rotationPivotX = rotationPivotX;
		this.rotationPivotY = rotationPivotY;
	}

	public UI(Offset x, Offset y, Size width, Size height) {
		this(x, y, width, height, 0f, new PixelOffset(), new PixelOffset());
	}

	private final Mat4 parentTransformBuffer = new Mat4();

	@Override
	protected void update(float delta) {
		localTransform.setIdentity();

		float pixelWidth = getPixelWidth();
		float pixelHeight = getPixelHeight();

		float cosRotation = Real.cos(rotation);
		float sinRotation = Real.sin(rotation);
		localTransform.xx = cosRotation * pixelWidth;
		localTransform.xy = sinRotation * pixelWidth;
		localTransform.yx = -sinRotation * pixelHeight;
		localTransform.yy = cosRotation * pixelHeight;

		float rotPivotX = rotationPivotX.getPixelOffsetX(this, this);
		float rotPivotY = rotationPivotY.getPixelOffsetY(this, this);
		float newRotPivotX = rotPivotX * cosRotation + rotPivotY * sinRotation;
		float newRotPivotY = rotPivotY * cosRotation - rotPivotX * sinRotation;

		// don't ask me why
		localTransform.wx = getPixelX() + (rotPivotY - newRotPivotY);
		localTransform.wy = getPixelY() + (rotPivotX - newRotPivotX);

		if (parent == null) {
			globalTransform.set(localTransform);
		} else {
			parentTransformBuffer.set(parent.globalTransform);
			float invXLength = 1f / Real.sqrt(parentTransformBuffer.xx * parentTransformBuffer.xx + parentTransformBuffer.xy * parentTransformBuffer.xy);
			parentTransformBuffer.xx *= invXLength;
			parentTransformBuffer.xy *= invXLength;
			float invYLength = 1f / Real.sqrt(parentTransformBuffer.yx * parentTransformBuffer.yx + parentTransformBuffer.yy * parentTransformBuffer.yy);
			parentTransformBuffer.yx *= invYLength;
			parentTransformBuffer.yy *= invYLength;
			Mat4.mul(parentTransformBuffer, localTransform, globalTransform);
		}
	}

	public float getPixelX() {
		return x.getPixelOffsetX(this, parent);
	}

	public float getPixelY() {
		return y.getPixelOffsetY(this, parent);
	}

	public float getPixelWidth() {
		return width.getPixelWidth(this, parent);
	}

	public float getPixelHeight() {
		return height.getPixelHeight(this, parent);
	}
	
	private final Mat4 invGlobalTransformBuffer = new Mat4();

	public Vec2 toLocal(Vec2 coord) {
		Vec4 coord4 = Vec4.create(coord, 0f, 1f);
		return Mat4.transform(Mat4.invert(globalTransform, invGlobalTransformBuffer), coord4, coord4);
	}
	
	public boolean intersects(Vec2 coord) {
		Vec2 localCoord = toLocal(coord);
		return localCoord.x >= 0f && localCoord.y >= 0 && localCoord.x < 1f && localCoord.y < 1f;
	}
}
