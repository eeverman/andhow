package yarnandtail.andhow.enumimp;

import yarnandtail.andhow.enumimp.ConfigPointDef;

/**
 *
 * @author eeverman
 */
public interface GlobalLoadConfInt<E extends Enum<E> & GlobalLoadConfInt> extends ConfigPointDef<E> {
	boolean isHelp();
	boolean isVerboseConf();
	boolean isVeryVerboseConf();
}
