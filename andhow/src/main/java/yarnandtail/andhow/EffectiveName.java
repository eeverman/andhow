package yarnandtail.andhow;

import java.util.Arrays;
import yarnandtail.andhow.util.TextUtil;

/**
 * A name for a Property that has been processed by a NamingStrategy to create
 * an effective name, which may be different than the original name.
 * 
 * The NamingStrategy is also responsible to creating an effective name.  An
 * effective name is the name that is matched on when trying to match up incoming
 * properties names found by a Loader.  This is distinct from the actual name to
 * accommodate case insensitive naming.  For instance, if a property is aliased
 * as 'bob', the effective name may be 'BOB' with all incoming names also converted
 * to uppercase.
 * 
 * @author ericeverman
 */
public class EffectiveName extends AName {

	private final String effective;
	
	
	public EffectiveName(String actual, String effective, boolean in, boolean out) {
		
		super(actual, in, out);
		
		if (effective == null || effective.length() == 0) {
			throw new AppFatalException("The effectivde name cannot be empty or null");
		} else if (! TextUtil.isValidPropertyAlias(effective)) {
			throw new AppFatalException("The effective name cannot contain whitespace or special characters from this list: "
					+ TextUtil.ILLEGAL_PROPERTY_ALIAS_CHARACTERS);
		}

		this.effective = effective;
	}
	

	
	/**
	 * The name as transformed by the NamingStrategy.
	 * 
	 * @return 
	 */
	public String getEffectiveName() {
		return effective;
	}

}
