package yarnandtail.andhow.staticparam;

import java.util.logging.Level;
import java.util.logging.Logger;
import yarnandtail.andhow.staticparam.valuetype.ValueType;
import yarnandtail.andhow.staticparam.valuetype.StringType;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint(String name) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, "", "", null, false);
	}
	
	public StringConfigPoint(String name, String defaultValue) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), defaultValue, "", "", null, false);
	}
	
	public StringConfigPoint(String explicitName,
			ConfigPointType paramType, ValueType<String> valueType,
			String defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);

	}
	
}
