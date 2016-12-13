package yarnandtail.andhow;

/**
 * Problems with invalid values, values that cannot be converted to their destination type.
 * @author eeverman
 */
public class PropertyValueProblem {
	
	protected final Loader loader;
	protected final Property<?> property;
	protected final Object unconvertable;
	protected final Object value;
	protected final Validator validator;
	protected final TYPE type;
	
	/**
	 * Creates an INVALID_VALUE, as determined by a validator
	 * @param loader
	 * @param prop
	 * @param value
	 * @param validator 
	 */
	public PropertyValueProblem(Loader loader, Property<?> prop, Object value, Validator<?> validator) {
		this.loader = loader;
		this.property = prop;
		this.unconvertable = null;
		this.value = value;
		this.validator = validator;
		this.type = TYPE.INVALID_VALUE;
	}
	
	/**
	 * Creates an instance of type UNCOVERTABLE_STRING
	 * 
	 * @param loader
	 * @param prop
	 * @param type 
	 */
	public PropertyValueProblem(Loader loader, Property<?> prop, String unconvertableString) {
		this.loader = loader;
		this.property = prop;
		this.unconvertable = unconvertableString;
		this.value = null;
		this.validator = null;
		this.type = TYPE.UNCOVERTABLE_STRING;
	}
	
	/**
	 * Creates an instance of type UNCOVERTABLE_OBJECT
	 * @param loader
	 * @param prop
	 * @param unconvertableObject 
	 */
	public PropertyValueProblem(Loader loader, Property<?> prop, Object unconvertableObject) {
		this.loader = loader;
		this.property = prop;
		this.unconvertable = unconvertableObject;
		this.value = null;
		this.validator = null;
		this.type = TYPE.UNCOVERTABLE_OBJECT;
	}

	public Loader getLoader() {
		return loader;
	}

	public Property<?> getProperty() {
		return property;
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
	 * of the Property.
	 * 
	 * Assume that the user already sees the name of the config property listed:
	 * Its name does not need to be repeated, only the value and the issue w/ it.
	 * @return 
	 */
	public String getMessageWithinFullContext() {
		switch (type) {
			case INVALID_VALUE:
				return validator.getInvalidMessage(value);
			case UNCOVERTABLE_STRING:
				return TextUtil.format("The string '{}' could not be converted to a {}", unconvertable, 
						property.getValueType().getDestinationType().getCanonicalName());
			case UNCOVERTABLE_OBJECT:
				String objType = TextUtil.NULL_PRINT;
				if (unconvertable != null) {
					objType = unconvertable.getClass().getCanonicalName();
				}

				return TextUtil.format("The object of type {} with a toString() value of '{}' could not be converted to a {}",
								objType, unconvertable, property.getValueType().getDestinationType().getCanonicalName());
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
