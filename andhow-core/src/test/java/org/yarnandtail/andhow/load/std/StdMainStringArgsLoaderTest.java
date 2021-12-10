package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.load.*;

import static org.junit.jupiter.api.Assertions.*;

class StdMainStringArgsLoaderTest extends BaseForLoaderTests {

	StdMainStringArgsLoader loader;
	LoaderEnvironmentBuilder leb;

	@BeforeEach
	public void initLoader() {
		loader = new StdMainStringArgsLoader();
		leb = new LoaderEnvironmentBuilder();
	}

	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof ReadLoader);
		assertEquals("main(String[] args)", loader.getSpecificLoadDescription());
		assertEquals("main(String[] args)", loader.getLoaderDialect());
		assertEquals("KeyValuePair", loader.getLoaderType());
		assertTrue(loader.isFlaggable());
		assertFalse(loader.isUnknownPropertyAProblem());
		assertTrue(loader.isTrimmingRequiredForStringValues());
		assertNull(loader.getClassConfig());
		assertNull(loader.getConfigSamplePrinter());
		assertTrue(loader.getInstanceConfig().isEmpty());
		loader.releaseResources();	// should cause no error
	}

	@Test
	public void emptyValuesTrimAndFlagsAreHandledAsFlags() {

		String basePath = BaseForLoaderTests.SimpleParams.class.getCanonicalName() + ".";

		String[] args = {
			basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "",
			basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "",
			basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "",
			basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "",
			basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + ""
		};

		leb.setCmdLineArgs(args);
		LoaderValues result = loader.load(appDef, leb, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertNull(result.getExplicitValue(BaseForLoaderTests.SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(BaseForLoaderTests.SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(BaseForLoaderTests.SimpleParams.STR_NULL));
		assertNull(result.getValue(BaseForLoaderTests.SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(BaseForLoaderTests.SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(BaseForLoaderTests.SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(BaseForLoaderTests.SimpleParams.FLAG_NULL));
	}

	@Test
	public void shouldNotCreateAProblemForUnknownProperties() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		String[] args = {
				basePath + "XXX" + KeyValuePairLoader.KVP_DELIMITER + "1",
				basePath + "YYY" + KeyValuePairLoader.KVP_DELIMITER + "2"
		};

		leb.setCmdLineArgs(args);
		LoaderValues result = loader.load(appDef, leb, appValuesBuilder);

		assertFalse(loader.isUnknownPropertyAProblem());
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void shouldCreateAProblemForUnknownPropertiesIfSetTrue() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		String[] args = {
				basePath + "XXX" + KeyValuePairLoader.KVP_DELIMITER + "1",
				basePath + "YYY" + KeyValuePairLoader.KVP_DELIMITER + "2"
		};

		leb.setCmdLineArgs(args);
		loader.setUnknownPropertyAProblem(true);
		LoaderValues result = loader.load(appDef, leb, appValuesBuilder);

		assertTrue(loader.isUnknownPropertyAProblem());
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}

		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}
}
