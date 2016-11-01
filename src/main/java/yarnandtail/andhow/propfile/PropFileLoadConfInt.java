package yarnandtail.andhow.propfile;

import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public interface PropFileLoadConfInt<E extends Enum<E> & PropFileLoadConfInt> extends ConfigPoint<E> {
	boolean isPropFileName();
	boolean isPropFilepath();
	boolean isDefaultPropFileName();
	boolean isDefaultPropFilepath();
	boolean isSkipPropFileLoad();
}
