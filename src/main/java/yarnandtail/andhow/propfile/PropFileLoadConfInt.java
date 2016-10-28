package yarnandtail.andhow.propfile;

import yarnandtail.andhow.ParamDefinition;

/**
 *
 * @author eeverman
 */
public interface PropFileLoadConfInt<E extends Enum<E> & ParamDefinition> {
	boolean isPropFileName();
	boolean isPropFilepath();
	boolean isDefaultPropFileName();
	boolean isDefaultPropFilepath();
	boolean isSkipPropFileLoad();
}
