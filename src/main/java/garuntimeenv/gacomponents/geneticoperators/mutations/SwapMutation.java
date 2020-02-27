package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.IMutation;

import java.util.Random;

/**
 * Class representing the swap mutation that exchanges to elements inside a sub genome
 */
public class SwapMutation extends Mutation implements IMutation {

    private static Random rand = new Random(TestManager.getSeed());

    /**
     * Constructor for the swap mutation operator
     */
    public SwapMutation() {
        super.setMutation(this);
    }

    /**
     * Exchange to elements of the sub genome
     * {@inheritDoc}
     */
    @Override
    public String[] applySubMutation(String[] subChromosome) {

        int length = subChromosome.length;
        int firstPos = rand.nextInt(length);
        int secondPos = rand.nextInt(length);

        String firstPosString = subChromosome[firstPos];
        String secondPosString = subChromosome[secondPos];

        subChromosome[firstPos] = secondPosString;
        subChromosome[secondPos] = firstPosString;

        return subChromosome;
    }

}
