package yarnandtail.andhow.staticparam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigGroupDescription;
import static yarnandtail.andhow.ConfigPointDef.EMPTY_STRING_LIST;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public abstract class ConfigPointBase implements ConfigPoint {

	private final static ArrayList<ConfigPointBase> instances = new ArrayList();
	
	private final String explicitName;
	private final ConfigPointType paramType;
	private final ValueType valueType;
	private final Object defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final boolean priv;
	
	public ConfigPointBase(String explicitName,
			ConfigPointType paramType, ValueType valueType,
			Object defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		List<String> aliasList;
		if (aliases != null && aliases.length > 0) {
			aliasList = Collections.unmodifiableList(Arrays.asList(aliases));
		} else {
			aliasList = EMPTY_STRING_LIST;
		}
	
				
		//Clean all values to be non-null
		this.explicitName = StringUtils.trimToNull(explicitName);
		this.paramType = paramType;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		this.priv = priv;
		
		instances.add(this);

	}
	

	
//	public static ConfigPointBase addFlag(String name) {
//		ConfigPointBase cpb = new ConfigPoint(name, ConfigPointType.FLAG, FlagType.instance(), null, "", "", null, false);
//		instances.add(cpb);
//		return cpb;
//	}
	
	

	@Override
	public String getGroupDescription() {
		if (this.getClass().getAnnotation(ConfigGroupDescription.class) != null) {
			return StringUtils.trimToEmpty(this.getClass().getAnnotation(ConfigGroupDescription.class).groupName());
		} else {
			return "";
		}
	}

	@Override
	public String getEntireSetDescription() {
		if (this.getClass().getAnnotation(ConfigGroupDescription.class) != null) {
			return StringUtils.trimToEmpty(this.getClass().getAnnotation(ConfigGroupDescription.class).groupDescription());
		} else {
			return "";
		}
	}
	
	@Override
	public ConfigPointType getPointType() {
		return paramType;
	}
	
	@Override
	public ValueType getValueType() {
		return valueType;
	}

	@Override
	public String getExplicitBaseName() {
		return explicitName;
	}

	@Override
	public String getShortDescription() {
		return shortDesc;
	}

	@Override
	public String getHelpText() {
		return helpText;
	}

	@Override
	public List<String> getBaseAliases() {
		return alias;
	}

	@Override
	public boolean isPrivate() {
		return priv;
	}
	
	@Override
	public Object getBaseDefaultObject() {
		return defaultValue;
	}
	
}
