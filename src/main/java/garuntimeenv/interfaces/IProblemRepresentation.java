package garuntimeenv.interfaces;

import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.runtimeexceptions.CannotRepairException;

/**
 * Interface for the problem representation classes
 * Is also an interchangeable property
 */
public interface IProblemRepresentation extends Property {

    /**
     * Creates a random chromosome for the {@code problem}
     * @param problem The problem for which a chromosome shall be generated
     * @return The random chromosome
     */
    <T extends IProblem> Chromosome createRandomRep(T problem);

    /**
     * Decodes the chromosome {@code rep} into a solution of the given {@code problem}.
     *
     * @param rep The chromosome to be decoded
     * @param problem The problem holding helper structures and constraints for the decoding
     * @return The decoded chromosome
     * @throws Exception If the decoding process crashes something is wrong with the chromosome
     */
    ISolution createSolutionFromChromosome(Chromosome rep, IProblem problem) throws Exception;

    /**
     * Repairs a broken chromosome to be normally repaired
     *
     * @param chromosome The broken chromosome
     * @throws CannotRepairException If the chromosome is to broken and can't be repaired
     */
    void repairChromosome(Chromosome chromosome) throws CannotRepairException;

}
