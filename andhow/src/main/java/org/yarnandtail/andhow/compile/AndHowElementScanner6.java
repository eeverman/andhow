package org.yarnandtail.andhow.compile;

import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;

/**
 *
 * @author ericeverman
 */
public class AndHowElementScanner6 extends ElementScanner6<ClassScannerState, String> {
	
	ProcessingEnvironment processEnv;
	Types typeUtils;
	Elements elemUtils;
	TypeElement propertyTypeElem;
	TypeMirror propertyTypeMirror;
	
	//single return value that is added to
	ClassScannerState returnVal = new ClassScannerState();
	
	//Modifiers required on Properties
	Collection<Modifier> requiredMods;

			
	public AndHowElementScanner6(ProcessingEnvironment processingEnv) {
		processEnv = processingEnv;
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
	public ClassScannerState visitTypeParameter(TypeParameterElement e, String p) {
		System.out.println("visitTypeParameter: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitTypeParameter(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ClassScannerState visitExecutable(ExecutableElement e, String p) {
		System.out.println("visitExecutable: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitExecutable(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ClassScannerState visitVariable(VariableElement e, String p) {

		boolean isProp = typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(propertyTypeMirror));
		System.out.println("visitVariable: " + e.toString() + " Type: " + typeUtils.erasure(e.asType()) + 
				" Kind: " + e.asType().getKind() + " is a Property: " + isProp +
				" A prop is " + typeUtils.erasure(propertyTypeMirror));
		
		if (isProp) {
			 returnVal.propertyRoots.add(typeUtils.erasure(e.getEnclosingElement().asType()).toString());
		}
		
		if (! e.getModifiers().containsAll(requiredMods)) {
			returnVal.errors.add(e.getEnclosingElement().asType() + "." + e.getSimpleName() + " Does not have required modifiers");
		}
		return returnVal;
	
	}

	@Override
	public ClassScannerState visitType(TypeElement e, String p) {
		System.out.println("visitType: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitType(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ClassScannerState visitPackage(PackageElement e, String p) {
		System.out.println("visitPackage: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitPackage(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ClassScannerState visitUnknown(Element e, String p) {
		System.out.println("visitUnknown: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitUnknown(e, p); //To change body of generated methods, choose Tools | Templates.
	}
	

}
