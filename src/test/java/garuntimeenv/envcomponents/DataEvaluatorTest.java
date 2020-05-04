package garuntimeenv.envcomponents;

import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.envcomponents.datalog.DataSeries;
import garuntimeenv.envcomponents.datalog.DataSet;
import garuntimeenv.gacomponents.jobshop.MakespanFitnessFunction;
import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.Property;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class DataEvaluatorTest {

    DataEvaluator dataEvaluator = new DataEvaluator();
    static HashMap<Property, DataSeries> dataSeries = new HashMap<>();
    IFitnessFunction fitnessFunction = new MakespanFitnessFunction();

    @Before
    public void setUp() {
        EnvConfig.getInstance().setVisualEnabled(false);
    }

    @Test
    public void testBreakThrough() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataSet testDataSet = new DataSet("Best Fitness");

        double avgBreakThrough = 5;

        int currentY = 100;
        int counter = 0;
        for (int i = 0; i < 50; i++) {
            for (int c = 0; c < avgBreakThrough; c++) {
                testDataSet.addDataPoint(counter++, currentY);
            }
            currentY -= Math.floor(Math.random() * 10) + 1;
        }
        DataSeries testDataSeries = new DataSeries("Test Series", null, null);
        testDataSeries.setRunTime(12345L);
        testDataSeries.addDataSet("Best Fitness", testDataSet);

        dataSeries.put(null, testDataSeries);

        Method m = dataEvaluator.getClass().getDeclaredMethod("getAvgBreakthroughRate", HashMap.class, IFitnessFunction.class);
        m.setAccessible(true);
        LinkedHashMap<Property, Double> breakThroughValues = (LinkedHashMap<Property, Double>) m.invoke(dataEvaluator, dataSeries, fitnessFunction);
        System.out.println(Arrays.toString(breakThroughValues.values().toArray()));

        assertEquals(avgBreakThrough, breakThroughValues.get(null), 0.0);
    }

    @Test
    public void testDataSetAvgAvg() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataSet testDataSet = new DataSet("Hall Of Fame");

        testDataSet.addDataPoint(5, 5);
        testDataSet.addDataPoint(6, 6);
        testDataSet.addDataPoint(7, 7);
        testDataSet.addDataPoint(8, 8);
        testDataSet.addDataPoint(9, 9);

        DataSeries testDataSeries = new DataSeries("Test Series", null, null);
        testDataSeries.addDataSet("Hall Of Fame", testDataSet);
        dataSeries.put(null, testDataSeries);

        Method m = dataEvaluator.getClass().getDeclaredMethod("getAverageOfDataSet", HashMap.class, String.class, double.class, boolean.class);
        m.setAccessible(true);
        LinkedHashMap<Property, Double> hallOfFameAvgValues = (LinkedHashMap<Property, Double>) m.invoke(dataEvaluator, dataSeries, "Hall Of Fame", 1, false);
        System.out.println(Arrays.toString(hallOfFameAvgValues.values().toArray()));

        assertEquals(7, hallOfFameAvgValues.get(null), 0.0);
    }

    @Test
    public void testDataSetAvgLast() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataSet testDataSet = new DataSet("Hall Of Fame");

        testDataSet.addDataPoint(0, 0);
        testDataSet.addDataPoint(1, 1);
        testDataSet.addDataPoint(2, 2);
        testDataSet.addDataPoint(3, 3);
        testDataSet.addDataPoint(4, 4);
        testDataSet.addDataPoint(5, 5);
        testDataSet.addDataPoint(6, 6);
        testDataSet.addDataPoint(7, 7);
        testDataSet.addDataPoint(8, 8);
        testDataSet.addDataPoint(9, 9);

        DataSeries testDataSeries = new DataSeries("Test Series", null, null);
        testDataSeries.addDataSet("Hall Of Fame", testDataSet);
        dataSeries.put(null, testDataSeries);

        Method m = dataEvaluator.getClass().getDeclaredMethod("getAverageOfDataSet", HashMap.class, String.class, double.class, boolean.class);
        m.setAccessible(true);
        LinkedHashMap<Property, Double> hallOfFameAvgValues = (LinkedHashMap<Property, Double>) m.invoke(dataEvaluator, dataSeries, "Hall Of Fame", 0, false);
        System.out.println(Arrays.toString(hallOfFameAvgValues.values().toArray()));

        assertEquals(9, hallOfFameAvgValues.get(null), 0.0);
    }

}
