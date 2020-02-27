package garuntimeenv.gacomponents;


import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.ISolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing the chromosome of the genetic algorithm
 */
public class Chromosome {

    private Genome[] genome;                    // The subchromosomes as a genome array
    private Number fitness = Double.NaN;        // The calculated fitness value
    private ISolution correspondingSolution;    // The corresponding solution

    /**
     * Constructor that sets the genomes of the chromosomes
     *
     * @param genome The genomes of the chromosome
     */
    public Chromosome(Genome[] genome) {
        this.genome = genome;
    }

    /**
     * Constructor that initializes an empty chromosome
     *
     * @param genomeAmount The number of machines
     * @param genomeLength The number of jobs
     */
    public Chromosome(int genomeAmount, int genomeLength) {
        genome = new Genome[genomeAmount];
        for (int i = 0; i < genomeAmount; i++)
            genome[i] = new Genome(genomeLength);
    }

    /**
     * Return the fitness of the chromosome
     *
     * @return The fitness value as Number
     */
    public Number getFitness() {
        return fitness;
    }

    /**
     * Setter for the fitness value
     *
     * @param fitness The new fitness value
     */
    public void setFitness(Number fitness) {
        this.fitness = fitness;
    }

    /**
     * Calculate the fitness of the chromosome
     * It is required that the corresponding solution is set
     *
     * @param fitnessFunction The fitness function which evaluates the corresponding solution
     * @return The fitness value
     */
    public Number calculateFitness(IFitnessFunction fitnessFunction) {
        this.fitness = fitnessFunction.calculateFitness(correspondingSolution);
        return this.fitness;
    }

    /**
     * Getter for the genomes / subchromosomes
     *
     * @return The genomes / subchromosomes
     */
    public Genome[] getGenome() {
        return genome;
    }

    /**
     * Return the genomes as list
     *
     * @return A array list containg the subchromosomes
     */
    public List<String>[] getGenomeAsListArray() {
        ArrayList<String>[] retArray = new ArrayList[this.genome.length];
        for (int i = 0; i < retArray.length; i++)
            retArray[i] = (ArrayList<String>) genome[i].getMutableListOfGenome();
        return retArray;
    }

    /**
     * Getter for the corresponding solution
     *
     * @return The corresponding solution
     */
    public ISolution getCorrespondingSolution() {
        return correspondingSolution;
    }

    /**
     * Set the corresponding solution of the chromosome
     * @param correspondingSolution The solution
     */
    public void setCorrespondingSolution(ISolution correspondingSolution) {
        this.correspondingSolution = correspondingSolution;
    }

    /**
     * Convert the chromosome into a string
     *
     * @return A string representing the chromosome
     */
    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder();
        for (Genome genome : this.genome)
            retString.append(genome.toString()).append('\n');
        return retString.toString();
    }

    /**
     * Calculates the same hashcode for the same chromosome
     * If the solution is set the solution will be used for the hash
     *
     * @return The hash for this chromosome
     */
    @Override
    public int hashCode() {
        if (this.correspondingSolution == null)
            return Arrays.hashCode(this.genome);
        else
            return this.correspondingSolution.hashCode();
    }

    /**
     * Compares two chromosomes on equals. If the solution is set compare the solution
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chromosome that = (Chromosome) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(genome, that.genome)) return false;
        if (!Objects.equals(fitness, that.fitness)) return false;
        return Objects.equals(correspondingSolution, that.correspondingSolution);
    }
}
