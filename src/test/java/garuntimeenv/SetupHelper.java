package garuntimeenv;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Config;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.gacomponents.Population;
import garuntimeenv.gacomponents.jobshop.JobShopPreferenceListRep;
import garuntimeenv.gacomponents.jobshop.JobShopProblem;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.interfaces.ISolution;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import garuntimeenv.utils.ProblemLoader;

import java.io.FileNotFoundException;
import java.util.Random;

public class SetupHelper {

    private static Random rand = new Random(TestManager.getSeed());

    private static ProblemLoader problemLoader = ProblemLoader.getInstance();
    private static JobShopPreferenceListRep jsspPrefList = new JobShopPreferenceListRep();
    private static Config config = new Config();

    public static Population getEvalulatedJSSPPopulation(int size) {
        return getEvalulatedJSSPPopulation(size, rand.nextInt());
    }

    public static Population getEvalulatedJSSPPopulation(int size, int index) {
        Population retPop = getJSSPPopulation(size, index);
        for (Chromosome chromosome : retPop.getChromosomes()) {
            try {
                ISolution solution = jsspPrefList.createSolutionFromChromosome(chromosome, getJobShopProblem(index));
                chromosome.setCorrespondingSolution(solution);
            } catch (Exception e) {
                e.printStackTrace();
            }
            chromosome.calculateFitness(config.getFitnessFunction());
        }
        return retPop;
    }

    public static Population getJSSPPopulation(int size) {
        return getJSSPPopulation(size, rand.nextInt());
    }

    public static Population getJSSPPopulation(int size, int index) {
        Chromosome[] retChromosomes = new Chromosome[size];
        for (int i = 0; i < size; i++) {
            retChromosomes[i] = getJSSPChromosome(index);
        }
        return new Population(retChromosomes);
    }

    public static Chromosome getJSSPChromosome() {
        return getJSSPChromosome(rand.nextInt());
    }

    public static Chromosome getJSSPChromosome(int index) {
        return jsspPrefList.createRandomRep(
                getJobShopProblem(index)
        );
    }

    public static JobShopProblem getJobShopProblem() {
        return (JobShopProblem) getProblem(ProblemLoader.problems.Job_Shop_Scheduling, rand.nextInt());
    }

    public static JobShopProblem getJobShopProblem(int index) {
        return (JobShopProblem) getProblem(ProblemLoader.problems.Job_Shop_Scheduling, index);
    }

    public static IProblem getProblem(ProblemLoader.problems problem, int index) {
        try {
            return (IProblem) problemLoader.loadProblem(problem, index).clone();
        } catch (FileNotFoundException | DataTypeNotSupportedException | CloneNotSupportedException | MalformedJsonException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Chromosome getChromosomeFromIntArray(int... input) {

        String[] convertedInput = new String[input.length];
        for (int i = 0; i < input.length; i++)
            convertedInput[i] = input[i] + "";

        Genome genome = new Genome(convertedInput);
        return new Chromosome(new Genome[]{genome});
    }

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        SetupHelper.config = config;
    }
}



