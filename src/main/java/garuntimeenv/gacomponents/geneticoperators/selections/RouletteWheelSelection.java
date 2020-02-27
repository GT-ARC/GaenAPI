package garuntimeenv.gacomponents.geneticoperators.selections;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.GAManager;
import garuntimeenv.gacomponents.ProblemProperties;
import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.Selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The roulette wheel selection with implemented sigmoid function that alters the selection probability
 */
public class RouletteWheelSelection implements Selection {

    // The current chromsomes out of which shall be choosen
    private List<Chromosome> currentChromosomes = new ArrayList<>();
    // The commulative sum
    private List<Number> cumulativeSums = new ArrayList<>();

    // The best fitness loaded through the fitness
    private static double bestFitness = 0;
    static {
        ProblemProperties.getINSTANCE().notifyOnDataUpdate("lower_bound", element -> bestFitness = element.getAsDouble());
    }

    private final static Random rand = new Random(TestManager.getSeed());

    private double steepness = 0.01;
    private double aggression = 0.1;
    // Will be newly calculated for each generation
    private double midPoint = Double.NaN;
    private double limitValue = Double.NaN;

    /**
     * Constructor for the roulette wheel selection algorithm
     */
    public RouletteWheelSelection() {
    }

    /**
     * Calculate the cumulative sums for the population
     *
     * @param fitnessFunction The fitness function to define the selection probability.
     */
    private void calculateCumulativeSums(IFitnessFunction fitnessFunction) {
        cumulativeSums.clear();

        limitValue = currentChromosomes.stream()
                .map(Chromosome::getFitness)
                .mapToDouble(Number::doubleValue).max().orElse(0) - bestFitness;
        midPoint = (currentChromosomes.stream()
                .map(Chromosome::getFitness)
                .mapToDouble(Number::doubleValue).average().orElse(1) - bestFitness) / limitValue;

        double maxValue = 0;
        // If a smaller fitness value is better
        if (fitnessFunction.isBetterSolution(0, 1)) {
            maxValue = limitValue;
        }
        // Create cumulative sum from fitness of generation
        double firstValue = maxValue - (currentChromosomes.get(0).getFitness().doubleValue() - bestFitness);
        cumulativeSums.add(firstValue * modifierFunction(firstValue));
        for (int i = 1; i < currentChromosomes.size(); i++) {
            double representativeValue = maxValue - (currentChromosomes.get(i).getFitness().doubleValue() - bestFitness);
            cumulativeSums.add(
                    cumulativeSums.get(i - 1).doubleValue() +
                            representativeValue * modifierFunction(representativeValue)
            );
        }
    }

    /**
     * The sigmoid function that modifies the value returning a value between 0 and 2
     *
     * @param x The value for which the factor shall be calculated
     * @return The factor
     */
    private double modifierFunction(double x) {
        return 2 / (1 + Math.exp(-steepness * (x - (limitValue * (midPoint + aggression)))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chromosome getNextChromosome() {
        double maxValue = this.cumulativeSums.get(this.cumulativeSums.size() - 1).doubleValue();

        // When the max value is 0 that means every chromosome has the same fitness
        if (maxValue == 0)
            return this.currentChromosomes.get(rand.nextInt(currentChromosomes.size()));

        double randomValue = rand.nextDouble() * maxValue;

        return this.currentChromosomes.get(this.bisect(randomValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewPopulation(Chromosome[] chromosomes, IFitnessFunction fitnessFunction) {
        this.aggression = GAManager.getINSTANCE().getCurrentConfig().getSelectionAggressive();
        this.currentChromosomes.clear();
        this.currentChromosomes.addAll(
                Arrays.stream(chromosomes)
                        .filter(c -> !c.getFitness().equals(fitnessFunction.getWorstFitness()))
                        .collect(Collectors.toList())
        );
        this.calculateCumulativeSums(fitnessFunction);
    }

    /**
     * Bisect algorithm for a random value between 0 and the biggest value in the commutative sum list
     * @param value The value of which the index shall be searched
     * @return The index to which the value belongs
     */
    private int bisect(double value) {
        int left = 0;
        int right = this.cumulativeSums.size() - 1;
        int index = 0;
        while (left <= right) {
            index = (int) Math.floor((left + right) / 2.);

            if (index == 0)
                break;

            double currentValue = this.cumulativeSums.get(index).doubleValue();
            double prevValue = this.cumulativeSums.get(index - 1).doubleValue();

            if (value > prevValue && currentValue >= value)
                break;

            if (value <= prevValue)
                right = index - 1;
            else
                left = index + 1;
        }
        return index;
    }

    /**
     * @return The name of the selection algorithm
     */
    @Override
    public String toString() {
        return "Roulette Wheel Selection";
    }
}
