package org.yarnandtail.andhow.util;

import java.io.PrintStream;
import java.util.logging.*;

/**
 * Handles writing a java.util.Logger to a PrintStream, by default System.err for
 * errors and System.out for non-errors.
 *
 * @author ericeverman
 */
public class AndHowLogHandler extends Handler {

	/**
	 * Messages beginning with this prefix are important for users to see, but not an error.
	 * This was created for the case where sample configuration was written to a tmp directory,
	 * possibly at the user's request.  The message must be displayed or they won't know where
	 * it was written to, but it should not look like an error.
	 */
	public static final String MANDATORY_NOTE_PREFIX = "Mandatory Note: ";

	private PrintStream errStream = null;
	private PrintStream outStream = null;

	public AndHowLogHandler() {
		configure();
	}

	public AndHowLogHandler(PrintStream errStream, PrintStream outStream) {
		this.errStream = errStream;
		this.outStream = outStream;
	}

	/**
	 * Returns the PrintStream used for error level logging (java.util.logging SEVERE level).
	 *
	 * @return
	 */
	public PrintStream getErrStream() {
		if (errStream != null) {
			return errStream;
		} else {
			return System.err;
		}
	}

	/**
	 * Sets the PrintStream used for error level logging (java.util.logging SEVERE level).
	 * <p>
	 * Setting to null will revert to the default error stream, which is System.err.
	 *
	 * @param errStream
	 */
	public void setErrStream(PrintStream errStream) {
		this.errStream = errStream;
	}

	/**
	 * Returns the PrintStream used for non-error level logging (java.util.logging WARNING and lower).
	 *
	 * @return
	 */
	public PrintStream getNonErrStream() {
		if (outStream != null) {
			return outStream;
		} else {
			return System.out;
		}
	}

	/**
	 * Sets the PrintStream used for non-error level logging (java.util.logging WARNING and lower).
	 * <p>
	 * Setting to null will revert to the default non-error stream, which is System.out.
	 *
	 * @param outStream
	 */
	public void setNonErrStream(PrintStream outStream) {
		this.outStream = outStream;
	}


	/**
	 * Mostly copied from java.util.logging.ConsoleHandler
	 */
	private void configure() {
		LogManager manager = LogManager.getLogManager();
		String cname = getClass().getName();

		setLevel(getLevelProperty(cname + ".level", Level.ALL));  //ie allow the Logger to do the level filtering
		setFilter(getFilterProperty(cname + ".filter", null));
		setFormatter(getFormatterProperty(cname + ".formatter", new AndHowLogFormatter()));
	}

	/**
	 * Publish a message that must be seen, but is not an error.
	 * <p>
	 * This was created for the case where sample configuration is written to a tmp directory,
	 * possibly at the user's request.  The message must be displayed or the user will not know where
	 * samples were written, but it should not look like an error.
	 * <p>
	 * Detection is be done by prefixing the message template text with {@code MANDATORY_NOTE_PREFIX} -
	 * this method assumes that detection has already been done.  This method will remove
	 * {@code MANDATORY_NOTE_PREFIX} anywhere it is found in the message string template.
	 * <p>
	 * @param record The mandatory note to publish to System.out or System.err
	 */
	protected void publishMandatoryNote(LogRecord record) {

		try {

			String msg = getFormatter().format(record);

			if (msg.contains(MANDATORY_NOTE_PREFIX)) {
				// The AndHowLogFormatter will remove the prefix, but others will not.
				// Some formatter insert color codes at the start, so don't use 'startsWith'
				msg = msg.replace(MANDATORY_NOTE_PREFIX, "");
			}

			getNonErrStream().print(msg);

		} catch (Exception ex) {
			// We don't want to throw an exception here, but we
			// report the exception to any registered ErrorManager.
			reportError(null, ex, ErrorManager.FORMAT_FAILURE);
			return;

		}
	}

	/**
	 * Publish a message to either System.out or System.err based on the message Level.
	 * <p>
	 * Message templates prefixed with {@code MANDATORY_NOTE_PREFIX} are passed directly to
	 * {@link #publishMandatoryNote(LogRecord)}.  This is done before applying formatting.
	 * <p>
	 * @param record The LogRecord to publish to System.out or System.err
	 */
	@Override
	public void publish(LogRecord record) {

		if (record.getMessage().startsWith(MANDATORY_NOTE_PREFIX)) {

			publishMandatoryNote(record);

		} else if (isLoggable(record)) {

			String msg;
			try {
				msg = getFormatter().format(record);
			} catch (Exception ex) {
				// We don't want to throw an exception here, but we
				// report the exception to any registered ErrorManager.
				reportError(null, ex, ErrorManager.FORMAT_FAILURE);
				return;
			}

			//Print errors to System.err, others to System.out

			if (record.getLevel().equals(Level.SEVERE)) {
				getErrStream().print(msg);
			} else {
				getNonErrStream().print(msg);
			}
		}

	}

	@Override
	public void flush() {
		getErrStream().flush();
		getNonErrStream().flush();
	}

	@Override
	public void close() throws SecurityException {
		killStream(errStream);
		killStream(outStream);
		errStream = System.err;
		outStream = System.out;
	}

	/**
	 * Kills a PrintStream, first ensuring that it is not currently assigned to
	 * System.out or System.err.
	 *
	 * @param printStream
	 */
	protected void killStream(PrintStream printStream) {
		if (printStream != null && !System.err.equals(printStream) && !System.out.equals(printStream)) {
			printStream.close();
		}
	}

	//
	//Methods below were copied w/ some small changes from LogManager to bypass
	//package protected access restrictions.

	// Package private method to get a Level property.
	// If the property is not defined or cannot be parsed
	// we return the given default value.
	Level getLevelProperty(String name, Level defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		if (val == null) {
			return defaultValue;
		}
		Level l = Level.parse(val.trim());
		return l != null ? l : defaultValue;
	}

	// Package private method to get a filter property.
	// We return an instance of the class named by the "name"
	// property. If the property is not defined or has problems
	// we return the defaultValue.
	Filter getFilterProperty(String name, Filter defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Filter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception.  Return the defaultValue.
		return defaultValue;
	}


	// Package private method to get a formatter property.
	// We return an instance of the class named by the "name"
	// property. If the property is not defined or has problems
	// we return the defaultValue.
	Formatter getFormatterProperty(String name, Formatter defaultValue) {
		String val = LogManager.getLogManager().getProperty(name);
		try {
			if (val != null) {
				Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
				return (Formatter) clz.newInstance();
			}
		} catch (Exception ex) {
			// We got one of a variety of exceptions in creating the
			// class or creating an instance.
			// Drop through.
		}
		// We got an exception.  Return the defaultValue.
		return defaultValue;
	}


}
