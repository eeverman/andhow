package org.yarnandtail.andhow.example.restclient;

import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.AndHowTestBase;
import org.yarnandtail.andhow.NonProductionConfig;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.load.KeyValuePairLoader;

/**
 *
 * @author eeverman
 */
public class SampleRestClientAppTest extends AndHowTestBase {
	
	String[] cmdLineArgs = new String[0];
	private static final String GROUP_PATH = "org.yarnandtail.andhow.example.restclient.SampleRestClientGroup";

	
	@Before
	public void setup() {
		
		cmdLineArgs = new String[0];
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		
		cmdLineArgs = new String[] {
			GROUP_PATH + ".CLASSPATH_PROP_FILE" + KeyValuePairLoader.KVP_DELIMITER + 
				"/org/yarnandtail/andhow/example/restclient/all.points.speced.properties"
		};
		
		NonProductionConfig.instance()
				.group(SampleRestClientGroup.class)
				.addCmdLineArgs(cmdLineArgs)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired()
				.forceBuild();
		
		assertEquals("/org/yarnandtail/andhow/example/restclient/all.points.speced.properties", 
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertEquals("  Big App  ", SampleRestClientGroup.APP_NAME.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(new Integer(8080), SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("doquery/", SampleRestClientGroup.REST_SERVICE_NAME.getValue());
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(new Integer(4), SampleRestClientGroup.RETRY_COUNT.getValue());
		assertFalse(SampleRestClientGroup.REQUEST_META_DATA.getValue());
		assertTrue(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());

	}
	
	@Test
	public void testMinimumPropsAreSet() {
		
		cmdLineArgs = new String[] {
			GROUP_PATH + ".CLASSPATH_PROP_FILE" + KeyValuePairLoader.KVP_DELIMITER + 
				"/org/yarnandtail/andhow/example/restclient/minimum.points.speced.properties"
		};		
				
		NonProductionConfig.instance()
				.group(SampleRestClientGroup.class)
				.addCmdLineArgs(cmdLineArgs)
				.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
				.classpathPropertiesRequired()
				.forceBuild();
		
		assertEquals("/org/yarnandtail/andhow/example/restclient/minimum.points.speced.properties", 
				SampleRestClientGroup.CLASSPATH_PROP_FILE.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(new Integer(8080), SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("query/", SampleRestClientGroup.REST_SERVICE_NAME.getValue());	//a default value
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(new Integer(2), SampleRestClientGroup.RETRY_COUNT.getValue());	//a default
		assertTrue(SampleRestClientGroup.REQUEST_META_DATA.getValue());	//a default
		assertFalse(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());	//a default

	}
	
	@Test
	public void testInvalidValues() throws Exception {
		
		SimpleNamingContextBuilder jndi = getJndi();
		jndi.activate();
		
		cmdLineArgs = new String[] {
			GROUP_PATH + ".CLASSPATH_PROP_FILE" + KeyValuePairLoader.KVP_DELIMITER + 
				"/org/yarnandtail/andhow/example/restclient/invalid.properties"
		};
				
		try {
			
			//Error expected b/c some values are invalid
			NonProductionConfig.instance()
					.group(SampleRestClientGroup.class)
					.addCmdLineArgs(cmdLineArgs)
					.setClasspathPropFilePath(SampleRestClientGroup.CLASSPATH_PROP_FILE)
					.classpathPropertiesRequired()
					.forceBuild();
		} catch (AppFatalException e) {
			
			//Value Problems (validation)
			//Due to loading from a prop file, the order of the file is not preserved,
			//so we cannot know the order that problems were encountered.
			ArrayList<Property<?>> expectedProblemPoints = new ArrayList();
			expectedProblemPoints.add(SampleRestClientGroup.REST_HOST);
			expectedProblemPoints.add(SampleRestClientGroup.REST_PORT);
			expectedProblemPoints.add(SampleRestClientGroup.REST_SERVICE_NAME);
			
			assertEquals(3, e.getProblems().filter(ValueProblem.class).size());
			assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(0).getBadValueCoord().getProperty()));
			assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(1).getBadValueCoord().getProperty()));
			assertTrue(expectedProblemPoints.contains(e.getProblems().filter(ValueProblem.class).get(2).getBadValueCoord().getProperty()));
			
			//
			// Loader problems
			assertEquals(1, e.getProblems().filter(LoaderProblem.class).size());
			assertEquals(SampleRestClientGroup.RETRY_COUNT, e.getProblems().filter(LoaderProblem.class).get(0).getBadValueCoord().getProperty());
		}
		

	}
	

}
