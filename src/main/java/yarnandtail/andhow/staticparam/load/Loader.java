package yarnandtail.andhow.staticparam.load;

import yarnandtail.andhow.*;
import java.util.List;

/**
 *
 * @author eeverman
 */
public interface Loader {
	List<ConfigPointValue> load(LoaderState state);
}
