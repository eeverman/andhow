package org.yarnandtail.andhow.internal.export;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.PropertyExport;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation w/ wrapper inner subclass to simplify wrapped instances for mapXXX methods.
 */
public class PropertyExportImpl implements PropertyExport {

	private final Property<?> property;
	private final Class<?> containingClass;
	private final EXPORT_CANONICAL_NAME canNameOpt;
	private final EXPORT_OUT_ALIASES aliasOpt;

	/**
	 * Full state constructor.
	 *
	 * @param property The Property being exported.
	 * @param containingClass The class immediately containing the Property.
	 * @param canonicalNameOpt Option of if/when to include the canonical name in exports
	 * @param outAliasOpt Option of if to include out alias names in the exports.
	 */
	public PropertyExportImpl(Property<?> property, Class<?> containingClass,
			EXPORT_CANONICAL_NAME canonicalNameOpt, EXPORT_OUT_ALIASES outAliasOpt) {

		this.property = property;
		this.containingClass = containingClass;
		this.canNameOpt = canonicalNameOpt;
		this.aliasOpt = outAliasOpt;
	}

	@Override public Property<?> getProperty() { return property; }

	@Override public Class<?> getContainingClass() { return containingClass; }

	@Override public EXPORT_CANONICAL_NAME getCanonicalNameOption() { return canNameOpt; }

	@Override public EXPORT_OUT_ALIASES getOutAliasOption() { return aliasOpt; }

	@Override public List<String> getExportNames() { return buildExportNames(); }

	@Override public Object getValue() { return property.getValue(); }

	@Override public String getValueAsString() { return property.getValueAsString(); }

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
	 * Build the export names based on annotation options.
	 * <p>
	 * The {@link ManualExportAllowed} annotation on the class containing the Property or
	 * a class containing that class determines the export name options.
	 * <p>
	 * See {@link #getCanonicalNameOption()}, {@link #getOutAliasOption()}, and
	 * {@link ManualExportAllowed} for details.
	 * <p>
	 * @return A list of names as determined by the annotation options.
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

	/**
	 * Construct a new PropertyExport, overriding the getExportNames() method.
	 * <p>
	 * @param exportNames The new list of export names to return from the getExportNames() method.
	 * @param inner The inner instance to delegate to for other methods.
	 * @return A new instance wrapping the inner and returning the new export names.
	 */
	protected static PropertyExport mapNames(List<String> exportNames, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public List<String> getExportNames() {
				return exportNames;
			}
		};
	}

	/**
	 * Construct a new PropertyExport, overriding the getValue() method.
	 * <p>
	 * @param value The new value to return from the getValue() method.
	 * @param inner The inner instance to delegate to for other methods.
	 * @return A new instance wrapping the inner and returning the new value.
	 */
	protected static PropertyExport mapValue(Object value, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public Object getValue() {
				return value;
			}
		};
	}

	/**
	 * Construct a new PropertyExport, overriding the getValueAsString() method.
	 * <p>
	 * @param value The new value to return from the getValueAsString() method.
	 * @param inner The inner instance to delegate to for other methods.
	 * @return A new instance wrapping the inner and returning the new value.
	 */
	protected static PropertyExport mapValueAsString(String value, PropertyExport inner) {
		return new PropertyExportWrap(inner) {
			@Override
			public String getValueAsString() {
				return value;
			}
		};
	}

	/**
	 * A wrapper class that make it easy to wrap, delegate and override methods.
	 */
	protected static class PropertyExportWrap implements PropertyExport {

		private final PropertyExport inner;

		/**
		 * Construct a wrapper instance that wraps 'inner' and delegates to it for all methods.
		 * @param inner The instance to delegate to for methods.
		 */
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

		//
		// Note:  The mapXXX methods cannot delegate or the result is a wrapped version of the
		// inner class, not this one.


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
