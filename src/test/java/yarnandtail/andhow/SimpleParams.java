package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface SimpleParams extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint("bob", "", "kvp_bob");
	StringConfigPoint KVP_NULL = new StringConfigPoint(null, "", "kvp_null");
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint(false, "", "flag_false");
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint(true, "", "flag_true");
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint(null, "", "flag_null");
}
