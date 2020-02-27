package garuntimeenv.envcomponents.datalog;

import garuntimeenv.gacomponents.Config;
import garuntimeenv.interfaces.Property;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.chartpart.Chart;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data series containing the data sets mapped to data name strings
 */
public class DataSeries {

    private String name;
    private Property testedProperty;
    private Config usedConfig;
    private HashMap<String, DataSet> dataSetMap;

    private long runTime = -1;

    // Visual stuff
    private JPanel chart;
    private XYChart xChart;

    /**
     * Constructor for the data series
     *
     * @param name           The name of the data series
     * @param usedConfig     the configuration used for the run for which this series is created
     * @param testedProperty The property that was tested
     */
    public DataSeries(String name, Config usedConfig, Property testedProperty) {
        this.testedProperty = testedProperty;
        this.usedConfig = usedConfig;
        this.name = name;
        dataSetMap = new HashMap<>();
    }

    /**
     * Get all data sets filtered by the parameters
     *
     * @param mean   If mean is true the returning collection will contain the mean data sets also
     * @param visual If visual is true the collection will also contain the visual data sets
     *               There are non visual data sets for example the consistency data
     * @return The collection filtered through the parameters
     */
    public Collection<DataSet> getDataSet(boolean mean, boolean visual) {
        if (mean) return dataSetMap.values();
        return dataSetMap.values().stream()
                .filter(s -> visual == s.isVisual())
                .filter(s -> !s.getName().contains("-mean")).collect(Collectors.toList());
    }

    /**
     * Get the names of all stored data set
     *
     * @param mean If mean is true the returning collection will contain the mean data sets also
     * @return A Set containing the data set names filtered through the parameters
     */
    public Set<String> getDataSetNames(boolean mean) {
        if (mean) return dataSetMap.keySet();
        return dataSetMap.keySet().stream()
                .filter(s -> !s.contains("-mean")).collect(Collectors.toSet());
    }

    /**
     * To add a new data set to the collection
     *
     * @param name    The name of the data set
     * @param dataSet The data set itself
     */
    public void addDataSet(String name, DataSet dataSet) {
        this.dataSetMap.put(name, dataSet);
    }

    /**
     * Chechs if the data series has a {@code dataSetName} set
     *
     * @param dataSetName The name of the searched data set
     * @return True if the data set is inside
     */
    public boolean containsDataSet(String dataSetName) {
        return this.dataSetMap.containsKey(dataSetName);
    }

    /**
     * Getter for the stored data sets
     *
     * @param dataName The requested data set name
     * @return The data set if present otherwise null
     */
    public DataSet get(String dataName) {
        return dataSetMap.get(dataName);
    }

    /**
     * Getter for the chart jpanel
     *
     * @return The belonging chart
     */
    public JPanel getChart() {
        return chart;
    }

    /**
     * Setter for the chart jpanel
     *
     * @param chart To be set chart
     */
    public void setChart(JPanel chart) {
        this.chart = chart;
    }

    /**
     * Getter for the XChart
     *
     * @return The belonging xchart
     */
    public XYChart getXChart() {
        return xChart;
    }

    /**
     * Setter for the xchart
     *
     * @param xChart To be set xchart instance
     */
    public void setXChart(Chart xChart) {
        this.xChart = (XYChart) xChart;
    }

    /**
     * Getter for the data series name
     *
     * @return The name of the data series
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the used configuration
     *
     * @return Return the used configuration for this run
     */
    public Config getUsedConfig() {
        return this.usedConfig;
    }

    /**
     * Returns the saved execution time or run time
     *
     * @return The runtime in long
     */
    public long getRunTime() {
        return runTime;
    }

    /**
     * Set the run time or execution time
     *
     * @param runTime The time in long
     */
    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    /**
     * Return the tested property
     *
     * @return The tested property
     */
    public Property getTestedProperty() {
        return testedProperty;
    }

}
