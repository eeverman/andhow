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
	static final String REQUIRED_HEADER_TEXT = "Properties marked as " + REQUIRED_TEXT + 
			" must receive a value during the loading process or " +
			AndHow.ANDHOW_INLINE_NAME + " will throw a RuntimeException.";
	
	/** Required point will be marked w/ this text in the sample */
	static final String VALIDATION_TEXT = "The configured property must";
	
	void printSampleStart(PrintStream out);
	
	void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group);

	void printProperty(PrintStream out, Class<? extends PropertyGroup> group, Property<?> point)
			throws IllegalArgumentException, IllegalAccessException, SecurityException;
	
	void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group);
	
	void printSampleEnd(PrintStream out);
}
