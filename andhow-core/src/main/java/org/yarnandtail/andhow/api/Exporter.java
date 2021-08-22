package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;

/**
 * The Exporter provides support for legacy applications that expect
 * to find key/value pairs in a specific source, such as system properties.
 * 
 * This allows applications to use AndHow for configuration and still
 maintain compatibility with legacy modules/libraries.  New code can begin to
 use the direct Property access that AndHow provides.
 
 Exporting is considered 'safe', so overlapping export configuration, such
 as application level and Group level are read permissively.
 * 
 * 
 * @author ericeverman
 */
public interface Exporter {
	
	/**
	 * Specify if canonical names should be exported.
	 * @param option 
	 */
	void setExportByCanonicalName(EXPORT_CANONICAL_NAME option);

	/**
	 * Specify if out alias names should be exported.
	 * @param option 
	 */
	void setExportByOutAliases(EXPORT_OUT_ALIASES option);

	/**
	 * Exports a Group.
	 * 
	 * Based on its configuration, an exporter can decide which
	 * properties should be exported and what name or aliases should be used
	 * when exporting.
	 * 
	 * @param group
	 * @param definition
	 * @param values
	 */
	void export(GroupProxy group, StaticPropertyConfigurationInternal definition, ValidatedValues values);
	
	public static enum EXPORT_CANONICAL_NAME {
		ALWAYS, ONLY_IF_NO_OUT_ALIAS, NEVER;
	}
	
	public static enum EXPORT_OUT_ALIASES {
		ALWAYS, NEVER, FIRST, LAST;
	}

	public static class Options {
		public final EXPORT_CANONICAL_NAME canonicalNameOption;
		public final EXPORT_OUT_ALIASES outAliasOption;

		public Options(EXPORT_CANONICAL_NAME canonicalNameOption, EXPORT_OUT_ALIASES outAliasOption) {
			this.canonicalNameOption = canonicalNameOption;
			this.outAliasOption = outAliasOption;
		}
	}
}
