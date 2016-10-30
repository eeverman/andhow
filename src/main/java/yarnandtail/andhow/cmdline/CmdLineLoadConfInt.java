package yarnandtail.andhow.cmdline;

import yarnandtail.andhow.ParamDefinition;

/**
 *
 * @author eeverman
 */
public interface CmdLineLoadConfInt<E extends Enum<E> & CmdLineLoadConfInt> extends ParamDefinition<E> {
	boolean isHelp();
}
