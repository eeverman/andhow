package org.yarnandtail.andhow.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Formats LogRecords into a string to be displayed (typically on the console).
 * <p>
 * This implementation is based on {@link java.util.logging.SimpleFormatter} with some changes to
 * use a custom format that varies based on the log level.  The 'severe' level prints text in red.
 */
public class AndHowLogFormatter extends Formatter {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_CYAN = "\u001b[36m";

	private static final String mandatoryNoteFormat = ANSI_CYAN + "%1$tF %1$tT | %4$s | %2$s | %5$s%6$s" + ANSI_RESET + "%n";
	private static final String normalFormat = "%1$tF %1$tT | %4$s | %2$s | %5$s%6$s%n";
	private static final String severeFormat = ANSI_RED + "%1$tF %1$tT | %4$s | %2$s | %5$s%6$s" + ANSI_RESET + "%n";
	private final Date date = new Date();

	/**
	 * Format a LogRecord into a string to be displayed (typically on the console).
	 * <p>
	 * This implementation is based on {@link java.util.logging.SimpleFormatter} with some changes to
	 * use a custom format that varies based on the log level.  The 'severe' level prints text in red.
	 * <p>
	 * @param record the log record to be formatted.
	 * @return The resulting string, which may be multi-line for stack traces
	 */
	public String format(LogRecord record) {
		date.setTime(record.getMillis());
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
					date,
					source,
					record.getLoggerName(),
					record.getLevel().getLocalizedName(),
					message,
					throwable);
		} else if (message.startsWith(AndHowLogHandler.MANDATORY_NOTE_PREFIX)) {
			return String.format(mandatoryNoteFormat,
					date,
					source,
					record.getLoggerName(),
					Level.INFO,
					message.substring(AndHowLogHandler.MANDATORY_NOTE_PREFIX.length()),
					throwable);
		} else {
			return String.format(severeFormat,
					date,
					source,
					record.getLoggerName(),
					record.getLevel().getLocalizedName(),
					message,
					throwable);
		}
	}
}
