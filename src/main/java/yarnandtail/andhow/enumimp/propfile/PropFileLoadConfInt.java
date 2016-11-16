package yarnandtail.andhow.enumimp.propfile;

import yarnandtail.andhow.enumimp.ConfigPointDef;

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
