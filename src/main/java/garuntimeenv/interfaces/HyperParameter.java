package garuntimeenv.interfaces;

import garuntimeenv.envcomponents.TestManager;

import java.util.List;

/**
 * Interface for all hyper parameters
 */
public interface HyperParameter {

    /**
     * Set the best property of the list
     * Sets the index according to {@code bestProperty}
     *
     * @param bestProperty The best property
     */
    <T extends Property> void setBestProperty(T bestProperty);

    /**
     * Return all property instances contained in the hyper parameter wrapper
     *
     * @return A list of all propertys
     */
    List<Property> getAllProperties();

    /**
     * Get the currently pointed at property
     *
     * @return The current property pointed at
     */
    Property getCurrentProperty();

    /**
     * Iterate to the next property contained in the hyper parameter
     *
     * @return True if there is a next property otherwise false
     */
    boolean setNextProperty();

    /**
     * Get the hyper parameter belonging to the object
     *
     * @return The test property
     */
    TestManager.testProperties getTestProperty();

    /**
     * Returns a string of the current property for printing purpose
     *
     * @return A string representing the current hyper parameter instance
     */
    String getCurrentPropertyString();
}
