package yarnandtail.andhow;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface NamingStrategy {
	
	/**
	 * Build a list of names for the Property
	 * @param configPoint Names for this point
	 * @param parentGroup ConfigPoints must be contained in a PropertyGroup to be exposed
	 * @param fieldName The name of the static field in the parentGroup that refers to the configPoint
	 * @return 
	 * @deprecated - To hard to remove the existing testing using fieldName right now, so keeping this version.
	 */
	Naming buildNames(Property configPoint, Class<? extends PropertyGroup> parentGroup, String fieldName);
	
	/**
	 * Builds a list of names for this Property, using the canoicalName
	 * @param configPoint
	 * @param parentGroup
	 * @param canonicalName
	 * @return 
	 */
	Naming buildNamesFromCanonical(Property configPoint, Class<? extends PropertyGroup> parentGroup, String canonicalName);
	
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
