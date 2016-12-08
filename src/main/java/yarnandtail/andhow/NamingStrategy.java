package yarnandtail.andhow;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface NamingStrategy {
	
	/**
	 * Build a list of names for the ConfigPoint
	 * @param configPoint Names for this point
	 * @param parentGroup ConfigPoints must be contained in a ConfigPointGroup to be exposed
	 * @param fieldName The name of the static field in the parentGroup that refers to the configPoint
	 * @return 
	 * @deprecated - To hard to remove the existing testing using fieldName right now, so keeping this version.
	 */
	Naming buildNames(ConfigPoint configPoint, Class<? extends ConfigPointGroup> parentGroup, String fieldName);
	
	/**
	 * Builds a list of names for this ConfigPoint, using the canoicalName
	 * @param configPoint
	 * @param parentGroup
	 * @param canonicalName
	 * @return 
	 */
	Naming buildNamesFromCanonical(ConfigPoint configPoint, Class<? extends ConfigPointGroup> parentGroup, String canonicalName);
	
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
