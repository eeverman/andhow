package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface ConfigValueCollection {
	
	ConfigParamValue getEffective(ConfigPointCommon configPoint);
	
}
