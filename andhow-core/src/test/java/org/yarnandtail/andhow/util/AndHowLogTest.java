/*
 */
package org.yarnandtail.andhow.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.RestoreSysPropsAfterThisTest;

import static org.junit.jupiter.api.Assertions.*;

public class AndHowLogTest {

	private static AndHowLog log = AndHowLog.getLogger(AndHowLogTest.class);

	ByteArrayOutputStream testErrByteArray;
	PrintStream testErrPrintStream;
	ByteArrayOutputStream testNonErrByteArray;
	PrintStream testNonErrPrintStream;

	public AndHowLogTest() {
	}

	@BeforeEach
	public void setUp() {

		AndHowLogHandler handler = (AndHowLogHandler) log.getHandlers()[0];

		// Assign a test stream for err
		testErrByteArray = new ByteArrayOutputStream();
		testErrPrintStream = new PrintStream(testErrByteArray, true);
		handler.setErrStream(testErrPrintStream);

		// Assign a test stream for err
		testNonErrByteArray = new ByteArrayOutputStream();
		testNonErrPrintStream = new PrintStream(testNonErrByteArray, true);
		handler.setNonErrStream(testNonErrPrintStream);

		// Reset the log level
		log.setLevel(null);
	}

	@AfterEach
	public void tearDown() {

		AndHowLogHandler handler = (AndHowLogHandler) log.getHandlers()[0];

		handler.setErrStream(null);
		handler.setNonErrStream(null);

	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTest() {
		assertTrue(log.getHandlers()[0] instanceof AndHowLogHandler);
		assertEquals(1, log.getHandlers().length);
	}

	@Test
	public void testMandatoryNoteWithReplacement() {
		log.setLevel(Level.SEVERE);
		log.mandatoryNote("A Str: {0} A number: {1} A \"str\" again: {0}", "X", 42);

		String pattern = ".*A Str: X A number: 42 A \"str\" again: X.*\\s+";
		MatcherAssert.assertThat(testNonErrByteArray.toString(), Matchers.matchesPattern(pattern));
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	@RestoreSysPropsAfterThisTest
	public void testTrace_String() {
		String sampleMsg = "This is a fact you don't care about";

		log.trace(sampleMsg);
		assertEquals(0, testNonErrByteArray.toString().length(), "trace is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(Level.FINEST);
		log.trace(sampleMsg);
		assertTrue(testNonErrByteArray.toString().contains(sampleMsg));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		// Complete reset
		tearDown();
		setUp();

		//
		// Set the log level to info and then trace (finest) via sys props
		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.INFO.getName());
		log.setLevel(null);

		log.trace(sampleMsg);
		assertEquals(0, testNonErrByteArray.toString().length(), "trace is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.FINEST.getName());
		log.setLevel(null);
		log.trace(sampleMsg);
		assertTrue(testNonErrByteArray.toString().contains(sampleMsg));
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	@RestoreSysPropsAfterThisTest
	public void testTrace_String_ObjectArr() {
		log.trace("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "trace is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(Level.FINEST);
		log.trace("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some debug on line 42, message 'Bee Boo'"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		// Complete reset
		tearDown();
		setUp();

		//
		// Set the log level to info and then trace (finest) via sys props
		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.INFO.getName());
		log.setLevel(null);

		log.trace("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "trace is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.FINEST.getName());
		log.setLevel(null);
		log.trace("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some debug on line 42, message 'Bee Boo'"));
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTrace_String_Throwable() {
		Exception ex = new Exception("ALERT!");
		log.trace("It is nothing serious", ex);
		assertEquals(0, testNonErrByteArray.toString().length(), "trace is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
		log.setLevel(Level.FINEST);
		log.trace("It is nothing serious", ex);
		assertTrue(testNonErrByteArray.toString().contains("It is nothing serious"));
		assertTrue(testNonErrByteArray.toString().contains("ALERT!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String() {
		log.debug("Hello!!!!");
		assertEquals(0, testNonErrByteArray.toString().length(), "debug is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(Level.FINE);
		log.debug("Hello!!!!");
		assertTrue(testNonErrByteArray.toString().contains("Hello!!!!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		// Complete reset
		tearDown();
		setUp();
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String_ObjectArr() {
		log.debug("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "debug is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(Level.FINE);
		log.debug("Some debug on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some debug on line 42, message 'Bee Boo'"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
		// Complete reset
		tearDown();
		setUp();
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String_Throwable() {
		Exception ex = new Exception("ALERT!");
		log.debug("It is nothing serious", ex);
		assertEquals(0, testNonErrByteArray.toString().length(), "debug is off by default");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(Level.FINE);
		log.debug("It is nothing serious", ex);
		assertTrue(testNonErrByteArray.toString().contains("It is nothing serious"));
		assertTrue(testNonErrByteArray.toString().contains("ALERT!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
		// Complete reset
		tearDown();
		setUp();
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String() {
		log.setLevel(Level.OFF);
		log.info("Hello!!!!");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(null);
		log.info("Hello!!!!");
		assertTrue(testNonErrByteArray.toString().contains("Hello!!!!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String_ObjectArr() {
		log.setLevel(Level.OFF);
		log.info("Some info on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(null);
		log.info("Some info on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some info on line 42, message 'Bee Boo'"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String_Throwable() {
		Exception ex = new Exception("ALERT!");
		log.setLevel(Level.OFF);
		log.info("It is nothing serious", ex);
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

		log.setLevel(null);
		log.info("It is nothing serious", ex);
		assertTrue(testNonErrByteArray.toString().contains("It is nothing serious"));
		assertTrue(testNonErrByteArray.toString().contains("ALERT!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String() {
		log.setLevel(Level.OFF);
		log.warn("Hello!!!!");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	
		log.setLevel(null);
		log.warn("Hello!!!!");
		assertTrue(testNonErrByteArray.toString().contains("Hello!!!!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String_ObjectArr() {
		log.setLevel(Level.OFF);
		log.warn("Some info on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	
		log.setLevel(null);
		log.warn("Some info on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some info on line 42, message 'Bee Boo'"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String_Throwable() {
		Exception ex = new Exception("ALERT!");

		log.setLevel(Level.OFF);
		log.warn("It cane be serious", ex);
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	
		log.setLevel(null);
		
		log.warn("It cane be serious", ex);
		assertTrue(testNonErrByteArray.toString().contains("It cane be serious"));
		assertTrue(testNonErrByteArray.toString().contains("ALERT!"));
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");

	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String() {
		log.setLevel(Level.OFF);
		log.error("Oh-No!!!!");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	
		log.setLevel(null);
		log.error("Oh-No!!!!");
		assertTrue(testErrByteArray.toString().contains("Oh-No!!!!"));
		assertEquals(0, testNonErrByteArray.toString().length(), "nonErr stream should be empty");
	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String_ObjectArr() {
		log.setLevel(Level.OFF);
		log.error("Big err on line {0}, message ''{1}''", "42", "Bee Boo");
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
	
		log.setLevel(null);
		log.error("Big err on line {0}, message ''{1}''", "42", "Bee Boo");
		assertTrue(testErrByteArray.toString().contains("Big err on line 42, message 'Bee Boo'"));
		assertEquals(0, testNonErrByteArray.toString().length(), "nonErr stream should be empty");
	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String_Throwable() {
		log.setLevel(Level.OFF);
		Exception ex = new Exception("ALERT!");
		log.error("Big Error!", ex);
		assertEquals(0, testNonErrByteArray.toString().length(), "Logging is off now");
		assertEquals(0, testErrByteArray.toString().length(), "err stream should be empty");
		
		log.setLevel(null);
		log.error("Big Error!", ex);
		assertTrue(testErrByteArray.toString().contains("Big Error!"));
		assertTrue(testErrByteArray.toString().contains("ALERT!"));
		assertEquals(0, testNonErrByteArray.toString().length(), "nonErr stream should be empty");
	}

}
