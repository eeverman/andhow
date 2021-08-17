package org.yarnandtail.andhow;

import java.lang.annotation.*;

/**
 * Classes annotation with this annotation will allow all AndHow Properties in the class to be
 * exported to a HashMap by any code holding a reference to the Class.
 *
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented  //Include values for this annotation in JavaDocs
public @interface AllowExport {
}
