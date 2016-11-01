package yarnandtail.andhow;

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
	 * 
	 * TODO:  There should be a preference for this on the configPoint itself.
	 * It makes sense that we know ahead of time that some params are private
	 * and not to be exposed.
	 * 
	 * Actually, we may need a way to specify that a param is loaded at a particular
	 * config level (order of loaders) and not any higher to keep the config point
	 * within what the point considers to be its 'internal' levels.
	 * 
	 * Then we need to provide a way to say to accept the default.
	 * @return 
	 */
	boolean isReverseLoadOrder();
	
	/**
	 * 
	 * TODO:  There should be a preference for this on the configPoint itself.
	 * It makes sense that we know ahead of time if a param should be accumulating
	 * or not, such as a list of directories in which the last to load is the default.
	 * 
	 * Then we need to provide a way to say to accept the default.
	 * @return 
	 */
	boolean isAccumulating();
	
}
