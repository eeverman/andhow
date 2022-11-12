package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFromFileBeforeAllTestsExtUnitTest {

	@Test
	void getExtensionType() {
		ConfigFromFileBeforeAllTestsExt ext = new ConfigFromFileBeforeAllTestsExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_INSTANCE, type.getStorage());
		assertEquals(ExtensionType.Effect.CONFIGURE, type.getEffect());
		assertEquals(ExtensionType.Scope.TEST_CLASS, type.getScope());
	}
}