package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeEachTest;
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
		if (context.getElement().isPresent()) {
			ConfigFromFileBeforeThisTest cff = context.getElement().get().getAnnotation(ConfigFromFileBeforeThisTest.class);
			return cff.filePath();
		} else {
			throw new IllegalStateException("Expected the @ConfigFromFileBeforeThisTest annotation on the '" +
					context.getRequiredTestMethod() + "' test method.");
		}
	}

	@Override
	protected Class<?>[] getClassesInScopeFromAnnotation(ExtensionContext context) {
		if (context.getElement().isPresent()) {
			ConfigFromFileBeforeThisTest cff = context.getElement().get().getAnnotation(ConfigFromFileBeforeThisTest.class);
			return cff.classesInScope();
		} else {
			throw new IllegalStateException("Expected the @ConfigFromFileBeforeThisTest annotation on the '" +
					context.getRequiredTestMethod() + "' test method.");
		}
	}

	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {
		super.beforeEach(context);
	}

	@Override
	public void afterEach(final ExtensionContext context) throws Exception {
		super.afterEach(context);
	}
}
