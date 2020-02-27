package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.IMutation;
import garuntimeenv.utils.MyLogger;

import java.util.Random;

/**
 * Class representing the insertion mutation
 */
public class InsertionMutation extends Mutation implements IMutation {

    final static MyLogger logger = MyLogger.getLogger(InsertionMutation.class);
    private Random rand = new Random(TestManager.getSeed());

    public InsertionMutation() {
        super.setMutation(this);
    }

    /**
     * Select a random element and insert it at different position
     * {@inheritDoc}
     */
    @Override
    public String[] applySubMutation(String[] subChromosome) {

        int elementPos = rand.nextInt(subChromosome.length);
        int moveTo = rand.nextInt(subChromosome.length);

        String element = subChromosome[elementPos];

        if (elementPos != moveTo) {
            // Shift each element to the position of
            if (elementPos < moveTo) {
                for (int c = elementPos; c < moveTo; c++) {
                    if (c + 1 < subChromosome.length) {
                        subChromosome[c] = subChromosome[c + 1];
                    }
                }
            } else {
                for (int c = elementPos; c > moveTo; c--) {
                    if (c - 1 >= 0) {
                        subChromosome[c] = subChromosome[c - 1];
                    }
                }
            }
            subChromosome[moveTo] = element;
        }
        return subChromosome;
    }
}
