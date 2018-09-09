package org.yarnandtail.andhow.service;

import java.util.List;

/**
 * Basic interface used by the {@code java.util.ServiceLoader} to discover all
 * implementations on the classpath.
 * <p>
 * At compile time, instances of this interface are generated and added to the
 * final artifact (typically a jar, war or ear file), which can be discovered
 * automatically via the {@code java.util.ServiceLoader} mechanism.
 * 
 * <h3>Property registration background</h3>
 * At compile time, the AndHowCompileProcessor (an annotation Processor), reads
 * user classes and generates a PropertyRegistrar instance for each root class
 * (non-inner class) that contains an AndHow {@code Property}.
 * Matching service files are also generated in the "META-INF/services/"
 * directory so the {@code PropertyRegistrar} instances can be discovered
 * through the {@code java.util.ServiceLoader} mechanism.
 * <p>
 * At run time, the {@code PropertyRegistrarLoader} discovers all
 * {@code PropertyRegistrar} instances.
 * Each {@code PropertyRegistrar} creates a {@code PropertyRegistrationList}
 * instance with a {@code PropertyRegistration} for each {@code Property}
 * present in the jar.
 * 
 * @author ericeverman
 */
public interface PropertyRegistrar {

	/**
	 * The Java canonical name of a root class containing AndHow Properties.
	 * <p>
	 * A 'root' class is a non-inner class.  All inner classes would be recored
	 * together under the root class.
	 * 
	 * @return 
	 */
	String getRootCanonicalName();

	/**
	 * Get the list if individual {@code Property} registrations.
	 * 
	 * @return A list of registrations.  The list may be unmodifiable
	 * or detached.
	 */
	List<PropertyRegistration> getRegistrationList();

}
