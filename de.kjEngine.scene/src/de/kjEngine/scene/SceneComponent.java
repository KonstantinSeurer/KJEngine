/**
 * 
 */
package de.kjEngine.scene;

import de.kjEngine.component.Component;

/**
 * @author konst
 *
 */
public class SceneComponent<ParentType extends SceneComponent<?, ?>, ComponentType extends SceneComponent<ParentType, ?>> extends Component<ParentType, ComponentType, Scene> {

	public SceneComponent(int flags) {
		super(flags);
	}
}
