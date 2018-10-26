package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.service.PropertyRegistrationList;
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

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import org.yarnandtail.andhow.util.TextUtil;

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
	
	private static final String INIT_CLASS_NAME = AndHowInit.class.getCanonicalName();
	private static final String TEST_INIT_CLASS_NAME = "org.yarnandtail.andhow.AndHowTestInit";
	
	private static final String SERVICES_PACKAGE = "";
	
	private static final String SERVICE_REGISTRY_META_DIR = "META-INF/services/";
	
	//Static to insure all generated classes have the same timestamp
	private static Calendar runDate;
	
	private final List<CauseEffect> registrars = new ArrayList();
	
	private final List<CauseEffect> initClasses = new ArrayList();		//List of init classes (should only ever be 1)
	private final List<CauseEffect> testInitClasses = new ArrayList();	//List of test init classes (should only ever be 1)

	private final List<CompileProblem> problems = new ArrayList();	//List of problems found. >0== RuntimeException
	
	public AndHowCompileProcessor() {
		//required by Processor API
		runDate = new GregorianCalendar();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		Filer filer = processingEnv.getFiler();
		Messager log = this.processingEnv.getMessager();
		
		boolean isLastRound = roundEnv.processingOver();
		

		if (isLastRound) {
			debug(log, "Final round of annotation processing. Total root element count: {}",
					roundEnv.getRootElements().size());

			
			if (initClasses.size() > 1) {
				problems.add(new CompileProblem.TooManyInitClasses(
						INIT_CLASS_NAME, initClasses));
			}
			
			if (testInitClasses.size() > 1) {
				problems.add(new CompileProblem.TooManyInitClasses(
						TEST_INIT_CLASS_NAME, testInitClasses));
			}
			
			if (problems.isEmpty()) {
				try {
					if (initClasses.size() == 1) {

						debug(log, "Found exactly 1 {} class: {}",
								INIT_CLASS_NAME, initClasses.get(0).fullClassName);
						
						writeServiceFile(filer, AndHowInit.class.getCanonicalName(), initClasses);

					}

					if (testInitClasses.size() == 1) {
						debug(log, "Found exactly 1 {} class: {}",
								TEST_INIT_CLASS_NAME, testInitClasses.get(0).fullClassName);
						
						writeServiceFile(filer, TEST_INIT_CLASS_NAME, testInitClasses);

					}

					if (registrars.size() > 0) {
						writeServiceFile(filer, PropertyRegistrar.class.getCanonicalName(), registrars);
					}
					
				} catch (IOException e) {
					throw new AndHowCompileException("Exception while trying to write generated files", e);
				}
			} else {
				error(log, "AndHow Property definition or Init class errors "
						+ "prevented compilation. Each of the following errors "
						+ "must be fixed before compilation is possible.");
				error(log, "AndHow errors discovered: {}", problems.size());
				
				for (CompileProblem err : problems) {
					error(log, err.getFullMessage());
				}

				throw new AndHowCompileException(problems);
			}
			
		} else {
			debug(log, "Another round of annotation processing. "
					+ "Current root element count: {}", roundEnv.getRootElements().size());


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

					debug(log, "Found {} AndHow Properties in class {} ", 
							ret.getRegistrations().size(), ret.getRootCanonicalName());
					
					PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(ret, AndHowCompileProcessor.class, runDate);
					registrars.add(new CauseEffect(gen.buildGeneratedClassFullName(), te));
					PropertyRegistrationList regs = ret.getRegistrations();

					try {
						writeClassFile(filer, gen, e);
						debug(log, "Wrote new generated class file {}", gen.buildGeneratedClassSimpleName());
					} catch (Exception ex) {
						error(log, "Unable to write generated classfile '" + gen.buildGeneratedClassFullName() + "'", ex);
						throw new RuntimeException(ex);
					}
				}
				
				problems.addAll(ret.getProblems());

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
	 * Logs a debug message using the javac standard Messager system.
	 * 
	 * @param log The Message instance to use
	 * @param pattern String pattern with curly variable replacement like this: {}
	 * @param args Arguments to put into the {}'s, in order.
	 */
	void debug(Messager log, String pattern, Object... args) {
		log.printMessage(Diagnostic.Kind.NOTE, TextUtil.format(pattern, args));
	}
	
	/**
	 * Logs an error message using the javac standard Messager system.
	 * 
	 * @param log The Message instance to use
	 * @param pattern String pattern with curly variable replacement like this: {}
	 * @param args Arguments to put into the {}'s, in order.
	 */
	void error(Messager log, String pattern, Object... args) {
		log.printMessage(Diagnostic.Kind.ERROR, TextUtil.format(pattern, args));
	}
	
	/**
	 * Match up a causal Element (Basically the compiler representation of a
	 * class to be compiled) w/ the Class name that will be registered in
	 * a service registry.
	 * 
	 * When the AnnotationProcessor writes a new file to the Filer, it wants
	 * a causal Element to associate with it, apparently this info could be
	 * used for reporting or something.
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
