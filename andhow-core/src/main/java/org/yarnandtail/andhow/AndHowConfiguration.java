package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.*;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration {
	List<Loader> buildLoaders();
	
	List<GroupProxy> getRegisteredGroups();

	NamingStrategy getNamingStrategy();
}
