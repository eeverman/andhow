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
		private List<String> inAliases;

		public Naming(String canonicalName, List<String> inAliases) {
			this.canonicalName = canonicalName;
			this.inAliases = inAliases;
		}
		
		public String getCanonicalName() {
			return canonicalName;
		}

		public List<String> getInAliases() {
			return inAliases;
		}

		public void setInAliases(List<String> inAliases) {
			this.inAliases = inAliases;
		}
	}
	
	/**
	 * Converts a standard classpath style property name to a URI based one.
	 * 
	 * Standard classpath style names look like this:  <code>com.acme.bigapp.PROPERTY_NAME</code>.
	 * A URI version of that would be this: <code>com/acme/bigapp/PROPERTY_NAME</code>.
	 * 
	 * No trimming or cleanup is done of the passed classpathNames - they are
	 * assumed to be in final form.
	 * Empty and null classpathNames shouldn't happen, but they just return
	 * themselves, respectively.
	 * 
	 * @param classpathName
	 * @return null if null is passed.
	 */
	public static String getUriName(String classpathName) {
		if (classpathName == null) return null;
		
		return classpathName.replaceAll("\\.", "/");
	}
	
	/**
	 * Returns true if the Uri version of the passed name would be a disctict name
	 * from the passed name.
	 * 
	 * If this returns false, the URI name and the classpathName would be the same.
	 * 
	 * If passed null, this returns false.
	 * 
	 * @param classpathName
	 * @return 
	 */
	public static boolean isUriNameDistict(String classpathName) {
		
		if (classpathName == null) return false;
		
		return ! classpathName.equals(getUriName(classpathName));
	}
}
