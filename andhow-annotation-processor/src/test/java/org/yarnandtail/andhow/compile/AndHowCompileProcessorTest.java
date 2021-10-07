package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.BeforeEach;
import org.yarnandtail.compile.*;
import static org.yarnandtail.andhow.compile.CompileProblem.*;

import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.*;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.*;
import javax.tools.Diagnostic.Kind;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.util.IOUtil;


import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class AndHowCompileProcessorTest {

	Messager log;
	ProcessingEnvironment pe;


	@BeforeEach
	void setUp() {
		log = Mockito.mock(Messager.class);

		pe = Mockito.mock(ProcessingEnvironment.class);
		Mockito.when(pe.getSourceVersion()).thenReturn(SourceVersion.RELEASE_8);
		Mockito.when(pe.getMessager()).thenReturn(log);
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

}
