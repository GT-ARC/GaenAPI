package garuntimeenv.gacomponents.geneticoperators.mutations;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.SetupHelper;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.geneticoperators.crossover.CrossoverHelper;
import garuntimeenv.gacomponents.geneticoperators.crossover.PMXTest;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.ProblemLoader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Set;

import static org.junit.Assert.fail;

public class SwapIMutationTest {

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    final static MyLogger logger = MyLogger.getLogger(PMXTest.class);


    @Test
    public void bulkSWAPTest() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException {
        for (int i = 0; i < 1000; i++) {
            this.SWAPTestRandom();
        }
    }

    //Random Test
    @Test
    public void SWAPTestRandom() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException {
        Chromosome testChromosome = SetupHelper.getJSSPChromosome(1);

        Set<String> elements = CrossoverHelper.getElementsFromChromosome(testChromosome);

        SwapMutation swapMutation = new SwapMutation();
        Chromosome mutatedChromosome = swapMutation.applyMutation(testChromosome);

        if (!CrossoverHelper.checkSetForElements(elements, mutatedChromosome)) {
            fail("Not all elements are in chromosome");
        }
    }

}
