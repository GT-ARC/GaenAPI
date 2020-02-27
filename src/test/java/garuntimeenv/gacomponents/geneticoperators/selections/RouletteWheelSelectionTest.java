package garuntimeenv.gacomponents.geneticoperators.selections;

import garuntimeenv.SetupHelper;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.gacomponents.GAManager;
import garuntimeenv.gacomponents.Population;
import garuntimeenv.gacomponents.jobshop.MakespanFitnessFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RouletteWheelSelectionTest {

    RouletteWheelSelection propRandSelect = new RouletteWheelSelection();

    @Test
    public void testChromosomeSelection() throws NoSuchFieldException, IllegalAccessException {
        new GAManager(null, new Config());
        int popAmount = 10;
        Population testPop = SetupHelper.getEvalulatedJSSPPopulation(popAmount, 1);
        List<Number> fitness = Arrays.stream(testPop.getChromosomes()).map(Chromosome::getFitness).collect(Collectors.toList());

        int maxValue = fitness.stream().reduce(0, (a, b) -> a.doubleValue() > b.doubleValue() ? a : b).intValue();
        fitness = fitness.stream().map(a -> maxValue - a.intValue()).collect(Collectors.toList());

        // Print each fitness of population and sum it up and print the fractions
        int sum = fitness.stream().reduce(0, (a, b) -> a.intValue() + b.intValue()).intValue();
        System.out.println(fitness);
        List<Double> fractions = fitness.stream().map(integer -> Math.round((double) integer.doubleValue() / (double) sum * 1000.0) / 1000.0).collect(Collectors.toList());
        System.out.println(fractions);

        propRandSelect.addNewPopulation(testPop.getChromosomes(), new MakespanFitnessFunction());

        System.out.println(
                FieldUtils.readField(propRandSelect, "cumulativeSums", true)
        );

        System.out.println(
                FieldUtils.readField(propRandSelect, "midPoint", true) + " " +
                        FieldUtils.readField(propRandSelect, "limitValue", true)

        );

        List<Chromosome> selectedChromosomes = new ArrayList<>();
        for (int i = 0; i < 1000; i++)
            selectedChromosomes.add(propRandSelect.getNextChromosome());

        List<Chromosome> controlChromosomes = Arrays.asList(testPop.getChromosomes());
        //controlChromosomes.sort(Comparator.comparingInt(Chromosome::getFitness));

        System.out.println(" ");

        // Print the amount of the received chromosomes
        int[] count = new int[popAmount];
        selectedChromosomes.forEach(chromosome -> {
            int index = controlChromosomes.indexOf(chromosome);
            count[index]++;
        });

        System.out.println(Arrays.toString(count));


        int receivedChromosomesSum = Arrays.stream(count).sum();
        double[] fractions2 = new double[popAmount];
        for (int i = 0; i < popAmount; i++)
            fractions2[i] = Math.round((double) count[i] / (double) receivedChromosomesSum * 1000.0) / 1000.0;
        System.out.println(Arrays.toString(fractions2));
    }

    @Test
    public void testBisect() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        RouletteWheelSelection testPropRandSelect = new RouletteWheelSelection();

//        FieldUtils.writeField(testPropRandSelect, "cumulativeSums", Arrays.asList(
//                306., 426., 848., 1165., 1403., 1851., 2204., 2204., 2461., 2921.
//        ), true);
        FieldUtils.writeField(testPropRandSelect, "cumulativeSums", Arrays.asList(
                1., 2.
        ), true);

        Method m = testPropRandSelect.getClass().getDeclaredMethod("bisect", double.class);
        m.setAccessible(true);

        int index = (int) m.invoke(testPropRandSelect, 2);
        System.out.println(index);
    }

}
