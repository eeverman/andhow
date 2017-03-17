package org.yarnandtail.andhow.api;

/**
 * A name for a Property, either incoming (when read by a property
 * Loader) or outgoing (when properties are exported to some destination).
 * 
 * @author ericeverman
 */
public class Name {

	/**
	 * These characters are not allowed in Property names because they may collide
	 * with characters allowed in various formats, in particular, uri style JNDI
	 * names or property files conventions.
	 *
	 * URLs require encoding for these characters: [whitespace];/?:@=&"<>#%{}|\^~[]`
	 */
	public static String ILLEGAL_PROPERTY_NAME_CHARS = " \t\n\r;/?:@=&\"<>#%{}|\\^~[]`";

	private final boolean in;
	private final boolean out;
	private final String actual;

	public Name(String name, boolean in, boolean out) {
		if (name == null || name.length() == 0) {
			throw new AppFatalException("The name cannot be empty or null");
		} else if (! isValidPropertyName(name)) {
			throw new AppFatalException("Names cannot contain whitespace or special characters from this list: "
					+ ILLEGAL_PROPERTY_NAME_CHARS);
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
	
	/**
	 * Returns true if the name contains no special characters, as defined in
 ILLEGAL_PROPERTY_NAME_CHARS.
	 *
	 * The alias is also not allowed to be null, empty, or start/end with a
	 * dot (.) character.
	 *
	 * @param name
	 * @return
	 */
	public static boolean isValidPropertyName(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		if (name.startsWith(".") || name.endsWith(".")) {
			return false;
		}
		for (char c : ILLEGAL_PROPERTY_NAME_CHARS.toCharArray()) {
			if (name.indexOf(c) > -1) {
				return false;
			}
		}
		return true;
	}

}
