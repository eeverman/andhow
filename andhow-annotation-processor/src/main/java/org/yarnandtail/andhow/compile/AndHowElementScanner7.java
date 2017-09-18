package org.yarnandtail.andhow.compile;

import com.sun.source.util.Trees;
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
	
	private Trees trees;
	
	//Modifiers required on Properties
	Collection<Modifier> requiredMods;

			
	public AndHowElementScanner7(ProcessingEnvironment processingEnv) {
		
		super(new CompileUnit());
		
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
			if (marker.isProperty()) {
				System.out.println("###### WE THINK THIS IS A PROPERTY ASSIGNMENT #######");
				
				DEFAULT_VALUE.foundProperty(e.getSimpleName().toString());
				
				
				if (! e.getModifiers().containsAll(requiredMods)) {
					DEFAULT_VALUE.addPropertyError(e.getSimpleName().toString(), "Does not have required modifiers");
				}
			}
		} else {
			System.out.println("Skipping variable " + e.toString() + " Type: " + typeUtils.erasure(e.asType()));	
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

}
