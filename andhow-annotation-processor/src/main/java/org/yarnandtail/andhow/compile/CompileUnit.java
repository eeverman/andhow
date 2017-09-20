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
	private final ArrayDeque<String> innerPathStack = new ArrayDeque();
	
	private PropertyRegistrationList registrations;	//late init

	private final List<String> errors = new ArrayList();
	
	public CompileUnit(String classCanonName) {
		this.classCanonName = classCanonName;
	}
	
	public void pushType(String simpleName) {
		innerPathStack.push(simpleName);
	}
	
	public String popType() {
		return innerPathStack.pollLast();
	}
	
	/**
	 * Marks that an AndHow Property field was found contained within the current
	 * TypeElement.
	 * 
	 * If there was a Property found in an enclosing type (such as a nested
	 * interface), this method has no effect.
	 */
	public void foundProperty(String name) {

		if (registrations == null) {
			registrations = new PropertyRegistrationList(classCanonName);
		}
		
		registrations.add(name, getInnerPath());
	}
	
	public String[] getInnerPath() {
		
		String[] innerPath = null;
		
		if (innerPathStack != null && innerPathStack.size() > 0) {
			innerPath =  innerPathStack.toArray(new String[innerPathStack.size()]);
		}

		return innerPath;
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
