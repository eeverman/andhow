package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.NamingStrategy;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration {
	List<Loader> buildLoaders();

	NamingStrategy getNamingStrategy();
}
