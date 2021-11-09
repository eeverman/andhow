package org.yarnandtail.andhow.test.bulktest;

/**
 * Special raw values that can be used for special behaviors in PropExpectations.
 * <p>
 * Not all behaviors are possible in each loader, so loaders will have to map
 * them to what makes sense.
 * <p>
 * To use, include as a raw value like: {@code SKIP.toString()}.
 * <p>
 * Not included is a normal value - its not needed b/c a normal value is just used directly as
 * a raw value.
 */
public enum RawValueType {
	SKIP("[[RAWVALUE_SKIP_KEY]]"), // Do not assign this property value - leave it missing completely.  Same as a null raw.
	NO_VALUE("[[RAWVALUE_NO_VALUE_KEY]]"), // Don't include a value, eg: "org.corp.PROPERTY="
	NO_VALUE_OR_DELIMITER("[[RAWVALUE_NO_VALUE_OR_DELIMITER_KEY]]"); // Nothing after the property name, eg: "or.corp.PROPERTY"

	private String _key;  // String ID to recognize an instance by

	private RawValueType(String key) {
		_key = key;
	}

	@Override
	public String toString() {
		return _key;
	}
}
