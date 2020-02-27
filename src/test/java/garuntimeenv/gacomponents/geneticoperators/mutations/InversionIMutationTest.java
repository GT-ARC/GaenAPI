package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.SetupHelper;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.geneticoperators.crossover.CrossoverHelper;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.fail;

public class InversionIMutationTest {

    @Test
    public void inverseMutationTest() {
        Chromosome testChromosome = SetupHelper.getJSSPChromosome(1);

        Set<String> elements = CrossoverHelper.getElementsFromChromosome(testChromosome);

        InversionMutation inversionMutation = new InversionMutation();
        Chromosome mutatedChromosome = inversionMutation.applyMutation(testChromosome);

        if (!CrossoverHelper.checkSetForElements(elements, mutatedChromosome)) {
            fail("Not all elements are in chromosome");
        }
    }
}
