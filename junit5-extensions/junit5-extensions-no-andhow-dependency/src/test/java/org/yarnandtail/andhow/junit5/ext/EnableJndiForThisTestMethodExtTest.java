package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnableJndiForThisTestMethodExtTest {

	@Test
	void getExtensionType() {
		EnableJndiForThisTestMethodExt ext = new EnableJndiForThisTestMethodExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_METHOD, type.getStorage());
		assertEquals(ExtensionType.Effect.ENVIRONMENT, type.getEffect());
		assertEquals(ExtensionType.Scope.SINGLE_TEST, type.getScope());
	}
}