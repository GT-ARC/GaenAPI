package garuntimeenv.envcomponents.propertys;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;

import java.util.List;

/**
 * Class representing a the list hyper parameters
 */
public class ListHyperParameter implements HyperParameter {

    private TestManager.testProperties testProperty;    // Belonging tested property or hyper parameter
    private List<Property> listProperties;              // The property instances to be iterated over
    private int currentIndex = 0;                       // The current index starts at zero

    /**
     * Constructor for list propertys
     *
     * @param testProperty   The hyper parameter
     * @param listProperties The list of properties
     */
    public ListHyperParameter(TestManager.testProperties testProperty, List<Property> listProperties) {
        this.testProperty = testProperty;
        this.listProperties = listProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Property> void setBestProperty(T bestProperty) {
        this.currentIndex = listProperties.indexOf(bestProperty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getCurrentProperty() {
        return listProperties.get(currentIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setNextProperty() {
        if (currentIndex + 1 < listProperties.size()) {
            currentIndex++;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentPropertyString() {
        return listProperties.get(currentIndex).getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestManager.testProperties getTestProperty() {
        return testProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Property> getAllProperties() {
        return this.listProperties;
    }
}
