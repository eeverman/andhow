package org.yarnandtail.andhow.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
}