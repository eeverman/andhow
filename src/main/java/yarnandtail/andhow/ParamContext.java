package yarnandtail.andhow;

import java.util.List;

/**
 * This isn't done:
 * * Needs an interface
 * * method to list effective alias names (probably build and store in contructor)
 * * method to get effective name
 * @author eeverman
 */
public class ParamContext<P extends ParamDefinition> implements ParamDefinitionInterface {
	
	P paramDef;
	NamingStrategy naming;

	public ParamContext(P paramDef, NamingStrategy naming) {
		this.paramDef = paramDef;
		this.naming = naming;
	}
	

	@Override
	public Class<? extends ParamDefinition> getEnumClass() {
		return paramDef.getEnumClass();
	}
	
	@Override
	public String getEntireSetName() {
		return paramDef.getEntireSetName();
	}
	
	@Override
	public String getEntireSetDescription() {
		return paramDef.getEntireSetDescription();
	}
	
	@Override
	public ParamType getParamType() {
		return paramDef.getParamType();
	}
	
	@Override
	public String getExplicitName() {
		return paramDef.getExplicitName();
	}
	
	@Override
	public String getShortDescription() {
		return paramDef.getShortDescription();
	}
	
	@Override
	public String getHelpText() {
		return paramDef.getHelpText();
	}

	@Override
	public List<String> getAlias() {
		return paramDef.getAlias();
	}

	@Override
	public List<Enum> getPossibleValueEnums() {
		return paramDef.getPossibleValueEnums();
	}
	
	@Override
	public Object getDefaultValue() {
		return paramDef.getDefaultValue();
	}

}
