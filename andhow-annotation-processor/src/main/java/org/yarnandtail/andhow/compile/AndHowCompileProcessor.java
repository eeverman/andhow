package org.yarnandtail.andhow.compile;

import java.util.logging.Level;
import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.yarnandtail.andhow.service.PropertyRegistration;
import com.sun.source.util.Trees;
import java.io.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;


import javax.tools.FileObject;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.util.AndHowLog;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 *
 * Note: check to ensure that Props are not referenced in static init blocks b/c
 * we may need to load the class (and run its init) before andHow init can
 * complete, causing a circular init loop.
 *
 * @author ericeverman
 */
@SupportedAnnotationTypes("*")
public class AndHowCompileProcessor extends AbstractProcessor {
	private static final AndHowLog LOG = AndHowLog.getLogger(AndHowCompileProcessor.class);
	
	private static final String SERVICES_PACKAGE = "";
	
	private static final String PROP_REG_SVS_RELATIVE_NAME = "META-INF/services/org.yarnandtail.andhow.service.PropertyRegistrar";

	private static final String INIT_SVS_RELATIVE_NAME = "META-INF/services/org.yarnandtail.andhow.AndHowInit";

	//Static to insure all generated classes have the same timestamp
	private static Calendar runDate;

	private Trees trees;
	
	private List<CauseEffect> registrars = new ArrayList();
	
	private List<CauseEffect> initClasses = new ArrayList();		//List of init classes (should only ever be 1)

	public AndHowCompileProcessor() {
		//required by Processor API
		runDate = new GregorianCalendar();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		boolean isLastRound = roundEnv.processingOver();
		
		Filer filer = this.processingEnv.getFiler();

		try {
			
			if (isLastRound) {
				LOG.debug("Final round of annotation processing.  Total root element count: {0}", roundEnv.getRootElements().size());
				
				if (initClasses.size() == 1) {
					writeServiceInitFile(filer, initClasses.get(0));
				} else if (initClasses.size() > 1) {
					LOG.error("There were multiple AndHowInit implementation classes found - only one is allowed.  List follows:");
					for (CauseEffect ce : initClasses) {
						LOG.error("AndHowInit instance: {0}", ce.fullClassName);
					}
					throw new RuntimeException("Multiple AndHowInit implementations were found - only one is allowed. See list above.");
				}
				
				if (registrars != null && registrars.size() > 0) {
					writeServiceRegistrarsFile(filer, registrars);
				}
				
			} else {
				LOG.trace("Another round of annotation processing.  Current root element count: {0}", roundEnv.getRootElements().size());
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		//
		//Scan all the Compilation units (i.e. class files) for AndHow Properties
		Iterator<? extends Element> it = roundEnv.getRootElements().iterator();
		for (Element e : roundEnv.getRootElements()) {


			TypeElement te = (TypeElement) e;
			AndHowElementScanner7 st = new AndHowElementScanner7(
					this.processingEnv, 
					Property.class.getCanonicalName(),
					AndHowInit.class.getCanonicalName());
			CompileUnit ret = st.scan(e);
			
			
			if (ret.isInitClass()) {
				initClasses.add(new CauseEffect(ret.getRootCanonicalName(), te));
			}

			if (ret.hasRegistrations()) {
				
				LOG.debug("Found {0} AndHow Properties in class {1} ", ret.getRegistrations().size(), ret.getRootCanonicalName());
				PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(ret, AndHowCompileProcessor.class, runDate);
				registrars.add(new CauseEffect(gen.buildGeneratedClassFullName(), te));
				PropertyRegistrationList regs = ret.getRegistrations();
				
				if (LOG.isLoggable(Level.FINEST)) {
					for (PropertyRegistration p : ret.getRegistrations()) {
						LOG.trace("Found AndHow Property ''{0}'' in root class ''{1}'', immediate parent is ''{2}''",
								p.getCanonicalPropertyName(), p.getCanonicalRootName(), p.getJavaCanonicalParentName());
					}
				}

				try {
					writeClassFile(filer, gen, e);
					LOG.trace("Wrote new generated class file " + gen.buildGeneratedClassSimpleName());
				} catch (Exception ex) {
					LOG.error("Unable to write generated classfile '" + gen.buildGeneratedClassFullName() + "'", ex);
					throw new RuntimeException(ex);
				}
			}

			if (ret.getErrors().size() > 0) {
				LOG.error(
						"AndHow Property definition errors prevented compilation to complete. " +
						"Each of the following errors must be fixed before compilation is possible.");
				for (String err : ret.getErrors()) {
					LOG.error("AndHow Property Error: {0}", err);
				}
				
				throw new RuntimeException("AndHowCompileProcessor threw a fatal exception - See error log for details.");
			}

		}

		return false;

	}

	public void writeClassFile(Filer filer, PropertyRegistrarClassGenerator generator, Element causingElement) throws Exception {

		String classContent = generator.generateSource();

		FileObject classFile = filer.createSourceFile(generator.buildGeneratedClassFullName(), causingElement);

		try (Writer writer = classFile.openWriter()) {
			writer.write(classContent);
		}	
	}
	
	protected void writeServiceRegistrarsFile(Filer filer, List<CauseEffect> registrars) throws Exception {
		
		//Get a unique causing elements
		HashSet<Element> set = new HashSet();
		for (CauseEffect ce : registrars) {
			set.add(ce.causeElement);
		}
		
		
		//The CLASS_OUTPUT location is used instead of SOURCE_OUTPUT because it
		//seems that non-Java files are not copied over from the SOURCE_OUTPUT
		//location.
		FileObject svsFile = filer.createResource(
				CLASS_OUTPUT, SERVICES_PACKAGE, PROP_REG_SVS_RELATIVE_NAME, set.toArray(new Element[set.size()]));

		try (Writer writer = svsFile.openWriter()) {
			for (CauseEffect ce : registrars) {
				writer.write(ce.fullClassName);
				writer.write(System.lineSeparator());
			}
		}
		
		LOG.trace("Wrote service registry log ''{0}'' to the location StandardLocation.CLASS_OUTPUT", PROP_REG_SVS_RELATIVE_NAME);
	}
	
	public void writeServiceInitFile(Filer filer, CauseEffect initClass) throws Exception {
		//The CLASS_OUTPUT location is used instead of SOURCE_OUTPUT because it
		//seems that non-Java files are not copied over from the SOURCE_OUTPUT
		//location.
		FileObject svsFile = filer.createResource(CLASS_OUTPUT, SERVICES_PACKAGE, INIT_SVS_RELATIVE_NAME, initClass.causeElement);

		try (Writer writer = svsFile.openWriter()) {
			writer.write(initClass.fullClassName);
		}
		
		LOG.trace("Wrote init service registry ''{0}'' to the location StandardLocation.CLASS_OUTPUT", INIT_SVS_RELATIVE_NAME);
	}
	
	/**
	 * Match up a causal Element w/ the Class name that will be registered in
	 * a service registry.
	 */
	protected static class CauseEffect {
		String fullClassName;
		Element causeElement;
		
		public CauseEffect(String fullClassName, Element causeElement) {
			this.fullClassName = fullClassName;
			this.causeElement = causeElement;
		}
	}

}
