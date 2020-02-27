package garuntimeenv.envcomponents.datalog;

import garuntimeenv.utils.InterfaceHelper;
import garuntimeenv.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that represents one dataset and its visible section
 */
public class DataSet {

    private boolean isVisual = true;
    private String name;
    private List<Number> xData, xDataVisible;
    private List<Number> yData, yDataVisible;
    private double meanSum;

    /**
     * Constructor for one data set
     * Initializing all lists
     *
     * @param name The data set name
     */
    public DataSet(String name) {
        this.name = name;
        this.xData = new ArrayList<>();
        this.yData = new ArrayList<>();
        this.xDataVisible = new ArrayList<>();
        this.yDataVisible = new ArrayList<>();
    }

    /**
     * Constructor for visual data sets
     *
     * @param name     The data set name
     * @param isVisual If the constructor should be visual
     *                 Default is true
     */
    public DataSet(String name, boolean isVisual) {
        this(name);
        this.isVisual = isVisual;
    }

    /**
     * Add a data point the the lists
     *
     * @param x value of the point
     * @param y value of the point
     */
    public void addDataPoint(Number x, Number y) {
        this.xData.add(x);
        this.yData.add(y);

        this.xDataVisible.add(x);
        this.yDataVisible.add(y);

        // Calculate the average
        meanSum += y.doubleValue();
        if (yData.size() > InterfaceHelper.meanWindowSize) {
            meanSum -= yData.get(yData.size() - InterfaceHelper.meanWindowSize).doubleValue();
        }

        if (InterfaceHelper.windowSize != -1 && xDataVisible.size() > InterfaceHelper.windowSize)
            xDataVisible.remove(0);
        if (InterfaceHelper.windowSize != -1 && yDataVisible.size() > InterfaceHelper.windowSize)
            yDataVisible.remove(0);
    }

    /**
     * Change the window size of the visual data
     * Redo the visible data lists
     *
     * @param windowSize The window size in int.
     *                   A value of -1 means all data
     */
    public void updateWindowSize(int windowSize) {
        if (windowSize < 0) {
            this.xDataVisible = new ArrayList<>(this.xData);
            this.yDataVisible = new ArrayList<>(this.yData);
        } else {
            int beginningX = Math.max(this.xData.size() - windowSize, 0);
            int beginningY = Math.max(this.yData.size() - windowSize, 0);
            this.xDataVisible = new ArrayList<>(this.xData.subList(beginningX, this.xData.size()));
            this.yDataVisible = new ArrayList<>(this.yData.subList(beginningY, this.yData.size()));
        }
    }

    /**
     * Iterator for the data of this data set
     */
    public static class DataIterator implements Iterator<Pair<Number, Number>> {

        // The list over which should be iterated
        private List<Number> xData;
        private List<Number> yData;

        private int index = 0;

        /**
         * Data iterator constructor
         *
         * @param xData The x data list
         * @param yData the y data list
         */
        public DataIterator(List<Number> xData, List<Number> yData) {
            if (xData.size() != yData.size())
                throw new IllegalArgumentException("The data lists have to be the same size");

            this.xData = xData;
            this.yData = yData;
        }

        /**
         * If some values should be skipped you can jump to a specific index
         *
         * @param index The index to be jumped to
         */
        public void jumpTo(int index) {
            if (index >= xData.size() || index < 0) throw new IndexOutOfBoundsException();
            this.index = index;
        }

        /**
         * If the list ha containing elements
         *
         * @return True if there are elements left
         */
        @Override
        public boolean hasNext() {
            return xData.size() >= index + 1;
        }

        /**
         * Get the next elements and increase the index
         *
         * @return The next element
         */
        @Override
        public Pair<Number, Number> next() {
            Pair<Number, Number> retElement = new Pair<>(xData.get(index), yData.get(index));
            index++;
            return retElement;
        }
    }

    /**
     * Getter for a specific data point
     *
     * @param index The index of the data point
     * @return the y value of the point at the specific index
     */
    public Number getDataPoint(int index) {
        return this.yData.get(index);
    }

    /**
     * Return a new data iterator for this data set
     *
     * @return A new data iterator object
     */
    public Iterator<Pair<Number, Number>> getDataIterator() {
        return new DataIterator(this.xData, this.yData);
    }

    /**
     * Getter for the x data list
     *
     * @return the x data list
     */
    public List<Number> getXData() {
        return xData;
    }

    /**
     * Getter for the visible x data
     *
     * @return The visible x data
     */
    public List<Number> getXDataVisible() {
        return xDataVisible;
    }

    /**
     * Getter for the y data
     *
     * @return the y data
     */
    public List<Number> getYData() {
        return yData;
    }

    /**
     * Getter for the visible y data
     *
     * @return
     */
    public List<Number> getYDataVisible() {
        return yDataVisible;
    }

    /**
     * Getter for the data set name
     *
     * @return The name of the data set
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the data set name
     *
     * @param name The name of this data set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get average for the mean data
     *
     * @return The mean of the last - mean window size values
     */
    public double getMean() {
        return meanSum / (Math.min(xDataVisible.size(), InterfaceHelper.meanWindowSize));
    }

    /**
     * Is the data set a visual one
     *
     * @return True if the data set is visual
     */
    public boolean isVisual() {
        return isVisual;
    }
}
