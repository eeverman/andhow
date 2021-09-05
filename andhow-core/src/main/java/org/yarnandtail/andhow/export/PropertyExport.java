package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;

import java.util.List;

/**
 * Info for a single Property being exported.
 * <p>
 * Exports are typically in the form of Key-Value pairs where the key is the Property canonical name
 * and/or alias and the value is the real value of the Property.  A single Property may have zero
 * or more names associated with it for export resulting in zero or more entries in the final
 * export.
 */
public interface PropertyExport {

	/**
	 * The Property to export.
	 *
	 * @return A Property instance.  Not null.
	 */
	Property<?> getProperty();

	/**
	 * The class directly containing the Property.
	 *
	 * @return The containing class.  Not null.
	 */
	Class<?> getContainingClass();

	/**
	 * Enum that indicates when/if the canonical name of the property should be included as a name
	 * in the export.
	 * <p>
	 * In normal usage, this option value is read from an export annotation on a class to be exported
	 * or its containing class.
	 *
	 * @return An EXPORT_CANONICAL_NAME value.  Not null.
	 */
	EXPORT_CANONICAL_NAME getCanonicalNameOption();

	/**
	 * Enum that indicates when/if outbound alias names of the property should be included as a name
	 * in the export.
	 * <p>
	 * In normal usage, this option value is read from an export annotation on a class to be exported
	 * or its containing class.
	 *
	 * @return An EXPORT_OUT_ALIASES value.  Not null.
	 */
	EXPORT_OUT_ALIASES getOutAliasOption();

	/**
	 * The complete set of export names, which may be null, zero or many.
	 * <p>
	 * This method builds and returns the default export name list based on the
	 * {@link #getCanonicalNameOption()} & {@link #getOutAliasOption()} options, or, if
	 * {@link #mapNames(List)} was used to construct this instance, the list of names specified.
	 *
	 * @return A list of names which may be empty or null.
	 */
	List<String> getExportNames();

	/**
	 * Fetch the value of the Property.
	 * <p>
	 * Nominally this is the same as {@code getProperty().getValue()}, however, subclasses can
	 * override to rewrite the value.  Take care to override the method that will be used by the
	 * collector: either {@link #getValue()} or {@link #getValueAsString()}.
	 *
	 * @return The value return from the Properties getValue method.  May be null.
	 */
	Object getValue();

	/**
	 * Fetch the value as a String from the Property.
	 * <p>
	 * Nominally this is the same as {@code getProperty().getValueAsString()}, however, subclasses can
	 * override to rewrite the value.  Take care to override the method that will be used by the
	 * collector: either {@link #getValue()} or {@link #getValueAsString()}.
	 * <p>
	 * Note:  The nominal implementation of this method should call
	 * {@code getProperty().getValueAsString()} and not delegate to {@link #getValue()}, since the
	 * type returned by getValue may be overridden and not match the Property type.
	 *
	 * @return The value return from the Properties getValueAsString method.  May be null.
	 */
	String getValueAsString();

	/**
	 * Map this instance to a new instance with new export names.
	 * <p>
	 * Calling {@link #getExportNames()} on the new instance returns the new exportNames,
	 * overriding the default name logic.  Mapping to an empty or null name list will result in no
	 * export for this Property.
	 * <p>
	 * mapNewNames allows export names to be modified in-stream, e.g. to upper case:
	 * <pre>{@code
	 * Map<String, String> export = AndHow.instance().export().stream().map(p ->
	 *   p.mapNames( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()))
	 * ).collect(ExportCollector.stringMap());
	 * }</pre>

	 * @param exportNames The new list of export names to be used, rather than the default
	 *                    constructed ones.
	 * @return A new instance which is basically a clone except it has new names.
	 */
	PropertyExport mapNames(List<String> exportNames);

	PropertyExport mapValue(Object value);

	public PropertyExport mapValueAsString(String value);

}
