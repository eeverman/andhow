package yarnandtail.andhow;

import yarnandtail.andhow.valuetype.ValueType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

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
	
	public ConfigPointBase(
			T defaultValue, String shortDesc,
			ConfigPointType paramType, ValueType<T> valueType, boolean priv,
			String explicitName, String helpText, String[] aliases) {
		
		List<String> aliasList;
		if (aliases != null && aliases.length > 0) {
			aliasList = Collections.unmodifiableList(Arrays.asList(aliases));
		} else {
			aliasList = ConfigPointUtil.EMPTY_STRING_LIST;
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
		Object v = AppConfig.instance().getValue(this);
		return cast(v);
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
