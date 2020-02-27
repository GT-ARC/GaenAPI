package garuntimeenv.utils;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.interfaces.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Util class containing static helper functions
 */
public class Utils {

    private static Random rand = new Random(TestManager.getSeed());

    /**
     * Print all subsets of given set[]
     * Modified version of https://java2blog.com/find-subsets-set-power-set/
     */
    public static List<List<Property>> getSubsets(Property[] set) {
        int n = set.length;
        List<List<Property>> retList = new ArrayList<>();

        // Run a loop from 0 to 2^n
        for (int i = 0; i < (1 << n); i++) {
            List<Property> subset = new ArrayList<>();
            int m = 1; // m is used to check set bit in binary representation.
            // Print current subset
            for (int j = 0; j < n; j++) {
                if ((i & m) > 0) {
                    subset.add(set[j]);
                }
                m = m << 1;
            }

            retList.add(subset);
        }
        return retList;
    }

    /**
     * Calculates the average difference between the values in the {@code values} list
     *
     * @param values The list containing the values to
     * @return The avg. difference
     */
    public static double calcDiffBetween(List<Double> values) {
        double diffSum = 0;
        for (int c = values.size() - 1; c > 0; c--) {
            for (int i = c; i > 0; i--) {
                diffSum += Math.abs(values.get(c) - values.get(i - 1));
            }
        }

        return diffSum / Utils.binomial(values.size(), 2);
    }

    /**
     * Calculates the binomial coefficient
     * https://stackoverflow.com/questions/36925730/java-calculating-binomial-coefficient
     *
     * @param n upper value
     * @param k lower value
     * @return the binomial coefficient
     */
    public static long binomial(int n, int k) {
        if (k > n - k)
            k = n - k;

        long b = 1;
        for (int i = 1, m = n; i <= k; i++, m--)
            b = b * m / i;
        return b;
    }

    /**
     * Transform the object list given into the class names
     *
     * @param list The list of
     * @return The list of class names of the objects in the list
     */
    public static String getObjectListName(List<Object> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (Iterator<Object> iterator = list.iterator(); iterator.hasNext(); ) {
            Object elem = iterator.next();
            stringBuilder.append(elem.getClass().getSimpleName());
            if (iterator.hasNext())
                stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * Return a pair order of the inserted to elements
     *
     * @param first  The first object
     * @param second The second object
     * @param <T>    Any object
     * @return A pair in which the order of key value is random
     */
    public static <T> Pair<T, T> getRandomObject(T first, T second) {
        T selected = rand.nextDouble() > 0.5 ? first : second;
        T other = selected == first ? second : first;
        return new Pair<>(selected, other);
    }

}
