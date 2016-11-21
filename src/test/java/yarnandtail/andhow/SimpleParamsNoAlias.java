package yarnandtail.andhow;

import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.FlagConfigPoint;

/**
 *
 * @author eeverman
 */
public interface SimpleParamsNoAlias extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint();
	StringConfigPoint KVP_NULL = new StringConfigPoint();
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint();
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint();
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint();
}
