/*
 */
package org.yarnandtail.andhow;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.StdConfig.StdConfigImpl;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class StdConfigTest {
	
	public StdConfigTest() {
	}

	/**
	 * Test of getNamingStrategy method, of class BaseConfig.
	 */
	@Test
	public void testGetNamingStrategy() {
		StdConfigImpl std = StdConfig.instance();
		assertTrue(std.getNamingStrategy() instanceof CaseInsensitiveNaming);
	}

	/**
	 * Test of buildLoaders method, of class BaseConfig.
	 */
	@Test
	public void testBuildLoadersWithoutAnyChanges() {
		StdConfigImpl std = StdConfig.instance();
		List<Loader> loaders = std.buildLoaders();
		assertEquals(7, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdMainStringArgsLoader.class, loaders.get(1).getClass());
		assertEquals(StdSysPropLoader.class, loaders.get(2).getClass());
		assertEquals(StdEnvVarLoader.class, loaders.get(3).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(4).getClass());
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(5).getClass());
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(6).getClass());
	}

	@Test
	public void testBuildLoadersWithCustomListOfStandardLoaders() {
		StdConfigImpl std = StdConfig.instance();
		
		List<Class<? extends StandardLoader>> newLoaders = new ArrayList();
		newLoaders.add(StdFixedValueLoader.class);
		newLoaders.add(StdJndiLoader.class);
		
		std.setStandardLoaders(newLoaders);
		List<Loader> loaders = std.buildLoaders();
		assertEquals(2, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(1).getClass());
		
		std.setStandardLoaders(BaseConfig.getDefaultLoaderList());
		loaders = std.buildLoaders();
		assertEquals(7, loaders.size());
		assertEquals(StdFixedValueLoader.class, loaders.get(0).getClass());
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(6).getClass());
		
		std.setStandardLoaders(StdSysPropLoader.class, StdPropFileOnFilesystemLoader.class);
		loaders = std.buildLoaders();
		assertEquals(2, loaders.size());
		assertEquals(StdSysPropLoader.class, loaders.get(0).getClass());
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(1).getClass());
	}
	
	@Test
	public void testBuildLoadersWithInsertingLoadersBeforeAndAfter() {
		StdConfigImpl std = StdConfig.instance();
		
		Loader loader1 = new MapLoader();
		Loader loader2 = new KeyValuePairLoader();
		Loader loader3 = new PropFileOnClasspathLoader();
		Loader loader4 = new PropFileOnFilesystemLoader();
		Loader loader5 = new FixedValueLoader();
		Loader loader6 = new MapLoader();
		Loader loader7 = new KeyValuePairLoader();
		Loader loader8 = new PropFileOnClasspathLoader();
		Loader loader9 = new PropFileOnFilesystemLoader();
		
		//At the beginning of the list
		std.insertLoaderBefore(StdFixedValueLoader.class, loader1);
		std.insertLoaderBefore(StdFixedValueLoader.class, loader2);
		std.insertLoaderAfter(StdFixedValueLoader.class, loader3);
		std.insertLoaderAfter(StdFixedValueLoader.class, loader4);
		std.insertLoaderBefore(StdMainStringArgsLoader.class, loader5);
		
		//At the end of the list
		std.insertLoaderBefore(StdPropFileOnFilesystemLoader.class, loader6);
		std.insertLoaderAfter(StdPropFileOnFilesystemLoader.class, loader7);
		std.insertLoaderBefore(StdPropFileOnClasspathLoader.class, loader8);
		std.insertLoaderAfter(StdPropFileOnClasspathLoader.class, loader9);
		
		List<Loader> loaders = std.buildLoaders();
		assertEquals(16, loaders.size());
		assertEquals(loader1, loaders.get(0));
		assertEquals(loader2, loaders.get(1));
		assertEquals(StdFixedValueLoader.class, loaders.get(2).getClass());
		assertEquals(loader3, loaders.get(3));
		assertEquals(loader4, loaders.get(4));
		assertEquals(loader5, loaders.get(5));
		assertEquals(StdMainStringArgsLoader.class, loaders.get(6).getClass());
		assertEquals(StdSysPropLoader.class, loaders.get(7).getClass());
		assertEquals(StdEnvVarLoader.class, loaders.get(8).getClass());
		assertEquals(StdJndiLoader.class, loaders.get(9).getClass());
		assertEquals(loader6, loaders.get(10));
		assertEquals(StdPropFileOnFilesystemLoader.class, loaders.get(11).getClass());
		assertEquals(loader7, loaders.get(12));
		assertEquals(loader8, loaders.get(13));
		assertEquals(StdPropFileOnClasspathLoader.class, loaders.get(14).getClass());
		assertEquals(loader9, loaders.get(15));
	}
	
}
