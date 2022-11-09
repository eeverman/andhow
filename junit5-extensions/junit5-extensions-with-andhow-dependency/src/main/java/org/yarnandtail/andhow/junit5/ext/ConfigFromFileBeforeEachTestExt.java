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

		ConfigFromFileBeforeEachTest cff = context.getRequiredTestClass().getAnnotation(ConfigFromFileBeforeEachTest.class);

		if (cff != null) {
			return cff.value();
		} else if (context.getParent().isPresent()) {
			return getFilePathFromAnnotation(context.getParent().get());
		}

		throw new IllegalStateException("Expected the @ConfigFromFileBeforeEachTest annotation on the '" +
				context.getRequiredTestClass() + "' class or a parent class for a @Nested test.");
	}

	@Override
	protected Class<?>[] getClassesInScopeFromAnnotation(ExtensionContext context) {
		ConfigFromFileBeforeEachTest cff = context.getRequiredTestClass().getAnnotation(ConfigFromFileBeforeEachTest.class);

		if (cff != null) {
			return cff.includeClasses();
		} else if (context.getParent().isPresent()) {
			return getClassesInScopeFromAnnotation(context.getParent().get());
		}

		throw new IllegalStateException("Expected the @ConfigFromFileBeforeEachTest annotation on the '" +
				context.getRequiredTestClass() + "' class or a parent class for a @Nested test.");
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
