package yarnandtail.andhow;

import yarnandtail.andhow.FlagConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.StringConfigPoint;
import static yarnandtail.andhow.ConfigPointGroup.*;

/**
 *
 * @author eeverman
 */
public interface SimpleParams extends ConfigPointGroup {
	
	StringConfigPoint KVP_BOB = new StringConfigPoint("kvp_bob", "bob");
	StringConfigPoint KVP_NULL = new StringConfigPoint("kvp_null");
	FlagConfigPoint FLAG_FALSE = new FlagConfigPoint("flag_false", false);
	FlagConfigPoint FLAG_TRUE = new FlagConfigPoint("flag_true", true);
	FlagConfigPoint FLAG_NULL = new FlagConfigPoint("flag_null");
}
