package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.util.List;

/**
 * Implementation of an ExtensionBase that configures AndHow from a single properties
 * file.  It is the base class to be used in one of three modes:
 * <li>BeforeAll/AfterAll Registered on a test class</li>
 * <li>BeforeEach/AfterEach Registered on a test class to apply to all methods</li>
 * <li>BeforeEach/AfterEach Registered on a test method to apply to a single test</li>
 * Mixing purposes (i.e. a single instance registered for use for more than one mode)
 * can result in confused state.
 */
public abstract class ConfigFromFileBaseExt extends ExtensionBase {

	/** The complete path to a properties file on the classpath */
	private String _classpathFile;

	/** The constructed config instance to be used for AndHow */
	protected AndHowConfiguration<? extends AndHowConfiguration> _config;

	/**
	 * New instance - validation of the classpathFile is deferred until use.
	 * @param classpathFile Complete path to a properties file on the classpath
	 */
	public ConfigFromFileBaseExt(String classpathFile) {
		if (classpathFile == null) {
			throw new IllegalArgumentException("The classpath properties file path cannot be null.");
		}
		_classpathFile = classpathFile;
	}

	/**
	 * Empty constructor used when the \@ConfigFromFile annotation is used.
	 *
	 * When the empty construct is used, the classpathFile is found via the
	 * \@ConfigFromFile annotation filePath property.
	 */
	public ConfigFromFileBaseExt() {	}

	/**
	 * When this Extension is being used in association w/ an annotation, this returns
	 * that class.
	 * @return
	 */
	protected abstract String getAnnotationFilePath(ExtensionContext context);

	protected String getClasspathFile(ExtensionContext context) {
		if (_classpathFile == null) {
			_classpathFile = getAnnotationFilePath(context);
		}

		System.out.println("CFF: filePath = " + _classpathFile);
		return _classpathFile;
	}

	/** Key to store the AndHowCore (if any) of AndHow. */
	protected static final String CORE_KEY = "core_key";

	/** Key to store the in-process configuration (if any) of the AndHow instance.
	 * When is the inProcessConfig non-null for AndHow?  Only when findConfig()
	 * has been called but AndHow has not yet initialized.
	 */
	protected static final String CONFIG_KEY = "config_key";

	/**
	 * Configure AndHow for a unit test class.
	 * <p>
	 * This does the following:
	 * <ul>
	 * <li>Store the state of AndHow and the configuration locator
	 * <li>Destroy the configured state of AndHow so it is unconfigured
	 * <li>Remove environment related loaders that would be unwanted during unit testing
	 * <li>Specify the classpath of a properties file to be used for configuration
	 * for this test, as spec'ed in the constructor to this class.
	 * </ul>
	 *
	 * @param context Passed by JUnit prior to any test in the class
	 * @throws Exception
	 */
	public void beforeAll(ExtensionContext context) throws Exception {

		// remove current core and keep to be later restored
		getPerTestClassStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));

		String fullPath = expandPath(getClasspathFile(context), context);
		verifyClassPath(fullPath);

		// New config instance created just as needed for testing
		_config = buildConfig(fullPath);

		// Remove current locator and replace w/ one that always returns a custom config
		getPerTestClassStore(context).put(
				CONFIG_KEY,
				AndHowTestUtils.setAndHowInProcessConfig(_config));

		System.out.println("CFF: beforeAll: filePath = " + fullPath);
	}

	public void afterAll(ExtensionContext context) throws Exception {
		Object core = getPerTestClassStore(context).remove(CORE_KEY, AndHowTestUtils.getAndHowCoreClass());
		AndHowTestUtils.setAndHowCore(core);

		AndHowConfiguration<? extends AndHowConfiguration> config =
				getPerTestClassStore(context).remove(CONFIG_KEY, AndHowConfiguration.class);
		AndHowTestUtils.setAndHowInProcessConfig(config);
	}


	/**
	 * Store the state of AndHow before this test, then destroy the state so AndHow is unconfigured.
	 * @param context
	 * @throws Exception
	 */
	public void beforeEach(ExtensionContext context) throws Exception {
		getPerTestMethodStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));

		String fullPath = expandPath(getClasspathFile(context), context);
		verifyClassPath(fullPath);

		// New config instance created just as needed for testing
		_config = buildConfig(fullPath);

		// Remove current locator and replace w/ one that always returns a custom config
		getPerTestMethodStore(context).put(
				CONFIG_KEY,
				AndHowTestUtils.setAndHowInProcessConfig(_config));

		System.out.println("CFF: beforeEach: filePath = " + fullPath);
	}

	/**
	 * Restore the state of AndHow to what it was before this test.
	 * @param context
	 * @throws Exception
	 */
	public void afterEach(ExtensionContext context) throws Exception {
		Object core = getPerTestMethodStore(context).remove(CORE_KEY, AndHowTestUtils.getAndHowCoreClass());
		AndHowTestUtils.setAndHowCore(core);

		AndHowConfiguration<? extends AndHowConfiguration> config =
				getPerTestMethodStore(context).remove(CONFIG_KEY, AndHowConfiguration.class);
		AndHowTestUtils.setAndHowInProcessConfig(config);
	}


	/**
	 * Remove the Loaders that are 'environment' related, meaning they could be set
	 * outside the scope of a unit test.
	 * <p>
	 * These loaders are removed to prevent environment from affecting a test.
	 *
	 * @param config The AndHowConfiguration to remove the loaders from.
	 */
	protected void removeEnvLoaders(AndHowConfiguration<? extends AndHowConfiguration> config) {

		List<Class<? extends StandardLoader>> loaders = config.getDefaultLoaderList();
		loaders.remove(StdSysPropLoader.class);
		loaders.remove(StdEnvVarLoader.class);
		loaders.remove(StdJndiLoader.class);

		config.setStandardLoaders(loaders).setStandardLoaders(loaders);
	}

	/**
	 * Construct a new AndHowConfiguration instance created as needed for testing
	 * @param classpathFile
	 * @return
	 */
	protected AndHowConfiguration<? extends AndHowConfiguration> buildConfig(String classpathFile) {
		AndHowConfiguration<? extends AndHowConfiguration> config = new StdConfig.StdConfigImpl();
		removeEnvLoaders(config);
		config.setClasspathPropFilePath(classpathFile).classpathPropertiesRequired();

		return config;
	}

	/**
	 * Expand the passed classpath to be an absolute classpath if it was a relative one.
	 * <p>
	 * A classpath is determined to be a relative path if it does not start with a slash.
	 * In that case, the package of the path is expanded to include the package of the test
	 * on which this extension is used.
	 * If the path contains a slash, but it does not start with a slash, it is an illegal argument.
	 * Other non-allowed characters in a single classpath are illegal, including spaces and commas.
	 * All paths are trimmed of whitespace before processing.
	 * Null paths will throw a NullPointer.
	 * @param classpath The classpath to the properties file the user wants to use to configure AndHow
	 * @param context The test context this extension is being run within
	 * @return
	 */
	protected String expandPath(String classpath, ExtensionContext context) {
		String fullPath = classpath.trim();

		if (! fullPath.startsWith("/")) {

			String pkgName = "";	//empty is correct for default pkg

			if (context.getRequiredTestClass().getPackage() != null) {
				//getPackage() returns null for the default pkg
				pkgName = context.getRequiredTestClass().getPackage().getName();
			}

			String pkgPath = pkgName.replace(".", "/");
			if (pkgPath.length() > 0) pkgPath = "/" + pkgPath;

			fullPath = pkgPath + "/" + fullPath;
		}

		return fullPath;
	}

	/**
	 * Throws an exception if the passed classpath does not exist
	 * @param classpath
	 */
	protected void verifyClassPath(String classpath) {
		if (getClass().getResource(classpath) == null) {
			throw new IllegalArgumentException(
					"The file '" + classpath + "' could not be found on the classpath.");
		}
	}
}

