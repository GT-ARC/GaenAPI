package garuntimeenv.gacomponents;

import com.google.gson.JsonElement;
import garuntimeenv.interfaces.MyObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that holds all problem properties and calls registered callbacks
 * if the requested property is set
 */
public class ProblemProperties {

    // Map containing the json elements
    private static HashMap<String, JsonElement> problemProperties = new HashMap<>();

    // Map holding the callback observers that registered for a specific property name
    private static HashMap<String, List<MyObserver>> callbacks = new HashMap<>();

    // Singleton instance
    private static ProblemProperties INSTANCE = new ProblemProperties();

    /**
     * Getter for the ProblemProperties Instance
     * Singleton pattern for the problem properties class with lazy loading
     *
     * @return The instance
     */
    public static ProblemProperties getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Get the property of the loaded json problem
     *
     * @param property The requested property name
     * @return The json element of the property or null if not present
     */
    public JsonElement getProperty(String property) {
        return problemProperties.get(property);
    }

    /**
     * Remove all loaded problem properties
     */
    public void clearProperties() {
        problemProperties.clear();
    }

    /**
     * Add a new problem to the problem properties map and call the registered observers
     *
     * @param key The name of the property
     * @param value The json element
     */
    public void addProblem(String key, JsonElement value) {
        problemProperties.put(key, value);
        if (callbacks.containsKey(key)) {
            for (MyObserver obs : callbacks.get(key)) {
                obs.setValue(value);
            }
        }
    }

    /**
     * Register to a property loading event for a specific property name
     *
     * @param key The property name
     * @param reference The call back function
     */
    public void notifyOnDataUpdate(String key, MyObserver reference) {
        if (!callbacks.containsKey(key)) {
            callbacks.put(key, new ArrayList<>());
        }

        callbacks.get(key).add(reference);
    }
}
