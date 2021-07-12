package org.yarnandtail.andhow;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.yarnandtail.andhow.internal.AndHowCore;

public class KillAndHowBeforeAllTests implements BeforeAllCallback, AfterAllCallback {

	/**
	 * The state of AndHow, stored before any tests in the current test class are run.
	 */
	private AndHowCore beforeAllAndHowCore;

	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		beforeAllAndHowCore = AndHowNonProductionUtil.getAndHowCore();
		AndHowNonProductionUtil.destroyAndHowCore();
	}

	@Override
	public void afterAll(ExtensionContext extensionContext) throws Exception {
		AndHowNonProductionUtil.setAndHowCore(beforeAllAndHowCore);
	}

}
