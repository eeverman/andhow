package org.yarnandtail.andhow.service;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author ericeverman
 */
public abstract class AbstractPropertyRegistrar implements PropertyRegistrar {
	
	@Override
	public List<PropertyRegistration> getRegistrationList() {
		PropertyRegistrationList list = new PropertyRegistrationList(getRootCanonicalName());
		addPropertyRegistrations(list);
		return Collections.unmodifiableList(list);
	}
	
	public abstract void addPropertyRegistrations(PropertyRegistrationList list);
	
}
