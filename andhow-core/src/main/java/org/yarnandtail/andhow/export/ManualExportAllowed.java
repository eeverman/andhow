package org.yarnandtail.andhow.export;

import static org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.AndHow;

import java.lang.annotation.*;

/**
 * Enables manual export of {@link Property}'s contained in the annotated class, for use
 * with frameworks that take configuration as key-value maps or similar.
 * <p>
 * Export is not allowed by default. This annotation applies to Properties in the annotated class
 * as well as those in inner classes/interfaces, however, inner classes can be excluded with
 * the {@link ManualExportNotAllowed} annotation.
 * <p>
 * {@link #useCanonicalName()}} and {@link #useOutAliases()} parameters specify which names for each
 * Property are included in the export, however, it is easy to rewrite or override the exported
 * names and values - See {@link AndHow#export(Class[])} for details on performing an export.
 * <p>
 * Annotation example:
 * <pre><code>
 * //Allow exports for this class & contained classes
 * {@literal @}ManualExportAllowed
 * public class MyClass {
 *
 *   // Two 'out' alias names are spec'ed, so key-value pairs would be:
 *   // int1out1 : [The value of INT1]
 *   // int1out2 : [The value of INT1]
 *   static IntProp INT1 = IntProp.builder()
 *       .aliasOut("int1out1").aliasOut("int1out2").build();
 *
 *   // No 'out' names are spec'ed. Default annotation options export
 *   // with the canonical name, e.g.: [package].MyClass.INT2.
 *   static IntProp INT2 = IntProp.builder().build();
 *
 *   {@literal @}ManualExportAllowed(
 *       useCanonicalName = EXPORT_CANONICAL_NAME.NEVER,
 *       useOutAliases = EXPORT_OUT_ALIASES.ALWAYS)
 *   static interface Inner1 {
 *
 *     // No 'out' names are spec'ed. The canonical option is
 *     // set to NEVER, so this property is not be included.
 * 	   static StrProp STR1 = StrProp.builder().build();
 *   }
 *
 *   //Inherits 'export allowed' from the containing class
 *   static class Allowed { ... }
 *
 *   //Block export for other contained classes or interfaces
 *   {@literal @}ManualExportNotAllowed
 *   static class No { ...Properties in here cannot be exported... }
 * }
 * </code></pre>
 */
@Retention(RetentionPolicy.RUNTIME) // Annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	// Only use on type declarations
@Documented  // Include values for this annotation in JavaDocs
public @interface ManualExportAllowed {

	/**
	 * Specifies when canonical names should be included in the list of export names.
	 * <p>
	 * The default, {@link EXPORT_CANONICAL_NAME#ONLY_IF_NO_OUT_ALIAS}, will include a
	 * {@link Property}'s canonical name only if the Property has no out aliases specified.
	 * <p>
	 * Combinations of this option and {@code useOutAliases} can result in multiple names for the
	 * same Property in the list of exported key-value pairs.
	 * <p>
	 * @return The export option for Property canonical names.
	 */
	EXPORT_CANONICAL_NAME useCanonicalName()
			default EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS;

	/**
	 * Specifies if out aliases should be included in the list of export names.
	 * <p>
	 * 'Out' aliases are alternate names for a {@link Property} for the purpose of export, so generally
	 * they are intended to be included.  Thus, the default is {@link EXPORT_OUT_ALIASES#ALWAYS}.
	 * <p>
	 * Properties may have multiple out alias names and a canonical name, so its possible to have
	 * multiple names for the same Property in the list of exported key-value pairs.
	 * <p>
	 * @return  The export option for Property 'out' aliases.
	 */
	EXPORT_OUT_ALIASES useOutAliases()
			default EXPORT_OUT_ALIASES.ALWAYS;

}
