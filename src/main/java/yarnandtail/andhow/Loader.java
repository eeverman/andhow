package yarnandtail.andhow;

import java.util.Map;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.load.LoaderState;

/**
 *
 * @author eeverman
 */
public interface Loader {
	Map<ConfigPoint<?>, Object> load(LoaderState state);
}
