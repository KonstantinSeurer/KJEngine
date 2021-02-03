/**
 * 
 */
package de.kjEngine.scene.animation;

import java.util.ArrayList;
import java.util.List;

import de.kjEngine.math.Mat4;
import de.kjEngine.math.Transform;
import de.kjEngine.scene.model.ModelComponent;

/**
 * @author konst
 *
 */
public class Skeleton {

	public static class Joint {

		private String name;
		private Mat4 initialTransform;
		private Mat4 inverseInitialTransform;
		private Mat4 globalInverseInitialTransform;
		private Transform transform;

		private Joint parent;
		private List<Joint> children = new ArrayList<>();

		/**
		 * @param name
		 * @param initialTransform
		 */
		public Joint(String name, Mat4 initialTransform) {
			this.name = name;
			this.initialTransform = initialTransform;
			inverseInitialTransform = Mat4.invert(initialTransform, null);
			globalInverseInitialTransform = new Mat4(inverseInitialTransform);
			transform = new Transform(initialTransform);
		}

		public Joint(Joint j) {
			name = j.name;
			initialTransform = j.initialTransform;
			inverseInitialTransform = new Mat4(j.inverseInitialTransform);
			globalInverseInitialTransform = new Mat4(j.globalInverseInitialTransform);
			transform = new Transform(j.transform);

			for (int i = 0; i < j.children.size(); i++) {
				addJoint(new Joint(j.children.get(i)));
			}
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the initialTransform
		 */
		public Mat4 getInitialTransform() {
			return initialTransform;
		}

		/**
		 * @return the inverseInitialTransform
		 */
		public Mat4 getInverseInitialTransform() {
			return inverseInitialTransform;
		}

		/**
		 * @return the transform
		 */
		public Transform getTransform() {
			return transform;
		}

		/**
		 * @return the globalInverseInitialTransform
		 */
		public Mat4 getGlobalInverseInitialTransform() {
			return globalInverseInitialTransform;
		}

		/**
		 * @return the parent
		 */
		public Joint getParent() {
			return parent;
		}

		/**
		 * @return the children
		 */
		public List<Joint> getChildren() {
			return children;
		}

		public Joint getChild(String name) {
			for (int i = 0; i < children.size(); i++) {
				Joint child = children.get(i);
				if (child.getName().equals(name)) {
					return child;
				}
			}
			return null;
		}

		public Joint setParent(Joint parent) {
			this.parent = parent;
			Mat4.mul(inverseInitialTransform, parent.globalInverseInitialTransform, globalInverseInitialTransform);
			for (int i = 0; i < children.size(); i++) {
				children.get(i).setParent(this);
			}
			transform.parent = parent.transform;
			return this;
		}

		public Joint addJoint(Joint joint) {
			joint.setParent(this);
			children.add(joint);
			return this;
		}

		public void update() {
			transform.update();
			for (int i = 0; i < children.size(); i++) {
				children.get(i).update();
			}
		}
	}

	private Joint root;
	private Joint[] joints;

	public Skeleton(Joint root, String[] orderedNames) {
		this.root = root;
		joints = new Joint[orderedNames.length];
		putJoint(root, orderedNames);
	}

	public Skeleton(Skeleton s) {
		root = new Joint(s.root);
		String[] orderedNames = new String[s.getJoints().length];
		for (int i = 0; i < orderedNames.length; i++) {
			orderedNames[i] = s.getJoints()[i].name;
		}
		joints = new Joint[orderedNames.length];
		putJoint(root, orderedNames);
	}

	private void putJoint(Joint joint, String[] orderedNames) {
		for (int i = 0; i < orderedNames.length; i++) {
			if (joint.getName().equals(orderedNames[i])) {
				joints[i] = joint;
				break;
			}
		}
		List<Joint> children = joint.getChildren();
		for (int i = 0; i < children.size(); i++) {
			putJoint(children.get(i), orderedNames);
		}
	}

	/**
	 * @return the joints
	 */
	public Joint[] getJoints() {
		return joints;
	}

	public Joint getJoint(String name) {
		for (int i = 0; i < joints.length; i++) {
			if (name.equals(joints[i].getName())) {
				return joints[i];
			}
		}
		return null;
	}

	public void apply(ModelComponent model) {
		int count = Math.min(joints.length, model.getTransforms().length);
		for (int i = 0; i < count; i++) {
			Mat4.mul(joints[i].transform.globalTransform, joints[i].globalInverseInitialTransform, model.getTransforms()[i]);
		}
	}

	/**
	 * @return the root
	 */
	public Joint getRoot() {
		return root;
	}

	public void update() {
		root.update();
	}
}
