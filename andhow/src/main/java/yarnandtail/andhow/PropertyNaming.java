package yarnandtail.andhow;

import java.util.List;

/**
 * Names by which a Property is recognized.
 * 
 * NamingStrategy returns an instance of this to create the names for
 * a property.  Names here are the canonical name and any aliases.
 * 
 * @author ericeverman
 */
public class PropertyNaming {

	private final String canonicalName;
	private final List<String> inAliases;
	private final List<String> effectiveNames;

	public PropertyNaming(String canonicalName, List<String> inAliases, List<String> effectiveNames) {
		this.canonicalName = canonicalName;
		this.inAliases = inAliases;
		this.effectiveNames = effectiveNames;
	}

	/**
	 * The property case canonical name of the property.
	 * @return 
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * Property case alias for a property.
	 * The naming strategy may be directed to add or remove aliases from the ones
	 * requested for a property to resolve conflicts, but nominally this is the
	 * list of in-type aliases requested by the Property.
	 * @return 
	 */
	public List<String> getInAliases() {
		return inAliases;
	}
	
	/**
	 * The complete list of names this property should be recognized by.
	 * 
	 * This is basically the canonical name plus aliases, however, it will
	 * generally be in upper case to allow case insensitive comparisons.
	 * 
	 * @return 
	 */
	public List<String> getEffectiveInNames() {
		return effectiveNames;
	}



}
