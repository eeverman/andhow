package org.yarnandtail.andhow.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

class AndHowLogHandlerTest {

	AndHowLogHandler handler;
	ByteArrayOutputStream testErrByteArray;
	PrintStream testErrPrintStream;
	ByteArrayOutputStream testNonErrByteArray;
	PrintStream testNonErrPrintStream;

	@BeforeEach
	void setUp() {

		// Assign a test stream for err
		testErrByteArray = new ByteArrayOutputStream();
		testErrPrintStream = new PrintStream(testErrByteArray, true);

		// Assign a test stream for err
		testNonErrByteArray = new ByteArrayOutputStream();
		testNonErrPrintStream = new PrintStream(testNonErrByteArray, true);

		handler = new AndHowLogHandler(testErrPrintStream, testNonErrPrintStream);
	}

	@Test
	public void basics() {
		assertSame(testErrPrintStream, handler.getErrStream());
		assertSame(testNonErrPrintStream, handler.getNonErrStream());
		handler.setErrStream(null);
		handler.setNonErrStream(null);
		assertSame(System.err, handler.getErrStream());
		assertSame(System.out, handler.getNonErrStream());

		testErrPrintStream = mock(PrintStream.class);
		testNonErrPrintStream = mock(PrintStream.class);
		handler.setErrStream(testErrPrintStream);
		handler.setNonErrStream(testNonErrPrintStream);

		handler.flush();
		verify(testErrPrintStream, times(1)).flush();
		verify(testNonErrPrintStream, times(1)).flush();
		verify(testErrPrintStream, times(0)).close();
		verify(testNonErrPrintStream, times(0)).close();

		handler.close();
		verify(testErrPrintStream, times(1)).close();
		verify(testNonErrPrintStream, times(1)).close();
	}
	
	@Test
	public void testPublish_With_Default_Handler_Config() throws IOException {
		LogRecord logRecord;
		
		handler = new AndHowLogHandler();
		logRecord = new LogRecord(handler.getLevel(), AndHowLogHandler.MANDATORY_NOTE_PREFIX);
		handler.setNonErrStream(testNonErrPrintStream);
		handler.setErrStream(testErrPrintStream);		
		handler.publish(logRecord);
		assertFalse(testNonErrByteArray.toString().contains(AndHowLogHandler.MANDATORY_NOTE_PREFIX));
		assertEquals(0, testErrByteArray.toString().length());
		handler.flush();
		handler.close();
		
		setUp();
		handler = new AndHowLogHandler();
		logRecord = new LogRecord(handler.getLevel(), "TEST");
		handler.setNonErrStream(testNonErrPrintStream);
		handler.setErrStream(testErrPrintStream);		
		handler.publish(logRecord);	
		assertTrue(testNonErrByteArray.toString().contains("TEST"));
		assertEquals(0, testErrByteArray.toString().length());				
		handler.flush();
		handler.close();
		
		setUp();
		handler = new AndHowLogHandler();		
		logRecord = new LogRecord(Level.SEVERE, "TEST");
		handler.setNonErrStream(testNonErrPrintStream);
		handler.setErrStream(testErrPrintStream);		
		handler.publish(logRecord);
		assertNotNull(testErrByteArray.toString());
		assertTrue(testErrByteArray.toString().contains("SEVERE"));
		assertEquals(0, testNonErrByteArray.toString().length());		
		handler.flush();
		handler.close();		
	}
		
	@Test
	public void testPublish_Format_Failure() throws IOException {
		handler.setLevel(Level.ALL);
		final LogRecord logRecordWithoutNotePrefix = new LogRecord(handler.getLevel(), "TEST");
		assertDoesNotThrow(() -> {
			handler.publish(logRecordWithoutNotePrefix);
		});
		assertEquals(0, testErrByteArray.toString().length());
		assertEquals(0, testNonErrByteArray.toString().length());
		handler.flush();
		handler.close();		
		
		setUp();
		handler.setLevel(Level.ALL);
		final LogRecord logRecordWithNotePrefix = new LogRecord(handler.getLevel(), AndHowLogHandler.MANDATORY_NOTE_PREFIX);
		assertDoesNotThrow(() -> {
			handler.publish(logRecordWithNotePrefix);
		});
		assertEquals(0, testErrByteArray.toString().length());
		assertEquals(0, testNonErrByteArray.toString().length());		
		handler.flush();
		handler.close();		
	}
	
	@Test
	public void testPublish_With_Log_Level_Off() {
		handler.setLevel(Level.OFF);
		LogRecord logRecord = new LogRecord(Level.OFF, "TEST");
		handler.publish(logRecord);
		assertFalse(handler.isLoggable(logRecord));
		assertEquals(0, testErrByteArray.toString().length());
		assertEquals(0, testNonErrByteArray.toString().length());		
		handler.flush();
		handler.close();
	}
	
	@Test
	public void testLog_Properties() throws IOException {
		Filter filter = new Filter() {			
			@Override
			public boolean isLoggable(LogRecord record) {
				return false;
			}
		};
		Formatter formatter = new AndHowLogFormatter();
		LogRecord logRecord = new LogRecord(handler.getLevel(), AndHowLogHandler.MANDATORY_NOTE_PREFIX);		
		
		assertEquals(Level.SEVERE, handler.getLevelProperty(".mockLevel", Level.SEVERE));
		assertEquals(Level.INFO, handler.getLevelProperty(".level", Level.SEVERE));
				
		assertEquals(false, handler.getFilterProperty(".filter", filter).isLoggable(logRecord));
		assertEquals(filter, handler.getFilterProperty("handlers", filter));
				
		assertEquals(formatter, handler.getFormatterProperty(".formatter", formatter));
		assertEquals(formatter, handler.getFormatterProperty("handlers", formatter));		
		assertEquals(new XMLFormatter().format(logRecord), handler.getFormatterProperty("java.util.logging.FileHandler.formatter", formatter).format(logRecord));
		
		handler.flush();
		handler.close();		
	}		
}