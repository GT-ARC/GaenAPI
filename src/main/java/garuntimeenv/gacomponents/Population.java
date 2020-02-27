package garuntimeenv.gacomponents;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing the population
 */
public class Population {

    // The chromosomes of the population
    private Chromosome[] chromosomes;

    /**
     * Constructor for the population which initializes an empty array
     *
     * @param populationSize The size of the population
     */
    public Population(int populationSize) {
        chromosomes = new Chromosome[populationSize];
    }

    /**
     * Constructor that takes the given chromosomes
     *
     * @param chromosomes The chromosomes of the population
     */
    public Population(Chromosome[] chromosomes) {
        this.chromosomes = chromosomes;
    }

    /**
     * Get the chromosomes a non mutatable list
     * @return List of chromosomes
     */
    public List<Chromosome> getChromosomesAsList() {
        return Arrays.asList(chromosomes);
    }

    /**
     * Getter for the chromsomes of the population
     *
     * @return The chromsomes
     */
    public Chromosome[] getChromosomes() {
        return chromosomes;
    }

    /**
     * Setter for the chromosomes of the population
     *
     * @param chromosomes The new chromosomes of the population
     */
    public void setChromosomes(Chromosome[] chromosomes) {
        this.chromosomes = chromosomes;
    }

    /**
     * Get the population size
     *
     * @return The size of the population
     */
    public int getSize() {
        return chromosomes.length;
    }

}
