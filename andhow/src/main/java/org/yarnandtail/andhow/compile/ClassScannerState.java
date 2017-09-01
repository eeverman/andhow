package org.yarnandtail.andhow.compile;

import java.util.*;
import javax.lang.model.element.TypeElement;

/**
 *
 * @author ericeverman
 */
public class ClassScannerState {
	
	TypeElement root;
	ArrayDeque<TypeElement> stack = new ArrayDeque();
	TypeElement activePropertyRoot;
	List<TypeElement[]> propertyRoots = new ArrayList();

	private List<String> errors = new ArrayList();
	
	public ClassScannerState(TypeElement root) {
		this.root = root;
		stack.push(root);
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
	
	public void errorProperty(String propName, String msg) {
		String path = stack.peek().getQualifiedName().toString();
		
		errors.add("The AndHow Property 'propName' in " + path + " is invalid: " + msg);
	}
 
	public TypeElement getRoot() {
		return root;
	}

	public List<TypeElement[]> getPropertyRoots() {
		return propertyRoots;
	}

	public List<String> getErrors() {
		return errors;
	}

}
