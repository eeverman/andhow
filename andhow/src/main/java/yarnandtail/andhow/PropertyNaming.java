package yarnandtail.andhow;

import java.util.*;

/**
 * Names by which a Property is recognized.
 * 
 * NamingStrategy returns an instance of this to create the names for
 * a property.  Names here are the canonical name and any aliases.
 * 
 * @author ericeverman
 */
public class PropertyNaming {

	private final Name canonicalName;
	private final List<Name> inAliases;

	public PropertyNaming(Name canonicalName, List<Name> inAliases) {
		this.canonicalName = canonicalName;
		
		if (inAliases != null) {
			this.inAliases = inAliases;
		} else {
			this.inAliases = Collections.emptyList();
		}
	}

	/**
	 * The canonical name of the property.
	 * @return 
	 */
	public Name getCanonicalName() {
		return canonicalName;
	}

	/**
	 * All In aliases for the property.
	 * 
	 * The naming strategy may be directed to add or remove aliases from the ones
	 * requested for a property to resolve conflicts, but nominally this is the
	 * list of in-type aliases requested by the Property.
	 * @return 
	 */
	public List<Name> getInAliases() {
		return inAliases;
	}
	
	/**
	 * A list of the canonical name and any aliases merged together.
	 * 
	 * @return 
	 */
	public List<Name> getAllInNames() {
		
		ArrayList<Name> ns = new ArrayList();
		ns.add(canonicalName);
		ns.addAll(inAliases);
		return Collections.unmodifiableList(ns);
	}
	
	
	/**
	 * A single name with its canonical version and effective version.
	 * 
	 * A NamingStrategy may clean up or change the case of a name from its
	 * actual name to its effective name.
	 * 
	 */
	public static class Name {
		private String actual;
		private String effective;

		public Name(String actual, String effective) {
			this.actual = actual;
			this.effective = effective;
		}

		/**
		 * Actual canonical version of the name.
		 * @return 
		 */
		public String getActual() {
			return actual;
		}

		/**
		 * NamingStrategy cleaned up version of the name.
		 * This is the name that should be used to match on when reading properties
		 * names from files.
		 * @return 
		 */
		public String getEffective() {
			return effective;
		}
		
		
	}

}
