package org.yarnandtail.andhow.compile;

import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;
import static javax.lang.model.util.ElementFilter.*;

/**
 *
 * @author ericeverman
 */
public class AndHowElementScanner7 extends ElementScanner7<CompileUnit, String> {
	
	ProcessingEnvironment processEnv;
	Types typeUtils;
	Elements elemUtils;
	TypeElement propertyTypeElem;
	TypeMirror propertyTypeMirror;
	
	//Modifiers required on Properties
	Collection<Modifier> requiredMods;

			
	public AndHowElementScanner7(ProcessingEnvironment processingEnv) {
		
		super(new CompileUnit());
		
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
	public CompileUnit visitTypeParameter(TypeParameterElement e, String p) {
		System.out.println("visitTypeParameter: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitTypeParameter(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompileUnit visitExecutable(ExecutableElement e, String p) {
		System.out.println("visitExecutable: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitExecutable(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompileUnit visitVariable(VariableElement e, String p) {

		boolean isProp = typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(propertyTypeMirror));
		System.out.println("visitVariable: " + e.toString() + " Type: " + typeUtils.erasure(e.asType()) + 
				" Kind: " + e.asType().getKind() + " is a Property: " + isProp +
				" A prop is " + typeUtils.erasure(propertyTypeMirror));
		
		if (isProp) {
			 DEFAULT_VALUE.typeContainsProperty();
			 
			if (! e.getModifiers().containsAll(requiredMods)) {
				DEFAULT_VALUE.addPropertyError(e.getSimpleName().toString(), "Does not have required modifiers");
			}
		}
		
		return DEFAULT_VALUE;
	
	}

	@Override
	public CompileUnit visitType(TypeElement e, String p) {
		System.out.println("visitType: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		this.DEFAULT_VALUE.pushType(e);
		
		scan(fieldsIn(e.getEnclosedElements()), p);
		scan(typesIn(e.getEnclosedElements()), p);
		
		this.DEFAULT_VALUE.popType();
		
		return DEFAULT_VALUE;
	}

	@Override
	public CompileUnit visitPackage(PackageElement e, String p) {
		System.out.println("visitPackage: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitPackage(e, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CompileUnit visitUnknown(Element e, String p) {
		System.out.println("visitUnknown: " + e.toString() + " Type: " + e.asType().toString() + " Kind: " + e.asType().getKind());
		return super.visitUnknown(e, p); //To change body of generated methods, choose Tools | Templates.
	}
	

}
