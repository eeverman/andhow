package yarnandtail.andhow.enumimp;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface Loader {
	List<ConfigPointValue> load(LoaderState state);
}
