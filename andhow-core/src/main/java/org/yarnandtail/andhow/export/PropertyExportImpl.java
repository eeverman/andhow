package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;

import java.util.ArrayList;
import java.util.List;

public class PropertyExportImpl implements PropertyExport {

	private final Property<?> property;
	private final Class<?> containingClass;
	private final EXPORT_CANONICAL_NAME canNameOpt;
	private final EXPORT_OUT_ALIASES aliasOpt;
	private final List<String> exportNames;

	public PropertyExportImpl(Property<?> property, Class<?> containingClass,
			EXPORT_CANONICAL_NAME canonicalNameOpt, EXPORT_OUT_ALIASES outAliasOpt) {

		this.property = property;
		this.containingClass = containingClass;
		this.canNameOpt = canonicalNameOpt;
		this.aliasOpt = outAliasOpt;
		exportNames = null;
	}

	private PropertyExportImpl(Property<?> property, Class<?> containingClass,
			EXPORT_CANONICAL_NAME canonicalNameOpt, EXPORT_OUT_ALIASES outAliasOpt, List<String> exportNames) {

		this.property = property;
		this.containingClass = containingClass;
		this.canNameOpt = canonicalNameOpt;
		this.aliasOpt = outAliasOpt;
		this.exportNames = exportNames;
	}

	@Override
	public Property<?> getProperty() {
		return property;
	}

	@Override
	public Class<?> getContainingClass() {
		return containingClass;
	}

	@Override
	public EXPORT_CANONICAL_NAME getCanonicalNameOption() {
		return canNameOpt;
	}

	@Override
	public EXPORT_OUT_ALIASES getOutAliasOption() {
		return aliasOpt;
	}

	@Override
	public List<String> getExportNames() {

		if (exportNames == null) {
			return buildExportNames();
		} else {
			return exportNames;
		}

	}

	@Override
	public PropertyExport clone(List<String> exportNames) {
		return new PropertyExportImpl(property, containingClass, canNameOpt, aliasOpt, exportNames);
	}

	/**
	 * Build the export names based on the EXPORT_CANONICAL_NAME & EXPORT_OUT_ALIASES options.
	 * @return
	 */
	protected List<String> buildExportNames() {
		List<String> names = new ArrayList(1);

		List<String> aliasNames = property.getOutAliases();

		if (aliasOpt.equals(EXPORT_OUT_ALIASES.ALWAYS)) {
			names.addAll(aliasNames);
		}

		if (
				canNameOpt.equals(EXPORT_CANONICAL_NAME.ALWAYS) ||
						(canNameOpt.equals(EXPORT_CANONICAL_NAME.ONLY_IF_NO_OUT_ALIAS) && aliasNames.isEmpty())) {
			names.add(property.getCanonicalName());
		}

		return names;
	}


}
