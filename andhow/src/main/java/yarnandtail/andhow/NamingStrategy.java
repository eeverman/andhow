package yarnandtail.andhow;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface NamingStrategy {
	
	/**
	 * Build a list of names for the Property
	 * @param prop The Property to be named
	 * @param containingGroup Properties must be contained in a PropertyGroup to be exposed
	 * @param fieldName The name of the static field in the containingGroup that refers to the prop
	 * @return 
	 * @deprecated - To hard to remove the existing testing using fieldName right now, so keeping this version.
	 */
	Naming buildNames(Property prop, Class<? extends PropertyGroup> containingGroup, String fieldName);
	
	/**
	 * Builds a list of names for this Property, using the canoicalName
	 * @param prop
	 * @param containingGroup
	 * @param canonicalName
	 * @return 
	 */
	Naming buildNamesFromCanonical(Property prop, Class<? extends PropertyGroup> containingGroup, String canonicalName);
	
	public static class Naming {
		private String canonicalName;
		private List<String> aliases;

		public Naming(String canonicalName, List<String> aliases) {
			this.canonicalName = canonicalName;
			this.aliases = aliases;
		}
		
		public String getCanonicalName() {
			return canonicalName;
		}

		public List<String> getAliases() {
			return aliases;
		}

		public void setAliases(List<String> aliases) {
			this.aliases = aliases;
		}
		
		
	}
}
