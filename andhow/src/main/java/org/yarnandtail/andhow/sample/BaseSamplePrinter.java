package org.yarnandtail.andhow.sample;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.GroupInfo;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.sample.TextLine.HRLine;
import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.api.BasePropertyGroup;


/**
 *
 * @author ericeverman
 */
public abstract class BaseSamplePrinter implements SamplePrinter {
	
	abstract PrintFormat getFormat();
	
	public abstract TextBlock getSampleFileStart();
	
	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract TextBlock getSampleStartComment(ConstructionDefinition definition);
	
	public abstract String getInAliaseString(ConstructionDefinition definition, EffectiveName name);
	
	public abstract TextBlock getActualProperty(ConstructionDefinition definition, 
			Class<? extends BasePropertyGroup> group, Property prop) throws Exception;
	
	public abstract TextBlock getSampleFileEnd();
	
	protected void printIfNotNull(PrintStream out, String line) {
		if (line != null) {
			out.println(line);
		}
	}
	
	protected void print(PrintStream out, TextBlock block, PrintFormat format) {
		
		if (block == null) return;
		
		boolean useBlockComment = false;
		boolean useLineComment = false;
		
		if (block.isBlankLineBefore()) {
			out.println();
		}
		
		if (block.isComment()) {
			useLineComment = format.usesLineComments();
			useBlockComment = format.usesBlockComments();
		}
		
		if (useBlockComment) {
			
			if (block.getLines().size() == 1) {
				
				TextLine line = block.getLine(0);
				
				if (block.isWrap()) {
					List<String> lines = line.getWrappedBlockComment(format, true, true);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getBlockComment(format, true, true));
				}
			
				
			} else {

				int rowToPrint = 0;
				
				while (rowToPrint < block.getLines().size()) {

					TextLine line = block.getLine(rowToPrint);
					boolean isFirstLine = rowToPrint == 0;
					boolean isLastLine = (rowToPrint == (block.getLines().size() - 1));
					

					//need to add the end comment on the last row
					if (line.wrap) {
						List<String> lines = line.getWrappedBlockComment(format, isFirstLine, isLastLine);
						lines.stream().forEachOrdered(l -> out.println(l));
					} else {
						String lineStr = line.getBlockComment(format, isFirstLine, isLastLine);
						out.println(lineStr);
					}

					rowToPrint++;
				}
			}
			
		} else if (useLineComment) {
			
			for (TextLine line : block.getLines()) {
				if (line.wrap) {
					List<String> lines = line.getWrappedLineComment(format);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getLineComment(format));
				}
			}
						
		} else {
			
			for (TextLine line : block.getLines()) {
				
				if (line instanceof HRLine) {
					if (format.usesBlockComments()) {
						out.println(line.getBlockComment(format, true, true));
					} else {
						out.println(line.getLineComment(format));
					}
				} else if (line.wrap) {
					List<String> lines = line.getWrappedLine(format);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getLine(format));
				}
			}
			
		}
		
		if (block.isBlankLineAfter()) {
			out.println();
		}
	}

	@Override
	public void printSampleStart(ConstructionDefinition definition, PrintStream out) {
		print(out, getSampleFileStart(), getFormat());
		TextBlock tb = getSampleStartComment(definition);
		
		if (tb != null) {
			tb.setBlankLineAfter(true);
			print(out, tb, getFormat());
		}
	}
	
	@Override
	public void printPropertyGroupStart(ConstructionDefinition definition, 
			PrintStream out, Class<? extends BasePropertyGroup> group) {
		
		TextBlock tb = new TextBlock(true, true);
		tb.setBlankLineAfter(true);
		tb.addHR();
		
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
				
				tb.addLine(TextUtil.format("Property Group '{}' - {}  Defined in interface {}", 
						name, desc, group.getCanonicalName()));

			} else {
				tb.addLine(TextUtil.format("Property Group {}", group.getCanonicalName()));
				tb.addLine(TextUtil.format("Description: {}", (name != null)?name:desc));
			}
			
		} else {
			tb.addLine(TextUtil.format("Property Group {}", group.getCanonicalName()));
		}
		
		print(out, tb, getFormat());
		
	}
	
	
	@Override
	public void printProperty(ConstructionDefinition definition, PrintStream out,
			Class<? extends BasePropertyGroup> group, Property<?> prop) {
		
		TextBlock tb = new TextBlock(true, true);
		tb.addBlank();

		
		try {
			
			String propFieldName = AndHowUtil.getFieldName(group, prop);
					
			tb.addLine(TextUtil.format("{} ({}) {}{}", 
					propFieldName,
					prop.getValueType().getDestinationType().getSimpleName(),
					(prop.isNonNullRequired())?SamplePrinter.REQUIRED_KEYWORD:"",
					(TextUtil.trimToNull(prop.getDescription()) == null)?"":" - " + prop.getDescription()));

			List<EffectiveName> effAliases = definition.getAliases(prop);
			List<String> inAliases = new ArrayList();
			effAliases.stream().filter(a -> a.isIn()).forEachOrdered(a -> {
				inAliases.add(getInAliaseString(definition, a));
			});
			if (inAliases.size() > 0) {
				tb.addLine(TextUtil.format("Recognized aliases: {}", String.join(", ", inAliases)));
			}
						
			if (prop.getDefaultValue() != null) {
				tb.addLine(DEFAULT_VALUE_TEXT + ": " + prop.getDefaultValue());
			}

			if (TextUtil.trimToNull(prop.getHelpText()) != null) {
				tb.addLine(prop.getHelpText());
			}

			if (prop.getValidators().size() == 1) {
				tb.addLine(
					TextUtil.format(THE_VALUE_MUST_TEXT + " " + prop.getValidators().get(0).getTheValueMustDescription())
				);
			}

			if (prop.getValidators().size() > 1) {
				tb.addLine(THE_VALUE_MUST_TEXT + ":");	
				for (Validator v : prop.getValidators()) {
					tb.addLine("\t- " + v.getTheValueMustDescription());
				}
			}

			print(out, tb, getFormat());

			TextBlock aptb = getActualProperty(definition, group, prop);
			aptb.setBlankLineAfter(true);	//Always want a space after

			print(out, aptb, getFormat());
		
		} catch (Exception ex) {
			
			tb.addLine(TextUtil.format("SECURITY EXCEPTION TRYING TO ACCESS FIELDS IN {}. " +
					"ENSURE ALL Property FIELDS ARE PUBLIC IN THE PropertyGroup " +
					"AND THAT THERE IS NOT A SECURITY MANAGER BLOCKING ACCESS TO REFLECTION.", group.getCanonicalName()));
			print(out, tb, getFormat());
		}
		
		
	}
	
	

	@Override
	public void printPropertyGroupEnd(ConstructionDefinition definition, 
			PrintStream out, Class<? extends BasePropertyGroup> group) {
	}
	
	@Override
	public void printSampleEnd(ConstructionDefinition definition, PrintStream out) {
		TextBlock tb = getSampleFileEnd();
		print(out, tb, getFormat());
	}
	
}
