package org.yarnandtail.andhow.compile;

import java.util.*;
import javax.lang.model.element.TypeElement;

/**
 * Metadata about a Type (the compile-time representation of a class) as it is compiled.
 * 
 * 'class' here means a class or interface for which the only logical parent is
 * its package.  Other types of nested classes, such as an inner class or
 * interface would be contained within the metadata of the parent class.
 * 
 * This class is used by doing a depth first scan of a Type.  Each Type as it is
 * found is found is pushType()'ed, building up a stack of the nested structure.
 * Within a type, variables are scanned.  If an AndHow Property is discovered,
 * typeContainsProperty() is called to mark the Type as parent to a Property.
 * 
 * Before scanning leaves a type, popType() must be called.
 * 
 * The parent class here is referred to as root.  
 * 
 * @author ericeverman
 */
public class CompileUnit {
	
	private TypeElement root;
	private ArrayDeque<TypeElement> stack = new ArrayDeque();
	private TypeElement activePropertyRoot;
	private List<TypeElement[]> propertyRoots = new ArrayList();

	private List<String> errors = new ArrayList();
	
	public CompileUnit() {

	}
	
	public void pushType(TypeElement element) {
		stack.push(element);
		
		if (root == null) {
			root = element;
		}
	}
	
	public void popType() {
		TypeElement head = stack.poll();
		if (head.equals(activePropertyRoot)) {
			activePropertyRoot = null;	//no longer in a prop container
		}
	}
	
	/**
	 * Marks that an AndHow Property field was found contained within the current
	 * TypeElement.
	 * 
	 * If there was a Property found in an enclosing type (such as a nested
	 * interface), this method has no effect.
	 */
	public void typeContainsProperty() {
		if (activePropertyRoot == null) {
			activePropertyRoot = stack.peek();
			
			propertyRoots.add(
					stack.toArray(new TypeElement[stack.size()])
				);
		}
	}
	
	public void addPropertyError(String propName, String msg) {
		String path = stack.peek().getQualifiedName().toString();
		
		errors.add("The AndHow Property '" + propName + "' in " + path + " is invalid: " + msg);
	}
 
	public TypeElement getRoot() {
		return stack.getLast();
	}

	public List<TypeElement[]> getPropertyRoots() {
		return propertyRoots;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	public boolean containsErrors() {
		return ! errors.isEmpty();
	}
	
	public boolean containsPropertyRoots() {
		return ! propertyRoots.isEmpty();
	}

}
