package garuntimeenv.gacomponents.jobshop;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.SetupHelper;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.ProblemLoader;
import org.junit.Test;

import java.io.FileNotFoundException;

public class JobShopPreferenceListRepTest {

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    final static MyLogger logger = MyLogger.getLogger(JobShopPreferenceListRepTest.class, "PreferenceListTest");

    @Test
    public void createSolutionFromChromosome() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException, DataTypeNotSupportedException {
        JobShopProblem problem = (JobShopProblem) problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling, 0);

        Genome[] g1 = new Genome[3];
        g1[0] = new Genome(new String[]{"1", "0", "2"});
        g1[1] = new Genome(new String[]{"1", "2", "0"});
        g1[2] = new Genome(new String[]{"0", "1", "2"});
        Chromosome p1 = new Chromosome(g1);

        JobShopPreferenceListRep prefListRep = new JobShopPreferenceListRep();
        try {
            prefListRep.createSolutionFromChromosome(p1, (IProblem) problem.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateRandomSolution() {
        JobShopProblem problem = SetupHelper.getJobShopProblem(1);
        JobShopPreferenceListRep prefListRep = new JobShopPreferenceListRep();
        logger.debug("PreferenceListTest", prefListRep.createRandomRep(problem).toString());
    }
}
