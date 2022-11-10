package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigFromFileBeforeThisTestExtUnitTest {

	@Test
	void getExtensionType() {
		ConfigFromFileBeforeThisTestExt ext = new ConfigFromFileBeforeThisTestExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_METHOD, type.getStorage());
		assertEquals(ExtensionType.Effect.CONFIGURE, type.getEffect());
		assertEquals(ExtensionType.Scope.SINGLE_TEST, type.getScope());
	}
}