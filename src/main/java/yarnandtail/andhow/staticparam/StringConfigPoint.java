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
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint(String name) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, "", "", null, false);
	}
	public StringConfigPoint(String explicitName,
			ConfigPointType paramType, ValueType valueType,
			String defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);

	}
	
	public String getValue() {
		return AppConfig.instance().getUserString(this);
	}
	
	String getExplicitValue() {
		return AppConfig.instance().getUserString(this);
	}
	
	String getDefaultValue() {
		return getBaseDefault();
	}

}
