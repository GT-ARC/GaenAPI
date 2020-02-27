package garuntimeenv.gacomponents.geneticoperators.mutations;

import garuntimeenv.SetupHelper;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.geneticoperators.crossover.CrossoverHelper;
import garuntimeenv.utils.MyLogger;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.fail;

public class InsertionIMutationTest {

    @Test
    public void bulkInsertionTest() {
        for (int i = 0; i < 100; i++) {
            this.insertMutationTest();
        }
    }

    @Test
    public void insertMutationTest() {
        MyLogger.setLevels("mutation");
        Chromosome testChromosome = SetupHelper.getJSSPChromosome(1);

        Set<String> elements = CrossoverHelper.getElementsFromChromosome(testChromosome);

        InsertionMutation insertionMutation = new InsertionMutation();
        Chromosome mutatedChromosome = insertionMutation.applyMutation(testChromosome);

        if (!CrossoverHelper.checkSetForElements(elements, mutatedChromosome)) {
            fail("Not all elements are in chromosome");
        }
    }
}
