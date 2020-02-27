package garuntimeenv.gacomponents.geneticoperators.crossover;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.CrossoverOperators;
import garuntimeenv.utils.MyLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The class implementing the partial-mapped crossover operator
 * Implemented by the instructions of https://www.youtube.com/watch?v=ZtaHg1C25Kk
 */
public class PMX implements CrossoverOperators {

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

            // Select two random cut points and determine the smaller value
            int length = parentGenome1.getGenomeSize();
            int firstPos = this.rand.nextInt(length);
            int secondPos = this.rand.nextInt(length);

            int smallerValue = Math.min(firstPos, secondPos);
            int biggerValue = Math.max(firstPos, secondPos);

            // Create a map of each genome to create better searching results
            Map<String, Integer> parentGenomeMap2 = new HashMap<>();
            for (int c = 0; c < length; c++) {
                parentGenomeMap2.put(parentGenome2.getElement(c), c);
            }


            String[] subOffspring = new String[length];

            // Copy the selected part of the first parent into the offspring
            Map<String, Integer> eleInCopiedPart = new HashMap<>();

            for (int c = smallerValue; c <= biggerValue; c++) {
                String currentElement = parentGenome1.getElement(c);
                eleInCopiedPart.put(currentElement, c);
                subOffspring[c] = currentElement;
            }

            for (int c = smallerValue; c <= biggerValue; c++) {
                String eleInP2 = parentGenome2.getElement(c);
                if (!eleInCopiedPart.keySet().contains(eleInP2)) {
                    int currentIndex = c;
                    boolean placed = false;
                    do {
                        String eleInP1 = parentGenome1.getElement(currentIndex);
                        int posInP2 = parentGenomeMap2.get(eleInP1);
                        if (subOffspring[posInP2] == null) {
                            // If the position isn't occupied put the element from p2 there
                            subOffspring[posInP2] = eleInP2;
                            placed = true;
                        } else {
                            // Else it's all ready occupied by a element
                            // Put it where the occupying element in p2 is
                            currentIndex = posInP2;
                        }
                    } while (!placed);
                }
            }

            // Copy the remaining genomes to the offspring
            for (int c = 0; c < length; c++) {
                if (subOffspring[c] == null)
                    subOffspring[c] = parentGenome2.getElement(c);
            }

            offSpringGenome[i] = new Genome(subOffspring);
        }

        return new Chromosome(offSpringGenome);
    }
}
