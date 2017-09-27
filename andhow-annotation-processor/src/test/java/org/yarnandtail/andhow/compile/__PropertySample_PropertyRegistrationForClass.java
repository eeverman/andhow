package org.yarnandtail.andhow.compile;

/**
 *
 * @author ericeverman
 */
public class __PropertySample_PropertyRegistrationForClass extends AbstractPropertyRegistrationsForClass {

	
	@Override
	public void addPropertyRegistrations(PropertyRegistrationList list) {
		list.add("STRING");
		list.add("STRING_PUB");
		
		//1ST inner class
		list.add("STRING", "PI");
		list.add("STRING", "PI", "PI_DC");
		list.add("STRING_PUB");
		list.add("STRING", "PI", "PI_DI");
		list.add("STRING_PUB");
		
		//2ND INNER CLASS
		list.add("STRING", "PC");
		list.add("STRING_PUB");
		list.add("STRING", "PC", "PC_PC");
		list.add("STRING", "PC", "PC_PI");
	}

	@Override
	public String getRootCanonicalName() {
		return "org.yarnandtail.andhow.compile.PropertySample";
	}

}
