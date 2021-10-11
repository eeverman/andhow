package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.AndHowTestInit;
import org.yarnandtail.andhow.compile.AndHowCompileProcessor.CauseEffect;
import org.yarnandtail.andhow.service.PropertyRegistrar;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
	RoundEnvironment roundNotFinal;
	RoundEnvironment roundFinal;

	@BeforeEach
	void setUp() throws Exception {
		//log = Mockito.mock(Messager.class, withSettings().verboseLogging()); //use to print each invocation
		log = Mockito.mock(Messager.class);

		filer = Mockito.mock(Filer.class);
		filerFileObject = Mockito.mock(FileObject.class);
		filerWriter = new StringWriter();
		Mockito.when(filer.createResource(any(), any(), any(), any())).thenReturn(filerFileObject);
		Mockito.when(filerFileObject.openWriter()).thenReturn(filerWriter);


		pe = Mockito.mock(ProcessingEnvironment.class);
		Mockito.when(pe.getSourceVersion()).thenReturn(SourceVersion.RELEASE_8);
		Mockito.when(pe.getMessager()).thenReturn(log);
		Mockito.when(pe.getFiler()).thenReturn(filer);

		noWriteProcessor = new AndHowCompileProcessor();
		noWriteProcessor = Mockito.spy(noWriteProcessor);
		Mockito.doNothing().when(noWriteProcessor).writeServiceFile(any(), any(), any());
		Mockito.doNothing().when(noWriteProcessor).writeClassFile(any(), any(), any());
		Mockito.when(noWriteProcessor.getJdkVersion()).thenReturn(8);

		runDate = new GregorianCalendar();
		registrars = new ArrayList<>();
		initClasses = new ArrayList<>();
		testInitClasses = new ArrayList<>();
		problems = new ArrayList<>();

		causeEffect1 = new CauseEffect("com.bigcorp.MyClass1", Mockito.mock(Element.class));
		causeEffect2 = new CauseEffect("com.bigcorp.MyClass2", Mockito.mock(Element.class));
		roundNotFinal = mock(RoundEnvironment.class);
		when(roundNotFinal.processingOver()).thenReturn(false);
		roundFinal = mock(RoundEnvironment.class);
		when(roundFinal.processingOver()).thenReturn(true);
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
	void processRoundsCallsCorrectRoundProcessor() throws Exception {

		Set<? extends TypeElement> annotations = mock(Set.class);

		Mockito.doNothing().when(noWriteProcessor).processNonFinalRound(any(), any(), any(),
				any(), any(), anyInt(), anyInt(), any(), any(), any(), any());
		Mockito.doNothing().when(noWriteProcessor).processFinalRound(any(), any(), anyInt(), anyInt(),
				any(), any(), any(), any());

		noWriteProcessor.init(pe);

		boolean over = roundNotFinal.processingOver();
		assertFalse(over);

		noWriteProcessor.process(annotations, roundNotFinal);
		noWriteProcessor.process(annotations, roundNotFinal);
		noWriteProcessor.process(annotations, roundFinal);

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).init(pe);
		processorCalls.verify(noWriteProcessor).processNonFinalRound(
				eq(pe), eq(roundNotFinal), any(), eq(filer), eq(log), eq(8), eq(8), any(), any(), any(), any());
		processorCalls.verify(noWriteProcessor).processNonFinalRound(
				eq(pe), eq(roundNotFinal), any(), eq(filer), eq(log), eq(8), eq(8), any(), any(), any(), any());
		processorCalls.verify(noWriteProcessor, times(1)).processFinalRound(
				eq(filer), eq(log), eq(8), eq(8), any(), any(), any(), any());
		processorCalls.verifyNoMoreInteractions();
	}


	@Test
	void processShowsJustOneIndeterminateVersionsWarning() throws Exception {

		registrars.add(causeEffect1);

		Mockito.doNothing().when(noWriteProcessor).processNonFinalRound(any(), any(), any(),
				any(), any(), anyInt(), anyInt(), any(), any(), any(), any());
		Mockito.doNothing().when(noWriteProcessor).processFinalRound(any(), any(), anyInt(), anyInt(),
				any(), any(), any(), any());
		noWriteProcessor.init(pe);

		when(noWriteProcessor.getJdkVersion()).thenReturn(9);	// getSrcVersion already returns 8

		Set<? extends TypeElement> annotations = mock(Set.class);

		noWriteProcessor.process(annotations, roundNotFinal);
		noWriteProcessor.process(annotations, roundNotFinal);
		noWriteProcessor.process(annotations, roundFinal);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found java source version: 8 jdk version: 9");
		logCalls.verify(log).printMessage(
				ArgumentMatchers.eq(Kind.MANDATORY_WARNING),
				ArgumentMatchers.matches("AndHowCompileProcessor: The source level is JDK8 " +
						".*current JDK is 9.*the 'Generated' annotation on proxy classes will be commented out"));
		logCalls.verifyNoMoreInteractions();

	}

	@Test
	void processNonFinalRoundScansAndWritesFiles() throws Exception {

		TypeElement type1 = mock(TypeElement.class);
		TypeElement type2 = mock(TypeElement.class);
		TypeElement type3 = mock(TypeElement.class);
		TypeElement type4 = mock(TypeElement.class);
		Element typeX = mock(Element.class);

		Set<Element> rootElements = new LinkedHashSet<>();
		rootElements.add(type1);
		rootElements.add(type2);
		rootElements.add(type3);
		rootElements.add(type4);
		rootElements.add(typeX);

		CompileUnit compileUnit1 = mock(CompileUnit.class);
		when(compileUnit1.istestInitClass()).thenReturn(true);
		when(compileUnit1.getRootCanonicalName()).thenReturn("org.MyTestInit1");

		CompileUnit compileUnit2 = mock(CompileUnit.class);
		when(compileUnit2.isInitClass()).thenReturn(true);
		when(compileUnit2.getRootCanonicalName()).thenReturn("org.MyInit2");

		CompileUnit compileUnit3 = PropertyRegistrarClassGeneratorTest.simpleCompileUnit();
		CompileUnit compileUnit4 = PropertyRegistrarClassGeneratorTest.complexCompileUnit();

		doReturn(rootElements).when(roundNotFinal).getRootElements();

		doReturn(compileUnit1).when(noWriteProcessor).scanTypeElement(pe, type1);
		doReturn(compileUnit2).when(noWriteProcessor).scanTypeElement(pe, type2);
		doReturn(compileUnit3).when(noWriteProcessor).scanTypeElement(pe, type3);
		doReturn(compileUnit4).when(noWriteProcessor).scanTypeElement(pe, type4);

		noWriteProcessor.init(pe);

		noWriteProcessor.processNonFinalRound(pe, roundNotFinal, runDate, filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).scanTypeElement(pe, type1);
		processorCalls.verify(noWriteProcessor).scanTypeElement(pe, type2);
		processorCalls.verify(noWriteProcessor).scanTypeElement(pe, type3);
		processorCalls.verify(noWriteProcessor).writeClassFile(eq(filer), any(), eq(type3));
		processorCalls.verify(noWriteProcessor).scanTypeElement(pe, type4);
		processorCalls.verify(noWriteProcessor).writeClassFile(eq(filer), any(), eq(type4));
		processorCalls.verify(noWriteProcessor).debug(any(), startsWith("Wrote new"), any());
		processorCalls.verifyNoMoreInteractions();

		verify(noWriteProcessor, times(0)).error(any(), any(), any());
		verify(noWriteProcessor, times(0)).warn(any(), any(), any());
	}

	@Test
	void processFinalRoundWithNoClassesFound() throws Exception {

		noWriteProcessor.processFinalRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verifyNoMoreInteractions();

		Mockito.verify(noWriteProcessor, never()).writeClassFile(any(), any(), any());
	}

	@Test
	void processFinalRoundWriteServiceFileForSingleInitClass() throws IOException {

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		initClasses.add(causeEffect1);

		noWriteProcessor.processFinalRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found exactly 1 org.yarnandtail.andhow.AndHowInit class: com.bigcorp.MyClass1");
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).writeServiceFile(filer, AndHowInit.class.getCanonicalName(), initClasses);
		processorCalls.verifyNoMoreInteractions();

		InOrder filerCalls = Mockito.inOrder(filer);
		filerCalls.verify(filer, times(1)).createResource(eq(CLASS_OUTPUT), eq(""),
				eq("META-INF/services/" + AndHowInit.class.getCanonicalName()),
				eq(causeEffect1.causeElement));
		filerCalls.verifyNoMoreInteractions();

	}

	@Test
	void processFinalRoundLogsErrorIfTwoInitClassButDoesNotThrow() throws Exception {

		initClasses.add(causeEffect1);
		initClasses.add(causeEffect2);

		noWriteProcessor.processFinalRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof CompileProblem.TooManyInitClasses);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.ERROR, "AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. " +
				"Each of the following (1) errors must be fixed before compilation is possible.");
		logCalls.verify(log).printMessage(eq(Kind.ERROR), startsWith("AndHowCompileProcessor: Multiple (2) implementations of " +
				"org.yarnandtail.andhow.AndHowInit were found"));
		logCalls.verifyNoMoreInteractions();

		Mockito.verify(noWriteProcessor, never()).writeServiceFile(any(), any(), any());
		Mockito.verify(noWriteProcessor, never()).writeClassFile(any(), any(), any());
	}


	@Test
	void processFinalRoundWriteServiceFileForSingleTestInitClass() throws IOException {

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		testInitClasses.add(causeEffect1);

		noWriteProcessor.processFinalRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found exactly 1 org.yarnandtail.andhow.AndHowTestInit class: com.bigcorp.MyClass1");
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor).writeServiceFile(filer, AndHowTestInit.class.getCanonicalName(), testInitClasses);
		processorCalls.verifyNoMoreInteractions();

		InOrder filerCalls = Mockito.inOrder(filer);
		filerCalls.verify(filer, times(1)).createResource(eq(CLASS_OUTPUT), eq(""),
				eq("META-INF/services/" + AndHowTestInit.class.getCanonicalName()),
				eq(causeEffect1.causeElement));
		filerCalls.verifyNoMoreInteractions();

	}

	@Test
	void processFinalRoundCreatesLogsErrorIfTwoTestInitClassButDoesNotThrow() throws Exception {

		testInitClasses.add(causeEffect1);
		testInitClasses.add(causeEffect2);

		noWriteProcessor.processFinalRound(filer, log, 8, 8,
				problems, initClasses, testInitClasses, registrars);

		assertEquals(1, problems.size());
		assertTrue(problems.get(0) instanceof CompileProblem.TooManyInitClasses);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.ERROR, "AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. " +
				"Each of the following (1) errors must be fixed before compilation is possible.");
		logCalls.verify(log).printMessage(eq(Kind.ERROR), startsWith("AndHowCompileProcessor: Multiple (2) implementations of " +
				"org.yarnandtail.andhow.AndHowTestInit were found"));
		logCalls.verifyNoMoreInteractions();

		Mockito.verify(noWriteProcessor, never()).writeServiceFile(any(), any(), any());
		Mockito.verify(noWriteProcessor, never()).writeClassFile(any(), any(), any());
	}

	@Test
	void processFinalRoundShouldNotThrowAnExceptionWhenWriteThrowsException() throws Exception {

		registrars.add(causeEffect1);

		Mockito.when(filer.createResource(any(), any(), any(), any())).thenThrow(new IOException());
		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		noWriteProcessor.processFinalRound(filer, log, 9, 9,
				problems, initClasses, testInitClasses, registrars);

		InOrder logCalls = Mockito.inOrder(log);
		logCalls.verify(log).printMessage(Kind.NOTE, "AndHowCompileProcessor: Found 1 top level classes containing AndHow Properties");
		logCalls.verify(log).printMessage(eq(Kind.ERROR), startsWith("AndHowCompileProcessor: Exception while trying to write generated files"));
		logCalls.verifyNoMoreInteractions();

		InOrder processorCalls = Mockito.inOrder(noWriteProcessor);
		processorCalls.verify(noWriteProcessor, times(1)).writeServiceFile(
				filer, PropertyRegistrar.class.getCanonicalName(), registrars);
	}

	@Test
	void processFinalRoundShouldWriteServiceFileForSingleRegistrar() throws Exception {

		registrars.add(causeEffect1);

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		noWriteProcessor.processFinalRound(filer, log, 9, 9,
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
	void processFinalRoundShouldWriteServiceFileForMultiRegistrar() throws Exception {

		registrars.add(causeEffect1);
		registrars.add(causeEffect2);

		Mockito.doCallRealMethod().when(noWriteProcessor).writeServiceFile(any(), any(), any());

		noWriteProcessor.processFinalRound(filer, log, 9, 9,
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

}
