package yarnandtail.andhow;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author eeverman
 */
public class ParamImm implements Param {

	private ConfigPoint def;
	private String fullArg;
	private String name;
	private String value;
	private Boolean valid;

	public ParamImm(ConfigPoint def, String fullArg, String name, String value, Boolean valid) {
		this.def = def;
		this.fullArg = fullArg;
		this.name = name;
		this.value = StringUtils.trimToNull(value);
		this.valid = valid;
		
		if (this.valid == null) {
			throw new RuntimeException("The valid flag must not be null when ConfigParamImm is constructed");
		}
	}
	
	@Override
	public ConfigPoint getParamDefinition() {
		return def;
	}

	@Override
	public String getOriginalText() {
		return fullArg;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Boolean isValid() {
		return valid;
	}

	@Override
	public Param toImmutable() {
		return this;
	}

}
