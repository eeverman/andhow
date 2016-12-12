package yarnandtail.andhow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
public @interface ConfigGroupDescription {
	String groupName();
	String groupDescription();
}
