package yarnandtail.andhow.cmdline;

import yarnandtail.andhow.ConfigPointDef;

/**
 *
 * @author eeverman
 */
public interface CmdLineLoadConfInt<E extends Enum<E> & CmdLineLoadConfInt> extends ConfigPointDef<E> {
	boolean isHelp();
}
