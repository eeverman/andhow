package org.yarnandtail.andhow.util;

import java.util.logging.*;

/**
 * A simple wrapper around the java.util.Logging utility that makes it behave
 * like SLF4J, including more use of standard message formatting.
 *
 * Methods that take Object... arguments use curly brace replacement, ie:<br>
 * <code>debug("Hello {}, Welcome to logging", "Carl");</code>, <br>
 * Logs: <code>Hello Carl, Welcome to logging</code><br>
 *
 * See https://www.slf4j.org/apidocs/index.html for complete examples.
 *
 * Configuration of logging levels happens can be done in three ways:
 * <ul>
 * <li>Explicitly calling setLevel() on a AndHowLog instance
 * <li>Setting a System property of the form [canonical name of class being
 * logged].level=[Level name]|Level number]. Note that these System properties
 * are read at the time of construction. If modified for a particular logger,
 * reloadLogLevel() must be called on the AndHowLog to pick up the change.
 * <li>Finally, the file based mechanism used by java.util.logging.LogManager is
 * used, however, this mechanism is impractical for most application level
 * logging configurations.
 * </ul>
 *
 * @author ericeverman
 */
public class AndHowLog extends java.util.logging.Logger {

	private static final AndHowLogHandler DEFAULT_HANDLER = new AndHowLogHandler();
	private static final String CN = AndHowLog.class.getCanonicalName();

	private final Class<?> clazz;

	public AndHowLog(Class<?> clazz) {
		super(clazz.getCanonicalName(), null);

		super.addHandler(DEFAULT_HANDLER);

		this.clazz = clazz;
		reloadLogLevel();
	}

	public AndHowLog(Class<?> clazz, Handler handler) {
		super(clazz.getCanonicalName(), null);

		super.addHandler(handler);

		this.clazz = clazz;
		reloadLogLevel();
	}

	/**
	 * Assigns a log level.
	 *
	 * This AndHowLog accepts System properties to assign a log level.
	 * Explicitly setting a null Level will re-read the level specified in Sys
	 * props, or if non, default to the java.util.AndHowLog interpretation of
	 * null.
	 *
	 * @param newLevel
	 * @throws SecurityException
	 */
	@Override
	public void setLevel(Level newLevel) throws SecurityException {

		if (newLevel != null) {
			super.setLevel(newLevel);
		} else {
			reloadLogLevel();
		}
	}

	private void reloadLogLevel() {
		String levelProp = clazz.getCanonicalName() + ".level";
		String levelStr = System.getProperty(levelProp);

		if (levelStr != null) {
			super.setLevel(Level.parse(levelStr));
		} else {
			super.setLevel(null);
		}
	}

	//
	//Trace
	public void trace(String msg) {
		if (isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void trace(String format, Object... arguments) {
		if (isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void trace(String msg, Throwable t) {
		if (isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Debug
	public void debug(String msg) {
		if (isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void debug(String format, Object... arguments) {
		if (isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void debug(String msg, Throwable t) {
		if (isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Info
	public void info(String msg) {
		if (isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void info(String format, Object... arguments) {
		if (isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void info(String msg, Throwable t) {
		if (isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Warn
	public void warn(String msg) {
		if (isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void warn(String format, Object... arguments) {
		if (isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void warn(String msg, Throwable t) {
		if (isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Error
	public void error(String msg) {
		if (isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void error(String format, Object... arguments) {
		if (isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void error(String msg, Throwable t) {
		if (isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.getInstance().calcLocation(CN);
			super.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

}
