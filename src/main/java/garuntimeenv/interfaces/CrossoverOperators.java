package garuntimeenv.interfaces;

import garuntimeenv.gacomponents.Chromosome;

/**
 * Interface for all crossover operators
 */
public interface CrossoverOperators extends Property {

    /**
     * Take to chromosomes as parent Chromosome and according to the
     * crossover strategy create an offspring
     *
     * @param parent1 The first parent chromosome
     * @param parent2 The second parent chromosome
     * @return Offspring of the two parent chromosomes
     */
    Chromosome createOffspring(Chromosome parent1, Chromosome parent2);
}
