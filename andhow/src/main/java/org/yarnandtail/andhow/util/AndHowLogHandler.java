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
	 * 
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
	 * 
	 * Setting to null will revert to the default non-error stream, which is System.out.
	 * @param outStream
	 */
	public void setNonErrStream(PrintStream outStream) {
		this.outStream = outStream;
	}
	
	
	
    /**
	 * Mostly copied from java.util.logging.ConsoleHandler
	 * 
	 */
    private void configure() {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        setLevel(getLevelProperty(cname +".level", Level.ALL));	//ie allow the Logger to do the level filtering
        setFilter(getFilterProperty(cname +".filter", null));
        setFormatter(getFormatterProperty(cname +".formatter", new SimpleFormatter()));
    }
	
	protected PrintStream getStreamForLevel(Level level) {
		if (level.intValue() >= Level.SEVERE.intValue()) {
			return getErrStream();
		} else {
			return getNonErrStream();
		}
	}
	
	@Override
	public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
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
		PrintStream ps = getStreamForLevel(record.getLevel());
		
		ps.println(msg);
		if (record.getThrown() != null) {
			record.getThrown().printStackTrace(ps);
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
		errStream = null;
		outStream = null;
	}
	
	/**
	 * Kills a PrintStream, first ensuring that it is not currently assigned to
	 * System.out or System.err.
	 * 
	 * @param printStream 
	 */
	protected void killStream(PrintStream printStream) {
		if (printStream != null && ! System.err.equals(printStream) && ! System.out.equals(printStream)) {
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
