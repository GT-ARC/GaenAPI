package garuntimeenv.utiltests;

import garuntimeenv.interfaces.IProblem;
import garuntimeenv.utils.ProblemLoader;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class ProblemPropertiesLoaderTest {

    private ProblemLoader problemLoader = ProblemLoader.getInstance();

    @Test
    public void loadProblemTest() {
        IProblem parsedProblem = null;

        try {
            parsedProblem = problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling);
        } catch (Exception exception) {
            exception.printStackTrace();
            fail();
        }
        System.out.println(parsedProblem.toString());
    }

    @Test
    public void loadJobShopProblemTest() {
        IProblem parsedProblem = null;

        try {
            parsedProblem = problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling);
        } catch (Exception exception) {
            exception.printStackTrace();
            fail();
        }
        System.out.println(parsedProblem.toString());
    }

}
