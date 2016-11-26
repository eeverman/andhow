package yarnandtail.andhow.example.restclient;

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
public class SampleRestClientApp extends AppConfigTestBase {
	
	String propFileLoaderPointbase = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
	String[] cmdLineArgs = new String[0];

	
	@Before
	public void setup() {
		
		cmdLineArgs = new String[0];
		
	}
	
	@Test
	public void testAllValuesAreSet() {
		
		cmdLineArgs = new String[] {
			propFileLoaderPointbase + "CLASSPATH_PATH" + AppConfig.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/all.points.speced.properties",
		};
				
				
		AppConfigBuilder.init()
				.addGroup(SampleRestClientGroup.class)
				.addLoader(new CmdLineLoader())
				.addLoader(new PropFileLoader())
				.setCmdLineArgs(cmdLineArgs)
				.build(reloader);
		
		assertEquals("/yarnandtail/andhow/example/restclient/all.points.speced.properties", 
				PropFileLoader.CONFIG.CLASSPATH_PATH.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(new Integer(8080), SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("doquery", SampleRestClientGroup.REST_SERVICE_NAME.getValue());
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(new Integer(4), SampleRestClientGroup.RETRY_COUNT.getValue());
		assertFalse(SampleRestClientGroup.REQUEST_META_DATA.getValue());
		assertTrue(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());

	}
	
	@Test
	public void testMinimumPointsAreSet() {
		
		cmdLineArgs = new String[] {
			propFileLoaderPointbase + "CLASSPATH_PATH" + AppConfig.KVP_DELIMITER + 
				"/yarnandtail/andhow/example/restclient/minimum.points.speced.properties",
		};
				
				
		AppConfigBuilder.init()
				.addGroup(SampleRestClientGroup.class)
				.addLoader(new CmdLineLoader())
				.addLoader(new PropFileLoader())
				.setCmdLineArgs(cmdLineArgs)
				.build(reloader);
		
		assertEquals("/yarnandtail/andhow/example/restclient/minimum.points.speced.properties", 
				PropFileLoader.CONFIG.CLASSPATH_PATH.getValue());
		assertEquals("aquarius.usgs.gov", SampleRestClientGroup.REST_HOST.getValue());
		assertEquals(new Integer(8080), SampleRestClientGroup.REST_PORT.getValue());
		assertEquals("query", SampleRestClientGroup.REST_SERVICE_NAME.getValue());	//a default value
		assertEquals("abc123", SampleRestClientGroup.AUTH_KEY.getValue());
		assertEquals(new Integer(2), SampleRestClientGroup.RETRY_COUNT.getValue());	//a default
		assertTrue(SampleRestClientGroup.REQUEST_META_DATA.getValue());	//a default
		assertFalse(SampleRestClientGroup.REQUEST_SUMMARY_DATA.getValue());	//a default

	}
	

}
