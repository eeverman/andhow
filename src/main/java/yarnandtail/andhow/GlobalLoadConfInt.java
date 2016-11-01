package yarnandtail.andhow;

import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public interface GlobalLoadConfInt<E extends Enum<E> & GlobalLoadConfInt> extends ConfigPoint<E> {
	boolean isHelp();
	boolean isVerboseConf();
	boolean isVeryVerboseConf();
}
