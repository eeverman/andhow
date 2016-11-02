package yarnandtail.andhow;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ConfigPoint<E extends Enum<E> & ConfigPoint> extends ConfigPointCommon {
	
	static List<Enum> EMPTY_ENUM_LIST = Arrays.asList(new Enum[0]);
	static List<String> EMPTY_STRING_LIST = Arrays.asList(ArrayUtils.EMPTY_STRING_ARRAY);
	
	/**
	 * Returns a core storage object for the details of this parameter.
	 * This is the one method that ConfigPoint classes must implement.
 This is generally not used by application code, but simplifies user constructed
 ParamDefinitions because nearly all code can be contained in the 'core'
 object and nearly all public methods can be created as default methods in
 the interface.
	 * @return 
	 */
	ConfigPointCommon getCore();
	
	@Override
	default Class<? extends ConfigPoint> getEnumClass() {
		return getCore().getEnumClass();
	}
	
	@Override
	default String getGroupDescription() {
		return getCore().getGroupDescription();
	}
	
	@Override
	default String getEntireSetDescription() {
		return getCore().getEntireSetDescription();
	}
	
	@Override
	default ConfigPointType getParamType() {
		return getCore().getParamType();
	}
	
	@Override
	default String getExplicitBaseName() {
		return getCore().getExplicitBaseName();
	}
	
	@Override
	default String getShortDescription() {
		return getCore().getShortDescription();
	}
	
	@Override
	default String getHelpText() {
		return getCore().getHelpText();
	}

	@Override
	default List<String> getBaseAliases() {
		return getCore().getBaseAliases();
	}

	@Override
	default List<Enum> getPossibleValueEnums() {
		return getCore().getPossibleValueEnums();
	}
	
	@Override
	default Object getBaseDefaultValue() {
		return getCore().getBaseDefaultValue();
	}
	
	@Override
	default boolean isPrivate() {
		return getCore().isPrivate();
	}

}
