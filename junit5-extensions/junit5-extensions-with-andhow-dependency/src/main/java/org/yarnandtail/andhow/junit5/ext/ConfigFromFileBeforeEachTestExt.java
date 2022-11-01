package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
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
	protected String getAnnotationFilePath(ExtensionContext context) {
		if (context.getElement().isPresent()) {
			ConfigFromFileBeforeEachTest cff = context.getElement().get().getAnnotation(ConfigFromFileBeforeEachTest.class);
			return cff.filePath();
		} else {
			throw new IllegalStateException("Expected the @ConfigFromFileBeforeEachTest annotation on the '" +
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
