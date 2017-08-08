package org.yarnandtail.andhow.api;

/**
 * A map of Properties to their loaded values.
 * 
 * This is basically a dedicated Map with Property keys and their associated
 * values.  Not all Properties will be in the map, only the ones which have a
 * value specified.  During runtime, an immutable instance of this class contains
 * all the needed information to map Properties to their values.
 * 
 * @author eeverman
 */
public interface ValueMap {
	/**
	 * The value found and loaded for this value by a Loader.
	 * 
	 * If no non-null value was found by a loader for this property, null is returned.
	 * 
	 * @param <T> prop The property to get the value for
	 * @return The value, if explicitly set, or null if not explicity set.
	 */
	<T> T getExplicitValue(Property<T> prop);
	
	/**
	 * The effective value, similar to Property.getValue, but specifically for
	 * the context of this ValueMap.
	 * 
	 * The effective value is the explicitly configured value, or if that is null,
	 * the default value.  Explicitly setting a property to null is not possible
	 * because it will just be ignored and the default used instead.
	 * 
	 * @param prop The property to get the value for.
	 * @return The explicit value or, if no explicit, the default value.  Otherwise null.
	 */
	<T> T  getValue(Property<T> prop);
	
	/**
	 * Returns true if the Property's value was explicitly set to a non-null value
	 * via one of the loaders.
	 * 
	 * @param prop The property to check
	 * @return True if this value is explicitly set.
	 */
	boolean isExplicitlySet(Property<?> prop);
}
