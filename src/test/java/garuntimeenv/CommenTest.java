package garuntimeenv;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.gacomponents.HallOfFame;
import garuntimeenv.gacomponents.geneticoperators.mutations.InsertionMutation;
import garuntimeenv.gacomponents.geneticoperators.mutations.InversionMutation;
import garuntimeenv.gacomponents.geneticoperators.mutations.SwapMutation;
import garuntimeenv.gacomponents.jobshop.JobShopPreferenceListRep;
import garuntimeenv.gacomponents.jobshop.JobShopProblem;
import garuntimeenv.gacomponents.jobshop.MakespanFitnessFunction;
import garuntimeenv.interfaces.*;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import garuntimeenv.utils.ProblemLoader;
import garuntimeenv.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static garuntimeenv.utils.Utils.getSubsets;

public class CommenTest {

    @Before
    public void setUp() {
        EnvConfig.getInstance().setVisualEnabled(false);
    }

    @Test
    public void testChromosomeHash() throws Exception {
        JobShopProblem jobShopProblem = (JobShopProblem)
                problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling, 0).clone();
        IProblemRepresentation rep = new JobShopPreferenceListRep();
        IFitnessFunction fitnessFunction = new MakespanFitnessFunction();

        String[] testChrome1_1 = new String[]{"1", "2", "0"};
        String[] testChrome1_2 = new String[]{"2", "0", "1"};
        String[] testChrome1_3 = new String[]{"1", "2", "0"};
        Genome genome1_1 = new Genome(testChrome1_1);
        Genome genome1_2 = new Genome(testChrome1_2);
        Genome genome1_3 = new Genome(testChrome1_3);
        Chromosome chromosome1 = new Chromosome(new Genome[]{genome1_1, genome1_2, genome1_3});
        int chromosomeHash1 = chromosome1.hashCode();
        System.out.println(chromosomeHash1);

        ISolution solution1 = rep.createSolutionFromChromosome(chromosome1, (IProblem) jobShopProblem.clone());
        chromosome1.setCorrespondingSolution(solution1);
        System.out.println(fitnessFunction.calculateFitness(solution1));
        System.out.println(solution1);
        int chromosomeHashWithProblem1 = chromosome1.hashCode();
        System.out.println(chromosomeHashWithProblem1);


        String[] testChrome2_1 = new String[]{"1", "2", "0"};
        String[] testChrome2_2 = new String[]{"2", "0", "1"};
        String[] testChrome2_3 = new String[]{"1", "2", "0"};
        Genome genome2_1 = new Genome(testChrome2_1);
        Genome genome2_2 = new Genome(testChrome2_2);
        Genome genome2_3 = new Genome(testChrome2_3);
        Chromosome chromosome2 = new Chromosome(new Genome[]{genome2_1, genome2_2, genome2_3});
        int chromosomeHash2 = chromosome2.hashCode();
        System.out.println(chromosomeHash2);

        ISolution solution2 = rep.createSolutionFromChromosome(chromosome2, jobShopProblem);
        System.out.println();
        chromosome2.setCorrespondingSolution(solution2);
        System.out.println(fitnessFunction.calculateFitness(solution2));
        System.out.println(solution2);
        int chromosomeHashWithProblem2 = chromosome2.hashCode();
        System.out.println(chromosomeHashWithProblem2);

        System.out.println(chromosomeHash1 == chromosomeHash2);
        System.out.println(chromosomeHashWithProblem1 == chromosomeHashWithProblem2);
        System.out.println(chromosome1.equals(chromosome2));

        HallOfFame hallOfFame = new HallOfFame(new MakespanFitnessFunction(), 2);
        hallOfFame.addChromosome(chromosome1);
        hallOfFame.addChromosome(chromosome2);
    }

    @Test
    public void testListHashes() {
        List<String> testList1 = new LinkedList<>();
        List<String> testList2 = new LinkedList<>();

        testList1.add("A");
        testList2.add("A");
        testList1.add("B");
        testList2.add("B");
        testList1.add("C");
        testList2.add("C");
        testList1.add("D");
        testList2.add("D");

        System.out.println(testList1.hashCode());
        System.out.println(testList2.hashCode());
        System.out.println(testList1.equals(testList2));
    }

    @Test
    public void voidHashMapTest() {
        HashMap<Integer, String> testMap1 = new HashMap<>();
        HashMap<Integer, String> testMap2 = new HashMap<>();

        testMap1.put(1, "A");
        testMap2.put(1, "A");
        testMap1.put(2, "B");
        testMap2.put(2, "B");
        testMap1.put(3, "C");
        testMap2.put(3, "C");
        testMap1.put(4, "D");
        testMap2.put(4, "D");

        System.out.println(testMap1.hashCode());
        System.out.println(testMap2.hashCode());
        System.out.println(testMap1.equals(testMap2));
    }

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();

    @Test
    public void chromosomeHashCode() throws MalformedJsonException, FileNotFoundException, DataTypeNotSupportedException, NoSuchFieldException, CloneNotSupportedException {
        JobShopProblem jobShopProblem = (JobShopProblem) problemLoader.loadProblem(ProblemLoader.problems.Job_Shop_Scheduling);

        Chromosome parent1 = new JobShopPreferenceListRep().createRandomRep((IProblem) jobShopProblem.clone());

        System.out.println(parent1);

        Chromosome parent2 = new JobShopPreferenceListRep().createRandomRep((IProblem) jobShopProblem.clone());
    }

    @Test
    public void speedTestList() {
        List<Integer> testList = new ArrayList<>();
        for (int i = 0; i < 10_000_000; i++) {
            testList.add(i);
        }
        System.out.println(testList.size());
    }

    @Test
    public void speedTestArray() {
        Integer[] testArray = new Integer[10_000_000];
        for (int i = 0; i < 10_000_000; i++) {
            testArray[i] = i;
        }
        List<Integer> testList = new ArrayList<Integer>();
        testList.addAll(Arrays.asList(testArray));
        System.out.println(testList.size());
    }

    @Test
    public void randomTest() {
        Random rnd1 = new Random(42);
        for (int i = 0; i < 10; i++) {
            System.out.print(rnd1.nextInt(100) + " ");
        }
        System.out.println();
        Random rnd2 = new Random(42);
        for (int i = 0; i < 10; i++) {
            System.out.print(rnd2.nextInt(100) + " ");
        }
    }

    @Test
    public void numberTest() {
        Number a = 0;
        System.out.println(a.getClass().getSimpleName());
    }

    @Test
    public void subsetTest() {

        List<Property> toBeTestedMutations = new ArrayList<>();
        toBeTestedMutations.add(new InsertionMutation());
        toBeTestedMutations.add(new InversionMutation());
        toBeTestedMutations.add(new SwapMutation());

        List<List<Property>> subsets = getSubsets(toBeTestedMutations.toArray(new Property[0]));
        subsets.sort(Comparator.comparingInt(List::size));

        for (List<Property> subset : subsets) {
            System.out.println(Arrays.toString(subset.toArray()));
        }
    }

    @Test
    public void filterTest() {
        List<Integer> testNumbers = new ArrayList<>();
        testNumbers.add(1);
        testNumbers.add(2);
        testNumbers.add(3);
        testNumbers.add(4);
        testNumbers.add(5);
        testNumbers.add(6);

        testNumbers = testNumbers.stream().filter(integer -> integer > 3).collect(Collectors.toList());

        System.out.println(Arrays.toString(testNumbers.toArray(new Integer[0])));
    }

    @Test
    public void numberString() {
        Number a = 5;
        System.out.println(a.toString());
    }

    @Test
    public void DoubleStreamTest() {
        List<Double> testNumbers = new ArrayList<>();
        testNumbers.add(1.);
        testNumbers.add(3.);
        testNumbers.add(8.);
        testNumbers.add(5.);

        DoubleStream doubleStream = testNumbers.stream().mapToDouble(value -> value);

        double diffSum = 0;
        for (int c = testNumbers.size() - 1; c > 0; c--) {
            for (int i = c; i > 0; i--) {
                diffSum += Math.abs(testNumbers.get(c) - testNumbers.get(i - 1));
            }
        }

        System.out.println(diffSum / Utils.binomial(testNumbers.size(), 2));

        System.out.println(doubleStream.average().orElse(Double.NaN));
    }
}
