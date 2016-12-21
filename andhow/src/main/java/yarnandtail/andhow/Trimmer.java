package yarnandtail.andhow;

/**
 * Trims raw Strings brought in by Loaders to remove whitespace.
 * 
 * @author ericeverman
 */
public interface Trimmer {
	
	/**
	 * Trims the raw string to remove whitespace
	 * 
	 * @param raw Null and empty strings will generally return null unless noted.
	 * @return A string with whitespace removed
	 */
	String trim(String raw);
}
