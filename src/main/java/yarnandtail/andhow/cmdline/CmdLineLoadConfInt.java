package yarnandtail.andhow.cmdline;

import yarnandtail.andhow.ParamDefinition;

/**
 *
 * @author eeverman
 */
public interface CmdLineLoadConfInt<E extends Enum<E> & ParamDefinition> {
	boolean isHelp();
}
