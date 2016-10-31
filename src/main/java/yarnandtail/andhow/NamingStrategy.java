package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface NamingStrategy {
	public String getFullName(ParamDefinitionInterface paramDef);
	public String getAliasName(ParamDefinitionInterface paramDef, String alias);
}
