package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.util.List;
import java.util.function.UnaryOperator;

public class ConfigFromFileExt extends ExtensionBase
		implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

	/** The complete path to a properties file on the classpath */
	private String _classpathFile;

	/** The constructed config instance to be used for AndHow */
	protected AndHowConfiguration<? extends AndHowConfiguration> _config;

	/**
	 * New instance - validation of the classpathFile is deferred until use.
	 * @param classpathFile Complete path to a properties file on the classpath
	 */
	public ConfigFromFileExt(String classpathFile) {
		_classpathFile = classpathFile;
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
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {

		// remove current core and keep to be later restored
		getPerTestClassStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));

		// New config instance created just as needed for testing
		_config = buildConfig(expandPath(_classpathFile, context));

		// Remove current locator and replace w/ one that always returns a custom config
		getPerTestClassStore(context).put(
				CONFIG_KEY,
				AndHowTestUtils.setAndHowInProcessConfig(_config));

	}

	@Override
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
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		getPerTestMethodStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));

		// New config instance created just as needed for testing
		_config = buildConfig(expandPath(_classpathFile, context));

		// Remove current locator and replace w/ one that always returns a custom config
		getPerTestMethodStore(context).put(
				CONFIG_KEY,
				AndHowTestUtils.setAndHowInProcessConfig(_config));
	}

	/**
	 * Restore the state of AndHow to what it was before this test.
	 * @param context
	 * @throws Exception
	 */
	@Override
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
		config.setClasspathPropFilePath(classpathFile);

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
			String pkgName = context.getRequiredTestClass().getPackageName();
			String pkgPath = pkgName.replace(".", "/");
			if (pkgPath.length() > 0) pkgPath = "/" + pkgPath;

			fullPath = pkgPath + "/" + fullPath;
		}

		return fullPath;
	}
}

