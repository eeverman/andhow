package org.yarnandtail.andhow.junit5.ext;

/**
 * Rich enum to describe a JUnit extension, including key properties for how the extension
 * interacts with runtime storage and how extensions interact with each other.
 */
public enum ExtensionType {
	CONFIG_EACH_TEST(Storage.TEST_METHOD, Scope.EACH_TEST, Effect.CONFIGURE),
	CONFIG_ALL_TESTS(Storage.TEST_INSTANCE, Scope.TEST_CLASS, Effect.CONFIGURE),
	CONFIG_THIS_TEST(Storage.TEST_METHOD, Scope.SINGLE_TEST, Effect.CONFIGURE),
	KILL_EACH_TEST(Storage.TEST_METHOD, Scope.EACH_TEST, Effect.KILL),
	KILL_ALL_TESTS(Storage.TEST_INSTANCE, Scope.TEST_CLASS, Effect.KILL),
	KILL_THIS_TEST(Storage.TEST_METHOD, Scope.SINGLE_TEST, Effect.KILL),
	MODIFY_ENV_EACH_TEST(Storage.TEST_METHOD, Scope.EACH_TEST, Effect.ENVIRONMENT),
	MODIFY_ENV_ALL_TESTS(Storage.TEST_INSTANCE, Scope.TEST_CLASS, Effect.ENVIRONMENT),
	MODIFY_ENV_THIS_TEST(Storage.TEST_METHOD, Scope.SINGLE_TEST, Effect.ENVIRONMENT),
	OTHER_EACH_TEST(Storage.TEST_METHOD, Scope.EACH_TEST, Effect.OTHER),
	OTHER_ALL_TESTS(Storage.TEST_INSTANCE, Scope.TEST_CLASS, Effect.OTHER),
	OTHER_THIS_TEST(Storage.TEST_METHOD, Scope.SINGLE_TEST, Effect.OTHER),
	OTHER(Storage.MIXTURE, Scope.MIXTURE, Effect.OTHER),
	;

	private Storage _storage;
	private Scope _scope;
	private Effect _effect;

	private ExtensionType(Storage storage, Scope scope, Effect effect) {
		_storage = storage;
		_scope = scope;
		_effect = effect;
	}

	public Storage getStorage() {
		return _storage;
	}

	public Scope getScope() {
		return _scope;
	}

	public Effect getEffect() {
		return _effect;
	}

	static enum Storage {
		/** State should be stored in the context of the test instance */
		TEST_INSTANCE,
		/** State should be stored in the context of the test method */
		TEST_METHOD,
		/** Custom storage / mixed usage - No one correct answer */
		MIXTURE
	}

	/**
	 * The level at which this extension (and any associated annotation) operate at.
	 * <p>
	 * Some extensions operate at the test class level, where they use beforeAll/afterAll
	 * events, others at the test method level w/ beforeEach/afterEach events.
	 */
	static enum Scope {
		/** Uses BeforeAll and AfterAll class-level events */
		TEST_CLASS,
		/** Uses BeforeEach and AfterEach events applied to all tests in the class,
		 *  (annotated at the class level) */
		EACH_TEST,
		/** Uses BeforeEach and AfterEach events applied to a single test method,
		 *  (annotated at the method level) */
		SINGLE_TEST,
		/** Uses a mixture of BeforeAll BeforeEach, etc..  Annotations may be on the class or method */
		MIXTURE
	}

	/**
	 * The effect of the extension, within the AndHow world.
	 */
	static enum Effect {
		/** Configures AndHow */
		CONFIGURE,
		/** Kills the AndHow configured state */
		KILL,
		/** Affects the environment that AndHow could use for configuration */
		ENVIRONMENT,
		/** Other effect that does not interact with AndHow */
		OTHER
	}
}
