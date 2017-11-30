package org.yarnandtail.andhow.service;

import java.util.List;

/**
 *
 * @author ericeverman
 */
public interface PropertyRegistrar {

	String getRootCanonicalName();

	List<PropertyRegistration> getRegistrationList();

}
