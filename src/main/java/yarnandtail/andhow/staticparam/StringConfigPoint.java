package yarnandtail.andhow.staticparam;

/**
 *
 * @author eeverman
 */
public interface StringConfigPoint extends ConfigPoint {
	String getValue();
	
	String getExplicitValue();
	
	String getDefaultValue();
}
