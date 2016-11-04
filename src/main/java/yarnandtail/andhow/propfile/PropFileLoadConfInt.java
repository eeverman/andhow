package yarnandtail.andhow.propfile;

import yarnandtail.andhow.ConfigPointDef;

/**
 *
 * @author eeverman
 */
public interface PropFileLoadConfInt<E extends Enum<E> & PropFileLoadConfInt> extends ConfigPointDef<E> {
	boolean isPropFileName();
	boolean isPropFilepath();
	boolean isDefaultPropFileName();
	boolean isDefaultPropFilepath();
	boolean isSkipPropFileLoad();
}
