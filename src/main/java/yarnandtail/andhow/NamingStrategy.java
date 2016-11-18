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
	 */
	Naming buildNames(ConfigPoint configPoint, Class<? extends ConfigPointGroup> parentGroup, String fieldName);
	
	public static class Naming {
		private String canonicalName;
		private String primaryName;
		private List<String> aliases;

		public Naming(String canonicalName, String primaryName, List<String> aliases) {
			this.canonicalName = canonicalName;
			this.primaryName = primaryName;
			this.aliases = aliases;
		}
		
		public String getCanonicalName() {
			return canonicalName;
		}

		public String getCommonName() {
			return primaryName;
		}

		public void setPrimaryName(String primaryName) {
			this.primaryName = primaryName;
		}

		public List<String> getAliases() {
			return aliases;
		}

		public void setAliases(List<String> aliases) {
			this.aliases = aliases;
		}
		
		
	}
}
