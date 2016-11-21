package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAliasRequired extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint("Bob", true);
	StringConfigPoint KVP_NULL = new StringConfigPoint(null, true);
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint(false, true);
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint(true, true);
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint(null, true);
}
