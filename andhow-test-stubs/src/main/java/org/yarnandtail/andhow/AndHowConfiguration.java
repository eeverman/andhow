package org.yarnandtail.andhow;

/**
 * A class to mock the real class of the same name and package.
 *
 * The classes in this module interact w/ the real AndHow classes via reflection so there is no
 * dependency.  There is no way to test that without having classes present with the same
 * name and classpath, thus, this and other classes exist in the test directory.
 */
public interface AndHowConfiguration<C extends AndHowConfiguration> {


}
