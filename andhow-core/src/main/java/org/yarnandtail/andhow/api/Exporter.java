package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

/**
 * An Exporter exports property names and values for classes that are annotated with
 * {@link org.yarnandtail.andhow.GroupExport}.
 * <p>
 * An example is the {@link org.yarnandtail.andhow.export.SysPropExporter}, which exports
 * properties to System.Properties.
 * <p>
 * Exporters are passed the list Properties contained directly in the annotated class,
 * not Properties contained in nested inner classes.
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
	 * At one time, this was an 'export all properties' feature, however, it was never fully
	 * implemented and would have broken the application security model.
	 *
	 * Subclasses can safely not implement it and rely on the no-op default implementation
	 * here.  If a subclass does implement it, the AndHow system will never call it.
	 * This method will be removed from this interface in the next major release.
	 *
	 * @deprecated This method violates the security and was never actually called by the
	 * system.  Removing with no replacement.
	 * @param definition
	 * @param values
	 */
	@Deprecated
	default void export(PropertyConfigurationInternal definition, ValidatedValues values) {
		return;
	}

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
	void export(GroupProxy group, PropertyConfigurationInternal definition, ValidatedValues values);

	public static enum EXPORT_CANONICAL_NAME {
		ALWAYS, ONLY_IF_NO_OUT_ALIAS, NEVER;
	}

	public static enum EXPORT_OUT_ALIASES {
		ALWAYS, NEVER;
	}
}
