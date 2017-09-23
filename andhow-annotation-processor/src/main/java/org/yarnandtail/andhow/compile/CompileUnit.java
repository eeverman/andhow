package org.yarnandtail.andhow.compile;

import java.util.*;

/**
 * Metadata about a Type (the compile-time representation of a class) as it is compiled.
 * 
 * 'class' here means a class or interface for which the only logical parent is
 its package.  Other types of nested classes, such as an inner class or
 interface would be contained within the metadata of the parent class.
 
 This class is used by doing a depth first scan of a Type.  Each Type as it is
 found is found is pushType()'ed, building up a innerPathStack of the nested structure.
 Within a type, variables are scanned.  If an AndHow Property is discovered,
 foundProperty() is called to mark the Type as parent to a Property.
 
 Before scanning leaves a type, popType() must be called.
 
 The parent class here is referred to as root.  
 * 
 * @author ericeverman
 */
public class CompileUnit {
	
	private final String classCanonName;
	private final ArrayDeque<SimpleType> innerPathStack = new ArrayDeque();
	
	private PropertyRegistrationList registrations;	//late init

	private final List<String> errors = new ArrayList();
	
	public CompileUnit(String classCanonName) {
		this.classCanonName = classCanonName;
	}
	
	public void pushType(SimpleType simpleName) {
		innerPathStack.push(simpleName);
	}
	
	public void pushType(String name, boolean _static) {
		pushType(new SimpleType(name, _static));
	}
	
	public SimpleType popType() {
		return innerPathStack.pollLast();
	}
	
	/**
	 * Marks that an AndHow Property field was found contained within the current
	 * TypeElement.
	 * 
	 * If there was a Property found in an enclosing type (such as a nested
	 * interface), this method has no effect.
	 * @param variableElement A SimpleType representing a variable to which
		an AndHow property is constructed and assigned to.
	 * @return True if the property could be added, false if an error was registered instead.
	 */
	public boolean foundProperty(SimpleVariable variableElement) {

		
		if (variableElement.isStatic() && variableElement.isFinal()) {
			if (registrations == null) {
				registrations = new PropertyRegistrationList(classCanonName);
			}

			registrations.add(variableElement.getName(), getInnerPathNames());
			
			return true;
		} else {
			addPropertyError(variableElement.getName(), "New AndHow Properties must be assigned to a static final field.");
			return false;
		}
	}
	
	public boolean foundProperty(String name, boolean _static, boolean _final) {
		return foundProperty(new SimpleVariable(name, _static, _final));
	}
	
	public List<SimpleType> getInnerPath() {
		
		List<SimpleType> innerPath = null;
		
		if (innerPathStack != null && innerPathStack.size() > 0) {
			innerPath = new ArrayList(innerPathStack);
		} else {
			innerPath = Collections.EMPTY_LIST;
		}

		return innerPath;
	}
	
	public List<String> getInnerPathNames() {
		
		List<String> pathNames = null;
		
		if (innerPathStack != null && innerPathStack.size() > 0) {
			pathNames = new ArrayList();
			
			for (SimpleType se : innerPathStack) {
				pathNames.add(se.getName());
			}
		} else {
			pathNames = Collections.EMPTY_LIST;
		}

		return pathNames;
	}
	
	public void addPropertyError(String propName, String msg) {
		errors.add("The AndHow Property '" + propName + "' in " + classCanonName + " is invalid: " + msg);
	}
 
	public String getCanonicalRootName() {
		return classCanonName;
	}

	public PropertyRegistrationList getRegistrations() {
		return registrations;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	public boolean hasErrors() {
		return ! errors.isEmpty();
	}
	
	public boolean hasRegistrations() {
		return registrations != null && ! registrations.isEmpty();
	}
	

}
