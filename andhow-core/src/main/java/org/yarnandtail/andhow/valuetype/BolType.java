package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

import java.util.Arrays;

/**
 * Metadata and parsing for configuration Properties of the {@link Boolean} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class BolType extends BaseValueType<Boolean> {

	private final static String[] TRUE_VALS = {"true", "t", "yes", "y", "on"};
	private final static String[] FALSE_VALS = {"false", "f", "no", "n", "off"};

	private static final BolType instance = new BolType();

	protected BolType() {
		super(Boolean.class);
	}

	/**
	 * Fetch the single, shared instace of this ValueType
	 * <p>
	 * @return An instance of the {@link #BolType()}
	 */
	public static BolType instance() {
		return instance;
	}

	/**
	 * Parses a String to a {@link Boolean}.  The String should already be trimmed.
	 * <p>
	 * Returns {@code True} if the String matches one of the 'true' values or
	 * {@code false} if it matches one of the false values (case-insensitive).
	 * Recognized {@code True} strings:
	 * <ul>
	 *   <li>true</li>
	 *   <li>t</li>
	 *   <li>yes</li>
	 *   <li>y</li>
	 *   <li>on</li>
	 * </ul>
	 * <p>
	 * Recognized {@code False} strings:
	 * <ul>
	 *   <li>false</li>
	 *   <li>f</li>
	 *   <li>no</li>
	 *   <li>n</li>
	 *   <li>off</li>
	 * </ul>
	 * Unrecognized values will throw a {@code ParsingException}, null values
	 * always return null.  Values are assumed to be trimmed to null (if
	 * appropriate from where they are loaded from) prior to being passed to this
	 * method.
	 * <p>
	 * <em>Note: The parsing behavior changed in the 0.5.0 release</em>
	 * to have an explicit list of False values.
	 * See <a href="https://github.com/eeverman/andhow/issues/658"></a>Issue 658</a>.
	 * <p>
	 *
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException If the value is not a recognized True or False string.
	 */
	@Override
	public Boolean parse(String sourceValue) throws ParsingException {

		if (sourceValue != null) {

			String v = sourceValue.toLowerCase();

			if ( Arrays.stream(TRUE_VALS).anyMatch(s -> s.equals(v)) ) {
				return true;
			} else if (Arrays.stream(FALSE_VALS).anyMatch(s -> s.equals(v))) {
				return false;
			} else {
				throw new ParsingException("Unable to convert to a Boolean value", sourceValue);
			}

		} else {
			return null;
		}
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean) o;
	}

}
