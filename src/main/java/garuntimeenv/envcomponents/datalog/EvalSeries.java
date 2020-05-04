package garuntimeenv.envcomponents.datalog;

import garuntimeenv.envcomponents.DataEvaluator;
import garuntimeenv.interfaces.HyperParameter;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.MyLogger;
import org.apache.logging.log4j.Level;
import org.knowm.xchart.CategoryChart;

import javax.swing.*;
import java.util.*;

/**
 * Class for storing the evaluation series data
 */
public class EvalSeries {

    final static MyLogger logger = MyLogger.getLogger(EvalSeries.class);

    // Save hyper parameter
    private HyperParameter testedGeneticProperty;

    // Data
    HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> values;
    private LinkedHashMap<Property, Double> scores;

    // Visual stuff
    private JPanel chart;
    private JPanel scoreChart;
    private CategoryChart scoreXChart;
    private HashMap<DataEvaluator.evaluationPoints, JPanel> panels = new HashMap<>();
    private HashMap<DataEvaluator.evaluationPoints, CategoryChart> charts = new HashMap<>();

    /**
     * Constructor for the evaluation series
     *
     * @param testedGeneticProperty The tested hyper parameter
     * @param values                The percentages achieved of each property
     * @param scores                The resulting scores
     */
    public EvalSeries(
            HyperParameter testedGeneticProperty,
            HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> values,
            LinkedHashMap<Property, Double> scores) {
        this.testedGeneticProperty = testedGeneticProperty;
        this.values = values;
        this.scores = scores;
    }

    /**
     * Add the {@code values} to the stored values in the data structure
     *
     * @param evaluationPoint the corresponding evaluation point
     * @param values          The list of values
     */
    public void addData(DataEvaluator.evaluationPoints evaluationPoint, List<Property> propertyList, List<Double> values) {

        if (propertyList == null || values == null) {
            logger.log(Level.ERROR, "EvalSeries", "Property is null");
            return;
        }

        if (propertyList.size() != values.size()) {
            logger.log(Level.ERROR, "EvalSeries", "Property list size doesnt match values list size");
            return;
        }

        if (!this.values.containsKey(evaluationPoint)) {
            this.values.put(evaluationPoint, new LinkedHashMap<>());
        }

        LinkedHashMap<Property, Double> currentSet = this.values.get(evaluationPoint);

        for (int i = 0; i < propertyList.size(); i++) {
            currentSet.put(propertyList.get(i), values.get(i));
        }
    }

    /**
     * Add a category XChart for the visual belonging to the {@code evaluationPoint}
     *
     * @param evaluationPoint The evaluation point the chart belongs to
     * @param chart           The category chart
     */
    public void addChart(DataEvaluator.evaluationPoints evaluationPoint, CategoryChart chart) {
        this.charts.put(evaluationPoint, chart);
    }

    /**
     * Return all XCharts of the eval series
     *
     * @return A set of maps each mapping the eval point to a category chart
     */
    public Set<Map.Entry<DataEvaluator.evaluationPoints, CategoryChart>> getXCharts() {
        return this.charts.entrySet();
    }

    /**
     * Setter for the percentage values
     *
     * @param values The values created by the automatic evaluation
     */
    public void setValues(HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> values) {
        this.values = values;
    }

    /**
     * Getter for a specific evaluation point returning all percentages
     *
     * @param evaluationPoint The requested evaluation point
     * @return Map containing pairs of property to double
     */
    public LinkedHashMap<Property, Double> getEvalPointData(DataEvaluator.evaluationPoints evaluationPoint) {
        return this.values.get(evaluationPoint);
    }

    /**
     * Get all stored data of the eval series
     *
     * @return Return the map of maps
     */
    public HashMap<DataEvaluator.evaluationPoints, LinkedHashMap<Property, Double>> getAllData() {
        return this.values;
    }

    /**
     * Get the tested genetic hyper parameter
     *
     * @return The hyper parameter belonging to this eval series
     */
    public HyperParameter getTestedHyperParameter() {
        return testedGeneticProperty;
    }

    /**
     * Getter for the Charts
     *
     * @return Mapping of eval points to jpanels
     */
    public HashMap<DataEvaluator.evaluationPoints, JPanel> getCharts() {
        return this.panels;
    }

    /**
     * The collective chart
     *
     * @return the collective chart containing all eval points
     */
    public JPanel getCollectiveChart() {
        return this.chart;
    }

    /**
     * Setter for the collective chart
     *
     * @param chart The collective jpanel chart
     */
    public void setCollectiveChart(JPanel chart) {
        this.chart = chart;
    }

    /**
     * Setter for the score chart
     *
     * @param scoreChart The score chart
     */
    public void setScoreChart(JPanel scoreChart) {
        this.scoreChart = scoreChart;
    }

    /**
     * Getter for the score chart
     *
     * @return the jpanel of the score chart
     */
    public JPanel getScoreChart() {
        return this.scoreChart;
    }

    /**
     * Adds a category chart to the eval series
     *
     * @param jpanel          The to be set JPanel
     * @param evaluationPoint The belonging evaluation point
     */
    public void addCategoryChart(JPanel jpanel, DataEvaluator.evaluationPoints evaluationPoint) {
        this.panels.put(evaluationPoint, jpanel);
    }

    /**
     * Getter for the scores
     *
     * @return Mapping of propertys to the scores
     */
    public LinkedHashMap<Property, Double> getScore() {
        return this.scores;
    }

    /**
     * Set the xchart for the score
     *
     * @param categoryChart The category chart containing the score data
     */
    public void setScoreXChart(CategoryChart categoryChart) {
        this.scoreXChart = categoryChart;
    }

    /**
     * Getter for the score XChart
     *
     * @return The category XChart containing the score data
     */
    public CategoryChart getScoreXChart() {
        return this.scoreXChart;
    }
}
