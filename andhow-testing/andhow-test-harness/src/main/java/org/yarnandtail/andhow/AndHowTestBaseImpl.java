package org.yarnandtail.andhow;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.internal.AndHowCore;

import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for AndHowJunit4/5TestBase classes.
 * See those classes for documentation.
 */
public class AndHowTestBaseImpl {
	private static AndHowCore beforeClassCore;
	private static Level beforeClassLogLevel;
	/**
	 * System properties before the tests and subclass @BeforeClass initializer
	 * are run.  Properties prior to the initialization of this test class are
	 * stored and then reinstated after the test class is complete.
	 */
	private static Properties beforeClassSystemProps;
	/**
	 * Builder for a temporary JNDI context
	 */
	private static SimpleNamingContextBuilder builder;
	private AndHowCore beforeTestCore;
	/**
	 * System properties before an individual test is run and the subclass @Before
	 * initializers are run.  Properties prior to a test are
	 * stored and then reinstated after the test is complete.  If a Test classes
	 * uses a @BeforeClass initializer that sets system properties, this will
	 * reinstate to that state.
	 */
	private Properties beforeTestSystemProps;

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test class.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	public static void andHowSnapshotBeforeTestClass() {
		//The SimpleNamingContextBuilder uses Commons Logging, which defaults to
		//using Java logging.  It spews a bunch of stuff the console during tests,
		//so this turns that off.

		beforeClassLogLevel = Logger.getGlobal().getLevel();  //store log level before class
		Logger.getGlobal().setLevel(Level.SEVERE);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(Level.SEVERE);


		beforeClassCore = AndHowNonProductionUtil.getAndHowCore();
		beforeClassSystemProps = AndHowNonProductionUtil.clone(System.getProperties());
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to this class' test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	public static void resetAndHowSnapshotAfterTestClass() {
		System.setProperties(beforeClassSystemProps);
		AndHowNonProductionUtil.setAndHowCore(beforeClassCore);

		//Reset to the log level prior to the test class
		Logger.getGlobal().setLevel(beforeClassLogLevel);
		Logger.getLogger(SimpleNamingContextBuilder.class.getCanonicalName()).setLevel(beforeClassLogLevel);
	}

	/**
	 * Simple consistent way to get an empty JNDI context.
	 * <p>
	 * bind() each variable, then call build().
	 *
	 * @deprecated This will be removed in the next major release to avoid
	 * having JNDI dependencies in a user visible class.  Most user will not
	 * need to test their apps with JNDI.
	 * @return A JNDI context for setting properties via JNDI.
	 * @throws NamingException If JNDI cannot be initiated.
	 */
	public SimpleNamingContextBuilder getJndi() throws NamingException {
		if (builder == null) {
			builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		}
		return builder;
	}

	/**
	 * Stores the AndHow Core (its state) and System Properties prior to a test.
	 * It also sets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to SEVERE.  If JNDI is used for a test, it's startup
	 * is verbose to System.out, so this turns it off.
	 */
	public void andHowSnapshotBeforeSingleTest() {
		beforeTestSystemProps = AndHowNonProductionUtil.clone(System.getProperties());
		beforeTestCore = AndHowNonProductionUtil.getAndHowCore();
	}

	/**
	 * Restores the AndHow Core (its state) and System Properties
	 * that were previously stored prior to a test run.
	 * It also resets the logging level for SimpleNamingContextBuilder (a JNDI
	 * related class) to what ever it was prior to the run.
	 */
	public void resetAndHowSnapshotAfterSingleTest() {
		System.setProperties(beforeTestSystemProps);
		AndHowNonProductionUtil.setAndHowCore(beforeTestCore);

		if (builder != null) {
			builder.clear();
		}
	}
}
