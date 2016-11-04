package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface ConfigValueCollection {
	
	ConfigPointValue getEffective(ConfigPointCommon configPoint);
	
}
