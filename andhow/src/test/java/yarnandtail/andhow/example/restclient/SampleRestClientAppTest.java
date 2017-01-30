package yarnandtail.andhow.example.restclient;

import yarnandtail.andhow.internal.ValueProblem;
import yarnandtail.andhow.internal.LoaderProblem;
import java.util.ArrayList;
import yarnandtail.andhow.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.JndiLoader;
import yarnandtail.andhow.load.PropFileLoader;

/**
 *
 * @author eeverman
 */
public class SampleRestClientAppTest extends AndHowTestBase {
	
	String propFileLoaderConfigBaseName = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgs = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgs = new String[0];
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		
		cmdLineArgs = new String[] {
			propFileLoaderConfigBaseName + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/all.points.speced.properties",
		};
				
				
		AndHow.builder()
				.group(SampleRestClientGroup.class)
				.loader(new CmdLineLoader())
				.loader(new PropFileLoader())
				.cmdLineArgs(cmdLineArgs)
				.reloadForNonPropduction(reloader);
		
		assertEquals("/yarnandtail/andhow/example/restclient/all.points.speced.properties", 
				PropFileLoader.CONFIG.CLASSPATH_PATH.getValue());
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
			propFileLoaderConfigBaseName + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/minimum.points.speced.properties",
		};
				
				
		AndHow.builder()
				.group(SampleRestClientGroup.class)
				.loader(new CmdLineLoader())
				.loader(new PropFileLoader())
				.cmdLineArgs(cmdLineArgs)
				.reloadForNonPropduction(reloader);
		
		assertEquals("/yarnandtail/andhow/example/restclient/minimum.points.speced.properties", 
				PropFileLoader.CONFIG.CLASSPATH_PATH.getValue());
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
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		jndi.activate();
		
		cmdLineArgs = new String[] {
			propFileLoaderConfigBaseName + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/invalid.properties",
		};
				
				
		try {
			
			//Error expected b/c some values are invalid
			AndHow.builder()
					.group(SampleRestClientGroup.class)
					.loader(new CmdLineLoader())
					.loader(new PropFileLoader())
					.loader(new JndiLoader())
					.cmdLineArgs(cmdLineArgs)
					.reloadForNonPropduction(reloader);
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
