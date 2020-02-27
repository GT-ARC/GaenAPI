package garuntimeenv.interfaces;

import com.google.gson.JsonElement;

/**
 * Observer pattern for the subscribing of problem loading
 *
 * E.G.
 *    In a Static context notifyOnDataUpdate has as second parameter a myObserver instance
 *     {
 *         ProblemProperties.getINSTANCE().notifyOnDataUpdate("lower_bound", element -> bestFitness = element.getAsDouble());
 *     }
 *
 */
public interface MyObserver {

    /**
     * Function gets called if the subscribed element is loaded
     *
     * @param element The loaded element as json element to be interpreted by the call back
     */
    void setValue(JsonElement element);

}
