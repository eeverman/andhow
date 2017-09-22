package org.yarnandtail.andhow.compile;

/**
 * Compile time representation of a variable w/ just enough information
 * to decide if it is a valid variable for an AndHow Property to be constructed
 * and assigned to.
 */
public class SimpleVariable {

	private final String name;
	private final boolean _static;
	private final boolean _final;

	/**
	 * 
	 * @param name	Name of this variable
	 * @param _static Is this var marked as static?
	 * @param _final Is this var marked as final?
	 */
	public SimpleVariable(String name, boolean _static, boolean _final) {
		this.name = name;
		this._static = _static;
		this._final = _final;
	}

	public String getName() {
		return name;
	}

	public boolean isStatic() {
		return _static;
	}

	public boolean isFinal() {
		return _final;
	}
	

}
