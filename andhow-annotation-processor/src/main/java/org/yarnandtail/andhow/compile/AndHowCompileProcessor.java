package org.yarnandtail.andhow.compile;

import java.util.logging.Level;
import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.yarnandtail.andhow.service.PropertyRegistration;
import com.sun.source.util.Trees;
import java.io.*;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;


import javax.tools.FileObject;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.service.*;
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
	
	private static final String INIT_CLASS_NAME = AndHowInit.class.getCanonicalName();
	private static final String TEST_INIT_CLASS_NAME = "org.yarnandtail.andhow.AndHowTestInit";
	
	private static final String SERVICES_PACKAGE = "";
	
	private static final String SERVICE_REGISTRY_META_DIR = "META-INF/services/";
	
	//Static to insure all generated classes have the same timestamp
	private static Calendar runDate;

	private Trees trees;
	
	private final List<CauseEffect> registrars = new ArrayList();
	
	private final List<CauseEffect> initClasses = new ArrayList();		//List of init classes (should only ever be 1)
	private final List<CauseEffect> testInitClasses = new ArrayList();	//List of test init classes (should only ever be 1)

	public AndHowCompileProcessor() {
		//required by Processor API
		runDate = new GregorianCalendar();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		boolean isLastRound = roundEnv.processingOver();
		
		Filer filer = this.processingEnv.getFiler();



		if (isLastRound) {
			LOG.debug("Final round of annotation processing.  Total root element count: {0}", roundEnv.getRootElements().size());

			try {
				if (initClasses.size() == 1) {

					LOG.info("Found exactly 1 {0} class: {1}", INIT_CLASS_NAME, initClasses.get(0).fullClassName);
					writeServiceFile(filer, AndHowInit.class.getCanonicalName(), initClasses);

				} else if (initClasses.size() > 1) {
					TooManyInitClassesException err = 
							new TooManyInitClassesException(INIT_CLASS_NAME, initClasses);

					err.writeDetails(LOG);
					throw err;
				}

				if (testInitClasses.size() == 1) {

					LOG.info("Found exactly 1 {0} class: {1}", TEST_INIT_CLASS_NAME, testInitClasses.get(0).fullClassName);
					writeServiceFile(filer, TEST_INIT_CLASS_NAME, testInitClasses);

				} else if (testInitClasses.size() > 1) {
					TooManyInitClassesException err = 
							new TooManyInitClassesException(TEST_INIT_CLASS_NAME, testInitClasses);

					err.writeDetails(LOG);
					throw err;
				}

				if (registrars != null && registrars.size() > 0) {
					writeServiceFile(filer, PropertyRegistrar.class.getCanonicalName(), registrars);
				}
			} catch (IOException e) {
				throw new RuntimeException("Exception while trying to write generated files", e);
			}
			
		} else {
			LOG.trace("Another round of annotation processing.  Current root element count: {0}", roundEnv.getRootElements().size());


			//
			//Scan all the Compilation units (i.e. class files) for AndHow Properties
			Iterator<? extends Element> it = roundEnv.getRootElements().iterator();
			for (Element e : roundEnv.getRootElements()) {


				TypeElement te = (TypeElement) e;
				AndHowElementScanner7 st = new AndHowElementScanner7(
						this.processingEnv, 
						Property.class.getCanonicalName(),
						INIT_CLASS_NAME,
						TEST_INIT_CLASS_NAME);
				CompileUnit ret = st.scan(e);


				if (ret.istestInitClass()) {
					testInitClasses.add(new CauseEffect(ret.getRootCanonicalName(), te));
				} else if (ret.isInitClass()) {
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
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"AndHow Property definition errors prevented compilation to complete. " +
							"Each of the following errors must be fixed before compilation is possible.");
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"AndHow Property definition errors discovered: " + ret.getErrors().size());
					for (String err : ret.getErrors()) {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
								"AndHow Property Error: " + err);
					}

					//The docs for printMessage(Kind.Error) say that an error
					//message should actually throw an error, but that does not
					//seem to happen, so the runtime exception is needed
					throw new RuntimeException("AndHowCompileProcessor threw a fatal exception - See the error details above.");
				}

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
	
	protected void writeServiceFile(Filer filer, 
			String fullyQualifiedServiceInterfaceName, 
			List<CauseEffect> implementingClasses) throws IOException {
		
		//Get a unique causing elements
		HashSet<Element> set = new HashSet();
		for (CauseEffect ce : implementingClasses) {
			set.add(ce.causeElement);
		}
		
		
		//The CLASS_OUTPUT location is used instead of SOURCE_OUTPUT because it
		//seems that non-Java files are not copied over from the SOURCE_OUTPUT
		//location.
		FileObject svsFile = filer.createResource(
				CLASS_OUTPUT, SERVICES_PACKAGE, SERVICE_REGISTRY_META_DIR + fullyQualifiedServiceInterfaceName,
				set.toArray(new Element[set.size()]));

		try (Writer writer = svsFile.openWriter()) {
			for (CauseEffect ce : implementingClasses) {
				writer.write(ce.fullClassName);
				writer.write(System.lineSeparator());
			}
		}
		
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
