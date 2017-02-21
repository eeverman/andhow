package org.yarnandtail.andhow;

import org.yarnandtail.andhow.util.TextUtil;

/**
 *  A name for a Property, either incoming (when read by a property
 * Loader) or outgoing (when properties are exported to some destination).
 * 
 * @author ericeverman
 */
public class Name {

	private final boolean in;
	private final boolean out;
	private final String actual;

	public Name(String name, boolean in, boolean out) {
		if (name == null || name.length() == 0) {
			throw new AppFatalException("The name cannot be empty or null");
		} else if (! TextUtil.isValidPropertyAlias(name)) {
			throw new AppFatalException("Names cannot contain whitespace or special characters from this list: "
					+ TextUtil.ILLEGAL_PROPERTY_ALIAS_CHARACTERS);
		}
		this.in = in;
		this.out = out;
		this.actual = name;
	}

	/**
	 * If true, this name is recognized when reading a property from a configuration
	 * source.
	 *
	 * When attempting to match an incoming property name, the effective name
	 * should be used and the incoming name should be transformed using the
	 * NamingStructure.transformIncomingClasspathName().
	 *
	 * @return
	 */
	public boolean isIn() {
		return in;
	}

	/**
	 * If true, this name is used when exporting properties to some other system.
	 *
	 * When exporting, the actual name is typically used, however, its possible
	 * that an exporter may have a reason to use the effective name.
	 *
	 * @return
	 */
	public boolean isOut() {
		return out;
	}

	/**
	 * The actual, originally name.
	 *
	 * @return
	 */
	public String getActualName() {
		return actual;
	}

}
