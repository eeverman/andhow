package org.yarnandtail.andhow.compile;

import java.util.List;

/**
 *
 * @author ericeverman
 */
public interface PropertyRegistrationsForClass {

	String getCanonicalRootName();

	List<PropertyRegistration> getRegistrationList();

}
