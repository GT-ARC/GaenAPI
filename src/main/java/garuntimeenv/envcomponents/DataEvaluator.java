package garuntimeenv.envcomponents;

import garuntimeenv.envcomponents.datalog.DataLogging;
import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.envcomponents.datalog.DataSeries;
import garuntimeenv.envcomponents.datalog.DataSet;
import garuntimeenv.gacomponents.ProblemProperties;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.Pair;

import java.util.*;

public class DataEvaluator {

    private final static MyLogger logger = MyLogger.getLogger(DataEvaluator.class);
    private HashMap<evaluationPoints, Double> defaultWeights;
    private HashMap<TestManager.testProperties, Map<evaluationPoints, Double>> evaluationWeights;
    private Double bestFitness = 0.;

    // Static environment to subscribe to a data read event from the problem properties
    {
        ProblemProperties.getINSTANCE().notifyOnDataUpdate("lower_bound", element -> bestFitness = element.getAsDouble());
    }

    // The Evaluation points each configuration is scored by
    public enum evaluationPoints {
        runtime,                    // Runtime of the genetic environment
        avgBreakthroughRate,        // Avg time between two new found best solutions
        avgHallOfFame,              // Avg value of the hall of fame
        lastHallOfFame,             // Avg of the n last values in the hall of fame data set
        consistency,                // Avg consistency of every data set in the data series
        endResult,                  // Avg of the last found solution
        bestOfConsistency,          // Consistency of the last found solution
        solutionAvg                 // Avg of the overall solutions
    }

    /**
     * Constructor for the Data evaluator
     */
    DataEvaluator() {
        setupDefaultWeights();
        setupTestPropertyWeights();
    }

    /**
     * Set up the specific evaluation weights for each property that needs changed property weights
     */
    private void setupTestPropertyWeights() {
        this.evaluationWeights = new HashMap<>();

        // For each test property create, designate each evaluation point that is changed a weight
        this.evaluationWeights.put(TestManager.testProperties.populationSize,
                Map.of(
                        evaluationPoints.runtime, 2.,
                        evaluationPoints.endResult, 2.
                )
        );
        this.evaluationWeights.put(TestManager.testProperties.crossoverChromosomes,
                Map.of(
                        evaluationPoints.avgHallOfFame, 2.
                )
        );
    }

    /**
     * Setup the default weights for each evaluation point that is used as a fallback if
     * the test property has no designated weight for that evaluation point
     */
    private void setupDefaultWeights() {
        this.defaultWeights = new HashMap<>();
        this.defaultWeights.put(evaluationPoints.runtime, 0.5);
        this.defaultWeights.put(evaluationPoints.avgBreakthroughRate, 1.5);
        this.defaultWeights.put(evaluationPoints.avgHallOfFame, 0.7);
        this.defaultWeights.put(evaluationPoints.lastHallOfFame, 1.);
        this.defaultWeights.put(evaluationPoints.solutionAvg, 0.8);
        this.defaultWeights.put(evaluationPoints.endResult, 1.);
        this.defaultWeights.put(evaluationPoints.consistency, 1.);
        this.defaultWeights.put(evaluationPoints.bestOfConsistency, 2.);
    }

    /**
     * Create a evaluation series for the {@code hyperParameter} with respect to the fitness function
     *
     * @param hyperParameter  The hyper parameter which data should be evaluated
     * @param fitnessFunction The uses fitness function
     * @return The winning property
     */
    public Property evaluateData(HyperParameter hyperParameter, IFitnessFunction fitnessFunction) {
        DataManager dataManager = DataManager.getInstance();

        List<Property> properties = hyperParameter.getAllProperties();
        if (properties.isEmpty())
            return null;

        LinkedHashMap<Property, DataSeries> avgDataSeries = DataLogging.getInstance().getAVGDataSeriesByProperty(properties);

        // If only one property is tested return it as best
        if (properties.size() == 1)
            return properties.get(0);

        if (avgDataSeries.containsValue(null))
            return properties.get(0);

        // For each category get the value set
        HashMap<evaluationPoints, LinkedHashMap<Property, Double>> values = new LinkedHashMap<>();
        values.put(evaluationPoints.runtime, getRuntimes(avgDataSeries));
        values.put(evaluationPoints.avgBreakthroughRate,
                getAvgBreakthroughRate(avgDataSeries, fitnessFunction));

        values.put(evaluationPoints.avgHallOfFame,
                getAverageOfDataSet(avgDataSeries, "Hall Of Fame", 1, true));
        values.put(evaluationPoints.lastHallOfFame,
                getAverageOfDataSet(avgDataSeries, "Hall Of Fame", 0.2, true));
        values.put(evaluationPoints.endResult,
                getAverageOfDataSet(avgDataSeries, "Best Fitness", 0, true));
        values.put(evaluationPoints.solutionAvg,
                getAverageOfDataSet(avgDataSeries, "Current Fitness", 1, true));
        values.put(evaluationPoints.consistency,
                getAverageOfDataSet(avgDataSeries, "consistency", 1, false));
        values.put(evaluationPoints.bestOfConsistency,
                getAverageOfDataSet(avgDataSeries, "Best Fitness consistency", 0, false));

        // Calculate the percentages out of the measured values
        HashMap<evaluationPoints, LinkedHashMap<Property, Double>> percentages = new LinkedHashMap<>();
        // For each data set create performance percentage
        for (Map.Entry<evaluationPoints, LinkedHashMap<Property, Double>> evaluationEntry : values.entrySet()) {
            evaluationPoints evaluationPoint = evaluationEntry.getKey();
            Map<Property, Double> hardValues = evaluationEntry.getValue();

            // Get the sum of the values to get the average
            double sum = hardValues.values().stream().reduce(Double::sum).get();
            LinkedHashMap<Property, Double> percentage = new LinkedHashMap<>();
            for (Map.Entry<Property, Double> hardValue : hardValues.entrySet()) {
                double currentWeight = getEvaluationWeightOrDefault(hyperParameter.getTestProperty(), evaluationPoint);
                // Depending if a better solution is bigger or smaller
                // invert the percentages
                if (evaluationPoint == evaluationPoints.runtime
                        || evaluationPoint == evaluationPoints.consistency
                        || evaluationPoint == evaluationPoints.bestOfConsistency
                        || fitnessFunction.isBetterSolution(0, 1)) {
                    percentage.put(hardValue.getKey(), currentWeight * (1 - (hardValue.getValue() / sum)));
                } else {
                    percentage.put(hardValue.getKey(), currentWeight * (hardValue.getValue() / sum));
                }
                logger.info("DataEvaluator", hyperParameter.getTestProperty() + " " + hardValue.getKey() + ": " + evaluationPoint + " " + hardValue + " -> " + percentage.get(hardValue.getKey()));
            }

            // Add the solution to the map
            percentages.put(evaluationPoint, percentage);
        }

        // Calculate the scores
        LinkedHashMap<Property, Double> scores = new LinkedHashMap<>();
        double currentBestScore = 0;
        Property bestProperty = null;
        for (Property currentProperty : properties) {

            double percentageSum = 0;
            for (LinkedHashMap<Property, Double> weightedPercentage : percentages.values())
                percentageSum += weightedPercentage.get(currentProperty);
            double score = percentageSum / percentages.size();

            scores.put(currentProperty, score);

            if (currentBestScore < score) {
                currentBestScore = score;
                bestProperty = currentProperty;
            }

            logger.info("DataEvaluator", currentProperty + " got the score " + score);
        }

        dataManager.createNewEvalSeries(hyperParameter, percentages, scores);

        logger.info("DataEvaluator", bestProperty + " won with best score: " + currentBestScore);
        return bestProperty;
    }

    /**
     * Returns the evaluation weight of a specific test property of the evaluation point
     * if for that test property no evaluation
     *
     * @param testedProperty  The specific tested property for which custom weights are set
     * @param evaluationPoint The evaluation point of which weight is required
     * @return The custom weight or if non is set the default weight
     */
    private double getEvaluationWeightOrDefault(TestManager.testProperties testedProperty, evaluationPoints evaluationPoint) {
        if (evaluationWeights.containsKey(testedProperty) &&
                evaluationWeights.get(testedProperty).containsKey(evaluationPoint))
            return evaluationWeights.get(testedProperty).get(evaluationPoint);

        return defaultWeights.get(evaluationPoint);
    }

    /**
     * Receive the runtime's of each data Series
     *
     * @param dataSeries the data series where the runtime's are saved in
     * @return A list of the runtime's as double values
     */
    private LinkedHashMap<Property, Double> getRuntimes(HashMap<Property, DataSeries> dataSeries) {
        LinkedHashMap<Property, Double> retMap = new LinkedHashMap<>();
        dataSeries.forEach((property, currDataSeries) -> retMap.put(property, (double) currDataSeries.getRunTime()));
        return retMap;
    }

    /**
     * Calculates the average breakthrough rate through the best fitness dataset in the dataSeries
     * The breakthrough rate is defined as the avg time between updated values
     *
     * @param dataSeries The data series containing the best fitness data
     * @return The avg breakthrough rate
     */
    private LinkedHashMap<Property, Double> getAvgBreakthroughRate(HashMap<Property, DataSeries> dataSeries, IFitnessFunction fitnessFunction) {
        LinkedHashMap<Property, Double> retStream = new LinkedHashMap<>();
        // Relevant data set
        LinkedHashMap<Property, DataSet> dataSets = getSpecificDataSet(dataSeries, "Best Fitness");

        for (Map.Entry<Property, DataSet> dataSet : dataSets.entrySet()) {
            int breakthroughCounter = -1;
            Number lastBreakthrough = fitnessFunction.getWorstFitness();
            Number timeOfLastBreakthrough = 0;

            double sumOfTimeBetween = 0;

            // Get the data iterator from the data set to go through each data point
            Iterator<Pair<Number, Number>> dataIter = dataSet.getValue().getDataIterator();
            while (dataIter.hasNext()) {
                Pair<Number, Number> dataPoint = dataIter.next();
                Number xValue = dataPoint.getKey();
                Number yValue = dataPoint.getValue();
                // If a new breakthrough
                if (fitnessFunction.isBetterSolution(yValue.doubleValue(), lastBreakthrough.doubleValue())) {
                    breakthroughCounter++;

                    sumOfTimeBetween += xValue.doubleValue() - timeOfLastBreakthrough.doubleValue();

                    lastBreakthrough = yValue;
                    timeOfLastBreakthrough = xValue;
                }
            }
            retStream.put(dataSet.getKey(), sumOfTimeBetween / breakthroughCounter);
        }
        return retStream;
    }

    /**
     * Calculates the average of the collected data given in the data series labeled with {@code name}.
     *
     * @param dataSeries The data series containg the hall of fame data
     * @param range      Value between 0+ and 1 how much data  of the end should be used
     *                   1 = The whole data
     *                   0 = no data at all
     * @return The list of doubles for each data series in {@code dataSeries}
     */
    private LinkedHashMap<Property, Double> getAverageOfDataSet(HashMap<Property, DataSeries> dataSeries, String name, double range, boolean fittnesRelated) {
        if (range < 0 || range > 1) throw new IllegalArgumentException();

        LinkedHashMap<Property, Double> retStream = new LinkedHashMap<>();
        LinkedHashMap<Property, DataSet> dataSets = getSpecificDataSet(dataSeries, name);

        for (Map.Entry<Property, DataSet> dataSet : dataSets.entrySet()) {

            double avg = Double.NaN;
            int counter = 0;
            int dataSize = dataSet.getValue().getXData().size();

            // Get the data iterator from the data set to go through each data point
            Iterator<Pair<Number, Number>> dataIter = dataSet.getValue().getDataIterator();
            int index = dataSize - (int) (dataSize * range);
            // Allways use at least one
            if (index == dataSize) {
                index = dataSize - 1;
            }
            if (index >= 0 && index < dataSize)
                ((DataSet.DataIterator) dataIter).jumpTo(index);


            while (dataIter.hasNext()) {
                Number yValue = dataIter.next().getValue();

                // To get better grading results remove the best possible score from the hard values
                if (fittnesRelated) yValue = Math.abs(yValue.doubleValue() - bestFitness);

                // If a new breakthrough
                if (Double.isNaN(avg)) avg = yValue.doubleValue();
                else
                    avg = (avg * counter + yValue.doubleValue()) / (counter + 1);

                counter++;
            }

            retStream.put(dataSet.getKey(), avg);
        }

        return retStream;
    }

    /**
     * Get Specific data set from property data series map
     *
     * @param dataSeries  The dataseries map with the data series containg the specific data set
     * @param dataSetName The name of the dataset
     * @return A map of with the property pointing to a specific {@code dataSetName}
     */
    private LinkedHashMap<Property, DataSet> getSpecificDataSet(HashMap<Property, DataSeries> dataSeries, String dataSetName) {
        LinkedHashMap<Property, DataSet> retMap = new LinkedHashMap<>();
        dataSeries.forEach((property, currDataSeries) -> retMap.put(property, currDataSeries.get(dataSetName)));
        return retMap;
    }
}
