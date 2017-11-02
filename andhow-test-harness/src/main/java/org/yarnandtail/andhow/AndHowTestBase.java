package org.yarnandtail.andhow;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.junit.*;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.internal.AndHowCore;

/**
 * All tests using AppConfig must extend this class so they have access to the
 * one and only AppConfig.Reloader, which is a single backdoor to cause the
 * AppConfig to reload.
 * 
 * @author eeverman
 */
public class AndHowTestBase {
	
	private static AndHowCore beforeClassCore;
	
	private AndHowCore beforeTestCore;
	
	private static Level beforeClassLogLevel;
	
	/**
	 * System properties before the tests and subclass @BeforeClass initializer
	 * are run.  Properties prior to the initialization of this test class are
	 * stored and then reinstated after the test class is complete.
	 */
	private static Properties beforeClassSystemProps;
	
	/**
	 * System properties before an individual test is run and the subclass @Before
	 * initializers are run.  Properties prior to a test are
	 * stored and then reinstated after the test is complete.  If a Test classes
	 * uses a @BeforeClass initializer that sets system properties, this will
	 * reinstate to that state.
	 */
	private Properties beforeTestSystemProps;
	
	/**
	 * Builder for a temporary JNDI context
	 */
	private static SimpleNamingContextBuilder builder;
	
	
	
	/**
	 * Simple consistent way to get an empty JNDI context.
	 * 
	 * bind() each variable, then call build().
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public SimpleNamingContextBuilder getJndi() throws NamingException {
		if (builder == null) {
			builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		}
		return builder;
	}
	
	@BeforeClass
	public static void andHowSnapshotBeforeTestClass() throws Exception {
		//The SimpleNamingContextBuilder uses Commons Logging, which defaults to
		//using Java logging.  It spews a bunch of stuff the console during tests,
		//so this turns that off.
		
		beforeClassLogLevel = Logger.getGlobal().getLevel();	//store log level before class
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(Level.SEVERE);
		
		
		beforeClassCore = AndHowNonProductionUtil.getAndHowCore();
		beforeClassSystemProps = AndHowNonProductionUtil.clone(System.getProperties());
	}
	
	@AfterClass
	public static void resetAndHowSnapshotAfterTestClass() {
		System.setProperties(beforeClassSystemProps);
		AndHowNonProductionUtil.setAndHowCore(beforeClassCore);
		
		//Reset to the log level prior to the test class
		Logger.getGlobal().setLevel(beforeClassLogLevel);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(beforeClassLogLevel);
	}
	
	@Before
	public void andHowSnapshotBeforeSingleTest() {
		beforeTestSystemProps = AndHowNonProductionUtil.clone(System.getProperties());
		beforeTestCore = AndHowNonProductionUtil.getAndHowCore();
	}
	
	@After
	public void resetAndHowSnapshotAfterSingleTest() {
		System.setProperties(beforeTestSystemProps);
		AndHowNonProductionUtil.setAndHowCore(beforeTestCore);
		
		if (builder != null) {
			builder.clear();
		}
	}
	
	
}
