package yarnandtail.andhow;

import org.apache.commons.lang3.StringUtils;

/**
 * Key-value pair
 * @author eeverman
 */
public class KVP {
	public static final KVP NULL_KVP = new KVP();
	
	private String name;
	private String value;
	
	private KVP() {}

	public KVP(String name) {
		this.name = name;
		
		if (name == null) {
			throw new UnparsableKVPException("The key (parameter name) cannot be empty");
		}
	}
	
	public KVP(String[] kvp) {
		if (kvp.length > 0) {
			name = StringUtils.trimToNull(kvp[0]);
			if (name == null) {
				throw new UnparsableKVPException("The key (parameter name) cannot be empty");
			}
		}
		
		if (kvp.length > 1) {
			value = StringUtils.trimToNull(kvp[1]);
		}
		
		if (kvp.length > 2) {
			throw new UnparsableKVPException("There can only be one key/name and one value - found extra");
		}
	}

	public KVP(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	
}
