package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.yarnandtail.andhow.util.NameUtil;
import java.util.*;

/**
 * Incrementally built metadata about a Type (compile-time representation of a class)
 * and its AndHow Properties (if any) as it is compiled.
 *
 * One instance of a CompileUnit corresponds to a single top level class, that is,
 * a class or interface that is not an inner class and for which the only logical
 * parent is its package. Inner classes and interfaces are represented as the
 * 'inner path' - the heirarchical list of nested inner classes from the outer
 * to the innermost one.  The state of the inner path is recorded each time
 * addProperty() is called, so that each Property can be registered with the
 * appropriate inner path, which will then make up its canonical name.
 *
 * This class is used by doing a depth first scan of a Type. As each Type is
 * found it is pushType'ed into the CompileUnit.  Types are popType()'ed  out
 * when the scan leaves a type.  Within a type, variables are scanned. If a
 * variable with the type AndHow Property is discovered and the assignment is
 * from a newly constructed Property (not just a reference), addProperty() is
 * called and the metadata about the inner path and variable name are recorded
 * here.
 * 
 * This class is stateful and is not thread safe.
 *
 * @author ericeverman
 */
public class CompileUnit {

	private final String classCanonName;
	private PropertyRegistrationList registrations;	//late init
	private List<CompileProblem> errors;	//late init
	private boolean initClass;	//True if an AndHowInit instance (and not AndHowTestInit)
	private boolean testInitClass;	//True if an AndHowTestInit instance
	
	/**
	 * This is being used as a stack. Always push into the tail of the queue and
	 * always 'pop' from the tail. The nested inner class order from outer to
	 * inner is then the normal iteration order of the queue from the head to
	 * the tail.  Uses late initiation.
	 */
	private ArrayDeque<SimpleType> innerPathStack = new ArrayDeque();

	/**
	 * Construct a new CompileUnit, which always is for a specific top level
	 * class.
	 *
	 * A top level class is a non-inner class.
	 *
	 * @param classCanonName The fully qualified name of a top level class.
	 */
	public CompileUnit(String classCanonName) {
		this.classCanonName = classCanonName;
	}
	
	public boolean isInitClass() {
		return initClass;
	}
	
	public void setInitClass(boolean initClass) {
		this.initClass = initClass;
	}
	
	public boolean istestInitClass() {
		return testInitClass;
	}
	
	public void setTestInitClass(boolean testInitClass) {
		this.testInitClass = testInitClass;
	}

	public void pushType(SimpleType simpleName) {
		
		if (innerPathStack == null) {
			innerPathStack = new ArrayDeque();
		}
		
		innerPathStack.addLast(simpleName);
	}

	public void pushType(String name, boolean _static) {
		pushType(new SimpleType(name, _static));
	}

	public SimpleType popType() {
		
		if (innerPathStack == null || innerPathStack.size() == 0) {
			throw new RuntimeException("The nesting order of inner classes is broken - expected to be in an inner class.");
		}
		return innerPathStack.pollLast();
	}

	/**
	 * Register an AndHow Property declaration in the current scope - either
	 * directly in the the top level class or the recorded path to an inner
	 * class.
	 *
	 * If modifiers are invalid, an error will be recorded rather than a
	 * Property.
	 *
	 * @param name The name of the variable the Property is assigned to.
	 * @param _static Does the variable has the static modifier?
	 * @param _final Is the variable declared as static?
	 * @return True if the property could be added, false if an error was
	 * recorded instead.
	 */
	public boolean addProperty(String name, boolean _static, boolean _final) {

		if (_static && _final) {
			if (registrations == null) {
				registrations = new PropertyRegistrationList(classCanonName);
			}

			registrations.add(name, getInnerPathNames());

			return true;
		} else {
			
			if (errors == null) {
				errors = new ArrayList();
			}
		
			String parentName = NameUtil.getJavaName(classCanonName, this.getInnerPathNames());
		
			if (_static) {
				errors.add(new CompileProblem.PropMissingFinal(parentName, name));
			} else if (_final) {
				errors.add(new CompileProblem.PropMissingStatic(parentName, name));
			} else {
				errors.add(new CompileProblem.PropMissingStaticFinal(parentName, name));
			}
			
			return false;
		}
	}

	/**
	 * Return the state of inner class nesting from the outermost to the
	 * innermost.
	 *
	 * If the current state is at the root of the top level class, an empty list
	 * is returned.
	 *
	 * @see #getInnerPathNames() for just the names of the nested inner classes.
	 * @return
	 */
	public List<SimpleType> getInnerPath() {

		List<SimpleType> innerPath;

		if (innerPathStack != null && innerPathStack.size() > 0) {
			innerPath = new ArrayList(innerPathStack);
			//Collections.reverse(innerPath);
		} else {
			innerPath = Collections.EMPTY_LIST;
		}

		return innerPath;
	}

	/**
	 * Return the inner class names, in order from the outermost to the
	 * innermost.
	 *
	 * If the current state is at the root of the top level class, an empty list
	 * is returned.
	 *
	 * @return
	 */
	public List<String> getInnerPathNames() {

		List<String> pathNames;

		if (innerPathStack != null && innerPathStack.size() > 0) {
			pathNames = new ArrayList();

			Iterator<SimpleType> it = innerPathStack.iterator();

			while (it.hasNext()) {
				pathNames.add(it.next().getName());
			}

		} else {
			pathNames = Collections.EMPTY_LIST;
		}

		return pathNames;
	}

	/**
	 * The fully qualified name of the top level class this CompileUnit is for.
	 *
	 * @return
	 */
	public String getRootCanonicalName() {
		return classCanonName;
	}
	
	/**
	 * Get just the simple name of the root class.
	 * 
	 * @return 
	 */
	public String getRootSimpleName() {
		
		int dotPos = classCanonName.lastIndexOf(".");
		
		if (dotPos > 0) {
			return classCanonName.substring(dotPos + 1);
		} else {
			return classCanonName;
		}
		
	}
	
	/**
	 * Get just the package of the root class.
	 * 
	 * @return null if the root class is in the default package.
	 */
	public String getRootPackageName() {
		
		int dotPos = classCanonName.lastIndexOf(".");
		
		if (dotPos > 0) {
			return classCanonName.substring(0, dotPos);
		} else {
			return null;
		}
		
	}

	/**
	 * The list of Properties in the CompileUnit, along with all the needed
	 * metadata to register them.
	 *
	 * @return
	 */
	public PropertyRegistrationList getRegistrations() {
		return registrations;
	}

	/**
	 * The list of Property errors, either directly added or created indirectly
	 * by adding properties that had invalid modifiers.
	 *
	 * @return
	 */
	public List<CompileProblem> getProblems() {
		if (errors != null) {
			return errors;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Returns true if the getProblems() list would be non-empty.
	 *
	 * @return
	 */
	public boolean hasErrors() {
		if (errors != null) {
			return !errors.isEmpty();
		} else {
			return false;
		}
		
	}

	/**
	 * Returns true if getRegistrations() would return a non-empty list.
	 *
	 * @return
	 */
	public boolean hasRegistrations() {
		return registrations != null && !registrations.isEmpty();
	}

}
