package org.yarnandtail.andhow;

import java.lang.annotation.*;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.Exporter;

/**
 * Causes all the Properties in the annotated class to be exported by the specified Exporter.
 * <p>
 * Exports via this annotation happen automatically at startup. There are also manual exports -
 * See {@link AndHow#export(Class[])}.
 * <p>
 * Only {@link Property}'s directly contained in the annotated class are exported:  Properties in
 * nested inner classes or interfaces are not included, though those inner classes could also be
 * annotated (this behaviour differs from manual exports).
 * <p>
 * This example would export the contained Properties to System.Properties, using 'out' aliases if
 * they exist, otherwise using canonical names:
 * <p><pre><code>
 * import org.yarnandtail.andhow.*;
 * import static org.yarnandtail.andhow.api.Exporter.*;
 * import org.yarnandtail.andhow.export.SysPropExporter;
 *
 * {@literal @}GroupExport(
 *   	exporter=SysPropExporter.class,
 * 		exportByCanonicalName=EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS,
 * 		exportByOutAliases=EXPORT_OUT_ALIASES.ALWAYS
 * )
 * class MyClass {
 * 		//Class containing Property classes...
 * }
 * </code></pre>
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
	 * property value under different names, which is permissable.
	 * 
	 * @return 
	 */
	org.yarnandtail.andhow.api.Exporter.EXPORT_CANONICAL_NAME exportByCanonicalName();
	
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
	org.yarnandtail.andhow.api.Exporter.EXPORT_OUT_ALIASES exportByOutAliases();

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
