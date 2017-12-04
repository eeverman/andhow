package org.yarnandtail.andhow;

import java.util.List;
import org.yarnandtail.andhow.api.*;

/**
 *
 * @author ericeverman
 */
public interface AndHowConfiguration<C extends AndHowConfiguration> {
	List<Loader> buildLoaders();
	
	List<GroupProxy> getRegisteredGroups();

	NamingStrategy getNamingStrategy();
	
	C setCmdLineArgs(String[] commandLineArgs);
	
	<T> C addFixedValue(Property<T> property, T value);
	
	C removeFixedValue(Property<?> property);
	
	void build();
}
