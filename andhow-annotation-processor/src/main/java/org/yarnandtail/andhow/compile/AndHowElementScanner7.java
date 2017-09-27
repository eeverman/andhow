package org.yarnandtail.andhow.compile;

import com.sun.source.util.Trees;
import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;
import static javax.lang.model.util.ElementFilter.*;

/**
 * This Scanner is only intended to work on a single compileable unit at a time.
 *
 * Do not use the scan(Iterable<? extends Element>) method defined in the super
 * class.
 *
 * @author ericeverman
 */
public class AndHowElementScanner7 extends ElementScanner7<CompileUnit, String> {

	private final Types typeUtils;
	private final TypeElement propertyTypeElem;
	private final TypeMirror propertyTypeMirror;
	private final Trees trees;
	
	CompileUnit compileUnit;		//Info on a single compileable file.  Late init.

	/**
	 * 
	 * @param processingEnv	The ProcessingEnvironment that a javax.annotation.processing.Processor
	 * is initialized with.
	 * @param typeNameOfAndHowProperty The fully qualified name of the interface that
	 * all AndHow Properties implement.
	 */
	public AndHowElementScanner7(ProcessingEnvironment processingEnv, String typeNameOfAndHowProperty) {

		super(null);

		trees = Trees.instance(processingEnv);
		typeUtils = processingEnv.getTypeUtils();
		propertyTypeElem = processingEnv.getElementUtils().getTypeElement(typeNameOfAndHowProperty);
		propertyTypeMirror = propertyTypeElem.asType();

	}

	@Override
	public CompileUnit visitVariable(VariableElement e, String p) {

		boolean isPropType = typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(propertyTypeMirror));

		if (isPropType) {

			PropertyVariableTreeScanner ts = new PropertyVariableTreeScanner();
			PropertyMarker marker = new PropertyMarker();
			ts.scan(trees.getPath(e), marker);
			if (marker.isNewProperty()) {
				compileUnit.addProperty(
						new SimpleVariable(e.getSimpleName().toString(),
						e.getModifiers().contains(Modifier.STATIC),
						e.getModifiers().contains(Modifier.FINAL))
				);
				
				System.out.println("Found new AndHow Property " + 
					NameUtil.getCanonicalPropertyName(compileUnit.getCanonicalRootName(), e.getSimpleName().toString(), compileUnit.getInnerPathNames())
				);

			} else {
				System.out.println("Found an AndHow Property variable '" + e.getSimpleName().toString() + "' in '" + compileUnit.getCanonicalRootName() +
						"' but it is just a reference to an existing property.");
			}
		} else {
			//Skip the property b/c it is not a of type that is an AndHow Property
		}

		return compileUnit;

	}

	@Override
	public CompileUnit visitType(TypeElement e, String p) {
		
		if (compileUnit == null) {
			
			//Just entered a top level class.
			//Construct a new CompileUnit to record state and scan its contents
			
			compileUnit = new CompileUnit(e.getQualifiedName().toString());
			
			scan(fieldsIn(e.getEnclosedElements()), p);
			scan(typesIn(e.getEnclosedElements()), p);
		} else {
				
			//Just entered an inner class (at an arbitrary level of nexting)
			//Push it onto the CompileUnit stack and scan its contents
			
			this.compileUnit.pushType(e.getSimpleName().toString(), e.getModifiers().contains(Modifier.STATIC));
			
			scan(fieldsIn(e.getEnclosedElements()), p);
			scan(typesIn(e.getEnclosedElements()), p);

			//Now leaving the inner class
			this.compileUnit.popType();
		}
				



		return compileUnit;
	}

}
