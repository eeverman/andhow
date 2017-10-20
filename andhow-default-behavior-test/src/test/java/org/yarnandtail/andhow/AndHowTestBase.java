package org.yarnandtail.andhow;



import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.junit.*;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * All tests using AppConfig must extend this class so they have access to the
 * one and only AppConfig.Reloader, which is a single backdoor to cause the
 * AppConfig to reload.
 * 
 * @author eeverman
 */
public class AndHowTestBase {
	
	public static AndHow.Reloader reloader = AndHow.builder().buildForNonPropduction();
	
	private static Properties beforeClassSystemProps;
	
	private Properties beforeTestSystemProps;

	/**
	 * Simple consistent way to get an empty JNDI context.
	 * 
	 * bind() each variable, then call build().
	 * 
	 * @return
	 * @throws NamingException 
	 */
	public static SimpleNamingContextBuilder getJndi() throws NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		return builder;
	}
	
	@BeforeClass
	public static void setupAllTests() {
		//The SimpleNamingContextBuilder uses Commons Logging, which defaults to
		//using Java logging.  It spews a bunch of stuff the console during tests,
		//so this turns that off.
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(Level.SEVERE);
		
		beforeClassSystemProps = System.getProperties();
	}
	
	@BeforeClass
	public static void saveSystemPropsBeforeClass() {
		beforeClassSystemProps = clone(System.getProperties());
	}
	
	@AfterClass
	public static void resetSystemPropsAfterClass() {
		System.setProperties(beforeClassSystemProps);
	}
	
	@Before
	public void saveSystemPropsBeforeTest() {
		beforeTestSystemProps = clone(System.getProperties());
	}
	
	@After
	public void resetSystemPropsAfterTest() {
		System.setProperties(beforeTestSystemProps);
	}
	
	/**
	 * Creates a clone of a Properties object so it can be detached from System.
	 * 
	 * @param props
	 * @return 
	 */
	protected static Properties clone(Properties props) {
		Properties newProps = new Properties();
		newProps.putAll(props);
		return newProps;
	}
	
}
