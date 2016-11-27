package yarnandtail.andhow;

/**
 * The values that are loaded or otherwise specified for a set of ConfigPoints
 * in a AppConfig.
 * 
 * This is basically a dedicated Map with ConfigPoint keys and their associated
 * value.  Not all ConfigPoints will be in the map, only the ones which have a
 * value specified.
 * 
 * @author eeverman
 */
public interface AppConfigValues {
	Object getValue(ConfigPoint<?> point);
	boolean isPointPresent(ConfigPoint<?> point);
}
