package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class PointValueProblem {
	
	public static final String NULL_PRINT = "[[null]]";
	

	
	protected final Loader loader;
	protected final ConfigPoint<?> point;
	protected final Object unconvertable;
	protected final Object value;
	protected final Validator validator;
	protected final TYPE type;
	
	/**
	 * Creates an INVALID_VALUE, as determined by a validator
	 * @param loader
	 * @param point
	 * @param value
	 * @param validator 
	 */
	public PointValueProblem(Loader loader, ConfigPoint<?> point, Object value, Validator<?> validator) {
		this.loader = loader;
		this.point = point;
		this.unconvertable = null;
		this.value = value;
		this.validator = validator;
		this.type = TYPE.INVALID_VALUE;
	}
	
	/**
	 * Creates an instance of type UNCOVERTABLE_STRING
	 * 
	 * @param loader
	 * @param point
	 * @param type 
	 */
	public PointValueProblem(Loader loader, ConfigPoint<?> point, String unconvertableString) {
		this.loader = loader;
		this.point = point;
		this.unconvertable = unconvertableString;
		this.value = null;
		this.validator = null;
		this.type = TYPE.UNCOVERTABLE_STRING;
	}
	
	/**
	 * Creates an instance of type UNCOVERTABLE_OBJECT
	 * @param loader
	 * @param point
	 * @param unconvertableObject 
	 */
	public PointValueProblem(Loader loader, ConfigPoint<?> point, Object unconvertableObject) {
		this.loader = loader;
		this.point = point;
		this.unconvertable = unconvertableObject;
		this.value = null;
		this.validator = null;
		this.type = TYPE.UNCOVERTABLE_OBJECT;
	}

	public Loader getLoader() {
		return loader;
	}

	public ConfigPoint<?> getPoint() {
		return point;
	}

	public Object getUnconvertable() {
		return unconvertable;
	}

	public Object getValue() {
		return value;
	}
	
	public Validator getValidator() {
		return validator;
	}
	
	public TYPE getType() {
		return type;
	}
	
	/**
	 * Builds a message describing the issue with the value within the context
	 * of the ConfigPoint.
	 * 
	 * Assume that the user already sees the name of the config point listed:
	 * Its name does not need to be repeated, only the value and the issue w/ it.
	 * is 
	 * @return 
	 */
	public String getMessageWithinFullContext() {
		switch (type) {
			case INVALID_VALUE:
				return validator.getInvalidMessage(value);
			case UNCOVERTABLE_STRING:
				String strEff = (unconvertable!=null)?unconvertable.toString():NULL_PRINT; 
				return "The string '" + strEff + "' could not be converted to a " + 
						point.getValueType().getDestinationType().getCanonicalName();
			case UNCOVERTABLE_OBJECT:
				String objType = NULL_PRINT;
				String objEff = NULL_PRINT;
				if (unconvertable != null) {
					objEff = unconvertable.toString();
					objType = unconvertable.getClass().getCanonicalName();
				}

				return "The object of type " + objType + " with a toString() value of '" +
						objEff + "' could not be converted to a " + 
						point.getValueType().getDestinationType().getCanonicalName();
			default:
				throw new RuntimeException("Unexpected TYPE");
		}
	}
	
	public enum TYPE {
		INVALID_VALUE, /* After conversion, the value is invalid */
		UNCOVERTABLE_STRING, /* The source string could not be converted to the destination type */
		UNCOVERTABLE_OBJECT /* The source object (non-String) could not be converted to the destination type */
	};
	
}
