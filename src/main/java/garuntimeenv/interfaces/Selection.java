package garuntimeenv.interfaces;

import garuntimeenv.gacomponents.Chromosome;

/**
 * Interface defining the functionality's of each selection algorithm
 */
public interface Selection extends Property {

    /**
     * Get the next chromosome according to the implemented selection algorithm
     * @return The next chromsome
     */
    Chromosome getNextChromosome();

    /**
     * Add a new population to the selection object from which shall be selected
     *
     * @param chromosomes The chromosomes as array
     * @param fitnessFunction The used fitness function
     */
    void addNewPopulation(Chromosome[] chromosomes, IFitnessFunction fitnessFunction);

}
