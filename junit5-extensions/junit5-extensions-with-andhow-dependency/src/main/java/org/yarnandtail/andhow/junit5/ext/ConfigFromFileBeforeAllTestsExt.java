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

	/**
	 * Find the annotated filePath property in the @ConfigFromFileBeforeAllTests annotation on the
	 * test class.
	 * <p>
	 * In the case of a @Nested test, the ConfigFromFileBeforeAllTestsExt is invoked again for each
	 * nested test class so the code recursively hunts up the inheritance chain to find the class
	 * with the annotation.
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected String getAnnotationFilePath(ExtensionContext context) {

		ConfigFromFileBeforeAllTests cff = context.getRequiredTestClass().getAnnotation(ConfigFromFileBeforeAllTests.class);

		if (cff != null) {
			return cff.filePath();
		} else if (context.getParent().isPresent()) {
			return getAnnotationFilePath(context.getParent().get());
		}

		throw new IllegalStateException("Expected the @ConfigFromFileBeforeAllTests annotation on the '" +
				context.getRequiredTestClass() + "' class or a parent class for a @Nested test.");
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