package garuntimeenv.envcomponents.propertys;

import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;

import java.util.ArrayList;
import java.util.List;

public class ValueHyperParameter implements HyperParameter {

    private static final double PRECESSION = EnvConfig.getInstance().roundTo;

    private int index = 0;
    private List<Property> properties;

    private Number startValue;
    private NumberProperty endValue;
    private NumberProperty currentValue;
    private NumberProperty stepValue;
    private TestManager.testProperties testProperty;

    public ValueHyperParameter(TestManager.testProperties testProperty, Number startValue, Number endValue, Number stepValue) {
        this.testProperty = testProperty;
        this.startValue = startValue;
        this.endValue = new NumberProperty(endValue);
        this.currentValue = new NumberProperty(startValue);
        this.stepValue = new NumberProperty(stepValue);

        this.properties = new ArrayList<>();
        this.properties.add(this.currentValue);

        genPropertyEntries();
    }

    @Override
    public Property getCurrentProperty() {
        return this.properties.get(index);
    }

    /**
     * Increases the index
     * When the next value is outside range of the set end value then return false
     *
     * @return currentValue + stepValue greater endValue
     */
    @Override
    public boolean setNextProperty() {
        if (index + 1 < properties.size()) {
            index++;
            return true;
        }
        return false;
    }

    /**
     * Create a new property based on the current property and the step value.
     */
    private void genPropertyEntries() {
        boolean next = true;
        do {
            // Get the type of the step value assuming all property values have the same type
            // Due to the fact that you cant do Number + Number you need a specific case for each "reasonable" number type
            switch (stepValue.getValue().getClass().getSimpleName()) {
                case "Integer":
                    // Check if next value would be out of range
                    if (currentValue.getValue().intValue() + stepValue.getValue().intValue() > endValue.getValue().intValue()) {
                        next = false;
                    } else {
                        // Create a new current value so you can set the "best" value easily with the property object reference
                        this.currentValue = new NumberProperty(currentValue.getValue().intValue() + stepValue.getValue().intValue());
                        // Add the newly created property to the property list
                        this.properties.add(this.currentValue);
                    }
                    break;
                case "Double":
                    if (currentValue.getValue().doubleValue() + stepValue.getValue().doubleValue() > endValue.getValue().doubleValue()) {
                        next = false;
                    } else {
                        double value = currentValue.getValue().doubleValue() + stepValue.getValue().doubleValue();
                        value = Math.round(value * PRECESSION) / PRECESSION;

                        this.currentValue = new NumberProperty(value);
                        this.properties.add(this.currentValue);
                    }
                    break;
                case "Long":
                    if (currentValue.getValue().longValue() + stepValue.getValue().longValue() > endValue.getValue().longValue()) {
                        next = false;
                    } else {
                        this.currentValue = new NumberProperty(currentValue.getValue().longValue() + stepValue.getValue().longValue());
                        this.properties.add(this.currentValue);
                    }
                    break;
                case "Float":
                    if (currentValue.getValue().floatValue() + stepValue.getValue().floatValue() > endValue.getValue().floatValue()) {
                        next = false;
                    } else {
                        this.currentValue = new NumberProperty(currentValue.getValue().floatValue() + stepValue.getValue().floatValue());
                        this.properties.add(this.currentValue);
                    }
                    break;
            }
        } while (next);
    }

    @Override
    public <T extends Property> void setBestProperty(T bestProperty) {
        this.currentValue = (NumberProperty) bestProperty;
    }

    @Override
    public TestManager.testProperties getTestProperty() {
        return testProperty;
    }

    @Override
    public String getCurrentPropertyString() {
        return this.properties.get(index).toString();
    }

    @Override
    public List<Property> getAllProperties() {
        return this.properties;
    }

    public Number getStartValue() {
        return startValue;
    }

    public double getDouble() {
        return ((NumberProperty) getCurrentProperty()).getValue().doubleValue();
    }

    public int getInt() {
        return ((NumberProperty) getCurrentProperty()).getValue().intValue();
    }

    public Long getLong() {
        return ((NumberProperty) getCurrentProperty()).getValue().longValue();
    }

    public float getFloat() {
        return ((NumberProperty) getCurrentProperty()).getValue().floatValue();
    }

}
