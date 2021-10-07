package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.AndHowTestInit;
import org.yarnandtail.andhow.compile.AndHowCompileProcessor.CauseEffect;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.yarnandtail.andhow.service.PropertyRegistrar;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class AndHowCompileProcessorTest {

	//A 'spied' processor that does not attempt to write files
	AndHowCompileProcessor noWriteProcessor;

	// Pieces of the AndHowCompileProcessor
	Calendar runDate;
	List<CauseEffect> registrars = new ArrayList<>();
	List<CauseEffect> initClasses = new ArrayList<>();
	List<CauseEffect> testInitClasses = new ArrayList<>();
	List<CompileProblem> problems = new ArrayList<>();

	// The PE and its return values
	ProcessingEnvironment pe;
	Filer filer;
	Messager log;

	// Internals of Filer
	FileObject filerFileObject;
	StringWriter filerWriter;

	// Some reusable values
	CauseEffect causeEffect1;
	CauseEffect causeEffect2;

	@BeforeEach
	void setUp() throws Exception {
		log = Mockito.mock(Messager.class);

		filer = Mockito.mock(Filer.class);
		filerFileObject = Mockito.mock(FileObject.class);
		filerWriter = new StringWriter();
		Mockito.when(filer.createResource(any(), any(), any(), any())).thenReturn(filerFileObject);
		Mockito.when(filerFileObject.openWriter()).thenReturn(filerWriter);



		pe = Mockito.mock(ProcessingEnvironment.class);
		Mockito.when(pe.getSourceVersion()).thenReturn(SourceVersion.RELEASE_8);
		Mockito.when(pe.getMessager()).thenReturn(log);

		noWriteProcessor = new AndHowCompileProcessor();
		noWriteProcessor = Mockito.spy(noWriteProcessor);
		Mockito.doNothing().when(noWriteProcessor).writeServiceFile(any(), any(), any());
		Mockito.doNothing().when(noWriteProcessor).writeClassFile(any(), any(), any());


		runDate = new GregorianCalendar();
		registrars = new ArrayList<>();
		initClasses = new ArrayList<>();
		testInitClasses = new ArrayList<>();
		problems = new ArrayList<>();

		causeEffect1 = new CauseEffect("com.bigcorp.MyClass1", Mockito.mock(Element.class));
		causeEffect2 = new CauseEffect("com.bigcorp.MyClass2", Mockito.mock(Element.class));

	}

	@Test
	void someBasicTests() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();
		acp.init(pe);
		assertEquals(8, acp.getSrcVersion());
		assertEquals(CompileUtil.getMajorJavaVersion(), acp.getJdkVersion());
		assertEquals(SourceVersion.latestSupported(), acp.getSupportedSourceVersion());

	}

	@Test
	void testDebugLogging() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();
		acp.debug(log, "My message part {} and {}", 1, "B");

		verify(log, times(1)).
				printMessage(Kind.NOTE, "AndHowCompileProcessor: My message part 1 and B");
	}

	@Test
	void testWarnLogging() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();
		acp.warn(log, "My message part {} and {}", 1, "B");

		verify(log, times(1)).
				printMessage(Kind.MANDATORY_WARNING, "AndHowCompileProcessor: My message part 1 and B");
	}

	@Test
	void testErrorLogging() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();
		acp.error(log, "My message part {} and {}", 1, "B");

		verify(log, times(1)).
				printMessage(Kind.ERROR, "AndHowCompileProcessor: My message part 1 and B");
	}

	@Test
	void causeEffectClassTest() {
		Element e = Mockito.mock(Element.class);
		AndHowCompileProcessor.CauseEffect ce =
				new AndHowCompileProcessor.CauseEffect("a", e);

		assertEquals("a", ce.fullClassName);
		assertSame(e, ce.causeElement);
	}

	@Test
	void processLastRoundWithNoClassesFound() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();

		acp.processLastRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 8");
		logCalls.verifyNoMoreInteractions();
	}

	@Test
	void processLastRoundIndeterminateVersionsWarningDoesntHappenWhenNoRegistrars() {
		AndHowCompileProcessor acp = new AndHowCompileProcessor();

		acp.processLastRound(filer, log, 8, 9,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 9");
		logCalls.verifyNoMoreInteractions();
	}

	@Test
	void processLastRoundIndeterminateVersionsWarningDoesHappenWithRegistrars() throws Exception {

		registrars.add(causeEffect1);

		noWriteProcessor.processLastRound(filer, log, 8, 9,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 9");
		logCalls.verify(log).printMessage(
				ArgumentMatchers.eq(Kind.MANDATORY_WARNING),
				ArgumentMatchers.matches("AndHowCompileProcessor: The source level is JDK8:.*current JDK is 9.*"));
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor, times(1)).writeServiceFile(
				filer, PropertyRegistrar.class.getCanonicalName(), registrars);
		processorCalls.verifyNoMoreInteractions();

		//This doesn't happen in the final round
//		Mockito.verify(noWriteProcessor, times(1))
//				.writeClassFile(eq(filer), any(), eq(causeEffect1.causeElement));
	}

	@Test
	void processLastRoundCallsWriteServiceFileForSingleInitClass() throws IOException {

		initClasses.add(causeEffect1);

		noWriteProcessor.processLastRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 8");
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found exactly 1 org.yarnandtail.andhow.AndHowInit class: com.bigcorp.MyClass1");
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).writeServiceFile(filer, AndHowInit.class.getCanonicalName(), initClasses);
		processorCalls.verifyNoMoreInteractions();
	}

	@Test
	void processLastRoundCreatesErrorIfTwoInitClass() throws Exception {

		initClasses.add(causeEffect1);
		initClasses.add(causeEffect2);

		assertThrows(AndHowCompileException.class, () ->
				noWriteProcessor.processLastRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars));

		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof CompileProblem.TooManyInitClasses);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 8");
		logCalls.verify(log, times(2)).printMessage(eq(Kind.ERROR), any());
		logCalls.verifyNoMoreInteractions();

		Mockito.verify(noWriteProcessor, never()).writeServiceFile(any(), any(), any());
		Mockito.verify(noWriteProcessor, never()).writeClassFile(any(), any(), any());
	}

	@Test
	void processLastRoundCallsWriteServiceFileForSingleTestInitClass() throws IOException {

		testInitClasses.add(causeEffect1);

		noWriteProcessor.processLastRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 8");
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found exactly 1 org.yarnandtail.andhow.AndHowTestInit class: com.bigcorp.MyClass1");
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).writeServiceFile(filer, AndHowTestInit.class.getCanonicalName(), testInitClasses);
		processorCalls.verifyNoMoreInteractions();
	}

	@Test
	void processLastRoundCreatesErrorIfTwoTestInitClass() throws Exception {

		testInitClasses.add(causeEffect1);
		testInitClasses.add(causeEffect2);

		assertThrows(AndHowCompileException.class, () ->
				noWriteProcessor.processLastRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars));

		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof CompileProblem.TooManyInitClasses);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 8");
		logCalls.verify(log, times(2)).printMessage(eq(Kind.ERROR), any());
		logCalls.verifyNoMoreInteractions();

		Mockito.verify(noWriteProcessor, never()).writeServiceFile(any(), any(), any());
		Mockito.verify(noWriteProcessor, never()).writeClassFile(any(), any(), any());
	}

	@Test
	void processLastRoundShouldThrowCompileExceptionWhenWriteThrowsException() throws Exception {

		registrars.add(causeEffect1);

		Mockito.when(filer.createResource(any(), any(), any(), any())).thenThrow(new IOException());
		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		assertThrows(AndHowCompileException.class, () -> noWriteProcessor.processLastRound(filer, log, 9, 9,
				problems, initClasses, testInitClasses, registrars));

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 9 jdk version: 9");
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor, times(1)).writeServiceFile(
				filer, PropertyRegistrar.class.getCanonicalName(), registrars);
		processorCalls.verifyNoMoreInteractions();
	}

	@Test
	void processLastRoundShouldWriteServiceFileForSingleRegistrar() throws Exception {

		registrars.add(causeEffect1);

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		noWriteProcessor.processLastRound(filer, log, 9, 9,
				problems, initClasses, testInitClasses, registrars);

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor, times(1)).writeServiceFile(
				filer, PropertyRegistrar.class.getCanonicalName(), registrars);
		processorCalls.verifyNoMoreInteractions();

		verify(filer, times(1)).createResource(eq(CLASS_OUTPUT), eq(""),
				eq("META-INF/services/" + PropertyRegistrar.class.getCanonicalName()),
				eq(causeEffect1.causeElement));

		String[] entries = filerWriter.toString().split(System.lineSeparator());
		assertEquals(1, entries.length);
		assertEquals(causeEffect1.fullClassName, entries[0]);
	}

	@Test
	void processLastRoundShouldWriteServiceFileForMultiRegistrar() throws Exception {

		registrars.add(causeEffect1);
		registrars.add(causeEffect2);

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		noWriteProcessor.processLastRound(filer, log, 9, 9,
				problems, initClasses, testInitClasses, registrars);

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor, times(1)).writeServiceFile(
				filer, PropertyRegistrar.class.getCanonicalName(), registrars);
		processorCalls.verifyNoMoreInteractions();

		verify(filer, times(1)).createResource(eq(CLASS_OUTPUT), eq(""),
				eq("META-INF/services/" + PropertyRegistrar.class.getCanonicalName()),
				any(), any());

		String[] entries = filerWriter.toString().split(System.lineSeparator());
		assertEquals(2, entries.length);
		assertEquals(causeEffect1.fullClassName, entries[0]);
		assertEquals(causeEffect2.fullClassName, entries[1]);
	}

	//Haven't verified Init class service write yet

}
