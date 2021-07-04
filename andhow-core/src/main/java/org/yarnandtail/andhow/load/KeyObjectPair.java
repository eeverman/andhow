package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Key-Object Pair
 * Contains a String name of a property and an Object value.
 */
public class KeyObjectPair {

	private String name;
	private Object value;

	private KeyObjectPair() {}

	public KeyObjectPair(String name) throws ParsingException {
		this.name = TextUtil.trimToNull(name);

		if (this.name == null) {
			throw new ParsingException("The key (parameter name) cannot be empty", name);
		}
	}

	public KeyObjectPair(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
