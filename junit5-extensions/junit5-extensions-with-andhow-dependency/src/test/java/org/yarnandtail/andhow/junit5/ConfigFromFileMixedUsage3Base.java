package org.yarnandtail.andhow.junit5;


import org.yarnandtail.andhow.AndHow;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A base class for the 'Usage3' test to test the interaction of tests w/ superclasses.
 */
@ConfigFromFileBeforeAllTests(value = "Conf1And2AsBob.properties", includeClasses = {Conf1.class, Conf2.class, Conf3.class})
class ConfigFromFileMixedUsage3Base {

	//Overriding this method does not cause the annotation to be applied to the overridden method
	@ConfigFromFileBeforeThisTest(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
	public void test2() {

	}

}