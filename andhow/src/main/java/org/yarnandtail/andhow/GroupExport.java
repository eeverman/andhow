package org.yarnandtail.andhow;

import java.lang.annotation.*;

/**
 * Annotation to direct the Properties in a PropertyGroup to be exported to a
 * destination such as System.Properties.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Repeatable(GroupExports.class)
@Documented	//Include values for this annotation in JavaDocs
public @interface GroupExport {
	
	/**
	 * Specifies if the canonical name should be used to export property values.
	 * 
	 * If this option and exportByOutAliases are both set to export (such as
	 * setting both to ALWAYS), it is possible to export multiple copies of the
	 * property value under different names, which is permissable.
	 * 
	 * @return 
	 */
	org.yarnandtail.andhow.Exporter.EXPORT_CANONICAL_NAME exportByCanonicalName();
	
	/**
	 * Specifies if the out aliases, which are basically aliases for the purpose
	 * of exports, should be used to export property values.
	 * 
	 * If this option and exportByCanonicalName are both set to export (such as
	 * setting both to ALWAYS), it is possible to export multiple copies of the
	 * property value under different names, which is permissable.
	 * 
	 * @return 
	 */
	org.yarnandtail.andhow.Exporter.EXPORT_OUT_ALIASES exportByOutAliases();

	/**
	 * The class of the exporter to use.
	 * @return
	 */
	Class<? extends Exporter> exporter();
}
