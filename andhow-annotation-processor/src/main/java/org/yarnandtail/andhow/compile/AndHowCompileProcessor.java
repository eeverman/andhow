package org.yarnandtail.andhow.compile;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;

import javax.tools.FileObject;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.service.*;

import static javax.tools.Diagnostic.*;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * This is the central AndHow compilation class, an Annotation Processor.
 *
 * An annotation processor nominally reads annotations as classes are compiled.
 * This class is annotated {@code SupportedAnnotationTypes("*")}, allowing it to
 * 'see' all classes as they are compiled.  This class then delegates to a
 * 'scanner' class that does deep inspection on compiled code, looking for
 * AndHow Properties.
 * <p>
 * When an AndHow Property is found in a class, a new {@code PropertyRegistrar}
 * class is created, which contains the list of Properties in that class.
 * There is a one-to-one correspondence between user classes that contain
 * AndHow Properties and auto-created {@code PropertyRegistrar} classes.
 * <p>
 * At runtime, AndHow will use the {@code ServiceLoader} to discover all instances
 * of {@code PropertyRegistrar} on the classpath, thus finding all AndHow
 * Property containing classes.
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

	/**
	 * A no-arg constructor is required.
	 */
	public AndHowCompileProcessor() {
		//used to ensure all metadata files have the same date
		runDate = new GregorianCalendar();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		//Unwrap the IntelliJ ProcessingEnvironment if needed
		super.init(unwrap(processingEnv));

	}

	/**
	 * The java language level the output file needs to be compatible with.
	 * <p>
	 * This is provided by the {@code ProcessingEnvironment.getSourceVersion()} as an enum, but is
	 * converted here to the major version number.  Version 1.8 return 8, ver. 9 returns 9 and so on.
	 * <p>
	 * @return The source level to generate code to.
	 */
	public int getSrcVersion() {
		return CompileUtil.getMajorJavaVersion(processingEnv.getSourceVersion());
	}

	/**
	 * The major version number of the JDK used to compile.
	 * <p>
	 * This taken from {@code System.getProperty("java.version")},
	 * See {@link CompileUtil#getMajorJavaVersion()}.
	 * JDK 1.8 return 8, JDK 9 returns 9 and so on.
	 * <p>
	 * @return The major version of the JDK used to compile.
	 */
	public int getJdkVersion() {
		return CompileUtil.getMajorJavaVersion();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		//Only scanning for declaration of AndHow Properties, so should
		//be immune to most new language constructs.
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		Filer filer = this.processingEnv.getFiler();
		Messager log = this.processingEnv.getMessager();

		boolean isLastRound = roundEnv.processingOver();


		if (isLastRound) {

			debug(log, "source version: {}  jdk version: {}", getSrcVersion(), getJdkVersion());

			if (! CompileUtil.isGeneratedVersionDeterministic(getSrcVersion(), getJdkVersion()) ) {
				warn(log, "The source level is JDK8: 'javac [--release=8] or [-source=8]'. " +
						"Current JDK is {}. Thus, the 'Generated' annotation on proxy classes will be " +
						"commented out.  Not an issue in most cases, but can be fixed by using JDK8 when " +
						"compiling for JRE8.  See: https://github.com/eeverman/andhow/issues/630", getJdkVersion());
			}


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

			//
			//Scan all the Compilation units (i.e. class files) for AndHow Properties
			Iterator<? extends Element> it = roundEnv.getRootElements().iterator();
			for (Element rootElement : roundEnv.getRootElements()) {

				TypeElement rootTypeElement = (TypeElement) rootElement;
				AndHowElementScanner7 st = new AndHowElementScanner7(
						this.processingEnv,
						Property.class.getCanonicalName(),
						INIT_CLASS_NAME,
						TEST_INIT_CLASS_NAME);
				CompileUnit compileUnit = st.scan(rootElement);

				if (compileUnit.istestInitClass()) {
					testInitClasses.add(new CauseEffect(compileUnit.getRootCanonicalName(), rootTypeElement));
				} else if (compileUnit.isInitClass()) {
					initClasses.add(new CauseEffect(compileUnit.getRootCanonicalName(), rootTypeElement));
				}

				if (compileUnit.hasRegistrations()) {

					debug(log, "Found {} AndHow Properties in class {} ",
							compileUnit.getRegistrations().size(), compileUnit.getRootCanonicalName());

					PropertyRegistrarClassGenerator gen =
							new PropertyRegistrarClassGenerator(
									compileUnit, AndHowCompileProcessor.class, runDate,
									getSrcVersion(), getJdkVersion());

					registrars.add(new CauseEffect(gen.buildGeneratedClassFullName(), rootTypeElement));

					try {
						writeClassFile(filer, gen, rootElement);
						debug(log, "Wrote new generated class file {}", gen.buildGeneratedClassSimpleName());
					} catch (Exception ex) {
						error(log, "Unable to write generated classfile '" + gen.buildGeneratedClassFullName() + "'", ex);
						throw new RuntimeException(ex);
					}
				}

				problems.addAll(compileUnit.getProblems());

			}
		}

		return false;
	}

	/**
	 * Writes a new class implementing the {@code PropertyRegistrar} interface.
	 *
	 * The new class directly corresponds to a user classes containing AndHow
	 * Properties and will contain meta data about the properties.
	 *
	 * @param filer The javac file system representation for writing files.
	 * @param generator AndHow class capable of generating source code for this
	 * {@code PropertyRegistrar} class.
	 * @param causingElement A javac Element, which generically refers to any
	 * piece of source code such as a keyword, class name, etc..  When a file
	 * is written to the filer, a {@code causingElement} is recorded as metadata
	 * so there is an association between the file and the reason it was written.
	 * Likely this is normally used to associate source code line numbers with
	 * generated code.
	 *
	 * @throws Exception If unable to write (out of disc space?)
	 */
	public void writeClassFile(Filer filer,
			PropertyRegistrarClassGenerator generator, Element causingElement) throws Exception {

		String classContent = generator.generateSource();

		FileObject classFile = filer.createSourceFile(
				generator.buildGeneratedClassFullName(), causingElement);

		try (Writer writer = classFile.openWriter()) {
			writer.write(classContent);
		}
	}

	protected void writeServiceFile(Filer filer, String fullyQualifiedServiceInterfaceName,
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
		log.printMessage(Kind.NOTE, "AndHowCompileProcessor: " + TextUtil.format(pattern, args));
	}

	/**
	 * Logs a warm message using the javac standard Messager system.
	 *
	 * @param log The Message instance to use
	 * @param pattern String pattern with curly variable replacement like this: {}
	 * @param args Arguments to put into the {}'s, in order.
	 */
	void warn(Messager log, String pattern, Object... args) {
		log.printMessage(Kind.MANDATORY_WARNING, "AndHowCompileProcessor: " + TextUtil.format(pattern, args));
	}

	/**
	 * Logs an error message using the javac standard Messager system.
	 *
	 * @param log The Message instance to use
	 * @param pattern String pattern with curly variable replacement like this: {}
	 * @param args Arguments to put into the {}'s, in order.
	 */
	void error(Messager log, String pattern, Object... args) {
		log.printMessage(Kind.ERROR, "AndHowCompileProcessor: " + TextUtil.format(pattern, args));
	}

	/**
	 * Match up a causal Element (Basically the compiler representation of a class to be compiled) w/
	 * the Class name that will be registered in a service registry.
	 * <p>
	 * When the AnnotationProcessor writes a new file to the Filer, it wants a causal Element to
	 * associate with it, potentially for report issues to the user.
	 */
	protected static class CauseEffect {
		String fullClassName;
		Element causeElement;

		public CauseEffect(String fullClassName, Element causeElement) {
			this.fullClassName = fullClassName;
			this.causeElement = causeElement;
		}
	}

	/**
	 * With the introduction of IntelliJ Idea 2020.3 release the ProcessingEnvironment
	 * is not of type com.sun.tools.javac.processing.JavacProcessingEnvironment
	 * but java.lang.reflect.Proxy.
	 * The com.sun.source.util.Trees.instance() throws an IllegalArgumentException when the proxied processingEnv is passed.
	 *
	 * @author Vicky Ronnen (vicky.ronnen@upcmail.nl or v.ronnen@gmail.com)
	 * @link https://youtrack.jetbrains.com/issue/IDEA-256707
	 * @param processingEnv possible proxied
	 * @return ProcessingEnvironment unwrapped from the proxy if proxied or the original processingEnv
	 */
	private ProcessingEnvironment unwrap(ProcessingEnvironment processingEnv) {
		if (Proxy.isProxyClass(processingEnv.getClass())) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(processingEnv);
			try {
				Field field = invocationHandler.getClass().getDeclaredField("val$delegateTo");
				field.setAccessible(true);
				Object o = field.get(invocationHandler);
				if (o instanceof ProcessingEnvironment) {
					return (ProcessingEnvironment) o;
				} else {
					processingEnv.getMessager().printMessage(Kind.ERROR, "got " + o.getClass() + " expected instanceof com.sun.tools.javac.processing.JavacProcessingEnvironment");
					return null;
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
				return null;
			}
		} else {
			return processingEnv;
		}
	}

}
