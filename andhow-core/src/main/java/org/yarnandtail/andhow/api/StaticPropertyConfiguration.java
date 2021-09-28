package org.yarnandtail.andhow.api;

import java.util.List;

/**
 * Public view of configuration and metadata for all known static Properties.
 * 
 * This is the entire domain of Properties defined as static properties
 * (ie the property is defined as a static variable) and on the classpath
 * (or explicitly configured) to be part of AndHow in the current classloader.
 * <br>
 * This definition does not include the actual values configured for the
 * Properties - That is in the ValidatedValues interface.
 * <br>
 * Note that it is not possible to list all known Properties or look them up
 * by name or group.  This is intentional to enforce security.  Property
 * declarations follow Java visibility rules, so you must have the reference to
 * the actual property to look up information or values for that property.  For
 * instance, this property:
 * <br>
 * <code>private static final StrProp FOO = StrProp.builder().build();</code>
 * <br>
 * Is declared as private.  If any part of the public AndHow API provides an
 * alternate path to access the property and read its value, it would bypass
 * the intended visibility of the property.
 * 
 */
public interface StaticPropertyConfiguration {

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

	/**
	 * All the effective 'in' & 'out' aliases for this property, not including the canonical name.
	 * <p>
	 * The returned aliases may differ from the original requested aliases in two ways:
	 * <ul>
	 * <li>{@link NamingStrategy} may modify 'In' aliases, e.g. convert them to uppercase for
	 *   case-insensitive matching.  This method returns the modified 'In' aliases.</li>
	 * <li>Aliases may be coalesced if:  If an 'In' alias matches an 'Out' alias, a single
	 * 'InAndOut' alias may be returned.</li>
	 * </ul>
	 * <p>
	 * @param property The property to fetch naming information for
	 * @return A non-null, non-modifiable list of alias {@link EffectiveName}s for this Property.
	 * @see Property#getInAliases()
	 * @see Property#getOutAliases()
	 * @see Property#getRequestedAliases()
	 */
	List<EffectiveName> getAliases(Property<?> property);

	/**
	 * The canonical name of a {@link Property}.
	 * <p>
	 * Canonical Property names are the full Java classname of the class containing the Property, plus
	 * the Property name, e.g. {@code org.acme.myapp.MyClass.MyProperty}.  Properties contained in
	 * inner classes and interfaces continue the same naming structure, e.g.
	 * {@code org.acme.myapp.MyClass.MyInnerClass.MyInnerInterface.MyProperty}.
	 *
	 * @param prop The Property to get the canonical name for.
	 * @return The canonical name of the Property.
	 */
	String getCanonicalName(Property<?> prop);

	/**
	 * Defines how names are created for Properties.
	 *
	 * @return The NamingStrategy in use.
	 */
	NamingStrategy getNamingStrategy();

}
