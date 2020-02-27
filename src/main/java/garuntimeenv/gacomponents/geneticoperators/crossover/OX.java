package garuntimeenv.gacomponents.geneticoperators.crossover;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.CrossoverOperators;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.Pair;
import garuntimeenv.utils.Utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Class representing the ordered crossover operator
 */
public class OX implements CrossoverOperators {

    final static MyLogger logger = MyLogger.getLogger(OX.class);
    private Random rand = new Random(TestManager.getSeed());

    /**
     * {@inheritDoc}
     */
    @Override
    public Chromosome createOffspring(Chromosome parent1, Chromosome parent2) {
        int subgenomeAmount = parent1.getGenome().length;
        Genome[] offSpringGenome = new Genome[subgenomeAmount];

        for (int i = 0; i < subgenomeAmount; i++) {
            // Take a random parent as the origin
            Pair<Genome, Genome> selectedChromosome = Utils.getRandomObject(parent1.getGenome()[i], parent2.getGenome()[i]);
            Genome parentGenome1 = selectedChromosome.getKey();
            Genome parentGenome2 = selectedChromosome.getValue();

            int length = parentGenome1.getGenomeSize();

            // Select two random cut points and determine the smaller value
            int firstPos = this.rand.nextInt(length);
            int secondPos = this.rand.nextInt(length);

            int smallerValue = Math.min(firstPos, secondPos);
            int biggerValue = Math.max(firstPos, secondPos);

            Set<String> notCopy = new HashSet<>();
            String[] subOffspring = new String[length];

            // Copy the selected part between the points from the first parent
            for (int c = smallerValue; c <= biggerValue; c++) {
                String element = parentGenome1.getElement(c);
                notCopy.add(element);
                subOffspring[c] = element;
            }

            // Copy the left part
            for (int c = 0, index = smallerValue == 0 ? biggerValue + 1 : 0; c < length; c++) {
                String element = parentGenome2.getElement(c);
                if (!notCopy.contains(element)) {
                    subOffspring[index] = element;
                    if (index + 1 >= smallerValue && index <= biggerValue)
                        index = biggerValue + 1;
                    else
                        index++;
                }
            }

            offSpringGenome[i] = new Genome(subOffspring);
        }
        return new Chromosome(offSpringGenome);
    }

}
