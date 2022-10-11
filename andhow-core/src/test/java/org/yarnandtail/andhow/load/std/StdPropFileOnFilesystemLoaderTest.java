package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.ReadLoader;
import org.yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;

import static org.junit.jupiter.api.Assertions.*;

class StdPropFileOnFilesystemLoaderTest {
	StdPropFileOnFilesystemLoader loader;

	@BeforeEach
	public void init() throws Exception {
		loader = new StdPropFileOnFilesystemLoader();
	}
	
	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof ReadLoader);
		assertEquals("unconfigured classpath", loader.getSpecificLoadDescription());
		assertEquals("KeyValuePair", loader.getLoaderDialect());
		assertEquals("PropertyFile", loader.getLoaderType());
		assertFalse(loader.isFlaggable());
		assertTrue(loader.isUnknownPropertyAProblem());
		assertTrue(loader.isTrimmingRequiredForStringValues());
		assertNull(loader.getClassConfig());
		assertTrue(loader.getConfigSamplePrinter() instanceof PropFileLoaderSamplePrinter);
		assertTrue(loader.getInstanceConfig().isEmpty());
		loader.releaseResources();	// should cause no error
	}


	@Test
	public void updateFilePath() {
		loader.setFilePath("/my/path/file.properties");
		assertTrue(loader.getSpecificLoadDescription().startsWith("file on the file system at path : /my/path/file.properties "));
	}

}