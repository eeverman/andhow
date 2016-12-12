package yarnandtail.andhow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to allow users to include documentation on PropertyGroups.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
public @interface PropertyGroupDescription {
	String groupName();
	String groupDescription();
}
