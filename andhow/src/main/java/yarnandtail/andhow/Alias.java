package yarnandtail.andhow;

import java.util.Arrays;
import yarnandtail.andhow.util.TextUtil;

/**
 * An alternate name for a Property, either incoming (when read by a property
 * Loader) or outgoing (when properties are exported to some destination).
 *
 * @author ericeverman
 */
public class Alias {

	boolean in;
	boolean out;
	String name;
	
	public Alias(String name, boolean in, boolean out) {
		
		if (name == null || name.length() == 0) {
			throw new AppFatalException("The alias cannot be empty or null");
		} else if (! TextUtil.isValidPropertyAlias(name)) {
			throw new AppFatalException("Aliases cannot contain whitespace or special characters from this list: "
					+ TextUtil.ILLEGAL_PROPERTY_ALIAS_CHARACTERS);
		}
		this.in = in;
		this.out = out;
		this.name = name;
	}
	

	public boolean isIn() {
		return in;
	}

	public boolean isOut() {
		return out;
	}

	public String getName() {
		return name;
	}

}
