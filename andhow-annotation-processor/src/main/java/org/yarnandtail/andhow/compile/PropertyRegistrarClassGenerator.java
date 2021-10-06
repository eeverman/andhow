package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.yarnandtail.andhow.service.PropertyRegistrar;
import org.yarnandtail.andhow.service.PropertyRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.yarnandtail.andhow.util.IOUtil;


/**
 * Generates the source code for a class generated as an AndHow proxy for an application class
 * that contains AndHow Properties.
 */
public class PropertyRegistrarClassGenerator {

	protected static final String CLASS_TEMPLATE_PATH =
			"/org/yarnandtail/andhow/compile/PropertyRegistrarClassGenerator_ClassTemplate.txt";
	protected static final String ANNOTATION_TEMPLATE_PATH =
			"/org/yarnandtail/andhow/compile/PropertyRegistrarClassGenerator_AnnTemplate.txt";
	protected static final String COMMENTED_ANNOTATION_TEMPLATE_PATH =
			"/org/yarnandtail/andhow/compile/PropertyRegistrarClassGenerator_CommentedAnnTemplate.txt";


	private final CompileUnit compUnit;
	private final Class<?> generatingClass;
	private final Calendar runDate;
	private final SimpleDateFormat dateFormat;
	private final int srcMajorVersion;
	private final int jdkMajorVersion;
	
	/**
	 * Create a new instance w all info needed to generateSource a PropertyRegistrar file.
	 * <p>
	 * Annotation Processor generated classes should have an @Generated annotation, however, the
	 * package of that class changed between JDK8 and JDK9.  The srcMajorVersion is used to distinguish
	 * which annotation to use.  JDK 1.8 = version 8, and so on.
	 * <p>
	 * @param compUnit CompileUnit instance w/ all needed class and property info
	 * @param generatingClass The class of our AnnotationProcessor to be listed as the generator in the
	 *                        Generated annotation.
	 * @param runDate  The Calendar date-time of the run, used for annotation.
	 *		Passed in so all generated files can have the same timestamp.
	 * @param srcMajorVersion The major java lang version the output needs to be compatible with.
	 * @param jdkMajorVersion The major version of the JDK currently running.
	 */
	public PropertyRegistrarClassGenerator(final CompileUnit compUnit, final Class<?> generatingClass,
			final Calendar runDate, final int srcMajorVersion, final int jdkMajorVersion) {

		this.compUnit = compUnit;
		this.generatingClass = generatingClass;
		this.runDate = runDate;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		this.srcMajorVersion = srcMajorVersion;
		this.jdkMajorVersion = jdkMajorVersion;
	}

	/**
	 * Loads a text file as a String from a classpath path.
	 * <p>
	 * @param path A path on the classpath, beginning with a slash.
	 * @return A String with the content of the file.
	 * @throws Exception IF the file cannot be read.
	 */
	public String getTemplate(String path) throws Exception {
		return IOUtil.getUTF8ResourceAsString(path);
	}

	/**
	 * Generates the source for the 'Generated' annotation to place on the generated class.
	 * <p>
	 * @return A String containing an annotation, which may be commented out.
	 * @throws Exception If the template file for the annotation cannot be found.
	 */
	public String generateAnnotationSource() throws Exception {
		boolean okToUseAnnotation = CompileUtil.isGeneratedVersionDeterministic(
				srcMajorVersion, jdkMajorVersion);

		String annotationTemplate =
				(okToUseAnnotation)?getTemplate(ANNOTATION_TEMPLATE_PATH):
						getTemplate(COMMENTED_ANNOTATION_TEMPLATE_PATH);

		String generatedAnnotation = String.format(annotationTemplate,
				CompileUtil.getGeneratedAnnotationClassName(jdkMajorVersion),
				generatingClass.getCanonicalName(),
				buildRunDateString(),
				compUnit.getRootCanonicalName(),
				PropertyRegistrar.class.getCanonicalName()
		);

		return generatedAnnotation;
	}

	/**
	 * Generates the complete source for the AndHow proxy class as a String.
	 * <p>
	 * @return A string containing the source code for a generated class.
	 * @throws Exception If any of the template files used for creating the class cannot be read.
	 */
	public String generateSource() throws Exception {

		String classTemplate = getTemplate(CLASS_TEMPLATE_PATH);
		String generatedAnnotation = generateAnnotationSource();

		String source = String.format(classTemplate,
				buildPackageString(),
				compUnit.getRootCanonicalName(),
				compUnit.getRootSimpleName(),
				buildGeneratedClassSimpleName(),
				generatedAnnotation,
				buildRegistrationAddsString(),
				jdkMajorVersion,
				srcMajorVersion
		);
		
		return source;
	}

	/**
	 * The package declaration of the generated class.
	 * <p>
	 * This should match the class its proxying, including an empty string for classes in the
	 * default package.
	 * <p>
	 * @return A String that can be directly used as the package declaration of the generated class.
	 */
	protected String buildPackageString() {
		if (compUnit.getRootPackageName() != null) {
			return "package " + compUnit.getRootPackageName() + ";";
		} else {
			return "";
		}
	}

	/**
	 * The simple class name of the generated class.
	 * <p>
	 * As per Java conventions, a generated class starts with a '$'.  The rest of the class name
	 * matches the class it proxies followed by "_AndHowProps".
	 * <p>
	 * @return The simple class name (i.e. no package info).
	 */
	protected String buildGeneratedClassSimpleName() {
		return "$" + compUnit.getRootSimpleName() + "_AndHowProps";
	}

	/**
	 * The complete canonical name of the generated class, including the package.
	 * <p>
	 * The package portion matches the package of the class being proxied.  The class name
	 * as described in {@link #buildGeneratedClassSimpleName()}.
	 *
	 * @return The canonical name of the generated class.
	 */
	protected String buildGeneratedClassFullName() {
		if (compUnit.getRootPackageName() != null) {
			return compUnit.getRootPackageName() + "." + buildGeneratedClassSimpleName();
		} else {
			return buildGeneratedClassSimpleName();
		}
	}

	/**
	 * A formatted date appropriate for the `Generated` annotation.
	 *
	 * @return A formatted date and time.
	 */
	protected String buildRunDateString() {
		return dateFormat.format(runDate.getTime());
	}

	/**
	 * Builds the source code for the content of the addPropertyRegistrations method.
	 * @return A String of source code.
	 */
	protected String buildRegistrationAddsString() {
		
		StringBuilder buf = new StringBuilder();
		
		PropertyRegistrationList regList = compUnit.getRegistrations();
		regList.sort();
		
		PropertyRegistration prevReg = null;
		
		for (PropertyRegistration pr : regList) {
			
			if ((prevReg != null && pr.compareInnerPathTo(prevReg) != 0) || (prevReg == null && pr.getInnerPathLength() > 0)) {
				//Do a 'full add' b/c this has a different inner path that prev
				//list.add("STRING", "PI", "PI_DC");
				buf.append("\t\tlist.add(\"").append(pr.getPropertyName()).append("\"");
				
				for (String step : pr.getInnerPath()) {
					buf.append(", \"").append(step).append("\"");
				}
				
				buf.append(");").append(System.lineSeparator());
				
			} else {
				//Do a simple add b/c this has the same inner path as prev
				buf.append("\t\tlist.add(\"").append(pr.getPropertyName()).append("\");").append(System.lineSeparator());
			}

			prevReg = pr;
			
		}
		
		return buf.toString();
	}
}
