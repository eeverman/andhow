package yarnandtail.andhow.enumimp.cmdline;

import yarnandtail.andhow.enumimp.ConfigPointDef;

/**
 *
 * @author eeverman
 */
public interface CmdLineLoadConfInt<E extends Enum<E> & CmdLineLoadConfInt> extends ConfigPointDef<E> {
	boolean isHelp();
}
