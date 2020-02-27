package garuntimeenv.envcomponents;

import garuntimeenv.envcomponents.datalog.DataLogging;
import garuntimeenv.envcomponents.datalog.DataSeries;
import garuntimeenv.envcomponents.datalog.DataSet;
import garuntimeenv.envcomponents.datalog.EvalSeries;
import garuntimeenv.interfaces.Property;
import garuntimeenv.utils.InterfaceHelper;
import garuntimeenv.utils.MyLogger;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for visualising the data in XChart graphs
 * and organsing them by hyper parameter, hyper parameter instanze and run
 * with small functionality
 */
public class DataVisualizer {

    final static MyLogger logger = MyLogger.getLogger(DataVisualizer.class);

    private HashMap<TestManager.testProperties, JTabbedPane> propertyToPanelMap = new HashMap<>();
    private HashMap<Property, JTabbedPane> propertyToTabbedPane = new HashMap<>();
    private JTabbedPane activePanel;
    private TestManager testManager = TestManager.getInstance();

    private boolean firstTime = true;

    private JFrame frame;

    /**
     * Constructor for the data visualizer creating the frame and initializing the first tabs
     *
     * @param dataLog The used data log
     */
    public DataVisualizer(DataLogging dataLog) {
        this.frame = new JFrame("Genetic Algorithm Visualizer");
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.activePanel = tabbedPane;
        this.propertyToPanelMap.put(this.testManager.getCurrentHyperParameter().getTestProperty(), tabbedPane);

        this.frame.add(tabbedPane, BorderLayout.CENTER);
        this.frame.add(InterfaceHelper.createConfigPanel(dataLog), BorderLayout.EAST);
        this.frame.add(InterfaceHelper.createControlPanel(), BorderLayout.PAGE_START);

        this.frame.validate();
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * Create a new xy chart instance and return a jpanel with it
     *
     * @param label      The title of the xchart instance
     * @param dataSeries The data series out of which the
     * @return JPanel with the xchart instance on it
     */
    public JPanel createXYChartPanel(String label, DataSeries dataSeries) {
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(500)
                .title(label)
                .xAxisTitle("Generation")
                .yAxisTitle("Fitness")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(5);
        chart.getStyler().setToolTipsEnabled(true);

        dataSeries.setXChart(chart);

        // Add the data sets from the series to the xchart of the series
        for (DataSet dataSet : dataSeries.getDataSet(true, false)) {
            if (dataSet.isVisual())
                chart.addSeries(dataSet.getName(), dataSet.getXData(), dataSet.getYData());
        }

        JPanel chartPanel = new XChartPanel<XYChart>(chart);
        dataSeries.setChart(chartPanel);

        return chartPanel;
    }

    /**
     * Create a new category chart instance and return a new jpanel instance with it on.
     *
     * @param label      The label for the chart
     * @param evalSeries The eval Series from the
     * @return The jpanel with the chart on
     */
    public JPanel createCategoryChart(String label,
                                      EvalSeries evalSeries,
                                      DataEvaluator.evaluationPoints evaluationPoint,
                                      boolean score) {
        CategoryChart categoryChart = new CategoryChartBuilder()
                .width(1500)
                .height(1000)
                .title(label)
                .xAxisTitle("Property")
                .yAxisTitle("Score")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        categoryChart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        categoryChart.getStyler().setMarkerSize(10);
        categoryChart.getStyler().setToolTipsEnabled(true);
        categoryChart.getStyler().setXAxisLogarithmic(true);

        if (label.equals("Scores")) {
            evalSeries.setScoreXChart(categoryChart);
        } else {
            evalSeries.addChart(evaluationPoint, categoryChart);
        }

        if (score) {
            categoryChart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
            LinkedHashMap<Property, Double> currentValues = evalSeries.getScore();
            categoryChart.addSeries(label,
                    currentValues.keySet().stream().map(Object::toString).collect(Collectors.toList()),
                    new ArrayList<Double>(currentValues.values())
            );
        } else {
            // If all properties come on one category chart
            if (evaluationPoint == null) {
                categoryChart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
                evalSeries.getAllData().forEach((key, value) ->
                        categoryChart.addSeries(
                                key.name(),
                                value.keySet().stream().map(Object::toString).collect(Collectors.toList()),
                                new ArrayList<Double>(value.values())
                        )
                );

            } else {
                categoryChart.getStyler().setDefaultSeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
                LinkedHashMap<Property, Double> currentValues = evalSeries.getEvalPointData(evaluationPoint);
                categoryChart.addSeries(label,
                        currentValues.keySet().stream().map(Object::toString).collect(Collectors.toList()),
                        new ArrayList<Double>(currentValues.values())
                );
            }
        }

        return new XChartPanel<>(categoryChart);
    }

    /**
     * Create a tabbed pane form the given evaluation series
     *
     * @param evalSeries the evaluation series
     */
    public void createTabbedPaneFromEvalSeries(EvalSeries evalSeries) {
        TestManager.testProperties testedProperty = evalSeries.getTestedHyperParameter().getTestProperty();
        JTabbedPane parentTappedPane = this.propertyToPanelMap.get(testedProperty);

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        parentTappedPane.add("Evaluation", jTabbedPane);

        jTabbedPane.add("Score", evalSeries.getScoreChart());
        jTabbedPane.add("Collective", evalSeries.getCollectiveChart());
        for (Map.Entry<DataEvaluator.evaluationPoints, JPanel> element : evalSeries.getCharts().entrySet()) {
            jTabbedPane.add(element.getKey().name(), element.getValue());
        }
    }

    /**
     * Creates a JPanel from the given data series
     *
     * @param dataSeries the new data series
     */
    public void createChartPanelFromDataSeries(DataSeries dataSeries) {

        // Create a new JTapped Pane if its a new run
        TestManager.testProperties currentTestProperty = this.testManager.getCurrentHyperParameter().getTestProperty();
        JTabbedPane parentTappedPane = this.propertyToPanelMap.get(currentTestProperty);

        JTabbedPane jTabbedPane = getTabbedPanelFromProperty(dataSeries.getTestedProperty());
        parentTappedPane.add(dataSeries.getTestedProperty().toString(), jTabbedPane);

        // Create a new chart panel for the new Data Series
        JPanel chartPanel = createXYChartPanel(dataSeries.getName(), dataSeries);
        jTabbedPane.addTab(dataSeries.getName(), chartPanel);

        if (EnvConfig.getInstance().isAutoJump()) {
            InterfaceHelper.selectProperty(currentTestProperty);
            jTabbedPane.setSelectedComponent(chartPanel);
            parentTappedPane.setSelectedComponent(jTabbedPane);
        }

        // If its the first time creating a data series create the frame
        try {
            if (firstTime) {
                frame.validate();
                frame.pack();
                frame.setVisible(true);
                firstTime = !firstTime;
            }
        } catch (ConcurrentModificationException | NullPointerException e) {
            // Skip the conncurrent modification error
        }
        frame.repaint();
    }

    /**
     * Creates a tabbed pane out of a list of data series
     *
     * @param dataSeriesList The list of data series
     * @return JTabbedPane containing the jpanels of the data series
     */
    private JTabbedPane createTabbedPane(List<DataSeries> dataSeriesList) {
        // Create new tabbed pane for the average of the series
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (DataSeries dataSeries : dataSeriesList)
            jTabbedPane.addTab(dataSeries.getName(), dataSeries.getChart());

        return jTabbedPane;
    }

    /**
     * Repaint the frame and the panel of the {@code currentDataSeries}
     *
     * @param currentDataSeries The data series containing the JPanel to be repainted
     */
    public synchronized void repaint(DataSeries currentDataSeries) {
        try {
            currentDataSeries.getChart().revalidate();
            currentDataSeries.getChart().repaint();
        } catch (ConcurrentModificationException | NullPointerException e) {
            // Skip the conncurrent modification error
        }
    }

    /**
     * Create a new tabbed pane for the current tested property
     * given through the test manager
     */
    public void createNewTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        TestManager.testProperties currentTestProperty = this.testManager.getCurrentHyperParameter().getTestProperty();
        propertyToPanelMap.put(currentTestProperty, tabbedPane);
        InterfaceHelper.addProperty(currentTestProperty);

        if (EnvConfig.getInstance().isAutoJump()) {
            InterfaceHelper.selectProperty(currentTestProperty);
            this.frame.remove(this.activePanel);
            this.frame.add(tabbedPane, BorderLayout.CENTER);
            this.activePanel = tabbedPane;
        }
    }

    /**
     * Change the chart panel when selecting a new hyper parameter from the hyper parameter list
     *
     * @param property The selected hyper parameter
     */
    public void changeChartPanel(TestManager.testProperties property) {
        if (this.activePanel == propertyToPanelMap.get(property)) return;
        this.frame.remove(this.activePanel);
        this.activePanel = propertyToPanelMap.get(property);
        this.activePanel.revalidate();
        this.activePanel.repaint();
        this.frame.add(this.activePanel, BorderLayout.CENTER);
        this.frame.validate();
        this.frame.repaint();
    }

    /**
     * Create a tabbed pane from the given property
     *
     * @param currentProperty The property to be turned into a tabbed pane
     * @return The created tabbed pane
     */
    public JTabbedPane getTabbedPanelFromProperty(Property currentProperty) {
        if (!propertyToTabbedPane.containsKey(currentProperty)) {
            JTabbedPane jTabbedPane = new JTabbedPane();
            jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            propertyToTabbedPane.put(currentProperty, jTabbedPane);
        }
        return propertyToTabbedPane.get(currentProperty);
    }
}
