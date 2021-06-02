package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.yarnandtail.andhow.service.PropertyRegistrar;
import org.yarnandtail.andhow.service.PropertyRegistration;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.yarnandtail.andhow.util.IOUtil;


/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarClassGenerator {

	private final CompileUnit compUnit;
	private final Class<?> generatingClass;
	private final Calendar runDate;
	private final SimpleDateFormat dateFormat;
	
	/**
	 * Create a new instance w all info needed to generateSource a PropertyRegistrar file.
	 * 
	 * @param compUnit CompileUnit instance w/ all needed class and property info
	 * @param generatingClass The class of our AnnotationProcessor to be listed as the generator in the
	 *                        Generated annotation.
	 * @param runDate  The Calendar date-time of the run, used for annotation.
	 *		Passed in so all generated files can have the same timestamp.
	 */
	public PropertyRegistrarClassGenerator(CompileUnit compUnit, Class<?> generatingClass, Calendar runDate) {
		this.compUnit = compUnit;
		this.generatingClass = generatingClass;
		this.runDate = runDate;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}
	
	public String getTemplatePath() {
		return "/" + PropertyRegistrarClassGenerator.class.getCanonicalName().replace(".", "/") + "_Template.txt";
	}
	
	public String getTemplate() throws Exception {
		return IOUtil.getUTF8ResourceAsString(getTemplatePath());
	}

	
	public String generateSource() throws Exception {

		String template = getTemplate();


		String source = String.format(template,
				buildPackageString(),
				compUnit.getRootCanonicalName(),
				compUnit.getRootSimpleName(),
				buildGeneratedClassSimpleName(),
				PropertyRegistrar.class.getCanonicalName(),
				generatingClass.getCanonicalName(),
				buildRunDateString(),
				buildRegistrationAddsString(),
				CompileUtil.getGeneratedAnnotationClassName()
		);
		
		return source;
	}
	
	protected String buildPackageString() {
		if (compUnit.getRootPackageName() != null) {
			return "package " + compUnit.getRootPackageName() + ";";
		} else {
			return "";
		}
	}
	
	//TODO:  Should this be moved to the nameUtil?
	protected String buildGeneratedClassSimpleName() {
		return "$" + compUnit.getRootSimpleName() + "_AndHowProps";
	}
	
	protected String buildGeneratedClassFullName() {
		if (compUnit.getRootPackageName() != null) {
			return compUnit.getRootPackageName() + "." + buildGeneratedClassSimpleName();
		} else {
			return buildGeneratedClassSimpleName();
		}
	}
	
	protected String buildRunDateString() {
		return dateFormat.format(runDate.getTime());
	}
	
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
