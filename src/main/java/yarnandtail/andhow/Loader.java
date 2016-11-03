package yarnandtail.andhow;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface Loader {
	List<ConfigParamValue> load(LoaderState state);
}
