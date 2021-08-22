package org.yarnandtail.andhow.export;

import java.lang.annotation.*;

/**
 * Classes annotation with this annotation will block any kind of export of the properties.
 *
 * Exports are not allowed by default, however, if a class allows exports, it also allows
 * them for its nested innerclasses and interfaces.  This annotation can be placed on an
 * innerclass or interface to disallow that.
 *
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented  //Include values for this annotation in JavaDocs
public @interface ManualExportNotAllowed {
}
