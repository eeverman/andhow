package yarnandtail.andhow;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import yarnandtail.andhow.Exporter.INCLUDE_CANONICAL_NAMES;
import yarnandtail.andhow.Exporter.INCLUDE_OUT_ALIAS_NAMES;

/**
 * Annotation to allow users to include documentation on PropertyGroups.
 * 
 * When sample configuration or documentation is generated, these values are used
 * to provide better details on groups.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented	//Include values for this annotation in JavaDocs
public @interface GroupExport {
	INCLUDE_CANONICAL_NAMES includeCanonicalNames();
	INCLUDE_OUT_ALIAS_NAMES includeExportAliasNames();

	/**
	 * The class of the exporter to use.
	 * @return
	 */
	Class<? extends Exporter> exporter();
}
