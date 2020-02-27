package garuntimeenv.utils;

import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.envcomponents.datalog.DataLogging;
import garuntimeenv.envcomponents.datalog.DataManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.internal.chartpart.Axis;
import org.knowm.xchart.internal.chartpart.AxisPair;
import org.knowm.xchart.internal.series.AxesChartSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Class with functions for the interface
 */
public class InterfaceHelper {

    public static int windowSize = EnvConfig.windowSize;
    public static int meanWindowSize = EnvConfig.meanWindowSize;

    private static final EnvConfig envConfig = EnvConfig.getInstance();

    // Window elements
    private static JPanel configPanel = new JPanel();
    private static JPanel controlPanel = new JPanel();

    private static JComboBox<TestManager.testProperties> comboBox;

    /**
     * Create the control panel for start stop and hyper parameter selection
     *
     * @return The new control JPanel
     */
    public static JPanel createControlPanel() {
        controlPanel.setLayout(new BorderLayout());

        JToggleButton startStopButton = new JToggleButton("Stop", envConfig.isPaused());
        startStopButton.setPreferredSize(new Dimension(100, 25));
        startStopButton.addItemListener(e -> {
            envConfig.setPaused(startStopButton.isSelected());
            if (startStopButton.isSelected()) {
                startStopButton.setText("Start");
            } else {
                startStopButton.setText("Stop");
            }
        });


        comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(400, 25));
        comboBox.addItem(TestManager.getInstance().getCurrentHyperParameter().getTestProperty());
        comboBox.addItemListener(e -> {
            DataManager.getInstance().getDataVisualizer().changeChartPanel((TestManager.testProperties) e.getItem());
        });

        JPanel button = new JPanel();
        button.add(startStopButton);

        JPanel comboB = new JPanel();
        comboB.add(comboBox);

        controlPanel.add(button, BorderLayout.CENTER);
        controlPanel.add(comboB, BorderLayout.WEST);

        controlPanel.validate();
        return controlPanel;
    }

    /**
     * Create the config panel to the right of the ui
     * Includes all the call back functions to the buttons
     *
     * @param dataLog The data log object
     * @return The new configuration JPanel
     */
    public static JPanel createConfigPanel(DataLogging dataLog) {
        int width = 100;
        configPanel.setPreferredSize(new Dimension(width, 200));
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));

        createWindowSizeElements(dataLog);
        createAutoJumpButton();

        configPanel.add(Box.createVerticalGlue());

//        configPanel.setBackground(new Color(255, 0, 0 ));
        configPanel.validate();
        return configPanel;
    }

    /**
     * Create the window size config text field with the respective call backs
     *
     * @param dataLog The used data log object in which the window size needs to be altered
     */
    private static void createWindowSizeElements(DataLogging dataLog) {
        JLabel windowSizeLabel = new JLabel("Fenstergröße: ");
        JTextField windowSizeTF = new JTextField(EnvConfig.windowSize + "", 1);

        windowSizeTF.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == 10) {
                            String text = windowSizeTF.getText();
                            int number = 0;
                            try {
                                number = Integer.parseInt(text);
                            } catch (NumberFormatException error) {
                                return;
                            }
                            windowSize = number;
                            dataLog.getDataLog().values().forEach(run ->
                                    run.forEach(dataSeries -> {
                                        dataSeries.getDataSet(true, true)
                                                .forEach(dataSet -> {
                                                    dataSet.updateWindowSize(windowSize);
                                                    dataSeries.getXChart().updateXYSeries(
                                                            dataSet.getName(),
                                                            dataSet.getXDataVisible(),
                                                            dataSet.getYDataVisible(),
                                                            null
                                                    );
                                                });
                                        dataSeries.getChart().repaint();
                                    }));

                            dataLog.getAveragedDataSeries().forEach(dataSeries -> {
                                dataSeries.getDataSet(true, true)
                                        .forEach(dataSet -> {
                                            dataSet.updateWindowSize(windowSize);
                                            dataSeries.getXChart().updateXYSeries(
                                                    dataSet.getName(),
                                                    dataSet.getXDataVisible(),
                                                    dataSet.getYDataVisible(),
                                                    null
                                            );
                                        });
                                dataSeries.getChart().repaint();
                            });

                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                }
        );

        windowSizeTF.setMaximumSize(new Dimension(2000, 25));

        configPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        configPanel.add(windowSizeLabel);
        configPanel.add(windowSizeTF);
    }

    /**
     * Create the button that handles the auto jump functionality
     */
    private static void createAutoJumpButton() {
        JLabel autoJumpLabel = new JLabel("Autojump: ");
        JToggleButton autoJumpButton = new JToggleButton("Turn Off", envConfig.isAutoJump());
        autoJumpButton.addItemListener(e -> {
            envConfig.setAutoJump(autoJumpButton.isSelected());
            if (autoJumpButton.isSelected()) {
                autoJumpButton.setText("Turn Off");
            } else {
                autoJumpButton.setText("Turn On");
            }
        });

        configPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        configPanel.add(autoJumpLabel);
        configPanel.add(autoJumpButton);
    }

    /**
     * Add a property to the hyper parameter combo box
     *
     * @param property The property to be tested
     */
    public static void addProperty(TestManager.testProperties property) {
        comboBox.addItem(property);
    }

    /**
     * Select a specific element in the hyper parameter combo box
     *
     * @param property The to be selected property
     */
    public static void selectProperty(TestManager.testProperties property) {
        comboBox.setSelectedItem(property);
    }

/**
 * The the y axis of the given xchart instances to the same scale
 *
 * @param charts The XCharts to be modified
 */
public static void setXChartToSameYAxis(List<XYChart> charts) {
    double min = getAxisValue(charts, true);
    double max = getAxisValue(charts, false);

    charts.forEach(xyChart -> {
        xyChart.getStyler().setYAxisMin(min);
        xyChart.getStyler().setYAxisMax(max);
    });
}

/**
 * Grab into protected parts of the xchart libary to get the biggest and lowest y axis values
 *
 * @param charts The charts in which is to be searched
 * @param min If a min or a max should be found
 * @return The min or max value regarding {@code min}
 */
    static double getAxisValue(List<XYChart> charts, boolean min) {
        DoubleStream stream = charts.stream()
                .map(xyChart -> xyChart.getSeriesMap().values())
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .flatMapToDouble(xySeries -> DoubleStream.of(min ? xySeries.getYMin() : xySeries.getYMax()));

        OptionalDouble value = min ? stream.min() : stream.max();

        if (value.isPresent())
            return value.getAsDouble();

        return 0;
    }
}
