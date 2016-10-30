package yarnandtail.andhow;

import yarnandtail.andhow.ParamDefinition;

/**
 *
 * @author eeverman
 */
public interface GlobalLoadConfInt<E extends Enum<E> & GlobalLoadConfInt> extends ParamDefinition<E> {
	boolean isHelp();
	boolean isVerboseConf();
	boolean isVeryVerboseConf();
}
