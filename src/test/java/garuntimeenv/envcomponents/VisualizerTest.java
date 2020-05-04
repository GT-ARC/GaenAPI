package garuntimeenv.envcomponents;

import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.interfaces.Property;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class VisualizerTest {

    private static DataManager dataManager;

    @Before
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        EnvConfig.getInstance().setVisualEnabled(true);
        Constructor<DataManager> constructor = DataManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        dataManager = constructor.newInstance();
    }

    // TODO remove the sleep timers

    @Ignore
    @Test
    public void createRandomVisual() throws InterruptedException {
        for (int c = 0; c < 4; c++) {
            int finalC = c;
            Property testProperty = new Property() {
                @Override
                public String toString() {
                    return "Test Property: " + finalC;
                }
            };
            dataManager.createNewDataSeries("Series " + c, null, testProperty);
            for (int i = 0; i < 20; i++) {
                dataManager.addDataPoint("TestData", i, Math.pow(i, 2) + (Math.random() * Math.pow(i, 2) / 2 - 10), true);
                dataManager.addDataPoint("Data Test", i, Math.pow(i, 2) + (Math.random() * Math.pow(i, 2) - 10));
                Thread.sleep(5);
            }
        }
    }

    @Ignore
    @Test
    public void createAveragedDataSeries() throws InterruptedException {
        for (int j = 0; j < 4; j++) {
            int finalJ = j;
            Property testProperty = new Property() {
                @Override
                public String toString() {
                    return "Test Property: " + finalJ;
                }
            };
            for (int c = 0; c < 4; c++) {
                dataManager.createNewDataSeries("Series " + c, null, testProperty);
                for (int i = 0; i < 200; i++) {
                    dataManager.addDataPoint("TestData", i, Math.pow(i, 2) + (Math.random() * Math.pow(i, 2) / 2 - 10), true);
                    dataManager.addDataPoint("Data Test", i, Math.pow(i, 2) + (Math.random() * Math.pow(i, 2) - 10));
                    Thread.sleep(2);
                }
            }
            dataManager.averageCurrentRun("Series Comp " + j, testProperty);
        }
    }
}
