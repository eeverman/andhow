package org.yarnandtail.andhow.util;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class AndHowLogFormatterTest {

	public static final String ANSI_RED_REGEX = "\\u001B\\[31m";
	public static final String ANSI_CYAN_REGEX = "\\u001b\\[36m";
	public static final String ANSI_RESET_REGEX = "\\u001B\\[0m";

	AndHowLogFormatter formatter;
	LogRecord record;

	//July 20, 1969, at 20:17 UTC
	GregorianCalendar calDate = new GregorianCalendar(1969, 6, 20, 20, 17, 00);

	@BeforeEach
	void setUp() {
		formatter = new AndHowLogFormatter();

	}

	@Test
	public void infoFormatShouldHaveNoColorAndDisplaySourceClassNameIfProvided() {
		record = new LogRecord(Level.INFO, "My Info Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		record.setSourceClassName("SrcClass");
		record.setLoggerName("MyLoggerName");

		String pattern = "1969-07-20 20:17:00 \\| INFO \\| SrcClass \\| My Info Msg A:A 1:1 2:2 Again:A\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	@Test
	public void infoFormatShouldDisplayLoggerNameIfNoClassNameProvided() {
		record = new LogRecord(Level.INFO, "My Info Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		//record.setSourceClassName("SrcClass");
		record.setLoggerName("MyLoggerName");

		String pattern = "1969-07-20 20:17:00 \\| INFO \\| MyLoggerName \\| My Info Msg A:A 1:1 2:2 Again:A\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	@Test
	public void infoFormatShouldHaveNullWhenNoSourceAtAllProvided() {
		record = new LogRecord(Level.INFO, "My Info Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		//record.setSourceClassName("SrcClass");
		//record.setLoggerName("MyLoggerName");

		String pattern = "1969-07-20 20:17:00 \\| INFO \\| null \\| My Info Msg A:A 1:1 2:2 Again:A\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	@Test
	public void infoFormatShouldIncludeStackTraceWhenThrowableProvided() {
		record = new LogRecord(Level.INFO, "My Info Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		record.setSourceClassName("SrcClass");
		record.setThrown(new TestThrow());

		String pattern =
				"1969-07-20 20:17:00 \\| INFO \\| SrcClass \\| My Info Msg A:A 1:1 2:2 Again:A" +
				"\\s+Line1\\s+Line2\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	@Test
	public void severeShouldUseRedAroundMessage() {
		record = new LogRecord(Level.SEVERE, "My Error Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		record.setSourceClassName("SrcClass");
		record.setThrown(new TestThrow());

		String pattern = ANSI_RED_REGEX +
				"1969-07-20 20:17:00 \\| SEVERE \\| SrcClass \\| My Error Msg A:A 1:1 2:2 Again:A" +
				"\\s+Line1\\s+Line2\\s+" + ANSI_RESET_REGEX + "\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	@Test
	public void manditoryNoteShouldUseCyanAndAlwaysBeInfoLevel() {
		record = new LogRecord(Level.SEVERE, "!!Important Note!! My Note Msg A:{0} 1:{1} 2:{2} Again:{0}");
		record.setParameters(new Object[] {"A", 1, 2.0f});
		record.setMillis(calDate.getTime().getTime());
		record.setSourceClassName("SrcClass");
		record.setThrown(new TestThrow());

		String pattern = ANSI_CYAN_REGEX +
												 "1969-07-20 20:17:00 \\| INFO \\| SrcClass \\| My Note Msg A:A 1:1 2:2 Again:A" +
												 "\\s+Line1\\s+Line2\\s+" + ANSI_RESET_REGEX + "\\s+$";
		MatcherAssert.assertThat(formatter.format(record), Matchers.matchesPattern(pattern));
	}

	public static class TestThrow extends Throwable {
		@Override
		public void printStackTrace(PrintWriter s) {
			s.println("Line1");
			s.println("Line2");
		}
	}
}