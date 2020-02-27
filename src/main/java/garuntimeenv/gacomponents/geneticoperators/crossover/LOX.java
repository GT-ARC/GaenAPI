package garuntimeenv.gacomponents.geneticoperators.crossover;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.CrossoverOperators;
import garuntimeenv.utils.MyLogger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class representing the linear order crossover operator
 * The way it is implemented is from the paper: A GENETIC ALGORITHM FOR THE JOB SHOP PROBLEM
 */
public class LOX implements CrossoverOperators {

    final static MyLogger logger = MyLogger.getLogger(PMX.class);
    private Random rand = new Random(TestManager.getSeed());

    /**
     * {@inheritDoc}
     */
    @Override
    public Chromosome createOffspring(Chromosome parent1, Chromosome parent2) {
        int subgenomeAmount = parent1.getGenome().length;
        Genome[] offSpringGenome = new Genome[subgenomeAmount];

        for (int i = 0; i < subgenomeAmount; i++) {
            Genome parentGenome1 = parent1.getGenome()[i];
            Genome parentGenome2 = parent2.getGenome()[i];

            int length = parentGenome1.getGenomeSize();

            // Select two random cut points and determine the smaller value
            int firstPos = this.rand.nextInt(length + 1);
            int secondPos = this.rand.nextInt(length + 1);

            int smallerValue = Math.min(firstPos, secondPos);
            int biggerValue = Math.max(firstPos, secondPos);

            // copy the offspring into left and right part of the middle of the cut and remove
            // the 'holes' in the respective half so to maintain the order
            // Copy the "removed" part

            LinkedHashMap<Integer, String> helperCopy = new LinkedHashMap<>();
            double middleOfCut = (firstPos + secondPos) / 2.;

            Map<String, Integer> parentGenomeMap2 = new HashMap<>();
            for (int c = 0; c < length; c++) {
                helperCopy.put(c, parentGenome2.getElement(c));
                parentGenomeMap2.put(parentGenome2.getElement(c), c);
            }

            // Create holes
            for (int c = smallerValue; c < biggerValue; c++) {
                int posInP2 = parentGenomeMap2.get(parentGenome1.getElement(c));
                helperCopy.remove(posInP2);
            }

            String[] subOffspring = new String[length];

            int leftCounter = 0;
            int rightCounter = helperCopy.size() - 1;
            int leftCaret = 0;
            int rightCaret = length - 1;

            // Copy into the offspring
            for (int c = 0; c < length; c++) {
                // On the left side
                if (c < smallerValue) {
                    String element = (String) helperCopy.values().toArray()[leftCounter++];
                    subOffspring[leftCaret++] = element;
                } else if (c >= biggerValue) {
                    String element = (String) helperCopy.values().toArray()[rightCounter--];
                    subOffspring[rightCaret--] = element;
                } else {
                    subOffspring[c] = parentGenome1.getElement(c);
                }

            }

            //System.out.println("offspring: " + Arrays.toString(subOffspring) + "\n");

            offSpringGenome[i] = new Genome(subOffspring);
        }
        return new Chromosome(offSpringGenome);
    }
}
