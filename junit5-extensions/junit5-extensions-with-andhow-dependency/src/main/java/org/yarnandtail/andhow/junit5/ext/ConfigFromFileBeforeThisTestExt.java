package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.junit5.*;
import sun.reflect.annotation.AnnotationType;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

	public Class<? extends Annotation> getAssociatedAnnotation() {
		return ConfigFromFileBeforeThisTest.class;
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
