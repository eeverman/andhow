package yarnandtail.andhow;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ParamDefinition<E extends Enum<E> & ParamDefinition> extends ParamDefinitionInterface {
	
	static List<Enum> EMPTY_ENUM_LIST = Arrays.asList(new Enum[0]);
	static List<String> EMPTY_STRING_LIST = Arrays.asList(ArrayUtils.EMPTY_STRING_ARRAY);
	
	/**
	 * Returns a core storage object for the details of this parameter.
	 * This is the one method that ParamDefinition classes must implement.
	 * This is generally not used by application code, but simplifies user constructed
	 * ParamDefinitions because nearly all code can be contained in the 'core'
	 * object and nearly all public methods can be created as default methods in
	 * the interface.
	 * @return 
	 */
	ParamDefinitionInterface getCore();
	
	@Override
	default Class<? extends ParamDefinition> getEnumClass() {
		return getCore().getEnumClass();
	}
	
	@Override
	default String getEntireSetName() {
		return getCore().getEntireSetName();
	}
	
	@Override
	default String getEntireSetDescription() {
		return getCore().getEntireSetDescription();
	}
	
	@Override
	default ParamType getParamType() {
		return getCore().getParamType();
	}
	
	@Override
	default String getExplicitName() {
		return getCore().getExplicitName();
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
	default List<String> getAlias() {
		return getCore().getAlias();
	}

	@Override
	default List<Enum> getPossibleValueEnums() {
		return getCore().getPossibleValueEnums();
	}
	
	@Override
	default Object getDefaultValue() {
		return getCore().getDefaultValue();
	}

}
