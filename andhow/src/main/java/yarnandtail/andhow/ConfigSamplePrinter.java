package yarnandtail.andhow;

import java.io.PrintStream;

/**
 * Can write a sample configuration to the passed in PrintStream.
 * 
 * This is intended to be implemented by Loaders who support writing configuration
 * samples.  If AndHow fails to startup correctly, each Loader implementing this
 * interface will create a sample configuration for the user to start from.
 * 
 * @author ericeverman
 */
public interface ConfigSamplePrinter {
	
	/** Required properties will be marked w/ this text in the sample */
	static final String REQUIRED_KEYWORD = "REQUIRED";
	
	/** A general explanation for a sample header of what a required property is */
	static final String REQUIRED_HEADER_TEXT = "Properties marked " + REQUIRED_KEYWORD + 
			" must be given a value or a RuntimeException will be thrown.";
	
	/** Lead-in text for validation requirements */
	static final String THE_VALUE_MUST_TEXT = "The property value must";
	
	/** Lead-in for a default value */
	static final String DEFAULT_VALUE_TEXT = "Default Value";
	
	void printSampleStart(PrintStream out);
	
	void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group);

	void printProperty(PrintStream out, Class<? extends PropertyGroup> group, Property<?> point)
			throws IllegalArgumentException, IllegalAccessException, SecurityException;
	
	void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group);
	
	void printSampleEnd(PrintStream out);
}
