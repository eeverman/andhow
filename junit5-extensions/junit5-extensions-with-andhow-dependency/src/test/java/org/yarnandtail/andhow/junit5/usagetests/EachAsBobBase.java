package org.yarnandtail.andhow.junit5.usagetests;


import org.yarnandtail.andhow.junit5.*;

/**
 * A base class for the 'Usage3' test to test the interaction of tests w/ superclasses.
 */
@ConfigFromFileBeforeEachTest(value = "Conf1And2AsBob.properties", includeClasses = {Conf1.class, Conf2.class, Conf3.class})
class EachAsBobBase {

	//Overriding this method does not cause the annotation to be applied to the overridden method
	@ConfigFromFileBeforeThisTest(value = "Conf1OnlyAsDeb.properties", includeClasses = {Conf1.class})
	public void test2() {

	}

}