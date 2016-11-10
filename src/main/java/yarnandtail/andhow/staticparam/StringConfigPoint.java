package yarnandtail.andhow.staticparam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static yarnandtail.andhow.ConfigPointDef.EMPTY_STRING_LIST;
import yarnandtail.andhow.ConfigPointType;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase {
	
	public StringConfigPoint(String explicitName,
			ConfigPointType paramType, ValueType valueType,
			Object defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);

	}
	
	public static StringConfigPoint addString(String name) {
		StringConfigPoint cpb = new StringConfigPoint(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, "", "", null, false);
		
		return cpb;
	}
	String getValue() {
		return "";
	}
	
	String getExplicitValue() {
		return "";
	}
	
	String getDefaultValue() {
		return "";
	}

}
