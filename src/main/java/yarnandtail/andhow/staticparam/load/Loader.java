package yarnandtail.andhow.staticparam.load;

import java.util.Map;
import yarnandtail.andhow.staticparam.ConfigPoint;

/**
 *
 * @author eeverman
 */
public interface Loader {
	Map<ConfigPoint, String> load(LoaderState state);
}
