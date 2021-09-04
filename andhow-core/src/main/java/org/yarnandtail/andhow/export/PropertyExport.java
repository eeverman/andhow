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
	 * The complete set of export names, which may be zero or many.
	 * <p>
	 * This method builds and returns the default export name list based on the
	 * {@link #getCanonicalNameOption()} & {@link #getOutAliasOption()} options, or, if
	 * {@link #clone(List)} was used to construct this instance, the list of names specified.
	 *
	 * @return A list of names which may be empty but not null.
	 */
	List<String> getExportNames();

	/**
	 * Clone this instance, specifying a predetermined list of export names.
	 * <p>
	 * Calling {@link #getExportNames()} on the new instance returns the specified list exportNames,
	 * overriding the default name logic.  Set exportNames to an empty list to have zero names.
	 * If exportNames is null, the clone is the same as the original and default names are used.
	 * <p>
	 * 'clone' allows the names to be modified in a stream, e.g.:
	 * <pre>{@code
	 * AndHow.instance().export().stream().map(p ->
	 *   p.clone( p.getExportNames().stream().map(n -> n.toUpperCase()).collect(toList()))
	 * ).collect(new StringMapExporter());
	 * }</pre>

	 * @param exportNames
	 * @return
	 */
	PropertyExport clone(List<String> exportNames);


}
