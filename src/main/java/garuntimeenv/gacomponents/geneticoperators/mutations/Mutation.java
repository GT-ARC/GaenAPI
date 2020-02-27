package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.IMutation;

import java.util.Random;

/**
 * Abstract parent class for all mutation operators
 * It creates a new chromosome from the given and apply the mutation onto each sub genome
 */
public abstract class Mutation {

    private IMutation mutation = null;
    private static Random rand = new Random(TestManager.getSeed());

    /**
     * Set the mutation operator
     *
     * @param mutation The mutation operator
     */
    public void setMutation(IMutation mutation) {
        this.mutation = mutation;
    }

    /**
     * Apply the mutation operator to the given chromosome on sub chromosome bases
     *
     * @param chromosome The to be mutated chromosome
     * @return The mutated chromosome
     */
    public Chromosome applyMutation(Chromosome chromosome) {
        int subGenomeAmount = chromosome.getGenome().length;
        Genome[] mutatedSubChromosomes = new Genome[subGenomeAmount];
        for (int i = 0; i < chromosome.getGenome().length; i++) {
            Genome newGenome = new Genome(chromosome.getGenome()[i].getString().clone());
            if (rand.nextDouble() < Config.latestInstance.getSubGenomeMutationProbability())
                mutation.applySubMutation(newGenome.getString());
            mutatedSubChromosomes[i] = newGenome;
        }
        return new Chromosome(mutatedSubChromosomes);
    }
}
