package yarnandtail.andhow.enumimp;

import yarnandtail.andhow.staticparam.ConfigPointType;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.enumimp.valuetype.ValueType;

/**
 * An actual usage of a ConfigPointDef in an application.
 * A ConfigPointDef is just the concept of a point in an application that can be
 configured.  A ConfigPointUsage means an application is ready to use the
 ConfigPointDef by adding application specific context, which includes these
 two items:
 <ul>
 * <li>Determining the actual parameter name and aliases used to configure the point.
 * Names and aliases cannot be hardcoded because there must be a way to mediate
 * if/when names from multiple modules and libraries collide.
 * <li>Supplying application specific default values.  These defaults override
 the default specified in the ConfigPointDef because the <i>Application knows
 * best</i> what reasonable defaults are for its usage.
 * </ul>
 * 
 * @author eeverman
 * @param <P>
 */
public class ConfigPointUsage<P extends ConfigPointDef> implements ConfigPointContext, ConfigPointCommon {
	
	final P confPt;
	final NamingStrategy naming;
	final Object defaultVal;
	boolean accumulating;
	boolean reverseLoadOrder;
	

	public ConfigPointUsage(P confPt, NamingStrategy naming, Object defaultValue, boolean accumulating, boolean reverseLoadOrde) {
		this.confPt = confPt;
		this.naming = naming;
		this.defaultVal = defaultValue;
		this.accumulating = accumulating;
		this.reverseLoadOrder = reverseLoadOrder;
	}
	
	@Override
	public String getEffectiveName() {
		return naming.getEffectiveName(confPt);
	}
	
	@Override
	public List<String> getEffectiveAliases() {
		List<String> ba = confPt.getBaseAliases();
		
		if (ba.size() > 0) {
			List<String> ea = new ArrayList(ba.size());
			ba.stream().forEach(c -> ea.add(naming.getEffectiveAlias(confPt, c)));
			return ea;
		} else {
			return ba;
		}
	}
	
	@Override
	public boolean isAccumulating() {
		return accumulating;
	}
	
	@Override
	public boolean isReverseLoadOrder() {
		return reverseLoadOrder;
	}
	
	@Override
	public Object getEffectiveDefault() {
		if (defaultVal != null) {
			return defaultVal;
		} else {
			return confPt.getBaseDefaultValue();
		}
	}

	@Override
	public Class<? extends ConfigPointDef> getEnumClass() {
		return confPt.getEnumClass();
	}
	
	@Override
	public String getGroupDescription() {
		return confPt.getGroupDescription();
	}
	
	@Override
	public String getEntireSetDescription() {
		return confPt.getEntireSetDescription();
	}
	
	@Override
	public ConfigPointType getPointType() {
		return confPt.getPointType();
	}
	
	@Override
	public ValueType getValueType() {
		return confPt.getValueType();
	}
	
	@Override
	public String getExplicitBaseName() {
		return confPt.getExplicitBaseName();
	}
	
	@Override
	public String getShortDescription() {
		return confPt.getShortDescription();
	}
	
	@Override
	public String getHelpText() {
		return confPt.getHelpText();
	}

	@Override
	public List<String> getBaseAliases() {
		return confPt.getBaseAliases();
	}

	@Override
	public List<Enum> getPossibleValueEnums() {
		return confPt.getPossibleValueEnums();
	}
	
	@Override
	public Object getBaseDefaultValue() {
		return confPt.getBaseDefaultValue();
	}
	
	@Override
	public boolean isPrivate() {
		return confPt.isPrivate();
	}

}
