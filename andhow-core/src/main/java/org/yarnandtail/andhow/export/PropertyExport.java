package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.Exporter.*;
import org.yarnandtail.andhow.api.Property;

import java.util.List;

/**
 * A set of data for a single Property being exported.
 * <p>
 * Exports are typically in the form of Key-Value pairs where the key is the {@link Property}
 * canonical name and/or 'out' aliases and the value is the real value of the Property.
 * <p>
 * {@link AndHow#export()} returns a Stream of {@link PropertyExport}'s, one PropertyExport per
 * Property. The {@link ExportCollector} has several collectors that transform and collect the
 * stream into Maps or {@link java.util.Properties} of key-value pairs.
 * <p>
 * See {@link AndHow#export()} for export details and examples.
 */
public interface PropertyExport {

	/**
	 * The Property being exported.
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
	 * Option read from the containing {@link ManualExportAllowed} annotation to determine if the
	 * canonical name of the Property should be included.
	 * <p>
	 * If included, the canonical name would be included in the list of names returned from
	 * {@link #getExportNames()} and then ultimately as one of the names in the final key-value
	 * pairs.
	 * <p>
	 * @return An EXPORT_CANONICAL_NAME value.  Not null.
	 */
	EXPORT_CANONICAL_NAME getCanonicalNameOption();

	/**
	 * Option read from the containing {@link ManualExportAllowed} annotation to determine if 'out'
	 * aliases of the Property should be included.
	 * <p>
	 * 'Out' aliases are alternate names for a {@link Property} for the purpose of export, so generally
	 * they are intended to be included.
	 * <p>
	 * @return An EXPORT_OUT_ALIASES value.  Not null.
	 */
	EXPORT_OUT_ALIASES getOutAliasOption();

	/**
	 * The complete list of export names for this property, which may be null, empty or many.
	 * <p>
	 * This method builds and returns the default export name list based on the
	 * {@link #getCanonicalNameOption()} & {@link #getOutAliasOption()} options, or, if
	 * {@link #mapNames(List)} was used to construct this instance, the list of names specified.
	 * <p>
	 * See {@link AndHow#export(Class[])} for an example that remaps export names.
	 * <p>
	 * @return A list of names which may be empty or null.
	 */
	List<String> getExportNames();

	/**
	 * Fetch the Object value of the Property, e.g., an Integer Property will return an Integer.
	 * <p>
	 * Nominally this is the same as {@code getProperty().getValue()}, however,
	 * {@link #mapValue(Object)} can be used to rewrite the value.
	 * Take care to map the method that will be used by the collector you have chosen:
	 * <ul>
	 * <li>'String' collectors call {@link #getValueAsString()}: use {@link #mapValueAsString(String)}</li>
	 * <li>'Object' collectors call {@link #getValue()}: use {@link #mapValue(Object)}</li>
	 * </ul><p>
	 * @return The value return from the Property getValue() method or a custom value.  May be null.
	 */
	Object getValue();

	/**
	 * Fetch the value as a String from the Property.
	 * <p>
	 * Nominally this is the same as {@code getProperty().getValueAsString()}, however,
	 * {@link #mapValueAsString(String)} can be used to rewrite the value.
	 * Take care to map the method that will be used by the collector you have chosen:
	 * <ul>
	 * <li>'String' collectors call {@link #getValueAsString()}: use {@link #mapValueAsString(String)}</li>
	 * <li>'Object' collectors call {@link #getValue()}: use {@link #mapValue(Object)}</li>
	 * </ul><p>
	 * <em>Implementation note:  The nominal implementation of this method should call
	 * {@code getProperty().getValueAsString()} and not delegate to {@link #getValue()}, since the
	 * type returned by getValue may be overridden and not match the Property type.</em>
	 * <p>
	 * @return The value returned from the Properties getValueAsString method or a custom value.  May be null.
	 */
	String getValueAsString();

	/**
	 * Intended for using with Java {@code stream()}s, this method maps this instance to a new one
	 * with new export names.
	 * <p>
	 * Calling {@link #getExportNames()} on the new instance returns the new exportNames.
	 * Specifying an empty or null list of names will remove this Property from export.
	 * <p>
	 * See {@link AndHow#export(Class[])} for an example that remaps export names.
	 * <p>
	 * @param exportNames A new list of export names to be used, rather than the default ones.
	 * @return A new instance identical to the original, except it has new names.
	 */
	PropertyExport mapNames(List<String> exportNames);

	/**
	 * Intended for using with Java {@code stream()}s, this method maps this instance to a new one
	 * with a new Object value for the Property, i.e., the 'value' part of a key-value pair.
	 * <p>
	 * Calling {@link #getValue()} on the new instance returns the new value, which may be null.
	 * <p>
	 * This example uses {@code mapValue()} to multiply Property values by 2 if they are integers:
	 * <pre>{@code
	 * Map<String, Object> export =
	 *  AndHow.instance().export(MyClass.class)
	 *   .map(p -> p.mapValue(
	 *     (p.getValue() != null && p.getValue() instanceof Integer)
	 *       ? (Integer)p.getValue() * 2:p.getValue()) )
	 *   .collect(ExportCollector.objectMap());
	 * }</pre>
	 * <p>
	 * Take care to map the method that will be used by the collector you have chosen:
	 * <ul>
	 * <li>'String' collectors call {@link #getValueAsString()}: use {@link #mapValueAsString(String)}</li>
	 * <li>'Object' collectors call {@link #getValue()}: use {@link #mapValue(Object)}</li>
	 * </ul>
	 * @param value The new value to return for the value of this Property.
	 * @return A new instance identical to the original, except it has a new object value.
	 */
	PropertyExport mapValue(Object value);

	/**
	 * Intended for using with Java {@code stream()}s, this method maps this instance to a new one
	 * with a new String value for the Property, i.e., the 'value' part of a key-value pair.
	 * <p>
	 * Calling {@link #getValueAsString()} on the new instance returns the new value, which may be null.
	 * <p>
	 * This example uses {@code mapValueAsString()} to convert Property values to uppercase:
	 * <pre>{@code
	 * Map<String, String> export =
	 *  AndHow.instance().export(MyClass.class)
	 *   .map( p -> p.mapValueAsString( p.getValueAsString() != null
	 *     ? p.getValueAsString().toUpperCase():null) )
	 *       .collect(ExportCollector.stringMap());
	 * }</pre>
	 * <p>
	 * Take care to map the method that will be used by the collector you have chosen:
	 * <ul>
	 * <li>'String' collectors call {@link #getValueAsString()}: use {@link #mapValueAsString(String)}</li>
	 * <li>'Object' collectors call {@link #getValue()}: use {@link #mapValue(Object)}</li>
	 * </ul>
	 * @param value The new value to return for the String value of this Property.
	 * @return A new instance identical to the original, except it has a new String value.
	 */
	PropertyExport mapValueAsString(String value);
}
