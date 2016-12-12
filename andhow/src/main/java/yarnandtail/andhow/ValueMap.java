package yarnandtail.andhow;

/**
 * The values that are loaded or otherwise specified for a set of ConfigPoints
 * in a AndHow.
 * 
 * This is basically a dedicated Map with ConfigPoint keys and their associated
 * value.  Not all ConfigPoints will be in the map, only the ones which have a
 * value specified.
 * 
 * @author eeverman
 */
public interface ValueMap {
	/**
	 * Get a value explicitly configured.
	 * 
	 * If not configured via one of the Loaders, null is returned, not the default
	 * value.
	 * 
	 * @param <T>
	 * @param point
	 * @return 
	 */
	<T> T getExplicitValue(ConfigPoint<T> point);
	
	/**
	 * The explicitly value, or if that is null, the default (which may also be null).
	 * 
	 * @param <T>
	 * @param point
	 * @return 
	 */
	<T> T  getEffectiveValue(ConfigPoint<T> point);
	
	/**
	 * Returns true if the point's value was explicitly set via one of the loaders.
	 * 
	 * At the moment, that means it would also return a non-null value, however,
	 * future versions may support explicit nulls.
	 * 
	 * @param point
	 * @return 
	 */
	boolean isExplicitlySet(ConfigPoint<?> point);
}
