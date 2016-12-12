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
	
	/** Required point will be marked w/ this text in the sample */
	static final String REQUIRED_TEXT = "REQUIRED";
	
	/** A general explanation for a sample header of what a required point is */
	static final String REQUIRED_HEADER_TEXT = "Value marked as " + REQUIRED_TEXT + 
			" must have a value assigned to them during the loading process or the " +
			"startup will fail with a RuntimeException.";
	
	/** Required point will be marked w/ this text in the sample */
	static final String VALIDATION_TEXT = "The configured value must";
	
	void printSampleStart(PrintStream out);
	
	void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group);

	void printProperty(PrintStream out, Class<? extends PropertyGroup> group, Property<?> point)
			throws IllegalArgumentException, IllegalAccessException, SecurityException;
	
	void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group);
	
	void printSampleEnd(PrintStream out);
}
