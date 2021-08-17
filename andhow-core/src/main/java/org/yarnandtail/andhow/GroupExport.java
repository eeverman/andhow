package org.yarnandtail.andhow;

import java.lang.annotation.*;
import org.yarnandtail.andhow.api.Exporter;

/**
 * Annotation to direct the Properties in a PropertyGroup to be exported to a
 * destination such as System.Properties.
 * 
 * @author eeverman
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Repeatable(GroupExport.List.class)
@Documented	//Include values for this annotation in JavaDocs
public @interface GroupExport {
	
	/**
	 * Specifies if the canonical name should be used to export property values.
	 * 
	 * If this option and exportByOutAliases are both set to export (such as
	 * setting both to ALWAYS), it is possible to export multiple copies of the
	 * property value under different names, which is permissible.
	 * 
	 * @return 
	 */
	org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME exportByCanonicalName()
			default Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS;
	
	/**
	 * Specifies if the out aliases, which are basically aliases for the purpose
	 * of exports, should be used to export property values.
	 * 
	 * If this option and exportByCanonicalName are both set to export (such as
	 * setting both to ALWAYS), it is possible to export multiple copies of the
	 * property value under different names, which is permissible.
	 * 
	 * @return 
	 */
	org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES exportByOutAliases()
			default Exporter.EXPORT_OUT_ALIASES.ALWAYS;

	/**
	 * The class of the exporter to use.
	 * @return
	 */
	Class<? extends Exporter> exporter();

	/**
	 * Required container for Repeatable annotations.
	 */
	@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
	@Target(ElementType.TYPE)	//Only use on type declarations
	@Documented	//Include values for this annotation in JavaDocs
	@interface List {
		GroupExport[] value();
	}
}
