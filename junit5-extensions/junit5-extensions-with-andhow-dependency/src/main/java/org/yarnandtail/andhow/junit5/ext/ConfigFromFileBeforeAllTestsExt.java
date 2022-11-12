package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.ConfigFromFileBeforeAllTests;

import java.lang.annotation.Annotation;

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
	public ExtensionType getExtensionType() {
		return ExtensionType.CONFIG_ALL_TESTS;
	}

	@Override
	public Class<? extends Annotation> getAssociatedAnnotation() {
		return ConfigFromFileBeforeAllTests.class;
	}

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		super.beforeAllOrEach(context);
	}

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		super.afterAllOrEach(context);
	}
}
