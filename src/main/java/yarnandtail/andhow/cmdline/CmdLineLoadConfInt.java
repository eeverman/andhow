package yarnandtail.andhow.cmdline;

import yarnandtail.andhow.ConfigPoint;

/**
 *
 * @author eeverman
 */
public interface CmdLineLoadConfInt<E extends Enum<E> & CmdLineLoadConfInt> extends ConfigPoint<E> {
	boolean isHelp();
}
