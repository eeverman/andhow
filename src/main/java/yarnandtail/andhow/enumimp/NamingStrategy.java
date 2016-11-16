package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface NamingStrategy {
	public String getEffectiveName(ConfigPointCommon paramDef);
	public String getEffectiveAlias(ConfigPointCommon paramDef, String alias);
}
