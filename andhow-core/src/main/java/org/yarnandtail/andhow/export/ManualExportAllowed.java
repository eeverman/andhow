package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Exporter;

import java.lang.annotation.*;

/**
 * Classes annotation with this annotation will allow all AndHow Properties in the class to be
 * exported to a HashMap or other structure by any code holding a reference to the Class.
 *
 * The {@code exportByCanonicalName} and {@code exportByOutAliases} parameters just provide defaults
 * when the manual export is done:  The code calling export can choose to export any way it chooses.
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented  //Include values for this annotation in JavaDocs
public @interface ManualExportAllowed {

	/**
	 * Specifies if the canonical name should be used to export property values.
	 *
	 * The default is {@code ONLY_IF_NO_OUT_ALIAS}, will export the canonical name and value only
	 * when there is no out alias for the Property.
	 *
	 * Combinations of this option and {@code exportByOutAliases} can result in multiple copies
	 * of the same value being exported.
	 *
	 * @return The export option for Property canonical names.
	 */
	Exporter.EXPORT_CANONICAL_NAME exportByCanonicalName()
			default Exporter.EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS;

	/**
	 * Specifies if the out aliases, which are aliases for the purpose
	 * of exports, should be used when exporting property values.
	 *
	 * The default is {@code ALWAYS}, which will export the Property's out-alias and value if an
	 * alias exists.  Its possible to have multiple 'out' aliases, resulting in multiples copies of
	 * the value being exported.  The {@code FIRST} and {@code LAST} options address this by
	 * allowing specifying the respective value from the list.
	 *
	 * @return  The export option for Property 'out' aliases.
	 */
	Exporter.EXPORT_OUT_ALIASES exportByOutAliases()
			default Exporter.EXPORT_OUT_ALIASES.ALWAYS;

}
