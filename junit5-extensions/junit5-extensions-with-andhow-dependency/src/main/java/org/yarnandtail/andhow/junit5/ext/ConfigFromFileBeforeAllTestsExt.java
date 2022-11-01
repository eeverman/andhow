package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeAllTests;

public class ConfigFromFileBeforeAllTestsExt extends ConfigFromFileBaseExt
		implements BeforeAllCallback, AfterAllCallback {

	/**
	 * Empty constructor used when the \@ConfigFromFileBeforeThisTest annotation is used.
	 *
	 * When the empty construct is used, the classpathFile is found via the
	 * \@ConfigFromFile annotation filePath property.
	 */
	public ConfigFromFileBeforeAllTestsExt() {
		super();
	}

	/**
	 * New instance - validation of the classpathFile is deferred until use.
	 * @param classpathFile Complete path to a properties file on the classpath
	 */
	public ConfigFromFileBeforeAllTestsExt(String classpathFile) {
		super(classpathFile);
	}

	@Override
	protected String getAnnotationFilePath(ExtensionContext context) {
		if (context.getElement().isPresent()) {
			ConfigFromFileBeforeAllTests cff = context.getElement().get().getAnnotation(ConfigFromFileBeforeAllTests.class);
			return cff.filePath();
		} else {
			throw new IllegalStateException("Expected the @ConfigFromFileBeforeAllTests annotation on the '" +
					context.getRequiredTestMethod() + "' test method.");
		}
	}

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		super.beforeAll(context);
	}

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		super.afterAll(context);
	}
}
