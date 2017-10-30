package org.yarnandtail.andhow.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author ericeverman
 */
public class AndHowLogFormatter extends Formatter{
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";

    private static final String normalFormat = "%1$tF %1$tT | %4$s | %2$s | %5$s%6$s%n";
    private static final String severeFormat = ANSI_RED + "%1$tF %1$tT | %4$s | %2$s | %5$s%6$s%n" + ANSI_RESET;
    private final Date dat = new Date();

    /**
     * Format the given LogRecord.
     * <p>
     * The formatting can be customized by specifying the
     * <a href="../Formatter.html#syntax">severeFormat string</a>
     * in the <a href="#formatting">
     * {@code java.util.logging.SimpleFormatter.severeFormat}</a> property.
     * The given {@code LogRecord} will be formatted as if by calling:
     * <pre>
     *    {@link String#format String.severeFormat}(severeFormat, date, source, logger, level, message, thrown);
 </pre>
     * where the arguments are:<br>
     * <ol>
     * <li>{@code severeFormat} - the {@link java.util.Formatter
     *     java.util.Formatter} severeFormat string specified in the
     {@code java.util.logging.SimpleFormatter.severeFormat} property
     or the default severeFormat.</li>
     * <li>{@code date} - a {@link Date} object representing
     *     {@linkplain LogRecord#getMillis event time} of the log record.</li>
     * <li>{@code source} - a string representing the caller, if available;
     *     otherwise, the logger's name.</li>
     * <li>{@code logger} - the logger's name.</li>
     * <li>{@code level} - the {@linkplain Level#getLocalizedName
     *     log level}.</li>
     * <li>{@code message} - the formatted log message
     *     returned from the {@link Formatter#formatMessage(LogRecord)}
     *     method.  It uses {@link java.text.MessageFormat java.text}
     *     formatting and does not use the {@code java.util.Formatter
     severeFormat} argument.</li>
     * <li>{@code thrown} - a string representing
     *     the {@linkplain LogRecord#getThrown throwable}
     *     associated with the log record and its backtrace
     *     beginning with a newline character, if any;
     *     otherwise, an empty string.</li>
     * </ol>
     *
     * <p>Some example formats:<br>
     * <ul>
     * <li> {@code java.util.logging.SimpleFormatter.severeFormat="%4$s: %5$s [%1$tc]%n"}
     *     <p>This prints 1 line with the log level ({@code 4$}),
     *     the log message ({@code 5$}) and the timestamp ({@code 1$}) in
     *     a square bracket.
     *     <pre>
     *     WARNING: warning message [Tue Mar 22 13:11:31 PDT 2011]
     *     </pre></li>
     * <li> {@code java.util.logging.SimpleFormatter.severeFormat="%1$tc %2$s%n%4$s: %5$s%6$s%n"}
     *     <p>This prints 2 lines where the first line includes
     *     the timestamp ({@code 1$}) and the source ({@code 2$});
     *     the second line includes the log level ({@code 4$}) and
     *     the log message ({@code 5$}) followed with the throwable
     *     and its backtrace ({@code 6$}), if any:
     *     <pre>
     *     Tue Mar 22 13:11:31 PDT 2011 MyClass fatal
     *     SEVERE: several message with an exception
     *     java.lang.IllegalArgumentException: invalid argument
     *             at MyClass.mash(MyClass.java:9)
     *             at MyClass.crunch(MyClass.java:6)
     *             at MyClass.main(MyClass.java:3)
     *     </pre></li>
     * <li> {@code java.util.logging.SimpleFormatter.severeFormat="%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%n"}
     *      <p>This prints 2 lines similar to the example above
     *         with a different date/time formatting and does not print
     *         the throwable and its backtrace:
     *     <pre>
     *     Mar 22, 2011 1:11:31 PM MyClass fatal
     *     SEVERE: several message with an exception
     *     </pre></li>
     * </ul>
     * <p>This method can also be overridden in a subclass.
     * It is recommended to use the {@link Formatter#formatMessage}
 convenience method to localize and severeFormat the message field.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
		
		if (record.getLevel().intValue() < Level.SEVERE.intValue()) {
			return String.format(normalFormat,
					dat,
					source,
					record.getLoggerName(),
					record.getLevel().getLocalizedName(),
					message,
					throwable);
		} else {
			return String.format(severeFormat,
					dat,
					source,
					record.getLoggerName(),
					record.getLevel().getLocalizedName(),
					message,
					throwable);
		}
    }
}
