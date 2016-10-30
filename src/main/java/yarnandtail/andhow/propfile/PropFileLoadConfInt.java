package yarnandtail.andhow.propfile;

import yarnandtail.andhow.ParamDefinition;

/**
 *
 * @author eeverman
 */
public interface PropFileLoadConfInt<E extends Enum<E> & PropFileLoadConfInt> extends ParamDefinition<E> {
	boolean isPropFileName();
	boolean isPropFilepath();
	boolean isDefaultPropFileName();
	boolean isDefaultPropFilepath();
	boolean isSkipPropFileLoad();
}
