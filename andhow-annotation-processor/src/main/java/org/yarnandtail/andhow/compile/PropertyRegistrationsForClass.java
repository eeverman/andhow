package org.yarnandtail.andhow.compile;

import java.util.List;

/**
 *
 * @author ericeverman
 */
public interface PropertyRegistrationsForClass {

	String getRootCanonicalName();

	List<PropertyRegistration> getRegistrationList();

}
