package garuntimeenv.envcomponents;

import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.envcomponents.propertys.*;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.gacomponents.geneticoperators.crossover.LOX;
import garuntimeenv.gacomponents.geneticoperators.crossover.OX;
import garuntimeenv.gacomponents.geneticoperators.crossover.PMX;
import garuntimeenv.gacomponents.geneticoperators.mutations.InsertionMutation;
import garuntimeenv.gacomponents.geneticoperators.mutations.InversionMutation;
import garuntimeenv.gacomponents.geneticoperators.mutations.SwapMutation;
import garuntimeenv.gacomponents.geneticoperators.selections.RouletteWheelSelection;
import garuntimeenv.gacomponents.jobshop.JobShopPreferenceListRep;
import garuntimeenv.interfaces.*;
import garuntimeenv.utils.MyLogger;

import java.util.*;

/**
 * The test manager class managing all hyper parameter objects and instances and
 * keeps track which one was tested and invokes the data evaluator
 */
public class TestManager {

    final static MyLogger logger = MyLogger.getLogger(TestManager.class);
    public static Random rand = new Random();
    //    public static Random rand = new Random(512315123123L);

    public enum testProperties {
        crossoverChromosomes,
        mutationChromosomes,
        mutationProbability,
        hallOfFameAmount,
        hallOfFamePercentage,
        populationSize,
        crossOverOperators,
        toBeTestedMutations,
        toBeTestedSelection,
        selectionAggression,
        toBeTestedRepresentation,
    }

    public static int testRepetition = 5;

    // Info for the maximum generation or termination
    public static int currMaxGenerations = 200;
    private static int startMaxGenerations = currMaxGenerations;

    private Config currentConfig = null;
    private int hyperParameterIndex = 0;
    private HyperParameter[] hyperParameters;
    private LinkedHashMap<testProperties, HyperParameter> properties = new LinkedHashMap<>();
    private testProperties currentTestedProperties = null;

    private DataEvaluator dataEvaluator;
    private static TestManager INSTANCE = new TestManager();

    public static TestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor for the TestManager
     * Sets all the hyper parameter objects into the properties map
     * and initializes the data evaluator
     */
    private TestManager() {
        setUpValueProperties();
//        setToBeTestedProbRep();
        setToBeTestedCrossOver();
        setToBeTestedMutations();
//        setToBeTestedSelectionStrategy();
        this.dataEvaluator = new DataEvaluator();

        this.hyperParameters = new HyperParameter[properties.size()];
        int index = 0;
        for (HyperParameter hyperParameter : properties.values()) {
            hyperParameters[index++] = hyperParameter;
        }

        this.currentTestedProperties = hyperParameters[0].getTestProperty();
    }

    /**
     * Set the current configurations into the (@code config) object thats stored in the properties hash map
     *
     * @param config The config where the properties are stored into
     */
    private void setConfig(Config config) {

        // Set the config parameter stored in the ga properties map
        if (this.currentTestedProperties == testProperties.crossoverChromosomes
                && this.properties.containsKey(testProperties.crossoverChromosomes)) {
            config.setCrossoverChromosomes(
                    ((ValueHyperParameter) this.properties.get(testProperties.crossoverChromosomes)).getDouble()
            );
        }
        if (this.currentTestedProperties == testProperties.mutationChromosomes
                && this.properties.containsKey(testProperties.mutationChromosomes)) {
            config.setMutationChromosomes(
                    ((ValueHyperParameter) this.properties.get(testProperties.mutationChromosomes)).getDouble()
            );
        }
        if (this.currentTestedProperties == testProperties.mutationProbability
                && this.properties.containsKey(testProperties.mutationProbability)) {
            config.setSubGenomeMutationProbability(
                    ((ValueHyperParameter) this.properties.get(testProperties.mutationProbability)).getDouble()
            );
        }
        if (this.currentTestedProperties == testProperties.hallOfFameAmount
                && this.properties.containsKey(testProperties.hallOfFameAmount)) {
            config.setHallOfFameSize(
                    ((ValueHyperParameter) this.properties.get(testProperties.hallOfFameAmount)).getInt()
            );
        }
        if (this.currentTestedProperties == testProperties.hallOfFamePercentage
                && this.properties.containsKey(testProperties.hallOfFamePercentage)) {
            config.setHallOfFamePercentage(
                    ((ValueHyperParameter) this.properties.get(testProperties.hallOfFamePercentage)).getDouble()
            );
        }
        if (this.currentTestedProperties == testProperties.populationSize
                && this.properties.containsKey(testProperties.populationSize)) {
            config.setPopulationSize(
                    ((ValueHyperParameter) this.properties.get(testProperties.populationSize)).getInt()
            );
        }
        if (this.currentTestedProperties == testProperties.selectionAggression
                && this.properties.containsKey(testProperties.selectionAggression)) {
            config.setSelectionAggression(
                    ((ValueHyperParameter) this.properties.get(testProperties.selectionAggression)).getDouble()
            );
        }

        // Set the operators

        if (this.currentTestedProperties == testProperties.crossOverOperators
                && this.properties.containsKey(testProperties.crossOverOperators)) {
            //noinspection unchecked
            config.setCrossoverOperators(
                    (List<CrossoverOperators>) ((ListProperty) this.properties.get(testProperties.crossOverOperators).getCurrentProperty()).getValue()
            );
        }

        if (this.currentTestedProperties == testProperties.toBeTestedMutations
                && this.properties.containsKey(testProperties.toBeTestedMutations)) {
            //noinspection unchecked
            config.setMutationOperators(
                    (List<IMutation>) ((ListProperty) this.properties.get(testProperties.toBeTestedMutations).getCurrentProperty()).getValue()
            );
        }

        if (this.currentTestedProperties == testProperties.toBeTestedSelection
                && this.properties.containsKey(testProperties.toBeTestedSelection)) {
            config.setPrevChromosomeSelection(
                    (Selection) this.properties.get(testProperties.toBeTestedSelection).getCurrentProperty()
            );
        }

        // Set the problem specific stuff

        if (this.currentTestedProperties == testProperties.toBeTestedRepresentation
                && this.properties.containsKey(testProperties.toBeTestedRepresentation)) {
            config.setRepresentation(
                    (IProblemRepresentation) this.properties.get(testProperties.toBeTestedRepresentation).getCurrentProperty()
            );
        }

        config.normalizeCrossoverMutation();


        if (this.getHyperParameterByIndex(this.hyperParameterIndex).getTestProperty()
                == testProperties.selectionAggression
        ) {
            config.setPrevChromosomeSelection(new RouletteWheelSelection());
        }
    }

    /**
     * Get the String label of the current configuration
     *
     * @return String representing the current property that is tested
     */
    public String getCurrConfigLabel() {
        HyperParameter currentProperty = this.getHyperParameterByIndex(this.hyperParameterIndex);
        String propertyName = currentProperty.getTestProperty().toString();
        return propertyName + ": " + currentProperty.getCurrentProperty().toString();
    }

    /**
     * Return the current property regarding the hyper parameter index
     *
     * @return The current tested property
     */
    public Property getCurrTestedProperty() {
        return this.getHyperParameterByIndex(this.hyperParameterIndex).getCurrentProperty();
    }

    /**
     * Returns a config set with the current values
     *
     * @return New Config with the currently set values
     */
    public Config getCurrentConfig() {
        Config initialConfig = new Config();
        setConfig(initialConfig);
        this.currentConfig = initialConfig;
        return initialConfig;
    }

    /**
     * Returns the next configuration
     *
     * @return
     */
    public Config getNextConfig() {
        // Get the current property and set it to its next property
        HyperParameter currentProperty = this.getHyperParameterByIndex(this.hyperParameterIndex);
        if (!currentProperty.setNextProperty()) {

            // Check if there are any data to be evaluate or written
            if (currentProperty.getAllProperties().size() > 1) {
                // Evaluate the current property
                Property bestProperty = dataEvaluator.evaluateData(currentProperty,
                        this.currentConfig.getFitnessFunction());
                if (bestProperty != null)
                    currentProperty.setBestProperty(bestProperty);

                // Write the data
                DataManager.getInstance().writeData(currentProperty);
            }

            if (currentProperty.getTestProperty() == testProperties.populationSize) {
                currMaxGenerations = startMaxGenerations;
            }

            // The border of the current property is reached go to the next property
            this.hyperParameterIndex++;

            // If all the properties are checked return null and signalize the end of the runtime environment
            if (this.hyperParameterIndex >= this.properties.size()) {
                this.hyperParameterIndex--;
                return null;
            }

            currentProperty = this.getHyperParameterByIndex(this.hyperParameterIndex);
            this.currentTestedProperties = currentProperty.getTestProperty();
            logger.info("TestManager", "Next tested property: " + currentProperty.getTestProperty());
            // Check if the new property has a property to check
            if (currentProperty.getAllProperties().size() <= 1) {
                logger.warning("testmanager", "Empty property skipped");
                return getNextConfig();
            }

            if (EnvConfig.getInstance().isVisualEnabled()) {
                DataManager.getInstance().getDataVisualizer().createNewTabbedPane();
            }
        }

        // Special case for the population size property
        // Due to the fact that the properties are stored in a linked-hashmap means the populationsize
        // will always be the first one
        if (currentProperty.getTestProperty() == testProperties.populationSize) {
            double currPopSize = ((NumberProperty) currentProperty.getCurrentProperty()).getValue().doubleValue();
            double startPopSize = ((ValueHyperParameter) currentProperty).getStartValue().doubleValue();

            double multiplier = currPopSize / startPopSize;

            currMaxGenerations = (int) (startMaxGenerations / multiplier);
        }

        return getCurrentConfig();
    }

    /**
     * Method that sets all the properties that are values in the properties list
     */
    private void setUpValueProperties() {
        this.properties.put(testProperties.mutationChromosomes, new ValueHyperParameter(testProperties.mutationChromosomes, 0.0d, 1, 0.2d));
        this.properties.put(testProperties.populationSize, new ValueHyperParameter(testProperties.populationSize, 50, 250, 50));
        this.properties.put(testProperties.selectionAggression, new ValueHyperParameter(testProperties.selectionAggression, -0.2d, 1, 0.1d));
        this.properties.put(testProperties.mutationProbability, new ValueHyperParameter(testProperties.mutationProbability, 0.d, 1d, 0.2d));
        this.properties.put(testProperties.crossoverChromosomes, new ValueHyperParameter(testProperties.crossoverChromosomes, 0.0d, 1, 0.2d));
        this.properties.put(testProperties.hallOfFameAmount, new ValueHyperParameter(testProperties.hallOfFameAmount, 50, 300, 50));
        this.properties.put(testProperties.hallOfFamePercentage, new ValueHyperParameter(testProperties.hallOfFamePercentage, 0.1d, 0.8d, 0.1d));
    }

    /**
     * Set to be tested problem representations
     */
    private void setToBeTestedProbRep() {
        List<Property> toBeTestedProbRep = new ArrayList<>();
        toBeTestedProbRep.add(new JobShopPreferenceListRep());
        ListHyperParameter selectionProperties = new ListHyperParameter(testProperties.toBeTestedRepresentation, toBeTestedProbRep);
        this.properties.put(testProperties.toBeTestedRepresentation, selectionProperties);
    }

    /**
     * Sets the crossover operators in the list
     */
    private void setToBeTestedCrossOver() {
        List<Property> toBeTestedCrossOver = new ArrayList<>();
        toBeTestedCrossOver.add(new LOX());
        toBeTestedCrossOver.add(new PMX());
        toBeTestedCrossOver.add(new OX());
        SetHyperParameter crossOverProperty = new SetHyperParameter(testProperties.crossOverOperators, toBeTestedCrossOver);
        this.properties.put(testProperties.crossOverOperators, crossOverProperty);
    }

    /**
     * Sets the mutation operators
     */
    private void setToBeTestedMutations() {
        List<Property> toBeTestedMutations = new ArrayList<>();
        toBeTestedMutations.add(new InsertionMutation());
        toBeTestedMutations.add(new InversionMutation());
        toBeTestedMutations.add(new SwapMutation());

        SetHyperParameter mutationsProperty = new SetHyperParameter(testProperties.toBeTestedMutations, toBeTestedMutations);
        this.properties.put(testProperties.toBeTestedMutations, mutationsProperty);
    }

    /**
     * Method to initialize the selection strategy
     */
    private void setToBeTestedSelectionStrategy() {
        List<Property> selectionOperatorsToBeTested = new ArrayList<>();
        selectionOperatorsToBeTested.add(new RouletteWheelSelection());
        ListHyperParameter selectionProperties = new ListHyperParameter(testProperties.toBeTestedSelection, selectionOperatorsToBeTested);
        this.properties.put(testProperties.toBeTestedSelection, selectionProperties);
    }

    /**
     * Return a random long number
     *
     * @return A random long number
     */
    public static long getSeed() {
        return rand.nextLong();
    }

    /**
     * Get the hyper parameter out of the array containg the hyper parameter
     *
     * @param index The index of the hyper parameter
     * @return
     */
    private HyperParameter getHyperParameterByIndex(int index) {
        if (index >= hyperParameters.length)
            return null;
        return hyperParameters[index];
    }

    /**
     * Get the current hyper parameter
     *
     * @return The current hyper parameter
     */
    public HyperParameter getCurrentHyperParameter() {
        return this.getHyperParameterByIndex(this.hyperParameterIndex);
    }

    /**
     * Get the collection of tested hyper parameter
     *
     * @return The collection of tested hyper parameter
     */
    public Collection<HyperParameter> getRuntimeProperties() {
        return this.properties.values();
    }

    /**
     * Getter for the current configuration
     *
     * @return return the current configuration thats tested
     */
    public Config getCurrentTestedConfig() {
        return this.currentConfig;
    }

    /**
     * Setter for the generation limit inside the genetic algorithm
     *
     * @param limit the generation limit
     */
    public void setGenerationLimit(int limit) {
        this.currMaxGenerations = limit;
    }
}
