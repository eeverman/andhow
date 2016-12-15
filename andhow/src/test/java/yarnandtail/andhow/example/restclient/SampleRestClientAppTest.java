package yarnandtail.andhow.example.restclient;

import java.util.ArrayList;
import yarnandtail.andhow.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.load.PropFileLoader;

/**
 *
 * @author eeverman
 */
public class SampleRestClientAppTest extends AndHowTestBase {
	
	String propFileLoaderPointbase = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgs = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgs = new String[0];
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		
		cmdLineArgs = new String[] {
			propFileLoaderPointbase + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/all.points.speced.properties",
		};
				
				
		AndHow.builder()
				.group(SampleRestClientGroup.class)
				.loader(new CmdLineLoader())
				.loader(new PropFileLoader())
				.cmdLineArgs(cmdLineArgs)
				.reloadForUnitTesting(reloader);
		
		assertEquals("/yarnandtail/andhow/example/restclient/all.points.speced.properties", 
				PropFileLoader.CONFIG.CLASSPATH_PATH.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(new Integer(8080), SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("doquery/", SampleRestClientGroup.REST_SERVICE_NAME.getValue());
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(new Integer(4), SampleRestClientGroup.RETRY_COUNT.getValue());
		assertFalse(SampleRestClientGroup.REQUEST_META_DATA.getValue());
		assertTrue(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());

	}
	
	@Test
	public void testMinimumPointsAreSet() {
		
		cmdLineArgs = new String[] {
			propFileLoaderPointbase + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/minimum.points.speced.properties",
		};
				
				
		AndHow.builder()
				.group(SampleRestClientGroup.class)
				.loader(new CmdLineLoader())
				.loader(new PropFileLoader())
				.cmdLineArgs(cmdLineArgs)
				.reloadForUnitTesting(reloader);
		
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
	public void testInvalidValues() {
		
		cmdLineArgs = new String[] {
			propFileLoaderPointbase + "CLASSPATH_PATH" + AndHow.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/invalid.properties",
		};
				
				
		try {
			
			//Error expected b/c some values are invalid
			AndHow.builder()
					.group(SampleRestClientGroup.class)
					.loader(new CmdLineLoader())
					.loader(new PropFileLoader())
					.cmdLineArgs(cmdLineArgs)
					.reloadForUnitTesting(reloader);
		} catch (AppFatalException e) {
			
			
			//Due to loading from a prop file, the order of the file is not preserved,
			//so we cannot know the order that problems were encountered.
			ArrayList<Property<?>> expectedProblemPoints = new ArrayList();
			expectedProblemPoints.add(SampleRestClientGroup.REST_HOST);
			expectedProblemPoints.add(SampleRestClientGroup.REST_PORT);
			expectedProblemPoints.add(SampleRestClientGroup.REST_SERVICE_NAME);
			expectedProblemPoints.add(SampleRestClientGroup.RETRY_COUNT);
			
			assertEquals(4, e.getPointValueProblems().size());
			assertTrue(expectedProblemPoints.contains(e.getPointValueProblems().get(0).getPropertyValueCoord().getProperty()));
			assertTrue(expectedProblemPoints.contains(e.getPointValueProblems().get(1).getPropertyValueCoord().getProperty()));
			assertTrue(expectedProblemPoints.contains(e.getPointValueProblems().get(2).getPropertyValueCoord().getProperty()));
			assertTrue(expectedProblemPoints.contains(e.getPointValueProblems().get(3).getPropertyValueCoord().getProperty()));
		}
		

	}
	

}
