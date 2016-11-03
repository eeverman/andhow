package yarnandtail.andhow;

import java.util.Properties;

/**
 * State of the loading process that the chain of loaders can access.
 * @author eeverman
 */
public interface LoaderState {
	String[] getCmdLineArgs();
	ConfigValueCollection getValueCollection();
}
