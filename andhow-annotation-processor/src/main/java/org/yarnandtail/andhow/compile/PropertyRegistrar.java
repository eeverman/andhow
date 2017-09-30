package org.yarnandtail.andhow.compile;

import java.util.List;

/**
 *
 * @author ericeverman
 */
public interface PropertyRegistrar {

	String getRootCanonicalName();

	List<PropertyRegistration> getRegistrationList();

}
