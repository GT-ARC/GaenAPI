package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.IMutation;
import garuntimeenv.utils.MyLogger;

import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class InversionMutation extends Mutation implements IMutation {

    final static MyLogger logger = MyLogger.getLogger(InversionMutation.class);
    private Random rand = new Random(TestManager.getSeed());

    public InversionMutation() {
        super.setMutation(this);
    }

    /**
     * Select a substring at random and invert the substring
     * {@inheritDoc}
     */
    @Override
    public String[] applySubMutation(String[] subChromosome) {

        // Select two random cut points and determine the smaller value
        int length = subChromosome.length;
        int firstPos = this.rand.nextInt(length);
        int secondPos = this.rand.nextInt(length);

        int smallerValue = min(firstPos, secondPos);
        int biggerValue = max(firstPos, secondPos);

        for (int left = smallerValue, right = biggerValue; left < right; left++, right--) {
            // Switch values
            String temp = subChromosome[left];
            subChromosome[left] = subChromosome[right];
            subChromosome[right] = temp;
        }

        return subChromosome;
    }

}
