package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ReadLoader;

import static org.junit.jupiter.api.Assertions.*;

class StdFixedValueLoaderTest {
	StdFixedValueLoader loader;

	@BeforeEach
	public void init() throws Exception {
		loader = new StdFixedValueLoader();
	}


	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof ReadLoader);
		assertEquals("a list of fixed values passed in during startup (not dynamically loaded)", loader.getSpecificLoadDescription());
		assertEquals("FromJavaSourceCode", loader.getLoaderDialect());
		assertEquals("FixedValue", loader.getLoaderType());
		assertFalse(loader.isFlaggable());
		assertTrue(loader.isUnknownPropertyAProblem());
		assertFalse(loader.isTrimmingRequiredForStringValues());
		assertNull(loader.getClassConfig());
		assertNull(loader.getConfigSamplePrinter());
		assertTrue(loader.getInstanceConfig().isEmpty());
		loader.releaseResources();	// should cause no error
	}
}