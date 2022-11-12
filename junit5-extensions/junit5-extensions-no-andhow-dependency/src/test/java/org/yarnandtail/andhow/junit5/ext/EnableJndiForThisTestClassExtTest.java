package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnableJndiForThisTestClassExtTest {

	@Test
	void getExtensionType() {
		EnableJndiForThisTestClassExt ext = new EnableJndiForThisTestClassExt();
		ExtensionType type = ext.getExtensionType();

		assertEquals(ExtensionType.Storage.TEST_INSTANCE, type.getStorage());
		assertEquals(ExtensionType.Effect.ENVIRONMENT, type.getEffect());
		assertEquals(ExtensionType.Scope.TEST_CLASS, type.getScope());
	}
}