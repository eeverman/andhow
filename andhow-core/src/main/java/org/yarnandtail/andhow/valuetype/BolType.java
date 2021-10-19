package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Metadata and parsing for configuration Properties of the {@link Boolean} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class BolType extends BaseValueType<Boolean> {

	private static final BolType instance = new BolType();

	private BolType() {
		super(Boolean.class);
	}

	/**
	 * @return An instance of the {@link #BolType()}
	 * @deprecated since 0.4.1. Use {@link #instance()} instead
	 */
	@Deprecated
	public static BolType get() {
		return instance();
	}

	/**
	 * @return An instance of the {@link #BolType()}
	 */
	public static BolType instance() {
		return instance;
	}

	/**
	 * Parses a String to a {@link Boolean}.  The String should already be trimmed.
	 * <p>
	 * <em>Note: The parsing behavior may change in the 0.5.0 release</em>
	 * to have an explicit list of False values.
	 * See <a href="https://github.com/eeverman/andhow/issues/658"></a>Issue 658</a>.
	 * <p>
	 * Trims the value and returns {@code True} if the trimmed String
	 * matches one of these values (case-insensitive) :
	 * <ul>
	 *   <li>true</li>
	 *   <li>t</li>
	 *   <li>yes</li>
	 *   <li>y</li>
	 *   <li>on</li>
	 * </ul>
	 * If it does not match a value in the list and does not trim to null, {@code False} is returned.
	 * If the value is null after trimming, null is returned.
	 * <p>
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws IllegalArgumentException (doesn't currently happen)
	 */
	@Override
	public Boolean parse(String sourceValue) throws IllegalArgumentException {

		if (TextUtil.trimToNull(sourceValue) != null) {
			return TextUtil.toBoolean(sourceValue);
		} else {
			return null;
		}
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean) o;
	}

}
