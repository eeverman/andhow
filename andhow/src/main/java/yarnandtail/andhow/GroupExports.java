package yarnandtail.andhow;

import java.lang.annotation.*;

/**
 * Required by the Java annotations system to support multiple instances of
 * GroupExport.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented	//Include values for this annotation in JavaDocs
public @interface GroupExports {
	GroupExport[] value();
}
