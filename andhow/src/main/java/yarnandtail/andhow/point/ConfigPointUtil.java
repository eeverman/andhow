package yarnandtail.andhow.point;

import org.apache.commons.lang3.BooleanUtils;

/**
 *
 * @author eeverman
 */
public class ConfigPointUtil {

	/**
	 * Parses a string to a boolean.
	 * It just uses Apache BooleanUtils, but this codifies that this is how its done.
	 * @param value
	 * @return 
	 */
	public static boolean toBoolean(String value) {
		return BooleanUtils.toBoolean(value);
	}

}
