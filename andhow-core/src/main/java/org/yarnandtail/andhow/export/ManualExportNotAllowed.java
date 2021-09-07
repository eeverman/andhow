package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.Property;

import java.lang.annotation.*;

/**
 * Disables manual export of {@link Property}'s contained in the annotated class, and inner
 * classes contained in the class.
 * <p>
 * Export is not allowed by default, so this is used when a containing class is annotated with
 * {@link ManualExportAllowed} and there is an inner class/interface which should not be exported.
 * <p>
 * Usage example:
 * <pre><code>
 * //Allow exports for this class & contained classes
 * {@literal @}ManualExportAllowed
 * public class MyClass {
 *
 *   // Export allowed
 *   static IntProp INT1 = IntProp.builder()
 *       .aliasOut("int1out1").build();
 *
 *   //Inherits 'export allowed' from the containing class
 *   static interface Inner1 {
 * 	   static StrProp STR1 = StrProp.builder().build();
 *   }
 *
 *   //Block export via the annotation
 *   {@literal @}ManualExportNotAllowed
 *   static class No { ...Properties in here cannot be exported... }
 * }
 * </code></pre><p>
 * See {@link AndHow#export()} for export details and examples.
 */
@Retention(RetentionPolicy.RUNTIME) //ensures this annotation is available to the VM, not just compiler
@Target(ElementType.TYPE)	//Only use on type declarations
@Documented  //Include values for this annotation in JavaDocs
public @interface ManualExportNotAllowed {
}
