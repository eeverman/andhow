package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestoreSysPropsAfterEachTestExtTest {

	@Test
	void getExtensionType() {
		RestoreSysPropsAfterEachTestExt ext = new RestoreSysPropsAfterEachTestExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.MIXTURE, type.getStorage());
		assertEquals(ExtensionType.Effect.OTHER, type.getEffect());
		assertEquals(ExtensionType.Scope.MIXTURE, type.getScope());
	}
}