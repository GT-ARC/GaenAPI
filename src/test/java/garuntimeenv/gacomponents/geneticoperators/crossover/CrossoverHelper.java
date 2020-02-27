package garuntimeenv.gacomponents.geneticoperators.crossover;

import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.CrossoverOperators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CrossoverHelper {

    public static Set<String> getElementsFromChromosome(Chromosome chromosome) {
        Set<String> elements = new HashSet<>();
        for (Genome g : chromosome.getGenome())
            elements.addAll(Arrays.asList(g.getString()));
        return elements;
    }

    public static boolean checkSetForElements(Set<String> checkSet, Chromosome chromosome) {
        for (Genome g : chromosome.getGenome()) {
            if (!Arrays.asList(g.getString()).containsAll(checkSet)) {
                return false;
            }
        }
        return true;
    }

    public static boolean testCrossover(CrossoverOperators operator, Chromosome p1, Chromosome p2) {
        Set<String> elements = getElementsFromChromosome(p1);

        Chromosome offspring = operator.createOffspring(p1, p2);

        System.out.println(offspring);
        return checkSetForElements(elements, offspring);
    }
}
