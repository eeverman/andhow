package org.yarnandtail.andhow.service;

import java.util.*;
import org.yarnandtail.andhow.AndHowInit;

/**
 * Dedicated wrapper for a java.util.ServiceLoader that loads PropertyRegistrar instances.
 * 
 * PropertyRegistrar instances are generated automatically at compile time for
 * classes using AndHow properties.  When a PropertyRegistrar is created, it is
 * recorded as a Service, making it visible to the ServiceLoader.
 * 
 * @author ericeverman
 */
public class InitLoader {
	private final ClassLoader classLoader;
	private final ServiceLoader<AndHowInit> loader;
	private final Object lock = new Object();	//Sync lock for instances
	
	
	public InitLoader() {
		this(InitLoader.class.getClassLoader());
	}
	
	public InitLoader(ClassLoader classLoader) {
		this.classLoader = (classLoader != null)?classLoader:InitLoader.class.getClassLoader();
		loader = ServiceLoader.load(AndHowInit.class, this.classLoader);
	}
	
	public List<AndHowInit> getInitiators() {
		
		synchronized (lock) {
			List<AndHowInit> list = new ArrayList();
			Iterator<AndHowInit> it = loader.iterator();
			while (it.hasNext()) {
				list.add(it.next());
			}
			
			return list;
		}
	}
	
	public void reload() {
		synchronized (lock) {
			loader.reload();
		}
	}

}
