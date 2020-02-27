package garuntimeenv.envcomponents.datalog;

import garuntimeenv.envcomponents.DataEvaluator;
import garuntimeenv.envcomponents.DataVisualizer;
import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.InterfaceHelper;
import garuntimeenv.utils.MyLogger;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.XYStyler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for managing all data.
 * The data is stored in data sets which are stored in data series belonging to a run of a hyper parameter instance.
 * If the writer flag is set, the collected data will be saved persistently
 * If the visual flag is set, the data will be displayed in a ui
 */
public class DataManager {

    private final static MyLogger logger = MyLogger.getLogger(DataManager.class);

    // Objects for doing the different features of the runtime environment
    private DataVisualizer dataVisualizer; // Frontend
    private DataSeries currentDataSeries;  // The series in which the data should be stored
    private DataLogging dataLog;           // Manages all stored data
    private DataWriter dataWriter;       // Writes the data to the persistent file space

    // Singleton pattern for the data manager
    private static DataManager INSTANCE = new DataManager();

    /**
     * Getter for the singleton instance pattern
     *
     * @return
     */
    public static DataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor for the data manager that checks if writer or visualizer are enabled
     */
    private DataManager() {
        this.dataLog = DataLogging.getInstance();
        if (EnvConfig.getInstance().isWriteData())
            this.dataWriter = new DataWriter(dataLog);
        if (EnvConfig.getInstance().isVisualEnabled())
            this.dataVisualizer = new DataVisualizer(dataLog);
    }

    /**
     * Generate new evaluation series for the {@code geneticProperty} hyper parameter.
     * Data in {@code percentages} values and {@code scores} for saving later
     *
     * @param geneticProperty The hyper parameter to which the data belongs
     * @param percentages     HashMap containing for each evaluation point another HashMap
     *                        in which each property instance of the hyper parameter
     *                        got a designated value
     * @param scores          HashMap in which each property got the score value designated
     */
    public void createNewEvalSeries(
            HyperParameter geneticProperty,
            HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> percentages,
            LinkedHashMap<Property, Double> scores
    ) {
        EvalSeries newEvalSeries = new EvalSeries(geneticProperty, percentages, scores);
        // Add the new data series
        dataLog.addEvalDataSeries(newEvalSeries);

        if (EnvConfig.getInstance().isVisualEnabled()) {
            // Set the scoreChart
            newEvalSeries.setScoreChart(
                    this.dataVisualizer.createCategoryChart("Scores", newEvalSeries, null, true)
            );
            // Set the jpanel chart for the collective evaluation panel
            newEvalSeries.setCollectiveChart(
                    this.dataVisualizer.createCategoryChart("Collective", newEvalSeries, null, false)
            );
            // Go through each possible evaluation point and create a new data series from it
            Arrays.stream(DataEvaluator.evaluationPoints.values()).forEach(
                    evaluationPoint -> newEvalSeries.addCategoryChart(this.dataVisualizer.createCategoryChart(
                            evaluationPoint.name(), newEvalSeries, evaluationPoint, false), evaluationPoint)
            );
            this.dataVisualizer.createTabbedPaneFromEvalSeries(newEvalSeries);
        }
    }

    /**
     * Save all data of the currently displayed chart with a label
     *
     * @param label of the data series
     */
    public DataSeries createNewDataSeries(String label, Config config, Property currentProperty) {

        // Create new data series and add as current series
        DataSeries newDataSeries = new DataSeries(label, config, currentProperty);
        this.currentDataSeries = newDataSeries;

        // Add the new data series
        dataLog.addDataSeries(newDataSeries);

        if (EnvConfig.getInstance().isVisualEnabled()) {
            this.dataVisualizer.createChartPanelFromDataSeries(newDataSeries);
        }

        return newDataSeries;
    }

    /**
     * Adds the data in the parameter as a new data set to the current dataSeries
     *
     * @param dataName The name of the data set
     * @param xData    The x data to be added
     * @param yData
     */
    private void addToCurrDataSeries(String dataName, Number[] xData, Number[] yData) {
        DataSet newDataSet = new DataSet(dataName);
        for (int i = 0; i < xData.length; i++)
            newDataSet.addDataPoint(xData[i], yData[i]);

        this.currentDataSeries.addDataSet(dataName, newDataSet);

        if (EnvConfig.getInstance().isVisualEnabled()) {
            XYSeries xySeries = this.currentDataSeries.getXChart().addSeries(dataName, newDataSet.getXDataVisible(), newDataSet.getYDataVisible());
            XYStyler styler = this.currentDataSeries.getXChart().getStyler();
            xySeries.setXYSeriesRenderStyle(styler.getDefaultSeriesRenderStyle());
            xySeries.setLineStyle(styler.getTheme().getSeriesLines()[0]);
        }
    }

    /**
     * Crate a new Panel where the data series are averaged
     *
     * @param label    The label of the new average data series
     * @param property The property which runs should be averaged
     */
    public void averageCurrentRun(String label, Property property) {

        // Create the average data series from the data log
        DataSeries averageSeries = null;
        try {
            averageSeries = dataLog.createAverageDataSeries(label, property);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If average series is null something is wrong with the data series
        if (averageSeries == null) return;
        // TODO change the way the exception is handled eg.

        if (EnvConfig.getInstance().isVisualEnabled()) {
            // Create the panel for the average series
            this.dataVisualizer.createChartPanelFromDataSeries(averageSeries);

            List<XYChart> xyCharts = dataLog.getDataSeriesOfProperty(property).stream()
                    .map(DataSeries::getXChart).collect(Collectors.toList());
            xyCharts.add(averageSeries.getXChart());

            InterfaceHelper.setXChartToSameYAxis(xyCharts);
        }
    }

    /**
     * Add the data point to current data series
     *
     * @param dataName The name of the data set in the data series
     * @param x        The x value of the data
     * @param y        The y value of the data
     */
    public void addDataPoint(String dataName, Number x, Number y) {

        if (!this.currentDataSeries.containsDataSet(dataName)) {
            this.addToCurrDataSeries(dataName, new Number[]{x}, new Number[]{y});
            return;
        }
        DataSet dataSet = this.currentDataSeries.get(dataName);
        dataSet.addDataPoint(x, y);

        if (EnvConfig.getInstance().isVisualEnabled()) {
            currentDataSeries.getXChart().updateXYSeries(dataName, dataSet.getXDataVisible(), dataSet.getYDataVisible(), null);
            this.dataVisualizer.repaint(this.currentDataSeries);
        }
    }

    /**
     * Add the data point to the current data series.
     * When the (@code mean) value is set to true, insert the mean as extra data line
     *
     * @param dataName The name of the data set in the data series
     * @param x        The x value of the data
     * @param y        The y value of the data
     * @param mean     determines the computation of the mean
     */
    public void addDataPoint(String dataName, Number x, Number y, boolean mean) {
        this.addDataPoint(dataName, x, y);
        if (!mean) return;

        if (!this.currentDataSeries.containsDataSet(dataName + "-mean")) {
            this.addToCurrDataSeries(dataName + "-mean", new Number[]{x}, new Number[]{y});
            return;
        }

        DataSet dataSet = this.currentDataSeries.get(dataName);
        double displayValue = dataSet.getMean();

        addDataPoint(dataName + "-mean", x, displayValue);
    }

    /**
     * Get the data visualizer
     *
     * @return The data visualizer object
     */
    public DataVisualizer getDataVisualizer() {
        return dataVisualizer;
    }

    /**
     * Put all graphs onto the same y scale
     */
    public void normalizeAllGraphs() {
        if (EnvConfig.getInstance().isVisualEnabled()) {
            this.dataVisualizer.repaint(this.currentDataSeries);
            InterfaceHelper.setXChartToSameYAxis(dataLog.getAllDataSeries()
                    .stream().map(DataSeries::getXChart).collect(Collectors.toList())
            );
        }
    }

    /**
     * Save data regarding the {@code hyperProp} persistently
     *
     * @param hyperProp The hyper parameter which data should be saved
     */
    public void writeData(HyperParameter hyperProp) {
        if (EnvConfig.getInstance().isWriteData())
            this.dataWriter.writeData(hyperProp);
    }
}
