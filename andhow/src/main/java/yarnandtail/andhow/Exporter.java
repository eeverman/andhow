package yarnandtail.andhow;

/**
 * The Exporter provides support for legacy applications that expect
 * to find key/value pairs in a specific source, such as system properties.
 * 
 * This allows applications to use AndHow for configuration and still
 * maintain compatibility with legacy modules/libraries.  New code can begin to
 * use the direct Property access that AndHow provides.
 * 
 * Exporting is considered 'safe', so overlapping export configuration, such
 * as application level and PropertyGroup level are read permissively.
 * 
 * 
 * @author ericeverman
 */
public interface Exporter {
	
	/**
	 * Specify if canonical names should be exported.
	 * @param option 
	 */
	void setCanonNameOption(INCLUDE_CANONICAL_NAMES option);

	/**
	 * Specify if export alias names should be exported.
	 * @param option 
	 */
	void setExportAliasOption(INCLUDE_OUT_ALIAS_NAMES option);
	
	
	/**
	 * Exports all properties.
	 * 
	 * Based on its configuration, an exporter can decide which
	 * properties should be exported and what name or aliases should be used
	 * when exporting.
	 * 
	 * @param definition
	 * @param values
	 */
	void export(ConstructionDefinition definition, ValueMap values);
	
	/**
	 * Exports a PropertyGroup.
	 * 
	 * Based on its configuration, an exporter can decide which
	 * properties should be exported and what name or aliases should be used
	 * when exporting.
	 * 
	 * @param group
	 * @param definition
	 * @param values
	 */
	void export(Class<? extends PropertyGroup> group, ConstructionDefinition definition, ValueMap values);
	
	public static enum INCLUDE_CANONICAL_NAMES {
		ALL, ONLY_IF_NO_OUT_ALIAS, NONE;
	}
	
	public static enum INCLUDE_OUT_ALIAS_NAMES {
		ALL, NONE;
	}
}
