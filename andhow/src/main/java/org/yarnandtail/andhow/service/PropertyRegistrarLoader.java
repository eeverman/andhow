package org.yarnandtail.andhow.service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 * Dedicated wrapper for a java.util.ServiceLoader that loads PropertyRegistrar instances.
 * 
 * PropertyRegistrar instances are generated automatically at compile time for
 * classes using AndHow properties.  When a PropertyRegistrar is created, it is
 * recorded as a Service, making it visible to the ServiceLoader.
 * 
 * @author ericeverman
 */
public class PropertyRegistrarLoader {
	private final ClassLoader classLoader;
	private final ServiceLoader<PropertyRegistrar> loader;
	private final Object lock = new Object();	//Sync lock for instances
	
	
	public PropertyRegistrarLoader() {
		this(PropertyRegistrarLoader.class.getClassLoader());
	}
	
	public PropertyRegistrarLoader(ClassLoader classLoader) {
		this.classLoader = (classLoader != null)?classLoader:PropertyRegistrarLoader.class.getClassLoader();
		loader = ServiceLoader.load(PropertyRegistrar.class, this.classLoader);
	}
	
	public List<PropertyRegistrar> getPropertyRegistrars() {
		
		synchronized (lock) {
			List<PropertyRegistrar> list = new ArrayList();
			Iterator<PropertyRegistrar> it = loader.iterator();
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
	
	/**
	 * Returns the list of groups that are registered via the ServiceLoader.
	 * 
	 * @return 
	 */
	public List<GroupProxy> getGroups() {
		
		//AndHow canonical group name to group mapping
		LinkedHashMap <String, GroupProxyMutable> groups = new LinkedHashMap();
		
		List<PropertyRegistrar> registrars = getPropertyRegistrars();
		
		for (PropertyRegistrar registrar : registrars) {
			for (PropertyRegistration registration : registrar.getRegistrationList()) {
				String grpName = registration.getCanonicalParentName();
				GroupProxyMutable grp = groups.get(grpName);
				
				if (grp == null) {
					grp = new GroupProxyMutable(registration.getCanonicalParentName(), registration.getJavaCanonicalParentName());
					groups.put(grpName, grp);
				}
				
				Class<?> group = null;
				Property prop = null;
				
				try {
					
					group = Class.forName(registration.getJavaCanonicalParentName());
					Field f = group.getDeclaredField(registration.getPropertyName());
					
					try {
						prop = (Property) f.get(null);
					} catch (Exception ex) {	
						f.setAccessible(true);
						prop = (Property) f.get(null);
					}
					
				} catch (ClassNotFoundException ex) {
					//TODO:  Need to return a custom problem type here
					throw new RuntimeException(ex);
				} catch (NoSuchFieldException | IllegalAccessException ex) {
					Logger.getLogger(PropertyRegistrarLoader.class.getName()).log(Level.SEVERE, null, ex);
					continue;
				} catch (SecurityException ex) {
					Logger.getLogger(PropertyRegistrarLoader.class.getName()).log(Level.SEVERE, null, ex);
					continue;
				} catch (IllegalArgumentException ex) {
					//f.get(null) assumes that this is  static field - throws this if not
					Logger.getLogger(PropertyRegistrarLoader.class.getName()).log(Level.SEVERE, null, ex);
					continue;
				}
				
				NameAndProperty nap = new NameAndProperty(registration.getPropertyName(), prop);
				grp.addProperty(nap);
			}
		}
		
		
		List<GroupProxy> list = new ArrayList();
		list.addAll(groups.values());
		return list;
	}
}
