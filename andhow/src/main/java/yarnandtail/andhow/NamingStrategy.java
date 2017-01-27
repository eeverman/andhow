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
	 * @return Null if the passed Property is not part of the PropertyGroup.
	 * @throws An exception if there is a security exception trying to read fields
	 *	inside of the containingGroup via reflection.
	 */
	PropertyNaming buildNames(
			Property prop, Class<? extends PropertyGroup> containingGroup) throws Exception;
	
	/**
	 * Transforms a property name found in a property source (like a properties file,
	 * cmd line argument, env variable, etc.) into the form needed to match the
	 * expected property names.
	 * 
	 * For the nominal case, this means converting it to upper case for case
	 * insensitive comparison.
	 * 
	 * @param name
	 * @return 
	 */
	String toEffectiveName(String name);
	
	/**
	 * Returns a description of how names are matched for use in samples and documentation.
	 * 
	 * For case insensitive matching, it might return something like:
	 * Names are matched in a case insensitive way, so "Bob" would match "bOB".
	 * 
	 * @return A short string to be used in generated samples and documentation.
	 */
	String getNameMatchingDescription();
	
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
	default String getUriName(String classpathName) {
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
	public default boolean isUriNameDistict(String classpathName) {
		
		if (classpathName == null) return false;
		
		return ! classpathName.equals(getUriName(classpathName));
	}
}
