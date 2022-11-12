package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestoreSysPropsAfterThisTestExtTest {

	@Test
	void getExtensionType() {
		RestoreSysPropsAfterThisTestExt ext = new RestoreSysPropsAfterThisTestExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_METHOD, type.getStorage());
		assertEquals(ExtensionType.Effect.OTHER, type.getEffect());
		assertEquals(ExtensionType.Scope.SINGLE_TEST, type.getScope());
	}
}