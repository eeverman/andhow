package org.yarnandtail.andhow.compile;

/**
 * Compile time representation of a type/class definition w/ just enough information
 * to decide if it is a valid parent to contain an AndHow Property.
 */
public class SimpleType {

	private final String name;
	private final boolean _static;

	/**
	 * 
	 * @param name	Simple name (no package info or reference to containing classes) of this type
	 * @param _static Is this a static or non-static element?
	 */
	public SimpleType(String name, boolean _static) {
		this.name = name;
		this._static = _static;
	}

	public String getName() {
		return name;
	}

	public boolean isStatic() {
		return _static;
	}

}
