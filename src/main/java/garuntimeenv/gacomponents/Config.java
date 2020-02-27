package garuntimeenv.gacomponents;

import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.gacomponents.geneticoperators.crossover.LOX;
import garuntimeenv.gacomponents.geneticoperators.mutations.InversionMutation;
import garuntimeenv.gacomponents.geneticoperators.selections.RouletteWheelSelection;
import garuntimeenv.gacomponents.jobshop.JobShopPreferenceListRep;
import garuntimeenv.gacomponents.jobshop.MakespanFitnessFunction;
import garuntimeenv.interfaces.*;
import garuntimeenv.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class containing the configuration for the genetic algorithm
 * Also used as default values
 */
public class Config {

    // The name of the configuration
    private String name;

    // If set the genetic algorithm checks in each generation if all elements are present in the chromosomes
    private boolean checkForCorrectness = false;

    // The used chromosome representation, fitness function and selection algorithm
    private IProblemRepresentation representation = new JobShopPreferenceListRep();
    private IFitnessFunction fitnessFunction = new MakespanFitnessFunction();
    private Selection prevChromosomeSelection = new RouletteWheelSelection();

    private int populationSize = 200;                       // Defining the amount of chromosome in each generation

    private double crossoverChromosomes = 0.4;              // The amount of chromosomes generated through crossover
    private double mutationChromosomes = 0.3;               // The amount of chromosomes generated through mutation
    private double subGenomeMutationProbability = 0.4;      // The probability of a sub genome to mutate

    private int hallOfFameSize = 300;                       // The size of the hall of fame
    private double hallOfFamePercentage = 0.2;              // The probability for a selected chromosome to come from the hall of fame
    private double selectionAggression = 0.;                // The selection aggresion

    // The used crossover and mutation operators
    private List<CrossoverOperators> crossoverOperators = new ArrayList<>();
    private List<IMutation> mutationOperators = new ArrayList<>();

    // Store the latest instance that is created here
    public static Config latestInstance = null;

    /**
     * Constructor that initilizes the used operators
     */
    public Config() {
        latestInstance = this;
        setNewConfig();
    }

    /**
     * Set the configuration
     */
    private void setNewConfig() {
        this.crossoverOperators.add(new LOX());
        this.mutationOperators.add(new InversionMutation());
    }

    /**
     * The normalize the crossover and mutation operator stuff
     */
    public void normalizeCrossoverMutation() {
        double sum = crossoverChromosomes + mutationChromosomes > 1 ? crossoverChromosomes + mutationChromosomes : 1;
        double pres = EnvConfig.getInstance().roundTo;

        crossoverChromosomes = Math.round(crossoverChromosomes / sum * pres) / pres;
        mutationChromosomes = Math.round(mutationChromosomes / sum * pres) / pres;
    }

    /**
     * Print the configuration as string line
     *
     * @return String representing the configuration
     */
    @Override
    public String toString() {
        return "Size: " + populationSize + " " +
                "agg: " + selectionAggression + " " +
                "C: " + crossoverChromosomes + " " +
                "M: " + mutationChromosomes + " " +
                "P: " + subGenomeMutationProbability + " " +
                "Fame: " + hallOfFameSize + " " +
                "FP: " + hallOfFamePercentage + " " +
                "Sel: " + prevChromosomeSelection.getClass().getSimpleName() + " " +
                "CO: " + Utils.getObjectListName(Collections.singletonList(crossoverOperators)) + " " +
                "MO: " + Utils.getObjectListName(Collections.singletonList(mutationOperators)) + " ";
    }

    /**
     * String with long hyper parameter names
     *
     * @return String representing the configuration
     */
    public String longString() {
        return "\n\tPopulation Size: " + populationSize + "\n" +
                "\tSelection Aggression: " + selectionAggression + "\n" +
                "\tCrossover Chromosomes: " + crossoverChromosomes + "\n" +
                "\tMutation Chromosomes: " + mutationChromosomes + "\n" +
                "\tSub genome mutation Probability: " + subGenomeMutationProbability + "\n" +
                "\tHall of Fame size: " + hallOfFameSize + "\n" +
                "\tHall of Fame selection percentage: " + hallOfFamePercentage + "\n" +
                "\tSelection Algorithm: " + prevChromosomeSelection.getClass().getSimpleName() + "\n" +
                "\tCrossover Operators: " + Utils.getObjectListName(Collections.singletonList(crossoverOperators)) + "\n" +
                "\tMutation Operators: " + Utils.getObjectListName(Collections.singletonList(mutationOperators)) + "\n";
    }

    // ##### Setter and Getter for the hyper parameter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckForCorrectness() {
        return checkForCorrectness;
    }

    public void setCheckForCorrectness(boolean checkForCorrectness) {
        this.checkForCorrectness = checkForCorrectness;
    }

    public IProblemRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(IProblemRepresentation representation) {
        this.representation = representation;
    }

    public IFitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(IFitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getCrossoverChromosomes() {
        return crossoverChromosomes;
    }

    public void setCrossoverChromosomes(double crossoverChromosomes) {
        this.crossoverChromosomes = crossoverChromosomes;
    }

    public double getMutationChromosomes() {
        return mutationChromosomes;
    }

    public void setMutationChromosomes(double mutationChromosomes) {
        this.mutationChromosomes = mutationChromosomes;
    }

    public double getSubGenomeMutationProbability() {
        return subGenomeMutationProbability;
    }

    public void setSubGenomeMutationProbability(double subGenomeMutationProbability) {
        this.subGenomeMutationProbability = subGenomeMutationProbability;
    }

    public int getHallOfFameSize() {
        return hallOfFameSize;
    }

    public void setHallOfFameSize(int hallOfFameSize) {
        this.hallOfFameSize = hallOfFameSize;
    }

    public double getHallOfFamePercentage() {
        return hallOfFamePercentage;
    }

    public void setHallOfFamePercentage(double hallOfFamePercentage) {
        this.hallOfFamePercentage = hallOfFamePercentage;
    }

    public List<CrossoverOperators> getCrossoverOperators() {
        return crossoverOperators;
    }

    public void setCrossoverOperators(List<CrossoverOperators> crossoverOperators) {
        this.crossoverOperators = crossoverOperators;
    }

    public List<IMutation> getMutationOperators() {
        return mutationOperators;
    }

    public void setMutationOperators(List<IMutation> mutationOperators) {
        this.mutationOperators = mutationOperators;
    }

    public Selection getPrevChromosomeSelection() {
        return prevChromosomeSelection;
    }

    public void setPrevChromosomeSelection(Selection prevChromosomeSelection) {
        this.prevChromosomeSelection = prevChromosomeSelection;
    }

    public void setSelectionAggression(double aDouble) {
        this.selectionAggression = aDouble;
    }

    public double getSelectionAggressive() {
        return this.selectionAggression;
    }


}
