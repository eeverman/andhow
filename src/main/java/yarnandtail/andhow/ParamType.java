package yarnandtail.andhow;

/**
 * The broad type of parameter.
 * @author eeverman
 */
public enum ParamType {
	/** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning */
	NON_PARAM(false, false),
	/** Flag that is true just by its presence.  May also be explicitly set true. */
	FLAG(true, false),
	/** Name-value parameter.  Only a single value or instance is allowed. */
	SINGLE_NAME_VALUE(false, false),
	/** Name-value parameter that allows multiple values or multiple instances. */
	MULTI_NAME_VALUE(false, true);
	
	private final boolean flag;
	private final boolean multipleOk;

	
	ParamType(boolean flag, boolean multipleOk) {
		this.flag = flag;
		this.multipleOk = multipleOk;
	}
	
	public boolean isFlag() {
		return flag;
	}
	
	public boolean isMultipleOk() {
		return multipleOk;
	}
	
	public boolean isReal() {
		return type.isReal();
	}
	
	public boolean isNotReal() {
		return ! type.isReal();
	}

}
