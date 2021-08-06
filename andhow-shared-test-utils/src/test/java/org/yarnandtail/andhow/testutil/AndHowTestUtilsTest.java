package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.AndHowCore;

import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class AndHowTestUtilsTest {

	@BeforeEach
	void setUp() {
		AndHow._fullyInitialize();
	}

	@AfterEach
	void tearDown() {
		AndHow._fullyDestroy();
	}

	@Test
	void testTheTest() {
		assertNotNull(AndHowTestUtils.getAndHow());
		assertNotNull(AndHowTestUtils.getAndHowCore());
		assertNotNull(AndHowTestUtils.setAndHowInitialization(null));
		assertFalse(AndHowTestUtils.setAndHowInitializing(true));
		assertNull(AndHowTestUtils.setAndHowInProcessConfig(null));
		assertNull(AndHowTestUtils.setAndHowConfigLocator(null));
	}
	
	@Test
	void invokeAndHowInstance() {
		AndHow._fullyDestroy();
		
		Object single = AndHowTestUtils.invokeAndHowInstance();
		assertNotNull(single);
		assertSame(single, AndHowTestUtils.invokeAndHowInstance());
	}

	@Test
	void killAndHowFully() {
		final AndHowConfiguration config1 = new AndHowConfiguration() {};
		final AndHowConfiguration config2 = new AndHowConfiguration() {};

		UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator1 =
				(c) -> { return config1; };

		//Set a few things that are normally null/false
		AndHowTestUtils.setAndHowInitializing(true);
		AndHowTestUtils.setAndHowConfigLocator(configLocator1);
		AndHowTestUtils.setAndHowInProcessConfig(config2);

		AndHowTestUtils.killAndHowFully();	//kill

		//Everying should be null-ish
		assertNull(AndHowTestUtils.getAndHow());
		assertNull(AndHowTestUtils.getAndHowCore());
		assertNull(AndHowTestUtils.setAndHowInitialization(null));
		assertFalse(AndHowTestUtils.setAndHowInitializing(true));
		assertNull(AndHowTestUtils.setAndHowInProcessConfig(null));
		assertNull(AndHowTestUtils.setAndHowConfigLocator(null));
	}

	@Test
	void getAndSetAndHow() {
		AndHow ahOrg = (AndHow) AndHowTestUtils.getAndHow();

		assertNotNull(ahOrg);

		assertThrows(
				RuntimeException.class,
				() -> AndHowTestUtils.setAndHow("A string, which is not an AndHow"),
				"Cannot set the andhow instance to something other than an AndHow"
		);

		AndHow ahReturnedFromSet = (AndHow) AndHowTestUtils.setAndHow(null);

		assertSame(ahOrg, ahReturnedFromSet);

		assertNull(AndHowTestUtils.setAndHow(ahOrg));

		assertSame(ahOrg, AndHowTestUtils.getAndHow());
	}


	@Test
	void getAndSetAndHowCore() {

		//
		// AndHow singleton is not null
		AndHowCore ahCore = (AndHowCore) AndHowTestUtils.getAndHowCore();

		assertNotNull(ahCore);

		assertThrows(
				RuntimeException.class,
				() -> AndHowTestUtils.setAndHowCore("A string, which is not an AndHowCore"),
				"Cannot set the core to something other than an AndHowCore"
		);

		AndHowCore ahReturnedFromSet = (AndHowCore) AndHowTestUtils.setAndHowCore(null);

		assertSame(ahCore, ahReturnedFromSet);

		assertNull(AndHowTestUtils.setAndHowCore(ahCore));

		assertSame(ahCore, AndHowTestUtils.getAndHowCore());

		//
		// AndHow singleton is null
		AndHowTestUtils.setAndHow(null);

		assertNull(AndHowTestUtils.getAndHowCore());
		assertNull(AndHowTestUtils.setAndHowCore(null), "ok to set core null if andhow is null");

		assertThrows(
				RuntimeException.class,
				() -> AndHowTestUtils.setAndHowCore(ahCore),
				"Cannot set a non-null value if the andhow instance is null"
		);
	}

	@Test
	void setAndHowInitialization() {
		AndHow.Initialization myInit = new AndHow.Initialization();

		AndHow.Initialization ahInit1stReturnedFromSet =
				AndHowTestUtils.setAndHowInitialization(myInit);

		assertNotNull(ahInit1stReturnedFromSet);
		assertNotSame(myInit, ahInit1stReturnedFromSet);

		assertThrows(
				RuntimeException.class,
				() -> AndHowTestUtils.setAndHowInitialization("A string, which is the wrong class"),
				"Cannot set the andhow init to something other than an Initialization"
		);

		assertSame(myInit, AndHowTestUtils.setAndHowInitialization(ahInit1stReturnedFromSet));
		assertSame(ahInit1stReturnedFromSet, AndHowTestUtils.setAndHowInitialization(null));
		assertNull(AndHowTestUtils.setAndHowInitialization(myInit));
	}

	@Test
	void setAndHowConfigLocator() {

		final AndHowConfiguration config1 = new AndHowConfiguration() {};
		final AndHowConfiguration config2 = new AndHowConfiguration() {};

		UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator1 =
				(c) -> { return config1; };
		UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator2 =
				(c) -> { return config2; };


		assertNull(AndHowTestUtils.setAndHowConfigLocator(configLocator1));
		assertSame(configLocator1, AndHowTestUtils.setAndHowConfigLocator(configLocator2));
		assertSame(configLocator2, AndHowTestUtils.setAndHowConfigLocator(null));
		assertNull(AndHowTestUtils.setAndHowConfigLocator(configLocator1));
		assertSame(configLocator1, AndHowTestUtils.setAndHowConfigLocator(configLocator2));
	}

	@Test
	void setAndHowInProcessConfig() {
		final AndHowConfiguration config1 = new AndHowConfiguration() {};
		final AndHowConfiguration config2 = new AndHowConfiguration() {};

		assertNull(AndHowTestUtils.setAndHowInProcessConfig(config1));
		assertThrows(
				RuntimeException.class,
				() -> AndHowTestUtils.setAndHowInProcessConfig("A string, which is the wrong class"),
				"Cannot set the andhow config to something other than an Configuration"
		);
		assertSame(config1, AndHowTestUtils.setAndHowInProcessConfig(config2));
		assertSame(config2, AndHowTestUtils.setAndHowInProcessConfig(null));
		assertNull(AndHowTestUtils.setAndHowInProcessConfig(config1));
	}

	@Test
	void setAndHowInitializing() {
		assertFalse(AndHowTestUtils.setAndHowInitializing(true));
		assertTrue(AndHowTestUtils.setAndHowInitializing(false));
		assertThrows(RuntimeException.class, () -> AndHowTestUtils.setAndHowInitializing(null));
	}

	@Test
	void getFindingConfig() {
		//This is really, really internal state, so there is no set method.
		assertFalse(AndHowTestUtils.getFindingConfig().get());
	}

}