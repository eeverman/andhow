package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeAllTests;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeEachTest;

public class ConfigFromFileBeforeEachTestExt extends ConfigFromFileBaseExt
		implements BeforeEachCallback, AfterEachCallback {

	/**
	 * Empty constructor used when the \@ConfigFromFileBeforeThisTest annotation is used.
	 *
	 * When the empty construct is used, the classpathFile is found via the
	 * \@ConfigFromFile annotation filePath property.
	 */
	public ConfigFromFileBeforeEachTestExt() {
		super();
	}

	/**
	 * New instance - validation of the classpathFile is deferred until use.
	 * @param classpathFile Complete path to a properties file on the classpath
	 */
	public ConfigFromFileBeforeEachTestExt(String classpathFile) {
		super(classpathFile);
	}

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.CONFIG_EACH_TEST;
	}

	/**
	 * Find the annotated filePath property in the @ConfigFromFileBeforeEachTest annotation on the
	 * test class.
	 * <p>
	 * In the case of a @Nested test, the ConfigFromFileBeforeEachTestExt is invoked again for each
	 * nested test class so the code recursively hunts up the inheritance chain to find the class
	 * with the annotation.
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected String getFilePathFromAnnotation(ExtensionContext context) {
		return findAnnotation(ConfigFromFileBeforeEachTest.class, context).value();
	}

	@Override
	protected Class<?>[] getClassesInScopeFromAnnotation(ExtensionContext context) {
		return findAnnotation(ConfigFromFileBeforeEachTest.class, context).includeClasses();
	}

	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {
		super.beforeAllOrEach(context);
	}

	@Override
	public void afterEach(final ExtensionContext context) throws Exception {
		super.afterAllOrEach(context);
	}
}
