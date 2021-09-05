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

	public PropertyExportImpl(Property<?> property, Class<?> containingClass,
			EXPORT_CANONICAL_NAME canonicalNameOpt, EXPORT_OUT_ALIASES outAliasOpt) {

		this.property = property;
		this.containingClass = containingClass;
		this.canNameOpt = canonicalNameOpt;
		this.aliasOpt = outAliasOpt;
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
		return buildExportNames();
	}

	@Override
	public Object getValue() {
		return property.getValue();
	}

	@Override
	public String getValueAsString() {
		return property.getValueAsString();
	}

	@Override
	public PropertyExport mapNames(List<String> exportNames) {
		return mapNames(exportNames, this);
	}

	@Override
	public PropertyExport mapValue(Object value) {
		return mapValue(value, this);
	}

	@Override
	public PropertyExport mapValueAsString(String value) {
		return mapValueAsString(value, this);
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


	protected static PropertyExport mapNames(List<String> exportNames, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public List<String> getExportNames() {
				return exportNames;
			}
		};
	}

	protected static PropertyExport mapValue(Object value, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public Object getValue() {
				return value;
			}
		};
	}

	protected static PropertyExport mapValueAsString(String value, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public String getValueAsString() {
				return value;
			}
		};
	}

	protected static class PropertyExportWrap implements PropertyExport {

		private final PropertyExport inner;

		public PropertyExportWrap(PropertyExport inner) { this.inner = inner; }

		@Override public Property<?> getProperty() { return inner.getProperty(); }

		@Override public Class<?> getContainingClass() { return inner.getContainingClass(); }

		@Override
		public EXPORT_CANONICAL_NAME getCanonicalNameOption() {
			return inner.getCanonicalNameOption();
		}

		@Override
		public EXPORT_OUT_ALIASES getOutAliasOption() {
			return inner.getOutAliasOption();
		}

		@Override public List<String> getExportNames() { return inner.getExportNames(); }

		@Override	public Object getValue() { return inner.getValue(); }

		@Override public String getValueAsString() { return inner.getValueAsString(); }

		@Override
		public PropertyExport mapNames(List<String> exportNames) {
			return PropertyExportImpl.mapNames(exportNames, this);
		}

		@Override
		public PropertyExport mapValue(Object value) {
			return PropertyExportImpl.mapValue(value, this);
		}

		@Override
		public PropertyExport mapValueAsString(String value) {
			return PropertyExportImpl.mapValueAsString(value, this);
		}
	}

}
