package garuntimeenv.envcomponents.datalog;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that holds all the collected data
 */
public class DataLogging {

    // Singleton pattern only one data logging instance needed
    // Using the lazy loading method
    private static DataLogging INSTANCE = new DataLogging();   // Data logging Instance
    private HashMap<Property, List<DataSeries>> dataLog;         // Holds the different data repetitions of each run
    private HashMap<Property, DataSeries> averagedDataSeries;    // Holds the averaged data series of the repetitions
    private HashMap<TestManager.testProperties, EvalSeries> evalDataSeries;    // The evaluation data series

    /**
     * Initializes the data logging object
     */
    private DataLogging() {
        this.dataLog = new HashMap<>();
        this.evalDataSeries = new HashMap<>();
        this.averagedDataSeries = new HashMap<>();
    }

    /**
     * Return the instance
     *
     * @return the data logging instance
     */
    public static DataLogging getInstance() {
        return INSTANCE;
    }

    /**
     * Adds the {@code dataSeries} to the current data log list indicated through the current run counter
     *
     * @param dataSeries To be added data series.
     */
    void addDataSeries(DataSeries dataSeries) {
        Property testedProperty = dataSeries.getTestedProperty();

        // If a new property is tested put a new array list in place for the different runs
        if (!dataLog.containsKey(testedProperty))
            dataLog.put(testedProperty, new ArrayList<>());

        // If the data log counter is increased create a new repetition list
        dataLog.get(testedProperty).add(dataSeries);
    }

    /**
     * Add the {@code evalSeries} to the eval data series map
     *
     * @param evalSeries The to be added eval series
     */
    void addEvalDataSeries(EvalSeries evalSeries) {
        TestManager.testProperties testedGeneticProperty = evalSeries.getTestedHyperParameter().getTestProperty();
        evalDataSeries.put(testedGeneticProperty, evalSeries);
    }

    /**
     * Averages latest <code>List<DataSeries></code> indicated through current run
     *
     * @param label           Label for the average series
     * @param currentProperty The tested property
     * @return Newly created averaged data series
     * @throws Exception If something is wrong with the previous repetitions
     */
    DataSeries createAverageDataSeries(String label, Property currentProperty) throws Exception {

        // Check if the data series is able to be averaged and get meta data of the data set
        HashMap<String, Object> datasetMetaData = sanityCheck(currentProperty);
        if (datasetMetaData == null)
            throw new Exception("Sanity Check in average Meta Data");

        // Retrieve the meta data
        Set<String> topicList = (Set<String>) datasetMetaData.get("dataNames");
        HashMap<String, Integer> longest = (HashMap<String, Integer>) datasetMetaData.get("longest");
        List<DataSeries> currentDataSeries = dataLog.get(currentProperty);

        // New data series for the averaged data sets
        DataSeries averageDataSeries = new DataSeries(label, (Config) datasetMetaData.get("config"), currentProperty);
        List<DataSet> consistencies = new ArrayList<>();

        // New data series for the averaged data sets
        topicList.forEach(s -> averageDataSeries.addDataSet(s, new DataSet(s)));

        for (String dataName : topicList) {
            // Create a dataset for the consistency of this current dataset
            DataSet consistencyDataSet = new DataSet(dataName + " consistency", false);
            averageDataSeries.addDataSet(dataName + " consistency", consistencyDataSet);
            consistencies.add(consistencyDataSet);

            // For each topic go though each data point
            for (int i = 0; i < longest.get(dataName); i++) {
                int currentIndex = i;

                // Get the current x value
                int currentX = currentDataSeries.get(0).get(dataName).getXData().get(currentIndex).intValue();

                // Get the relevant data points
                List<Double> setValues = currentDataSeries.stream()
                        .map(series -> series.get(dataName))
                        .map(set -> set.getDataPoint(currentIndex).doubleValue())
                        .collect(Collectors.toList());

                // Get the avg of the current generation
                double avgValue = setValues.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);

                // Calculate the avg value difference
                double avgDifference = Utils.calcDiffBetween(setValues);

                consistencyDataSet.addDataPoint(currentX, avgDifference);
                averageDataSeries.get(dataName).addDataPoint(currentX, avgValue);
            }
        }

        // Get the avg consistency
        int maxDataLength = longest.values().stream().flatMapToInt(IntStream::of).max().orElse(-1);
        DataSet consistencyDataSet = new DataSet("consistency", false);
        averageDataSeries.addDataSet("consistency", consistencyDataSet);
        for (int i = 0; i < maxDataLength; i++) {
            int index = i;
            double avg = consistencies.stream()
                    .mapToDouble(dataSet -> {
                                if (dataSet.getDataPoint(index) == null)
                                    return 0;
                                else
                                    return dataSet.getDataPoint(index).doubleValue();
                            }
                    ).average().orElse(Double.NaN);
            consistencyDataSet.addDataPoint(index, avg);
        }

        // Set the avg runtime of the configuration
        averageDataSeries.setRunTime(
                (long) currentDataSeries.stream().mapToLong(DataSeries::getRunTime).average().orElse(Double.NaN)
        );

        this.averagedDataSeries.put(currentProperty, averageDataSeries);
        return averageDataSeries;
    }


    /**
     * Check if each data set of each data series in the last data repetition
     * has the same size with the same topic. In the same process return the metadata of the data series's
     *
     * @return The meta data
     */
    private HashMap<String, Object> sanityCheck(Property currentProperty) {

        // If the data series is null
        if (dataLog.get(currentProperty) != null && dataLog.get(currentProperty).size() == 0)
            return null;

        Set<String> dataNames = getDataSeriesOfProperty(currentProperty).get(0).getDataSetNames(false);

        HashMap<String, Integer> longest = new HashMap<>();

        // Check if every data series contains the same data sets
        for (DataSeries dataSeries : getDataSeriesOfProperty(currentProperty)) {
            for (DataSet dataSet : dataSeries.getDataSet(false, true)) {
                int size = dataSet.getXData().size();
                if (!longest.containsKey(dataSet.getName()))
                    longest.put(dataSet.getName(), size);
                else if (size > longest.get(dataSet.getName()))
                    longest.put(dataSet.getName(), size);
            }
            if (!dataNames.containsAll(dataSeries.getDataSetNames(false)))
                return null;
        }

        HashMap<String, Object> ret = new HashMap<>();
        ret.put("dataNames", dataNames);
        ret.put("longest", longest);
        if (!getDataSeriesOfProperty(currentProperty).isEmpty())
            ret.put("config", getDataSeriesOfProperty(currentProperty).get(0).getUsedConfig());
        return ret;
    }

    /**
     * Receive all averaged data series for each property in {@code propreties}
     *
     * @param properties The list of related properties
     * @return The averaged data series of the properties
     */
    public LinkedHashMap<Property, DataSeries> getAVGDataSeriesByProperty(List<Property> properties) {
        LinkedHashMap<Property, DataSeries> retDataSeries = new LinkedHashMap<>();
        for (Property prop : properties)
            retDataSeries.put(prop, this.averagedDataSeries.get(prop));
        return retDataSeries;
    }

    /**
     * Returns the DataSeries for the {@code property}
     *
     * @return List of DataSeries
     */
    public List<DataSeries> getDataSeriesOfProperty(Property property) {
        return dataLog.get(property);
    }

    /**
     * Returns the DataSeries for the {@code property}
     *
     * @return List of DataSeries
     */
    public DataSeries getAverageDataSeriesOfProperty(Property property) {
        return this.averagedDataSeries.get(property);
    }

    /**
     * Return the full data log
     *
     * @return The list of each repetition
     */
    public HashMap<Property, List<DataSeries>> getDataLog() {
        return dataLog;
    }

    /**
     * Returns the list of average data series
     *
     * @return The average data series
     */
    public List<DataSeries> getAveragedDataSeries() {
        return new ArrayList<>(averagedDataSeries.values());
    }

    /**
     * Return all stored data series
     *
     * @return A list of data series
     */
    public List<DataSeries> getAllDataSeries() {
        ArrayList<DataSeries> retList = new ArrayList<>(averagedDataSeries.values());
        dataLog.values().forEach(retList::addAll);
        return retList;
    }

    /**
     * Returns the eval data series for the hyper parameter
     *
     * @param hyperProp The hyper parameter that the eval properie should be from
     * @return The stored eval series belonging to {@code hyperProp}
     */
    public EvalSeries getEval(HyperParameter hyperProp) {
        return this.evalDataSeries.get(hyperProp.getTestProperty());
    }
}
