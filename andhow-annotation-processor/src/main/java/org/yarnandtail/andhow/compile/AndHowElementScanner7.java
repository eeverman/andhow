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

	ProcessingEnvironment processEnv;
	Types typeUtils;
	Elements elemUtils;
	TypeElement propertyTypeElem;
	TypeMirror propertyTypeMirror;
	CompileUnit compileUnit;		//Info on a single compileable file

	private Trees trees;

	//Modifiers required on Properties
	Collection<Modifier> requiredMods;

	public AndHowElementScanner7(ProcessingEnvironment processingEnv) {

		super(null);

		processEnv = processingEnv;
		trees = Trees.instance(processingEnv);
		elemUtils = processEnv.getElementUtils();
		typeUtils = processEnv.getTypeUtils();
		propertyTypeElem = processingEnv.getElementUtils().getTypeElement("org.yarnandtail.andhow.api.Property");
		propertyTypeMirror = propertyTypeElem.asType();

		requiredMods = new ArrayList();
		requiredMods.add(Modifier.FINAL);
		requiredMods.add(Modifier.PUBLIC);
		requiredMods.add(Modifier.STATIC);
	}

	@Override
	public CompileUnit visitVariable(VariableElement e, String p) {

		boolean isPropType = typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(propertyTypeMirror));

		if (isPropType) {
			System.out.println("Found AndHow Property variable " + e.getSimpleName().toString() + " Type: " + typeUtils.erasure(e.asType()));

			AndHowTreeScanner ts = new AndHowTreeScanner();
			PropertyMarker marker = new PropertyMarker();
			ts.scan(trees.getPath(e), marker);
			if (marker.isNewProperty()) {
				System.out.println("###### WE THINK THIS IS A PROPERTY ASSIGNMENT #######");

				compileUnit.foundProperty(
						new SimpleVariable(e.getSimpleName().toString(),
						e.getModifiers().contains(Modifier.STATIC),
						e.getModifiers().contains(Modifier.FINAL))
				);

			}
		} else {
			System.out.println("Skipping variable " + e.toString() + " Type: " + typeUtils.erasure(e.asType()));
		}

		return compileUnit;

	}

	@Override
	public CompileUnit visitType(TypeElement e, String p) {
		
		if (compileUnit == null) {
			System.out.println("visitType: (root) " + e.getQualifiedName().toString());

			compileUnit = new CompileUnit(e.getQualifiedName().toString());
		}
				
		//TODO:  This is not right, but it seems to work - why??
		//The current convention is to NOT push the TLC, but we seem to get the
		//correct paths printed out (maybe they are not right??)
		this.compileUnit.pushType(
				new SimpleType(e.getSimpleName().toString(), e.getModifiers().contains(Modifier.STATIC))
		);

		scan(fieldsIn(e.getEnclosedElements()), p);
		scan(typesIn(e.getEnclosedElements()), p);

		this.compileUnit.popType();

		return compileUnit;
	}

}
