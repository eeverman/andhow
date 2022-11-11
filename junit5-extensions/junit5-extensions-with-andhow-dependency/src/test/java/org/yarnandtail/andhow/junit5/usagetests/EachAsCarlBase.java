package org.yarnandtail.andhow.junit5.usagetests;


import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeEachTest;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeThisTest;

/**
 * A base class for the 'Usage3' test to test the interaction of tests w/ superclasses.
 */
@ConfigFromFileBeforeEachTest(value = "Conf1And2AsCarl.properties", includeClasses = {Conf1.class, Conf2.class, Conf3.class})
class EachAsCarlBase {

}