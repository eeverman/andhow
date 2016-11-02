package yarnandtail.andhow.valuetype;

/**
 *
 * @author eeverman
 */
public class StringVal extends BaseValueType implements ValueType {

	private StringVal() {
		super(String.class, false, false, TrimStyle.TO_NULL, true);
	}

	@Override
	public Object convert(Object sourceValue) throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
