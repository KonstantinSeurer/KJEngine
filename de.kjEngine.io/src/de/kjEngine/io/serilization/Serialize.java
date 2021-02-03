/**
 * 
 */
package de.kjEngine.io.serilization;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author konst
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
public @interface Serialize {

}
