package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.Loader;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration {
	List<Loader> buildLoaders();
}
