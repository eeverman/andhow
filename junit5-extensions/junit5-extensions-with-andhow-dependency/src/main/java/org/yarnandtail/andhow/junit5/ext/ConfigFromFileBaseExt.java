package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.StandardLoader;
import org.yarnandtail.andhow.load.std.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.platform.commons.support.SearchOption.INCLUDE_ENCLOSING_CLASSES;

/**
 * Implementation of an ExtensionBase that configures AndHow from a single properties
 * file.  It is the base class to be used in one of three modes:
 * <li>BeforeAll/AfterAll Registered on a test class</li>
 * <li>BeforeEach/AfterEach Registered on a test class to apply to all methods</li>
 * <li>BeforeEach/AfterEach Registered on a test method to apply to a single test</li>
 * Mixing purposes (i.e. a single instance registered for use for more than one mode)
 * can result in confused state.
 *
 * Note that JUnit has an unexpected handling of nested test classes when the parent class and the
 * nested test class have the same annotation:  JUnit creates only a single instance of an Extension.
 * Thus, no state can be kept in the extension other than via the context store mechanism, or, for
 * config info, read configuration from annotation parameters EACH TIME.  Configuration parameters
 * cannot be cached (other than on the context store) because the cached values will bleed over from
 * the parent class to the child class.  For instance, if the parent class is annotated with:
 * @ConfigFromFileBeforeAllTests(filePath = "parent.properties")
 * and the nested class is annotated with:
 * @ConfigFromFileBeforeAllTests(filePath = "child.properties")
 * storing the config value of the parent in the extension means that the cached value will be found
 * and used when the child is configured.  Read from the annotation each time!
 */
public abstract class ConfigFromFileBaseExt extends ExtensionBase {

	/** Key to store the AndHowCore (if any) of AndHow. */
	protected static final String CORE_KEY = "core_key";

	/** Key to store the in-process configuration (if any) of the AndHow instance.
	 * When is the inProcessConfig non-null for AndHow?  Only when findConfig()
	 * has been called but AndHow has not yet initialized.
	 */
	protected static final String CONFIG_KEY = "config_key";

	/** The complete path to a properties file on the classpath */
	private String _classpathFile;

	/**
	 * This Optional has unique usage:
	 * null:  (Optional not initialized) means that this class was constructed via
	 * the default constructor, which likely means it is being used via an
	 * annotation.  The value for this config param should be discovered at runtime
	 * from the annotation.
	 * Optional.isPresent() == false:  The value was set to empty in the java
	 * constructor.  This class was constructed w/ a standard constructor that
	 * spec'ed this value as empty or null.  DON'T TRY TO FIND A VALUE IN AN
	 * ANNOTATION - THERE ISN'T ONE.
	 * Optional.isPresent() == true:  This class was constructed w/ a standard
	 * constructor that spec'ed this value.  DON'T TRY TO FIND A VALUE IN AN
	 * ANNOTATION - THERE ISN'T ONE.
	 */
	private Optional<Class<?>[]> _classesInScope;

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
		_classesInScope = Optional.empty();
	}

	public ConfigFromFileBaseExt(String classpathFile, Class<?>[] configClasses) {
		if (classpathFile == null) {
			throw new IllegalArgumentException("The classpath properties file path cannot be null.");
		}

		_classpathFile = classpathFile;
		_classesInScope = Optional.of(configClasses);
	}

	/**
	 * Empty constructor used when the \@ConfigFromFile annotation is used.
	 *
	 * When the empty construct is used, the classpathFile and classesInScope are
	 * found via the \@ConfigFromFile annotation filePath property.
	 * <p>
	 * <strong>This constructor cannot be used to create an instance of this class
	 * other than via the annotation mechanism.</strong>  When this constructor is
	 * used, the class assumes it will find its configuration in an annotation,
	 * which will not be present other than within the context of a JUnit test.
	 */
	public ConfigFromFileBaseExt() {	}

	/**
	 * When this Extension is being used in association w/ an annotation, this returns
	 * that class.
	 * @return
	 */
	protected abstract String getFilePathFromAnnotation(ExtensionContext context);

	/**
	 * When this Extension is being used in association w/ an annotation, this returns
	 * the optional configClasses List.
	 * @return
	 */
	protected abstract Class<?>[] getClassesInScopeFromAnnotation(ExtensionContext context);


	//
	//The beforeAll/beforeEach/afterAllAfter each can probably be consolidated into
	//generic before and after at this point, since the storage choice is abstracted.


	/**
	 * Configure AndHow for a unit test class or test method.
	 * <p>
	 * The differences between class-level and method level is state storage.
	 * That is determined by the ExtensionType returned by getExtensionType().
	 * This method does the following:
	 * <ul>
	 * <li>Store the state of AndHow and the configuration locator at the appropriate storage level.
	 * <li>Destroy the configured state of AndHow so it is unconfigured
	 * <li>Remove environment related loaders that would be unwanted during unit testing
	 * <li>Specify the classpath of a properties file to be used for configuration
	 * for this test, as spec'ed in the constructor to this class.
	 * </ul>
	 *
	 * @param context Passed by JUnit prior to any test in the class
	 * @throws Exception
	 */
	public void beforeAllOrEach(ExtensionContext context) throws Exception {

		// Store the old core and set the current core to null
		getStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));

		// New config instance created just as requested for testing
		_config = buildConfig(context);

		// Remove current locator and replace w/ one that always returns a custom config
		getStore(context).put(CONFIG_KEY, AndHowTestUtils.setAndHowInProcessConfig(_config));
	}

	/**
	 * Restore the state of AndHow to what it was before this test or this test class.
	 * @param context
	 * @throws Exception
	 */
	public void afterAllOrEach(ExtensionContext context) throws Exception {
		Object core = getStore(context).remove(CORE_KEY, AndHowTestUtils.getAndHowCoreClass());
		AndHowTestUtils.setAndHowCore(core);

		AndHowConfiguration<? extends AndHowConfiguration> config =
				getStore(context).remove(CONFIG_KEY, AndHowConfiguration.class);
		AndHowTestUtils.setAndHowInProcessConfig(config);
	}

	protected <A extends Annotation> A findAnnotation(Class<A> annotationClass, ExtensionContext context) {

		Optional<A> ann = null;
		Optional<AnnotatedElement> ae = context.getElement();

		switch (getExtensionType().getScope()) {
			case TEST_CLASS:
			case EACH_TEST:

				// Will EACH_TEST annotation on the class result in method level elements?

				// This method will search superclasses
				ann = AnnotationSupport.findAnnotation(context.getElement().get(), annotationClass);

				if (! ann.isPresent()) {
					//This method will hunt nested classes
					ann = AnnotationSupport.findAnnotation(
							context.getRequiredTestClass(), annotationClass, INCLUDE_ENCLOSING_CLASSES);
				}

				if (! ann.isPresent()) {
					throw new IllegalStateException("Expected the @" + annotationClass.getName() + " annotation on the '" +
							context.getRequiredTestClass() + "' class, superclass or a parent class for a @Nested test.");
				}

				break;
			case SINGLE_TEST:
				ann = AnnotationSupport.findAnnotation(context.getElement(), annotationClass);

				if (! ann.isPresent()) {
					throw new IllegalStateException("Expected the @" + annotationClass.getName() + " annotation on the '" +
							context.getRequiredTestMethod().getName() + "' test method of " + context.getRequiredTestClass());
				}

				break;
			default:
				throw new IllegalStateException("Cannot call findAnnotation() if the getExtensionType() returns " +
						"a type that doesn't use TEST_CLASS, EACH_TEST or SINGLE_TEST scope.");
		}

 		return ann.get();

	}

	/**
	 * Find the user configured properties file path.
	 * <p>
	 * If configured via an annotation, read the annotated value EVERY TIME, DO NOT CACHE THE VALUE.
	 * @param context
	 * @return
	 */
	protected String getClasspathFile(ExtensionContext context) {
		if (_classpathFile == null) {
			return getFilePathFromAnnotation(context);
		} else {
			return _classpathFile;
		}
	}

	/**
	 * Find the user configured classes in scope.
	 * <p>
	 * If configured via an annotation, read the annotated value EVERY TIME, DO NOT CACHE THE VALUE.
	 * @param context
	 * @return A List that is never null but may be empty.
	 */
	protected List<Class<?>> getClassesInScope(ExtensionContext context) {
		if (_classesInScope == null) {
			//Not initialized, so this is annotation construction
			return stem(Arrays.asList(getClassesInScopeFromAnnotation(context)));
		} else if (_classesInScope.isPresent()) {
			return stem(Arrays.asList(_classesInScope.get()));	//Std java class construction was used
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Generate a comprehensive list of classes that includes inner class which might contain
	 * AndHow properties.
	 */
	protected List<Class<?>> stem(List<Class<?>> clazzes) {
		final List<Class<?>> stemmed = new ArrayList<>();
		stemmed.addAll(clazzes);

		int index = 0;

		while (index < stemmed.size()) {
			Class<?> c = stemmed.get(index);

			Arrays.stream(c.getDeclaredClasses()).forEach( cc -> {
					if (Modifier.isStatic(cc.getModifiers())) {
						stemmed.add(cc);
					}
			});

			index++;
		}

		return stemmed;
	}

	/**
	 * Construct a new AndHowConfiguration instance created as needed for testing
	 *
	 * @param context
	 * @return
	 */
	protected AndHowConfiguration<? extends AndHowConfiguration> buildConfig(ExtensionContext context) {

		String fullPath = expandPath(getClasspathFile(context), context);
		verifyClassPath(fullPath);
		List<Class<?>> clazzes = getClassesInScope(context);

		AndHowConfiguration<? extends AndHowConfiguration> config = new StdConfig.StdConfigImpl();
		removeEnvLoaders(config);
		config.setClasspathPropFilePath(fullPath).classpathPropertiesRequired();

		if (! clazzes.isEmpty()) {
			AndHowTestUtils.setConfigurationOverrideGroups(config, clazzes);
		}

		return config;
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

