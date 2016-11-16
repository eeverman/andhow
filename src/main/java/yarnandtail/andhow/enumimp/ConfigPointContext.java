package yarnandtail.andhow.enumimp;

import java.util.List;

/**
 *
 * @author eeverman
 */
public interface ConfigPointContext {

	List<String> getEffectiveAliases();

	String getEffectiveName();
	
	Object getEffectiveDefault();
	
	/**
	 * Accumulating is a context choice to build up (in order) from outer to inner.
	 * @return 
	 */
	boolean isAccumulating();
	
	/*
	 * Reverse the loading order
	 * This is a load preference that would be sensative to the ordering of the 
	 * loaders, so is appropriate only at the context level.
	 */
	boolean isReverseLoadOrder();
	
}
