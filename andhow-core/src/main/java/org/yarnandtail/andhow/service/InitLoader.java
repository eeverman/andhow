package org.yarnandtail.andhow.service;

import java.util.*;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.AndHowConfiguration;

/**
 * Dedicated wrapper for a java.util.ServiceLoader that loads InitLoader instances.
 * 
 * InitLoader instances are discovered automatically at compile time.  If a
 * TestInitLoader is found, it takes precedence over an InitLoader instance.
 * 
 * @author ericeverman
 */
public class InitLoader {
	private final ServiceLoader<AndHowInit> loader;
	private final Object lock = new Object();	//Sync lock for instances
	
	
	public InitLoader() {
		this(InitLoader.class.getClassLoader());
	}
	
	public InitLoader(ClassLoader classLoader) {
		ClassLoader cl = (classLoader != null)?classLoader:InitLoader.class.getClassLoader();
		loader = ServiceLoader.load(AndHowInit.class, cl);
	}
	
	/**
	 * Returns true if there are no more than 1 production init and 1 test init.
	 * @return 
	 */
	public boolean isValidState() {
		return (getInitInstances().size() <= 1);
	}
	
	/**
	 * Returns true if there are any <code>AndHowInit</code> instances available
	 * from the loader, even if more than one.
	 * @return 
	 */
	public boolean hasConfig() {
		return (getInitInstances().size() > 0);
	}
	
	/**
	 * Returns an AndHowConfiguration from an AndHowInit instance from the service
	 * loader, or if not found, the default one passed in.
	 * 
	 * The service loader instance if found if there is exactly one instance found,
	 * otherwise the defaultConfig is used.  <code>isValidState</code> will return
	 * false if there is more than one instance found by the service loader.
	 * 
	 * @param defaultConfig Config to use if no AndHowInit is found to provide one.
	 * @return 
	 */
	public AndHowConfiguration getAndHowConfiguration(AndHowConfiguration defaultConfig) {
		if (getInitInstances().size() == 1) {
			return getInitInstances().get(0).getConfiguration();
		} else {
			return defaultConfig;
		}
	}
	
	/**
	 * Returns all the AndHowInit instances found by the service loader.
	 * 
	 * @return 
	 */
	public List<AndHowInit> getInitInstances() {
		
		synchronized (lock) {
			List<AndHowInit> list = new ArrayList();
			Iterator<AndHowInit> it = loader.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			
			return list;
		}
	}
	
	/**
	 * Forces a reload of the service loader, which normally caches instances.
	 */
	public void reload() {
		synchronized (lock) {
			loader.reload();
		}
	}

}
