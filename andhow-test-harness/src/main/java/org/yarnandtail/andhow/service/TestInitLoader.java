package org.yarnandtail.andhow.service;

import java.util.*;
import org.yarnandtail.andhow.*;

/**
 * Dedicated wrapper for a java.util.ServiceLoader that loads TestInitLoader instances.
 * 
 * TestInitLoader instances are discovered automatically at compile time.  If a
 * TestInitLoader is found, it takes precedence over an InitLoader instance.
 * 
 * The TestInitLoader subclasses and completely overrides the InitLoader.  This
 * allows this class to be loaded via <code>Class.forName</code> and cast to
 * an InitLoader instance.  During production this class (and the test
 * harness module) are not expected to be on the classpath.
 * 
 * @author ericeverman
 */
public class TestInitLoader extends InitLoader {
	private final ServiceLoader<AndHowTestInit> loader;
	private final Object lock = new Object();	//Sync lock for instances
	
	
	public TestInitLoader() {
		this(TestInitLoader.class.getClassLoader());
	}
	
	public TestInitLoader(ClassLoader classLoader) {
		ClassLoader cl = (classLoader != null)?classLoader:TestInitLoader.class.getClassLoader();
		loader = ServiceLoader.load(AndHowTestInit.class, cl);
	}
	
	/**
	 * Returns true if there are no more than 1 production init and 1 test init.
	 * @return 
	 */
	@Override
	public boolean isValidState() {
		return (getInitInstances().size() <= 1);
	}
	
	/**
	 * Returns true if there are any <code>AndHowInit</code> instances available
	 * from the loader, even if more than one.
	 * @return 
	 */
	@Override
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
	@Override
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
	@Override
	public List<AndHowInit> getInitInstances() {
		
		synchronized (lock) {
			List<AndHowInit> list = new ArrayList();
			Iterator<AndHowTestInit> it = loader.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			
			return list;
		}
	}
	
	/**
	 * Forces a reload of the service loader, which normally caches instances.
	 */
	@Override
	public void reload() {
		synchronized (lock) {
			loader.reload();
		}
	}

}
