package yarnandtail.andhow.load;

import java.util.Map;
import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public interface Loader {
	Map<ConfigPoint<?>, Object> load(LoaderState state);
}
