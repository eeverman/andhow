package org.yarnandtail.andhow.internal;

import java.lang.annotation.*;
import org.yarnandtail.andhow.GroupExport;
import org.yarnandtail.andhow.GroupExport;

/**
 * Not for general use:  Required by the Java annotations system to support
 * multiple instances of the GroupExport annotation.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented	//Include values for this annotation in JavaDocs
public @interface GroupExports {
	GroupExport[] value();
}
