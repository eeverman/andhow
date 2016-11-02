package yarnandtail.andhow;

/**
 * The broad type of parameter.
 * @author eeverman
 */
public enum ConfigPointType {
	/** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning *//** A Param with no meaning */
	NON_PARAM(false, false, false),
	/** Flag that is true just by its presence.  May also be explicitly set true. */
	FLAG(true, false, false),
	/** Name-value parameter.  Only a single value or instance is allowed. */
	SINGLE_NAME_VALUE(false, false, false),
	/** 
	 * Name-value parameter that allows multiple values or multiple instances. 
	 * As values are found by the pipeline of ConfigLoaders, only the values
	 * found by the first ConfigLoader to find values for the parameter are kept -
	 * all other are considered overridden.
	 */
	MULTI_NAME_VALUE(false, true, false),
	/** 
	 * Name-value parameter that allows multiple values or multiple instances.
	 * As values are found by the pipeline of ConfigLoaders, the values are
	 * added to the list of values.
	 */
	MULTI_ACCUMULATE_NAME_VALUE(false, true, false);
	
	private final boolean flag;
	private final boolean multipleOk;
	private final boolean accumulate;

	
	ConfigPointType(boolean flag, boolean multipleOk, boolean accumulate) {
		this.flag = flag;
		this.multipleOk = multipleOk;
		this.accumulate = accumulate;
	}
	
	public boolean isFlag() {
		return flag;
	}
	
	public boolean isMultipleOk() {
		return multipleOk;
	}

	public boolean isAccumulate() {
		return accumulate;
	}
	
	public boolean isReal() {
		return ! NON_PARAM.equals(this);
	}
	
	public boolean isNotReal() {
		return NON_PARAM.equals(this);
	}
	
	

}
