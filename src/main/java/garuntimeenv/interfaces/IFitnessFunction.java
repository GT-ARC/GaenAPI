package garuntimeenv.interfaces;

import garuntimeenv.gacomponents.Chromosome;

import java.util.Comparator;

/**
 * Interface representing defining which functions shall be implemented by
 * the fitness function
 */
public interface IFitnessFunction<T extends Number> extends Comparator<Chromosome>, Property {

    /**
     * Calculate the fitness of the given Solution in the given solution of problem
     *
     * @param solution The solution of which the fitness shall be calculated
     * @return The fitness value in type {@code T}
     */
    <S extends ISolution> T calculateFitness(S solution);

    /**
     * Compare two fitness values and return true if the first value is better
     * @param firstFitness The first fitness value
     * @param secondFitness The second fitness value
     * @return True if the first is better then the second
     */
    boolean isBetterSolution(T firstFitness, T secondFitness);

    /**
     * Compare two fitness values in Double Type
     * This is for example used when comparing values from the averaged data series
     *
     * @param firstFitness The first fitness value
     * @param secondFitness The second fitness value
     * @return True if the first is better then the second
     */
    boolean isBetterSolution(Double firstFitness, Double secondFitness);

    /**
     * Return the worst possible fitness
     * @return The worst possible fitness
     */
    T getWorstFitness();
}
