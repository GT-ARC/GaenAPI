package garuntimeenv;

import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.utils.MyLogger;
import org.junit.Before;
import org.junit.Test;

public class TestConfigTest {

    private TestManager testManager = TestManager.getInstance();

    @Before
    public void setUp() {
        EnvConfig.getInstance().setVisualEnabled(false);
        EnvConfig.getInstance().setWriteData(false);
    }

    @Test
    public void testConfigTest() {
        MyLogger.setLevels("testmanager");
        Config currentConf = testManager.getCurrentConfig();
        System.out.println("init Config: " + currentConf);
        int counter = 0;
        while ((currentConf = testManager.getNextConfig()) != null) {
            System.out.println("Config " + counter++ + ": " + currentConf);
        }
    }

    @Test
    public void getNextConfig() {
        Config config = testManager.getNextConfig();
        System.out.println(config);
    }
}
