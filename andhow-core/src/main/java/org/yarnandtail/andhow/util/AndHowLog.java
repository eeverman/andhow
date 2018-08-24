package org.yarnandtail.andhow.util;

import java.util.Arrays;
import java.util.logging.*;
import java.util.logging.Logger;

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
public class AndHowLog {

	private static final AndHowLogHandler DEFAULT_HANDLER = new AndHowLogHandler();
	private static final String CN = AndHowLog.class.getCanonicalName();

	private final Class<?> clazz;
	private final Logger baseLogger;
	
	public static AndHowLog getLogger(Class<?> clazz) {
		Logger baseLog = Logger.getLogger(clazz.getCanonicalName());
		return new AndHowLog(baseLog, clazz);
	}
	
	public static AndHowLog getLogger(Class<?> clazz, Handler handler) {
		Logger baseLog = Logger.getLogger(clazz.getCanonicalName());
		return new AndHowLog(baseLog, clazz, handler);
	}
	
	private AndHowLog(Logger baseLog, Class<?> clazz) {
		this(baseLog, clazz, null);
	}

	private AndHowLog(Logger baseLog, Class<?> clazz, Handler handler) {
		baseLogger = baseLog;
		baseLogger.setUseParentHandlers(false);	//Don't double print messages
		
		if (handler == null) {
			handler = DEFAULT_HANDLER;
		}
		
		Handler[] orgHandlers = baseLogger.getHandlers();
		
		//Make sure only have our single handler in place
		for (Handler h : orgHandlers) {
			baseLogger.removeHandler(h);
		}
		
		baseLogger.addHandler(handler);

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
	public void setLevel(Level newLevel) throws SecurityException {

		if (newLevel != null) {
			baseLogger.setLevel(newLevel);
		} else {
			reloadLogLevel();
		}
	}
	
	public Handler[] getHandlers() {
		return baseLogger.getHandlers();
	}
	
	public void addHandler(Handler handler) {
		baseLogger.addHandler(handler);
	}
	
	public void removeHandler(Handler handler) {
		baseLogger.removeHandler(handler);
	}
	
   /**
     * Check if a message of the given level would actually be logged
     * by this logger.  This check is based on the Loggers effective level,
     * which may be inherited from its parent.
     *
     * @param   level   a message logging level
     * @return  true if the given message level is currently being logged.
     */
    public boolean isLoggable(Level level) {
        return baseLogger.isLoggable(level);
    }

	private void reloadLogLevel() {
		String levelProp = clazz.getCanonicalName() + ".level";
		String levelStr = System.getProperty(levelProp);

		if (levelStr != null) {
			baseLogger.setLevel(Level.parse(levelStr));
		} else {
			baseLogger.setLevel(null);
		}
	}

	//
	//Trace
	public void trace(String msg) {
		if (baseLogger.isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void trace(String format, Object... arguments) {
		if (baseLogger.isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void trace(String msg, Throwable t) {
		if (baseLogger.isLoggable(Level.FINEST)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINEST, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Debug
	public void debug(String msg) {
		if (baseLogger.isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void debug(String format, Object... arguments) {
		if (baseLogger.isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void debug(String msg, Throwable t) {
		if (baseLogger.isLoggable(Level.FINE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.FINE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Info
	public void info(String msg) {
		if (baseLogger.isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void info(String format, Object... arguments) {
		if (baseLogger.isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void info(String msg, Throwable t) {
		if (baseLogger.isLoggable(Level.INFO)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.INFO, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Warn
	public void warn(String msg) {
		if (baseLogger.isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void warn(String format, Object... arguments) {
		if (baseLogger.isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void warn(String msg, Throwable t) {
		if (baseLogger.isLoggable(Level.WARNING)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.WARNING, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

	//
	//Error
	public void error(String msg) {
		if (baseLogger.isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg);
		}
	}

	public void error(String format, Object... arguments) {
		if (baseLogger.isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), format, arguments);
		}
	}

	public void error(String msg, Throwable t) {
		if (baseLogger.isLoggable(Level.SEVERE)) {
			StackTraceElement ste = StackLocator.calcLocation(CN);
			baseLogger.logp(Level.SEVERE, ste.getClassName(), ste.getMethodName() + ":" + ste.getLineNumber(), msg, t);
		}
	}

}
