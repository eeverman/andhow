package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.staticparam.valuetype.ValueType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigGroupDescription;
import static yarnandtail.andhow.ConfigPointDef.EMPTY_STRING_LIST;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.ParsingException;

/**
 *
 * @author eeverman
 */
public abstract class ConfigPointBase<T> implements ConfigPoint<T> {
	
	private final String explicitName;
	private final ConfigPointType paramType;
	private final ValueType valueType;
	private final T defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final boolean priv;
	
	public ConfigPointBase(String explicitName,
			ConfigPointType paramType, ValueType<T> valueType,
			T defaultValue, String shortDesc, String helpText, String[] aliases,
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
		
		
		//So do we even need this?  It seems like we don't care to track all of 
		//these until they are actually registered in an app.  We can build a more
		//targetted list when they are actually registered.
//		StackTraceElement[] st = new Throwable().fillInStackTrace().getStackTrace();
//		for (int i = 0; i < st.length; i++) {
//			try {
//				String className = st[i].getClassName();
//				Class<?> clazz = Class.forName(className);
//				System.out.println("Checking  " + clazz.getName());
//				if (ConfigPointGroup.class.isAssignableFrom(clazz)) {
//					System.out.println("Found it!  " + clazz.getName() + " is the calling class");
//					System.out.println("Method name: " + st[i].getMethodName());
//
//				
//					break;
//				}
//			} catch (ClassNotFoundException ex) {
//				Logger.getLogger(ConfigPointBase.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}
	}
	
	@Override
	public ConfigPointType getPointType() {
		return paramType;
	}
	
	@Override
	public ValueType<T> getValueType() {
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
	public T getBaseDefault() {
		return defaultValue;
	}
	
	@Override
	public T getValue() {
		T v = getExplicitValue();
		if (v != null) {
			return v;
		} else {
			return getDefaultValue();
		}
	}
	
	@Override
	public T getExplicitValue() {
		if (AppConfig.instance().isPointPresent(this)) {
			try {
				return convertString(AppConfig.instance().getPointUserString(this));
			} catch (ParsingException ex) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public T convertString(String str) throws ParsingException {
		return getValueType().convert(str);
	}
	
	@Override
	public T getDefaultValue() {
		return getBaseDefault();
	}
	
}
