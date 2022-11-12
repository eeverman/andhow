package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import static org.yarnandtail.andhow.junit5.ext.ExtensionType.Storage.*;

public abstract class ExtensionBase {

	/**
	 * The ExtensionType that describes the basic behaviors and attributes of the extension.
	 *
	 * @return
	 */
	protected abstract ExtensionType getExtensionType();


	/**
	 * Get the appropriate Store based on the getExtensionType() value of the Extension.
	 *
	 * @param context
	 * @return
	 * @throws IllegalStateException If the subclass's getExtensionType() returns a value that has
	 * a storage type of Storage.MIXTURE.  Those implementations must determine the appropriate
	 * storage themselves.
	 */
	protected ExtensionContext.Store getStore(ExtensionContext context) {
		ExtensionType type = getExtensionType();

		switch (type.getStorage()) {
			case TEST_INSTANCE:
				return getPerTestClassStore(context);
			case TEST_METHOD:
				return getPerTestMethodStore(context);
			default:
				throw new IllegalStateException("Cannot call getStore() if the getExtensionType() returns " +
						"a type that doesn't use TEST_INSTANCE or TEST_METHOD storage.");
		}

	}

	/**
	 * Create or return a unique storage space, which is unique per the test class.
	 *
	 * More specifically, the store is unique per the combination of:
	 * <ul>
	 *  <li>{@code this} extension class</li>
	 *  <li>The test class instance that is invoking this extension</li>
	 * </ul>
	 *
	 * This method should not be called for storage + retrieval related to a test method, since
	 * it will not be unique enough (other methods could overwrite its value).
	 *
	 * @param context  The ExtensionContext passed in to one of the callback methods.
	 * @return The store that can be used to store and retrieve values.
	 */
	protected ExtensionContext.Store getPerTestClassStore(ExtensionContext context) {
		return context.getStore(getPerTestNamespace(context));
	}

	/**
	 * This implementation is currently wrong - should be based on test instance.
	 * @param context
	 * @return
	 */
	protected ExtensionContext.Namespace getPerTestNamespace(ExtensionContext context) {
		return ExtensionContext.Namespace.create(getClass(), context.getRequiredTestClass());
	}

	/**
	 * Create or return a unique storage space, which is unique per the test method.
	 *
	 * More specifically, the store is unique per the combination of:
	 * <ul>
	 *  <li>{@code this} extension class</li>
	 *  <li>The test class instance that is invoking this extension</li>
	 *  <li>The test method that is associated with this call
	 *  	(i.e, the test method associated with a BeforeEachCallback or AfterEachCallback)</li>
	 * </ul>
	 *
	 * This method cannot be called for a non-test method related callback because
	 * {@code context.getRequiredTestMethod} will be null and will throw an exception.
	 *
	 * @param context  The ExtensionContext passed in to one of the callback methods.
	 * @return The store that can be used to store and retrieve values.
	 */
	protected ExtensionContext.Store getPerTestMethodStore(ExtensionContext context) {
		return context.getStore(getPerTestMethodNamespace(context));
	}

	protected ExtensionContext.Namespace getPerTestMethodNamespace(ExtensionContext context) {
		return ExtensionContext.Namespace.create(getClass(), context.getRequiredTestInstance(), context.getRequiredTestMethod());
	}
}
