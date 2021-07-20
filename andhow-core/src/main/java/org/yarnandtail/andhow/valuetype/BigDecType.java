package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

import java.math.BigDecimal;

/**
 * Type representation of Java BigDecimal objects.
 *
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 *
 * @author chace86
 */
public class BigDecType extends BaseValueType<BigDecimal> {

    private static final BigDecType INSTANCE = new BigDecType();

    private BigDecType() {
        super(BigDecimal.class);
    }

    /**
     * Construct an instance of BigDecType
     */
    public static BigDecType instance() {
        return INSTANCE;
    }


    /**
     * {@inheritDoc}
     * For more information on how the sourceValue is parsed, see
     * <a href="https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#BigDecimal-java.lang.String-">BigDecimal String constructor</a>.
     */
    @Override
    public BigDecimal parse(String sourceValue) throws ParsingException {
        if (sourceValue == null) {
            return null;
        }
        try {
            return new BigDecimal(sourceValue);
        } catch (Exception e) {
            throw new ParsingException("Unable to convert to a BigDecimal numeric value", sourceValue, e);
        }
    }

    @Override
    public BigDecimal cast(Object o) throws RuntimeException {
        return (BigDecimal)o;
    }
}
