package garuntimeenv.envcomponents.propertys;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hyper parameter class converting the input list into a su
 */
public class SetHyperParameter implements HyperParameter {

    private int currentIndex = 0;
    private List<ListProperty> subsets;
    private TestManager.testProperties testProperty;


    public SetHyperParameter(TestManager.testProperties testProperty, List<Property> operators) {
        this.testProperty = testProperty;
        this.subsets = new ArrayList<>();
        // From the operators in the operators list create subsets to also check the composition of each
        for (List<? extends Property> properties : Utils.getSubsets(operators.toArray(new Property[0]))) {
            ListProperty lwp = new ListProperty(properties);
            this.subsets.add(lwp);
        }
        this.subsets = this.subsets.stream().filter(subsets -> subsets.getValue().size() > 0).collect(Collectors.toList());
    }

    @Override
    public <T extends Property> void setBestProperty(T bestProperty) {
        this.currentIndex = subsets.indexOf(bestProperty);
    }

    @Override
    public Property getCurrentProperty() {
        return subsets.get(currentIndex);
    }

    @Override
    public boolean setNextProperty() {
        if (currentIndex + 1 < subsets.size()) {
            currentIndex++;
            return true;
        }
        return false;
    }

    @Override
    public TestManager.testProperties getTestProperty() {
        return this.testProperty;
    }

    @Override
    public String getCurrentPropertyString() {
        StringBuilder retString = new StringBuilder();
        for (Object prop : ((ListProperty) getCurrentProperty()).getValue())
            retString.append(prop.getClass().getSimpleName());
        return retString.toString();
    }

    @Override
    public List<Property> getAllProperties() {
        return new ArrayList<>(this.subsets);
    }

}
