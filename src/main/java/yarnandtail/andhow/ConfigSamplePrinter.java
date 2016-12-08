package yarnandtail.andhow;

import java.io.PrintStream;

/**
 * Can write a sample configuration to the passed in PrintStream.
 * 
 * This is intended to be implemented by Loaders who support writing configuration
 * samples.  If the AppConfig fails to startup correctly, the AppConfig will
 * ask each Loader implementing this interface to incrementally create a sample
 * configuration for the user to start from.
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
	
	void printConfigGroupStart(PrintStream out, Class<? extends ConfigPointGroup> group);

	void printConfigPoint(PrintStream out, Class<? extends ConfigPointGroup> group, ConfigPoint<?> point)
			throws IllegalArgumentException, IllegalAccessException, SecurityException;
	
	void printConfigGroupEnd(PrintStream out, Class<? extends ConfigPointGroup> group);
	
	void printSampleEnd(PrintStream out);
}
