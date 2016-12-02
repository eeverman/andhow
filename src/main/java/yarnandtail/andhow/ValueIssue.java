package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public abstract class ValueIssue {
	protected final ConfigPoint<?> point;
	protected final Object value;
	
	ValueIssue(ConfigPoint<?> point, Object value) {
		this.point = point;
		this.value = value;
	}

	public ConfigPoint<?> getPoint() {
		return point;
	}

	public Object getValue() {
		return value;
	}
	
	/**
	 * Builds a message describing the issue with the value withing the context
	 * of the ConfigPoint.
	 * 
	 * Assume that the user already sees the name of the config point listed:
	 * Its name does not need to be repeated, only the value and the issue w/ it.
	 * is 
	 * @return 
	 */
	public abstract String getMessageInPointContext();
	
}
