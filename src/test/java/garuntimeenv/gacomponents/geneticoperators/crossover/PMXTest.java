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

public class PMXTest {

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    final static MyLogger logger = MyLogger.getLogger(PMXTest.class);

    @Test
    public void PMXTest() {
        Genome[] g1 = new Genome[1];
        g1[0] = new Genome(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        Genome[] g2 = new Genome[1];
        g2[0] = new Genome(new String[]{"9", "3", "7", "8", "2", "6", "5", "1", "4"});
        Chromosome p1 = new Chromosome(g1);
        Chromosome p2 = new Chromosome(g2);

        if (!CrossoverHelper.testCrossover(new PMX(), p1, p2))
            fail();
    }

    /**
     *
     */
    @Test
    public void PMXTest2() {
        Genome[] g1 = new Genome[1];
        g1[0] = new Genome(new String[]{"11", "3", "9", "7", "12", "0", "8", "4", "6", "5", "13", "1", "14", "2", "10"});
        Genome[] g2 = new Genome[1];
        g2[0] = new Genome(new String[]{"11", "12", "3", "0", "9", "4", "7", "2", "5", "1", "8", "13", "14", "6", "10"});
        Chromosome p1 = new Chromosome(g1);
        Chromosome p2 = new Chromosome(g2);

        if (!CrossoverHelper.testCrossover(new PMX(), p1, p2))
            fail();
    }

    /**
     * Hard test http://www.rubicite.com/Tutorials/GeneticAlgorithms/CrossoverOperators/PMXCrossoverOperator.aspx
     * With values 3 and 7 for the switching elements
     */
    @Test
    public void PMXTest3() {
        Genome[] g1 = new Genome[1];
        g1[0] = new Genome(new String[]{"8", "4", "7", "3", "6", "2", "5", "1", "9", "0"});
        Genome[] g2 = new Genome[1];
        g2[0] = new Genome(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
        Chromosome p1 = new Chromosome(g1);
        Chromosome p2 = new Chromosome(g2);

        if (!CrossoverHelper.testCrossover(new PMX(), p1, p2))
            fail();
    }

    /**
     * Due to the randomeness of the pmx test multiple times to make sure there is no error
     * in the code
     *
     * @throws FileNotFoundException
     * @throws MalformedJsonException
     */
    @Test
    public void bulkPMXTest() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException, DataTypeNotSupportedException {
        for (int i = 0; i < 100; i++) {
            this.PMXTestRandom();
        }
    }

    /**
     * Test the PMX Crossover with a random rep
     *
     * @throws FileNotFoundException
     * @throws MalformedJsonException
     */
    @Test
    public void PMXTestRandom() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException, DataTypeNotSupportedException {
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

        if (!CrossoverHelper.testCrossover(new PMX(), parent1, parent2))
            fail();

    }


}
