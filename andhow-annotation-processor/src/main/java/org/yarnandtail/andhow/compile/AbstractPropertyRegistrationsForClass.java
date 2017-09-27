package org.yarnandtail.andhow.compile;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author ericeverman
 */
public abstract class AbstractPropertyRegistrationsForClass implements PropertyRegistrationsForClass {
	
	@Override
	public List<PropertyRegistration> getRegistrationList() {
		PropertyRegistrationList list = new PropertyRegistrationList(getRootCanonicalName());
		addPropertyRegistrations(list);
		return Collections.unmodifiableList(list);
	}
	
	public abstract void addPropertyRegistrations(PropertyRegistrationList list);
	
}
