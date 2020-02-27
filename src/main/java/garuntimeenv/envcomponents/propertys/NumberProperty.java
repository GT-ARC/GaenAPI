package garuntimeenv.envcomponents.propertys;

import garuntimeenv.interfaces.Property;

/**
 * Wrapper class that holds a number value
 */
public class NumberProperty implements Property {

    private Number value;

    /**
     * Constructor that wraps a Number into a property
     *
     * @param value The number value to be wrapped
     */
    public NumberProperty(Number value) {
        this.value = value;
    }

    /**
     * Getter for the number property
     *
     * @return The number value
     */
    public Number getValue() {
        return value;
    }

    /**
     * Setter for the number value
     *
     * @param value The value to be set
     */
    public void setValue(Number value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @return The representation of the number
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
