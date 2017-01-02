/*
 */
package yarnandtail.andhow.load;

import java.io.PrintStream;
import yarnandtail.andhow.ConfigSamplePrinter;
import yarnandtail.andhow.GroupInfo;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import static yarnandtail.andhow.ReportGenerator.DEFAULT_LINE_WIDTH;
import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.Validator;

/**
 *
 * @author ericeverman
 */
public abstract class BaseSamplePrinter implements ConfigSamplePrinter {
	
	//private static final String LINE_PREFIX = "# ";
	
	protected void printIfNotNull(PrintStream out, String line) {
		if (line != null) {
			out.println(line);
		}
	}
	
	public abstract String getSampleFileStart();
	
	public abstract String getBlockCommentStart();
	
	public abstract String getBlockCommentEnd();
	
	/**
	 * Include the character and a trailing space.  Otherwise null.
	 * @return 
	 */
	public abstract String getLineCommentPrefix();
	
	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract String getSectionHeader();
	
	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract String getSampleStartIntro();
	
	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract String getSampleStartDescription();
	
	public abstract String getActualProperty(Class<? extends PropertyGroup> group, Property prop) throws Exception;

	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract String getPropertyGroupEnd();
	
	public abstract String getSampleFileEnd();

	@Override
	public void printSampleStart(PrintStream out) {
		printIfNotNull(out, getSampleFileStart());
		printIfNotNull(out, getBlockCommentStart());
		printIfNotNull(out, getSectionHeader());
		printIfNotNull(out, getSampleStartIntro());
		printIfNotNull(out, getSampleStartDescription());
		printIfNotNull(out, getBlockCommentEnd());
	}
	
	@Override
	public void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group) {
		out.println();
		out.println();
		printIfNotNull(out, getBlockCommentStart());
		printIfNotNull(out, getSectionHeader());
		
		String name = null;
		String desc = null;
		
		GroupInfo groupDesc = group.getAnnotation(GroupInfo.class);
		if (groupDesc != null) {
			name = TextUtil.trimToNull(groupDesc.name());
			desc = TextUtil.trimToNull(groupDesc.desc());
		}
		
		if (name != null || desc != null) {
			if (name != null && desc != null) {
				
				if (! desc.endsWith(".")) desc = desc + ".";
				
				TextUtil.println(out, DEFAULT_LINE_WIDTH, getLineCommentPrefix(), 
						"Property Group '{}' - {}  Defined in interface {}", name, desc, group.getCanonicalName());

			} else {
				TextUtil.println(out, getLineCommentPrefix() + "Property Group {}", group.getCanonicalName());
				TextUtil.println(out, DEFAULT_LINE_WIDTH, getLineCommentPrefix(), "Description: {}", (name != null)?name:desc);
			}
			
			
		} else {
			TextUtil.println(out, getLineCommentPrefix() + "Property Group {}", group.getCanonicalName());
		}
		
		printIfNotNull(out, getBlockCommentEnd());
		
	}
	
	
	@Override
	public void printProperty(PrintStream out, Class<? extends PropertyGroup> group,
			Property<?> prop) throws Exception {
		
		
		String propFieldName = PropertyGroup.getFieldName(group, prop);
		
		out.println();
		printIfNotNull(out, getBlockCommentStart());
		
		TextUtil.println(out, DEFAULT_LINE_WIDTH, getLineCommentPrefix(), "{} ({}) {}{}", 
				propFieldName,
				prop.getValueType().getDestinationType().getSimpleName(),
				(prop.isRequired())?ConfigSamplePrinter.REQUIRED_KEYWORD:"",
				(TextUtil.trimToNull(prop.getShortDescription()) == null)?"":" - " + prop.getShortDescription());
		
		if (prop.getDefaultValue() != null) {
			out.println(getLineCommentPrefix() + DEFAULT_VALUE_TEXT + ": " + prop.getDefaultValue());
		}
		
		if (TextUtil.trimToNull(prop.getHelpText()) != null) {
			TextUtil.println(out, DEFAULT_LINE_WIDTH, getLineCommentPrefix(), prop.getHelpText());
		}
		
		if (prop.getValidators().size() == 1) {
			TextUtil.println(out, DEFAULT_LINE_WIDTH, getLineCommentPrefix(), THE_VALUE_MUST_TEXT + " " + prop.getValidators().get(0).getTheValueMustDescription());
		}
		
		if (prop.getValidators().size() > 1) {
			out.println(getLineCommentPrefix() + THE_VALUE_MUST_TEXT + ":");	
			for (Validator v : prop.getValidators()) {
				out.println(getLineCommentPrefix() + "\t- " + v.getTheValueMustDescription());
			}
		}
		
		printIfNotNull(out, getBlockCommentEnd());
		
		printIfNotNull(out, getActualProperty(group, prop));
	}
	
	

	@Override
	public void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group) {
		printIfNotNull(out, getPropertyGroupEnd());
	}
	
	@Override
	public void printSampleEnd(PrintStream out) {
		out.println();
		
		printIfNotNull(out, getBlockCommentStart());
		printIfNotNull(out, getSectionHeader());
		printIfNotNull(out, getBlockCommentEnd());
		printIfNotNull(out, getSampleFileEnd());
	}

	
}
