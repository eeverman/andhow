package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeAllTests;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeThisTest;

public class ConfigFromFileBeforeThisTestExt extends ConfigFromFileBaseExt
		implements BeforeEachCallback, AfterEachCallback {

	/**
	 * Empty constructor used when the \@ConfigFromFileBeforeThisTest annotation is used.
	 *
	 * When the empty construct is used, the classpathFile is found via the
	 * \@ConfigFromFile annotation filePath property.
	 */
	public ConfigFromFileBeforeThisTestExt() {
		super();
	}

	/**
	 * New instance - validation of the classpathFile is deferred until use.
	 * @param classpathFile Complete path to a properties file on the classpath
	 */
	public ConfigFromFileBeforeThisTestExt(String classpathFile) {
		super(classpathFile);
	}

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.CONFIG_THIS_TEST;
	}

	/**
	 * Find the annotated filePath property in the @ConfigFromFileBeforeThisTest annotation on the
	 * test class.
	 * <p>
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected String getFilePathFromAnnotation(ExtensionContext context) {
		return findAnnotation(ConfigFromFileBeforeThisTest.class, context).value();
	}

	@Override
	protected Class<?>[] getClassesInScopeFromAnnotation(ExtensionContext context) {
		return findAnnotation(ConfigFromFileBeforeThisTest.class, context).includeClasses();
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
