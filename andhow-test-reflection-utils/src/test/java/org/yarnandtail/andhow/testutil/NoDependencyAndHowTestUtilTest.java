package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.AndHowCore;

import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class NoDependencyAndHowTestUtilTest {

	@BeforeEach
	void setUp() {
		AndHow.fullyInitialize();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testTheTest() {
		assertNotNull(NoDependencyAndHowTestUtil.getAndHow());
		assertNotNull(NoDependencyAndHowTestUtil.getAndHowCore());
		assertNotNull(NoDependencyAndHowTestUtil.setAndHowInitialization(null));
		assertFalse(NoDependencyAndHowTestUtil.setAndHowInitializing(true));
		assertNull(NoDependencyAndHowTestUtil.setAndHowInProcessConfig(null));
		assertNull(NoDependencyAndHowTestUtil.setAndHowConfigLocator(null));
	}

	@Test
	void killAndHowFully() {
	}

	@Test
	void getAndSetAndHow() {
		AndHow ahOrg = (AndHow)NoDependencyAndHowTestUtil.getAndHow();

		assertNotNull(ahOrg);

		assertThrows(
				RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.setAndHow("A string, which is not an AndHow"),
				"Cannot set the andhow instance to something other than an AndHow"
		);

		AndHow ahReturnedFromSet = (AndHow)NoDependencyAndHowTestUtil.setAndHow(null);

		assertSame(ahOrg, ahReturnedFromSet);

		assertNull(NoDependencyAndHowTestUtil.setAndHow(ahOrg));

		assertSame(ahOrg, NoDependencyAndHowTestUtil.getAndHow());
	}


	@Test
	void getAndSetAndHowCore() {

		//
		// AndHow singleton is not null
		AndHowCore ahCore = (AndHowCore)NoDependencyAndHowTestUtil.getAndHowCore();

		assertNotNull(ahCore);

		assertThrows(
				RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.setAndHowCore("A string, which is not an AndHowCore"),
				"Cannot set the core to something other than an AndHowCore"
		);

		AndHowCore ahReturnedFromSet = (AndHowCore)NoDependencyAndHowTestUtil.setAndHowCore(null);

		assertSame(ahCore, ahReturnedFromSet);

		assertNull(NoDependencyAndHowTestUtil.setAndHowCore(ahCore));

		assertSame(ahCore, NoDependencyAndHowTestUtil.getAndHowCore());

		//
		// AndHow singleton is null
		NoDependencyAndHowTestUtil.setAndHow(null);

		assertNull(NoDependencyAndHowTestUtil.getAndHowCore());
		assertNull(NoDependencyAndHowTestUtil.setAndHowCore(null), "ok to set core null if andhow is null");

		assertThrows(
				RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.setAndHowCore(ahCore),
				"Cannot set a non-null value if the andhow instance is null"
		);
	}

	@Test
	void setAndHowInitialization() {
		AndHow.Initialization myInit = new AndHow.Initialization();

		AndHow.Initialization ahInit1stReturnedFromSet =
				NoDependencyAndHowTestUtil.setAndHowInitialization(myInit);

		assertNotNull(ahInit1stReturnedFromSet);
		assertNotSame(myInit, ahInit1stReturnedFromSet);

		assertThrows(
				RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.setAndHowInitialization("A string, which is the wrong class"),
				"Cannot set the andhow init to something other than an Initialization"
		);

		assertSame(myInit, NoDependencyAndHowTestUtil.setAndHowInitialization(ahInit1stReturnedFromSet));
		assertSame(ahInit1stReturnedFromSet, NoDependencyAndHowTestUtil.setAndHowInitialization(null));
		assertNull(NoDependencyAndHowTestUtil.setAndHowInitialization(myInit));
	}

	@Test
	void setAndHowConfigLocator() {

		final AndHowConfiguration config1 = new AndHowConfiguration() {};
		final AndHowConfiguration config2 = new AndHowConfiguration() {};

		UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator1 =
				(c) -> { return config1; };
		UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator2 =
				(c) -> { return config2; };


		assertNull(NoDependencyAndHowTestUtil.setAndHowConfigLocator(configLocator1));
		assertSame(configLocator1, NoDependencyAndHowTestUtil.setAndHowConfigLocator(configLocator2));
		assertSame(configLocator2, NoDependencyAndHowTestUtil.setAndHowConfigLocator(null));
		assertNull(NoDependencyAndHowTestUtil.setAndHowConfigLocator(configLocator1));
		assertSame(configLocator1, NoDependencyAndHowTestUtil.setAndHowConfigLocator(configLocator2));
	}

	@Test
	void setAndHowInProcessConfig() {
		final AndHowConfiguration config1 = new AndHowConfiguration() {};
		final AndHowConfiguration config2 = new AndHowConfiguration() {};

		assertNull(NoDependencyAndHowTestUtil.setAndHowInProcessConfig(config1));
		assertThrows(
				RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.setAndHowInProcessConfig("A string, which is the wrong class"),
				"Cannot set the andhow config to something other than an Configuration"
		);
		assertSame(config1, NoDependencyAndHowTestUtil.setAndHowInProcessConfig(config2));
		assertSame(config2, NoDependencyAndHowTestUtil.setAndHowInProcessConfig(null));
		assertNull(NoDependencyAndHowTestUtil.setAndHowInProcessConfig(config1));
	}

	@Test
	void setAndHowInitializing() {
		assertFalse(NoDependencyAndHowTestUtil.setAndHowInitializing(true));
		assertTrue(NoDependencyAndHowTestUtil.setAndHowInitializing(false));
		assertThrows(RuntimeException.class, () -> NoDependencyAndHowTestUtil.setAndHowInitializing(null));
	}

	@Test
	void getFindingConfig() {
		//This is really, really internal state, so there is no set method.
		assertFalse(NoDependencyAndHowTestUtil.getFindingConfig().get());
	}

	@Test
	void getClassByNameTest() {
		Class<?> thisClass = NoDependencyAndHowTestUtil.getClassByName(
				"org.yarnandtail.andhow.testutil.NoDependencyAndHowTestUtilTest");

		assertSame(this.getClass(), thisClass);

		assertThrows(RuntimeException.class,
				() -> NoDependencyAndHowTestUtil.getClassByName("I.dont.exist.Something"));
	}
}