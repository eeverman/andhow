package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFromFileBeforeEachTestExtUnitTest {

	@Test
	void getExtensionType() {
		ConfigFromFileBeforeEachTestExt ext = new ConfigFromFileBeforeEachTestExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_METHOD, type.getStorage());
		assertEquals(ExtensionType.Effect.CONFIGURE, type.getEffect());
		assertEquals(ExtensionType.Scope.EACH_TEST, type.getScope());
	}
}