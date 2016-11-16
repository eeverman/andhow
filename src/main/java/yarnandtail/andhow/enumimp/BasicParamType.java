package yarnandtail.andhow.enumimp;

/**
 * The broad type of parameter.
 * @author eeverman
 */
enum BasicParamType {

	/** An application related flag that is true just by its presence.  May also be explicitly set true */
	FLAG(true, false),
	/** A single-value application related name-value parameter.  Multiple values or multiple instances of the parameter are not allowed.  */
	SINGLE_NAME_VALUE(false, false),
	/** A multi-value application related name-value parameter.  Multiple values and multiple instances of the parameter are supported.  */
	MULTI_NAME_VALUE(false, true);
	
	private final boolean flag;
	private final boolean multipleOk;
	
	BasicParamType(boolean flag, boolean multipleOk) {
		this.flag = flag;
		this.multipleOk = multipleOk;
	}

	public boolean isFlag() {
		return flag;
	}

	public boolean isMultipleOk() {
		return multipleOk;
	}

}
