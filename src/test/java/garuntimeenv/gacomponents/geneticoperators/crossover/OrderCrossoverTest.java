package garuntimeenv.gacomponents.geneticoperators.crossover;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.gacomponents.jobshop.JobShopPreferenceListRep;
import garuntimeenv.gacomponents.jobshop.JobShopProblem;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.ProblemLoader;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.fail;

public class OrderCrossoverTest {

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    final static MyLogger logger = MyLogger.getLogger(OrderCrossoverTest.class);

    /**
     * Test the Order Crossover Operator with a specific genome
     */
    @Test
    public void OXTest() {
        Genome[] g1 = new Genome[1];
        g1[0] = new Genome(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        Genome[] g2 = new Genome[1];
        g2[0] = new Genome(new String[]{"7", "6", "9", "4", "3", "2", "1", "5", "8"});
        Chromosome p1 = new Chromosome(g1);
        Chromosome p2 = new Chromosome(g2);

        if (!CrossoverHelper.testCrossover(new OX(), p1, p2))
            fail();
    }

    /**
     * Test the LOK Crossover with a random rep
     *
     * @throws FileNotFoundException
     * @throws MalformedJsonException
     */
    @Test
    public void OXTestRandom() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException, DataTypeNotSupportedException {
        JobShopProblem jobShopProblem =
                (JobShopProblem) problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling);

        Chromosome parent1 = null;
        Chromosome parent2 = null;
        try {
            parent1 = new JobShopPreferenceListRep().createRandomRep((IProblem) jobShopProblem.clone());
            parent2 = new JobShopPreferenceListRep().createRandomRep((IProblem) jobShopProblem.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (!CrossoverHelper.testCrossover(new OX(), parent1, parent2))
            fail();

    }

}
