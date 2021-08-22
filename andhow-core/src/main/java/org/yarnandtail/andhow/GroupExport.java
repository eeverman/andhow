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
	 * The default is {@code INHERIT_FROM_CONTAINER}, which will inherit this setting from the
	 * containing class if this annotation is on a nested inner class.  If no containing class exists,
	 * or the containing class doesn't specify, the default is {@code ONLY_IF_NO_OUT_ALIAS}.
	 *
	 * Combinations of this option and {@code exportByOutAliases} can result in multiple copies
	 * of the same value being exported, which is permissible.
	 * 
	 * @return The export option for Property canonical names.
	 */
	org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME exportByCanonicalName()
			default Exporter.EXPORT_CANONICAL_NAME.INHERIT_FROM_CONTAINER;
	
	/**
	 * Specifies if the out aliases, which are aliases for the purpose
	 * of exports, should be used when exporting property values.
	 *
	 * The default is {@code INHERIT_FROM_CONTAINER}, which will inherit this setting from the
	 * containing class if this annotation is on a nested inner class.  If no containing class exists,
	 * or the containing class doesn't specify, the default is {@code ALWAYS}.
	 * <p>
	 * Its possible to have multiple 'out' alias, and {@code ALWAYS} will export all of them.
	 * This will result in multiple copies of the value being exported.  The {@code FIRST} and
	 * {@code LAST} options address this by allowing specifying the respective value from the list.
	 * <p>
	 * 
	 * @return  The export option for Property 'out' aliases.
	 */
	org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES exportByOutAliases()
			default Exporter.EXPORT_OUT_ALIASES.INHERIT_FROM_CONTAINER;

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
