package garuntimeenv.envcomponents.propertys;

import garuntimeenv.interfaces.Property;

import java.util.List;

/**
 * Wrapper class that holds a list of properties as a property
 */
public class ListProperty implements Property {

    // List containing the operator properties
    private List<? extends Property> value;

    /**
     * Constructor for the list property
     *
     * @param properties List of the properties
     */
    public ListProperty(List<? extends Property> properties) {
        value = properties;
    }

    /**
     * Getter for the properties
     */
    public List<? extends Property> getValue() {
        return value;
    }

    /**
     * Setter for the list
     *
     * @param value The list of properties
     */
    public void setValue(List<? extends Property> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (Property prop : value) {
            stringBuilder.append(prop.getClass().getSimpleName().replace("Mutation", ""));
            if (value.indexOf(prop) != value.size() - 1) stringBuilder.append(" ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
