package garuntimeenv.interfaces;

/**
 * Interface defining the functionality of the mutation operators
 */
public interface IMutation extends Property {

    /**
     * Take the sub-chromosomes from the chromosome as String array and apply
     * the mutation operator on each sub-chromosome.
     * The Mutation operator should mutate in place.
     *
     * @param chromosome The sub chromosomes as string array
     * @return The mutated chromosomes
     */
    String[] applySubMutation(String[] chromosome);

}
