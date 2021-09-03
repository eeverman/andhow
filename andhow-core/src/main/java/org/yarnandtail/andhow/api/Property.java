package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.AndHow;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a configuration point of an application.
 * 
 * Implementations are typed so that they return String, Integer, Boolean, etc.,
 rather than just configuration strings.
 
 Implementor's Notes:
 See IntProp as a best example of how to implement a new Property.
 The reason for creating a new implementation would be to handle a new type, 
 such as a DateTime type.
 
 <ul>
 * <li>All implementations can use the PropertyBase as a base class.  Most
 * methods are already present w/ just a few methods left to implement.
 * <li>All implementations should have a static builder() method that returns
 * a builder capable of building an instance.  by convention, builders are
 * inner classes of their associated Property.  The PropertyBuilderBase is an
 * easy base class to extend that provide nearly all needed functionality.
 * <li>Builders should provide easy access to Validators for their appropriate type.
 * For instance the StrProp has a value type of String and has an
 * associated StringRegex Validator.  By convention, the builder methods to add
 * validators use the 'must' terminology, as in:  mustMatchRegex(String regex),
 * or mustStartWith(String prefix).
 * </ul>
 * @author eeverman
 */
public interface Property<T> {
	
	/**
	 * Returns the effective value of this property.
	 * 
	 * The effective value is the explicitly configured value, or if that is null,
	 * the default value.  Explicitly setting a property to null is not possible
	 * because it will just be ignored and the default used instead.
	 * 
	 * @return May be null, unless the property is marked as required.
	 */
	T getValue();
	
	/**
	 * The value found and loaded for this value by a Loader.
	 * 
	 * If no non-null value was found by a loader for this property, null is returned.
	 * 
	 * @return May be null
	 */
	T getExplicitValue();
	
	/**
	 * The default value, as defined when this Property was constructed.
	 * 
	 * @return May be null
	 */
	T getDefaultValue();
	
	/**
	 * If true, the effective value must be non-null to be considered valid.
	 * 
	 * The effective value is the explicitly configured value, or if that is null,
	 * the default value.
	 * 
	 * @return True if a non-null value is required.
	 */
	boolean isNonNullRequired();
	
	/**
	 * The basic type of the property:  Flag, name/value, multi=value.
	 * @return Never null
	 */
	PropertyType getPropertyType();
	
	/**
	 * The type of the value (String, Number, Integer, etc).
	 * For Properties that allow multiple values (not yet implemented), an array
	 * of values of the specified type can be fetched.
	 * @return Never null
	 */
	ValueType<T> getValueType();
	
	/**
	 * The Trimmer responsible for trimming String values before they are converted
	 * to the appropriate property type.
	 * 
	 * @return Never null
	 */
	Trimmer getTrimmer();
	
	/**
	 * A description of the property, what it is for and what it does in the system.
	 * 
	 * When the description is presented, the canonical name, validation requirements
	 * and helpText is presented as well, so it does not need to all be included in
	 * the description.
	 * 
	 * @return May be empty but not null.
	 */
	String getDescription();
	
	/**
	 * List of validators to validate the converted value.
	 * @return An unmodifiable list of Validators.  Never null.
	 */
	List<Validator<T>> getValidators();
	
	/**
	 * 'In' & 'Out' aliases requested when this Property was constructed.
	 * <p>
	 * The returned aliases may differ from the original requested aliases or those returned by
	 * {@link #getInAliases()} in two ways:
	 * <ul>
	 * <li>{@link NamingStrategy} may modify 'In' aliases, e.g. convert them to uppercase for
	 *   case-insensitive matching.  {@link #getInAliases()} returns the modified 'In' aliases.</li>
	 * <li>Aliases may be coalesced if:  If an 'In' alias matches an 'Out' alias, a single
	 * 'InAndOut' alias may be returned.</li>
	 * </ul>
	 *
	 * @return A non-null list of alias {@link Name}s for this Property.
	 */
	List<Name> getRequestedAliases();
	
	
	/**
	 * Additional help information for this Property that will be included in configuration documentation.
	 * 
	 * Help text can provide added details, such as details on how to specify
	 * special values or interrelated properties.
	 * 
	 * @return May be empty but not null.
	 */
	String getHelpText();

	/**
	 * All the effective 'in' aliases for this property, not including the canonical name.
	 * <p>
	 * 'In' aliases are additional names by which a Property value may receive its value from
	 * a configuration source.  The returned aliases may differ from aliases returned by
	 * {@link #getRequestedAliases()} because the {@link NamingStrategy} may modify them
	 * (e.g. convert them to uppercase for case-insensitive matching).
	 * <p>
	 * Aliases may be 'in' and/or 'out', so there may be overlap between names returned
	 * here and those returned from {@link #getOutAliases()}.
	 *
	 * @return A non-null list of 'in' aliases.  May or may not be writable, but is
	 * 	 disconnected from the underlying list - edits have no effect.
	 */
	default List<String> getInAliases() {
		return AndHow.instance().getAliases(this).stream().filter(n -> n.isIn())
				.map(n -> n.getEffectiveInName()).collect(Collectors.toList());
	}

	/**
	 * All the 'out' aliases for this property, not including the canonical name.
	 * <p>
	 * 'Out' aliases are names used when a Property is exported, such as exporting a group of
	 * Property names & values as a Map for a framework that configures this way.
	 * <p>
	 * Aliases may be 'in' and/or 'out', so there may be overlap between names returned
	 * here and those returned from {@link #getInAliases()}.
	 *
	 * @return A non-null list of 'out' aliases.  May or may not be writable, but is
	 * 	disconnected from the underlying list - edits have no effect.
	 */
	default List<String> getOutAliases() {
		return AndHow.instance().getAliases(this).stream().filter(n -> n.isOut())
				.map(n -> n.getEffectiveOutName()).collect(Collectors.toList());
	}

	/**
	 * The canonical name of a {@link Property}.
	 * <p>
	 * Canonical Property names are the full Java classname of the class containing the Property, plus
	 * the Property name, e.g. {@code org.acme.myapp.MyClass.MyProperty}.  Properties contained in
	 * inner classes and interfaces continue the same naming structure, e.g.
	 * {@code org.acme.myapp.MyClass.MyInnerClass.MyInnerInterface.MyProperty}.
	 *
	 * @return The canonical name of the Property.
	 */
	default String getCanonicalName() {
		return AndHow.instance().getCanonicalName(this);
	}

	/**
	 * Converts the effective value of the Property to a String.
	 * <p>
	 * Usually this is the same as calling {@code toString()} on the effective value, but some
	 * Properties may have custom {@link ValueType}s or more complex conversions,
	 * such as dates and times.
	 *
	 * @return Effective value converted to a String, or null if the value is null.
	 */
	default String getValueAsString() {
		return getValueType().toString(getValue());
	}

	/**
	 * True if the Property's value is explicitly set to a non-null value via one of the loaders.
	 *
	 * @return True if this value is explicitly set.
	 */
	default boolean isExplicitlySet() {
		return AndHow.instance().isExplicitlySet(this);
	}

}
