package yarnandtail.andhow.load;

import java.io.File;
import java.net.URL;
import yarnandtail.andhow.internal.LoaderProblem;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import static org.junit.Assert.*;

import yarnandtail.andhow.*;
import yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class PropertyFileFromFilesystemLoaderUnitTest {
	
	ConstructionDefinitionMutable appDef;
	ValueMapWithContextMutable appValuesBuilder;
	File tempPropertiesFile = null;
	
	public static interface TestProps extends PropertyGroup {
		StrProp FILEPATH = StrProp.builder().required().build();
	}
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new ConstructionDefinitionMutable(bns);
		
		appDef.addProperty(TestProps.class, TestProps.FILEPATH);

		
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB);
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL);
		
		//copy a properties file to a temp location
		URL inputUrl = getClass().getResource("/yarnandtail/andhow/load/SimpleParams1.properties");
		tempPropertiesFile = File.createTempFile("andhow_test", ".properties");
		tempPropertiesFile.deleteOnExit();
		FileUtils.copyURLToFile(inputUrl, tempPropertiesFile);

	}
	
	@After
	public void afterTest() {
		if (tempPropertiesFile != null) {
			tempPropertiesFile.delete();
		}
	}
	
	
	@Test
	public void testHappyPath() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.FILEPATH, tempPropertiesFile.getAbsolutePath()));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromFilesystemLoader pfl = new PropertyFileFromFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("kvpNullValue", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testPropFileLoaderWithMissingFile() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.FILEPATH, "/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromFilesystemLoader pfl = new PropertyFileFromFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(1, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.SourceNotFoundLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	/**
	 * The loader itself is OK w/ not having its parameter specified - it just
	 * ignores.
	 */
	@Test
	public void testPropFileLoaderWithNoClasspathConfigured() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		//evl.add(new PropertyValue(TestProps.FILEPATH, "/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromFilesystemLoader pfl = new PropertyFileFromFilesystemLoader(TestProps.FILEPATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}

}
