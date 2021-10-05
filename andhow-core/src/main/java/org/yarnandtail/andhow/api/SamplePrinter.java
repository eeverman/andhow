package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import java.io.PrintStream;

/**
 * Statelessly writes a sample configuration to the passed in PrintStream.
 *
 * Implementers may be invoked by request or when configuration initiation fails.
 * Each type of configuration source has a Loader associated with it, and that
 * loader can have a SamplePrinter instance associated with it so that when a
 * particular loader is used (such as a properties file loader), its possible to
 * print a sample configuration if the startup fails.
 *
 * @author ericeverman
 */
public interface SamplePrinter {

	/** Required properties will be marked w/ this text in the sample */
	static final String REQUIRED_KEYWORD = "NON-NULL";

	/** A general explanation for a sample header of what a required property is */
	static final String REQUIRED_HEADER_TEXT = "Properties marked " + REQUIRED_KEYWORD +
			" must be given a non-null value or have a non-null default, or a RuntimeException will be thrown.";

	/** Lead-in text for validation requirements */
	static final String THE_VALUE_MUST_TEXT = "The property value must";

	/** Lead-in for a default value */
	static final String DEFAULT_VALUE_TEXT = "Default Value";

	void printSampleStart(PropertyConfigurationInternal definition, PrintStream out);

	void printPropertyGroupStart(PropertyConfigurationInternal definition, PrintStream out,
                                 GroupProxy group);

	void printProperty(PropertyConfigurationInternal definition, PrintStream out,
                       GroupProxy group, Property<?> prop);

	void printPropertyGroupEnd(PropertyConfigurationInternal definition, PrintStream out, GroupProxy group);

	void printSampleEnd(PropertyConfigurationInternal definition, PrintStream out);

	/**
	 * The file extention to use for sample files, eg 'properties' or 'xml'.
	 * @return
	 */
	String getSampleFileExtension();

}
