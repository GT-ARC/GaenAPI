package garuntimeenv.gacomponents;

import garuntimeenv.SetupHelper;
import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.gacomponents.geneticoperators.crossover.PMXTest;
import garuntimeenv.gacomponents.jobshop.JobShopProblem;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.ProblemLoader;
import org.junit.Test;

public class GAManagerTest {

    private Population testPopulation = null;
    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    final static MyLogger logger = MyLogger.getLogger(PMXTest.class);
    private JobShopProblem jobShopProblem;
    private DataManager dataManager;

    private GAManager GAManager;

    public void randSetUp(int element) throws Exception {
        EnvConfig.getInstance().setVisualEnabled(false);
        if (element == -1) {
            jobShopProblem =
                    (JobShopProblem) problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling);
        } else {
            jobShopProblem =
                    (JobShopProblem) problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling, element);
        }

        TestManager.getInstance().setGenerationLimit(100);
        dataManager = DataManager.getInstance();
        Config config = SetupHelper.getConfig();
        dataManager.createNewDataSeries("GA Manager Test Series", config, new Property() {
        });
        GAManager = new GAManager(jobShopProblem, config);
    }

//    @Test
    public void testSpecificInstance() throws Exception {
        this.randSetUp(0);
        GAManager.startEnvironment();
    }
}

