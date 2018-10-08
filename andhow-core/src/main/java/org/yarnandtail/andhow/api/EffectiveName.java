package org.yarnandtail.andhow.api;

/**
 * A name for a Property that has been processed by a NamingStrategy to create
 * an effectiveIn name, which may be different than the original name.
 *
 * The NamingStrategy is also responsible to creating an effectiveIn name. An
 * effectiveIn name is the name that is matched on when trying to match up
 * incoming properties names found by a Loader. This is distinct from the actual
 * name to accommodate case insensitive naming. For instance, if a property is
 * aliased as 'bob', the effectiveIn name may be 'BOB' with all incoming names
 * also converted to uppercase.
 *
 * @author ericeverman
 */
public class EffectiveName extends Name {

	private final String effectiveIn;
	private final String effectiveOut;
	
	
	/**
	 * A new instance using the actual name as the effective out.
	 * 
	 * @param actual
	 * @param effectiveIn
	 * @param in
	 * @param out 
	 */
	public EffectiveName(String actual, String effectiveIn, boolean in, boolean out) {
		
		super(actual, in, out);
		
		if (effectiveIn == null || effectiveIn.length() == 0) {
			throw new AppFatalException("The effective 'in' name cannot be empty or null");
		} else if (! Name.isValidPropertyName(effectiveIn)) {
			throw new AppFatalException("The effective 'in' name cannot contain whitespace or special characters from this list: "
					+ Name.ILLEGAL_PROPERTY_NAME_CHARS);
		}

		this.effectiveIn = effectiveIn;
		this.effectiveOut = actual;
	}
	
	/**
	 * The name as transformed by the NamingStrategy.
	 * 
	 * @return 
	 */
	public String getEffectiveInName() {
		return effectiveIn;
	}
	
	public String getEffectiveOutName() {
		return effectiveOut;
	}

}
