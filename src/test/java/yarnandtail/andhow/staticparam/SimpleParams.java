package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class SimpleParams extends ConfigPointBase {
	
	public static final ConfigPointBase MY_KVP = ConfigPointBase.addString("kvp");
	public static final ConfigPointBase MY_FLAG = ConfigPointBase.addFlag("flag");
	
	public SimpleParams(String explicitName, ConfigPointType paramType, ValueType valueType, Object defaultValue, String shortDesc, String helpText, String[] aliases, boolean priv) {
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);
	}
	

	
}
