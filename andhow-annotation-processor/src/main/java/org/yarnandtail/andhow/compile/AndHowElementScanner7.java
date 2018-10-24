package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.util.NameUtil;
import com.sun.source.util.Trees;
import java.util.logging.Level;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;
import org.yarnandtail.andhow.util.AndHowLog;

import static javax.lang.model.util.ElementFilter.*;

/**
 * This Scanner is only intended to work on a single compileable unit at a time.
 *
 * Do not use the {@code scan(Iterable<? extends Element>)} method defined in the super
 * class.
 *
 * @author ericeverman
 */
public class AndHowElementScanner7 extends ElementScanner7<CompileUnit, String> {
	private static final AndHowLog LOG = AndHowLog.getLogger(AndHowElementScanner7.class);

	private final Types typeUtils;
	//private final TypeElement propertyTypeElem;
	private final TypeMirror propertyTypeMirror;
	private final TypeMirror initTypeMirror;
	private final TypeMirror testInitTypeMirror;
	private final Trees trees;
	
	CompileUnit compileUnit;		//Info on a single compileable file.  Late init.

	/**
	 * 
	 * @param processingEnv	The ProcessingEnvironment that a javax.annotation.processing.Processor
	 * is initialized with.
	 * @param typeNameOfAndHowProperty The fully qualified name of the interface that
	 * all AndHow Properties implement.
	 */
	public AndHowElementScanner7(ProcessingEnvironment processingEnv,
			String typeNameOfAndHowProperty,
			String typeNameOfAndHowInit,
			String typeNameOfAndHowTestInit) {

		super(null);

		trees = Trees.instance(processingEnv);
		typeUtils = processingEnv.getTypeUtils();
		
		//Type for a property
		TypeElement propertyTypeElem = processingEnv.getElementUtils().getTypeElement(typeNameOfAndHowProperty);
		propertyTypeMirror = propertyTypeElem.asType();
		
		//Type for an AndHowInit
		TypeElement initTypeElem = processingEnv.getElementUtils().getTypeElement(typeNameOfAndHowInit);
		initTypeMirror = initTypeElem.asType();
		
		//Type for an AndHowTestInit
		TypeElement testInitTypeElem = processingEnv.getElementUtils().getTypeElement(typeNameOfAndHowTestInit);
		if (testInitTypeElem != null) {
			testInitTypeMirror = testInitTypeElem.asType();
		} else {
			testInitTypeMirror = null;
		}

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
						e.getSimpleName().toString(),
						e.getModifiers().contains(Modifier.STATIC),
						e.getModifiers().contains(Modifier.FINAL)
				);
				
				if (LOG.isLoggable(Level.FINE)) {
					LOG.debug("Found creating of an AndHow Property in source code: {0}",
							NameUtil.getAndHowName(compileUnit.getRootCanonicalName(), e.getSimpleName().toString(), compileUnit.getInnerPathNames()));
				}

			} else {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.trace("Found an AndHow Property variable ''{0}'' in ''{1}'', but it is just a reference to an existing property.",
							e.getSimpleName().toString(), compileUnit.getRootCanonicalName());
				}
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
			
			if (e.getKind().equals(ElementKind.CLASS) && ! e.getModifiers().contains(Modifier.ABSTRACT)) {
				if (testInitTypeMirror != null &&
						typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(testInitTypeMirror))) {
					
					compileUnit.setTestInitClass(true);
					
				} else if (typeUtils.isAssignable(typeUtils.erasure(e.asType()), typeUtils.erasure(initTypeMirror))) {
					compileUnit.setInitClass(true);
				}
			}

			
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
