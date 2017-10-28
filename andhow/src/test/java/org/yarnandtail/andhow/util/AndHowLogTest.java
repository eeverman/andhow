/*
 */
package org.yarnandtail.andhow.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import org.junit.*;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class AndHowLogTest {
	
	private static AndHowLog log = new AndHowLog(AndHowLogTest.class);
	

	ByteArrayOutputStream testErrByteArray;
	PrintStream testErrPrintStream;
	ByteArrayOutputStream testNonErrByteArray;
	PrintStream testNonErrPrintStream;
	
	public AndHowLogTest() {
	}
	
	
	
	@Before
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
	}
	
	@After
	public void tearDown() {
		
		AndHowLogHandler handler = (AndHowLogHandler) log.getHandlers()[0];
		
		handler.setErrStream(null);
		handler.setNonErrStream(null);
		
		//Reset the log level
		System.clearProperty(AndHowLogTest.class.getCanonicalName() + ".level");
		log.setLevel(null);
	}
	
	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTest() {
		assertTrue(log.getHandlers()[0] instanceof AndHowLogHandler);
		assertEquals(1, log.getHandlers().length);
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTrace_String() {
		String sampleMsg = "This is a fact you don't care about";
		
		log.trace(sampleMsg);
		assertEquals("trace is off by default", 0, testNonErrByteArray.toString().length());
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
		
		log.setLevel(Level.FINEST);
		log.trace(sampleMsg);
		assertTrue(testNonErrByteArray.toString().contains(sampleMsg));
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
		
		//Complete reset
		tearDown();
		setUp();
		
		//
		//Set the log level to info and then trace (finest) via sys props
		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.INFO.getName());
		log.setLevel(null);
		
		log.trace(sampleMsg);
		assertEquals("trace is off by default", 0, testNonErrByteArray.toString().length());
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
		
		System.setProperty(AndHowLogTest.class.getCanonicalName() + ".level", Level.FINEST.getName());
		log.setLevel(null);
		log.trace(sampleMsg);
		assertTrue(testNonErrByteArray.toString().contains(sampleMsg));
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTrace_String_ObjectArr() {
	}

	/**
	 * Test of trace method, of class AndHowLog.
	 */
	@Test
	public void testTrace_String_Throwable() {
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String() {
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String_ObjectArr() {
	}

	/**
	 * Test of debug method, of class AndHowLog.
	 */
	@Test
	public void testDebug_String_Throwable() {
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String() {
		log.info("Hello!!!!");
		assertTrue(testNonErrByteArray.toString().contains("Hello!!!!"));
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String_ObjectArr() {
		log.info("Some info on line {}, message '{}'", "42", "Bee Boo");
		assertTrue(testNonErrByteArray.toString().contains("Some info on line 42, message 'Bee Boo'"));
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
	}

	/**
	 * Test of info method, of class AndHowLog.
	 */
	@Test
	public void testInfo_String_Throwable() {
		Exception ex = new Exception("ALERT!");
		
		log.info("It is nothing serious", ex);
		assertTrue(testNonErrByteArray.toString().contains("It is nothing serious"));
		assertTrue(testNonErrByteArray.toString().contains("ALERT!"));
		assertEquals("err stream should be empty", 0, testErrByteArray.toString().length());
	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String() {
	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String_ObjectArr() {
	}

	/**
	 * Test of warn method, of class AndHowLog.
	 */
	@Test
	public void testWarn_String_Throwable() {
	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String() {
		log.error("Oh-No!!!!");
		assertTrue(testErrByteArray.toString().contains("Oh-No!!!!"));
		assertEquals("nonErr stream should be empty", 0, testNonErrByteArray.toString().length());
	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String_ObjectArr() {
		log.error("Big err on line {}, message '{}'", "42", "Bee Boo");
		assertTrue(testErrByteArray.toString().contains("Big err on line 42, message 'Bee Boo'"));
		assertEquals("nonErr stream should be empty", 0, testNonErrByteArray.toString().length());
	}

	/**
	 * Test of error method, of class AndHowLog.
	 */
	@Test
	public void testError_String_Throwable() {
		Exception ex = new Exception("ALERT!");
		
		log.error("Big Error!", ex);
		assertTrue(testErrByteArray.toString().contains("Big Error!"));
		assertTrue(testErrByteArray.toString().contains("ALERT!"));
		assertEquals("nonErr stream should be empty", 0, testNonErrByteArray.toString().length());
	}
	
}
