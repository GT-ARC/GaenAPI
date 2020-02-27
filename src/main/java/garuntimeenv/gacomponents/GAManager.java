package garuntimeenv.gacomponents;

import garuntimeenv.envcomponents.EnvConfig;
import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.envcomponents.datalog.DataManager;
import garuntimeenv.gacomponents.geneticoperators.mutations.Mutation;
import garuntimeenv.interfaces.*;
import garuntimeenv.runtimeexceptions.CannotRepairException;
import garuntimeenv.utils.MyLogger;
import garuntimeenv.utils.Pair;
import org.apache.log4j.lf5.LogLevel;

import java.util.*;

/**
 * Class managing the genetic algorithm
 */
public class GAManager {

    // Genetic operator
    private IProblem problem;
    private IProblemRepresentation rep;
    private IFitnessFunction fitnessFunction;
    private Selection selection;
    private List<CrossoverOperators> crossoverOperators;
    private List<IMutation> mutationOperators;

    // Holds the populations and manages
    private List<Population> populations = new ArrayList<>();
    private int genCounter = 0;

    final static MyLogger logger = MyLogger.getLogger(GAManager.class);
    final static Random rand = new Random(TestManager.getSeed());

    // HashSet containing the elements of the first generated chromosome
    private HashSet<String> elementOfChromosome = new HashSet<>();

    // The best overall generated chromosome
    private Chromosome bestChromosome;

    // The data manager instance and environment configuration
    private static DataManager dataManager = DataManager.getInstance();
    private static EnvConfig envConfig = EnvConfig.getInstance();

    // The hall of fame data structure
    private HallOfFame hallOfFame;

    // The used hyper parameter as configuration object
    private Config config;

    // Singleton pattern of the ga manager
    private static GAManager INSTANCE;

    /**
     * Getter for the ga manager instance
     * @return The current ga manager instance
     */
    public static GAManager getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Constructor that sets initilizes the ga manager
     *
     * @param problem The used problem
     * @param config The used configuration
     */
    public GAManager(IProblem problem, Config config) {
        this.problem = problem;
        this.config = config;
        EnvConfig.getInstance().setGaManager(this);
        this.initializeManager();
        INSTANCE = this;
    }

    /**
     * Initialize the manager and load the genetic operator from the configuration
     */
    private void initializeManager() {
        // Get the operates saved in the config
        this.rep = this.config.getRepresentation();
        this.fitnessFunction = this.config.getFitnessFunction();
        this.selection = this.config.getPrevChromosomeSelection();
        this.mutationOperators = this.config.getMutationOperators();
        this.crossoverOperators = this.config.getCrossoverOperators();

        this.hallOfFame = new HallOfFame(config.getFitnessFunction(), config.getHallOfFameSize());
        this.genCounter = 0;
    }

    /**
     * Start the genetic runtime environment
     */
    public void startEnvironment() {
        createInitialPopulation();
        for (int generationCounter = 0; generationCounter < TestManager.currMaxGenerations; generationCounter++) {
            if (EnvConfig.getInstance().isPaused() && EnvConfig.getInstance().isVisualEnabled()) {
                generationCounter--;
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                evaluateFitnessOfPopulation(getLastGeneration());
                createNextGeneration();
                if (this.config.isCheckForCorrectness() && !checkForCorrectChromosomes(getLastGeneration())) {
                    break;
                }
                this.genCounter++;
            }
        }
    }

    /**
     * Check if the population
     */
    public boolean checkForCorrectChromosomes(Population population) {
        for (Chromosome genome : population.getChromosomes()) {
            for (Genome g : genome.getGenome())
                if (!Arrays.asList(g.getString()).containsAll(elementOfChromosome))
                    return false;
        }
        return true;
    }

    /**
     * Create the next generation depending on the last one
     */
    private void createNextGeneration() {
        selection.addNewPopulation(getLastGeneration().getChromosomes(), config.getFitnessFunction());
        ArrayList<Chromosome> nextGen = new ArrayList<>();

        // Create inherited chromosomes and mutate all current one
        this.createInheritedChromosomes(nextGen);
        this.createMutatedChromosomes(nextGen);

        // Fill the remaining spaces with random chromosomes
        while (nextGen.size() < this.config.getPopulationSize()) {
            try {
                nextGen.add(rep.createRandomRep((IProblem) this.problem.clone()));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // Add newly created generation to the population list
        this.populations.add(
                new Population(
                        nextGen.toArray(
                                new Chromosome[0]
                        )
                )
        );

        // If in memory safe mode keep the population list of size 20
        if (envConfig.isMemSafeMode() && this.populations.size() > 20) {
            this.populations.remove(0);
        }
    }

    /**
     * Select by the rules of the current selection algorithm two chromosomes and apply crossover operations to
     * create a offspring chromosome
     *
     * @param chromosomes The chromosome in which the offspring's have to be filled in
     */
    private void createInheritedChromosomes(ArrayList<Chromosome> chromosomes) {
        // Check if there are crossover operators are defined
        if (crossoverOperators.isEmpty())
            return;

        int amount = (int) (this.config.getPopulationSize() * this.config.getCrossoverChromosomes());

        for (int i = 0; i < amount; i++) {
            Pair<Chromosome, Chromosome> parents = selectParentChromosome();
            CrossoverOperators crossOpp = crossoverOperators.get(rand.nextInt(crossoverOperators.size()));
            Chromosome offspring = crossOpp.createOffspring(parents.getKey(), parents.getValue());

            chromosomes.add(offspring);
        }
    }

    /**
     * Creates new chromosomes from either the previous generation
     * with the set mutation probability to every chromosome in {@code chromosomes}
     *
     * @param chromosomes The chromosome in which the offspring's have to be filled in
     */
    private void createMutatedChromosomes(ArrayList<Chromosome> chromosomes) {
        if (mutationOperators.isEmpty())
            return;

        int amount = (int) (this.config.getPopulationSize() * this.config.getMutationChromosomes());

        for (int i = 0; i < amount; i++) {
            Chromosome selectedChromosome = this.selection.getNextChromosome();
            Mutation mutation = (Mutation) this.mutationOperators.get(rand.nextInt(this.mutationOperators.size()));
            Chromosome offspring = mutation.applyMutation(selectedChromosome);
            chromosomes.add(offspring);
        }
    }


    /**
     * Select two chromosomes in terms with the selected selection algorithm and return them as Pair
     *
     * @return The pair of chromosomes
     */
    private Pair<Chromosome, Chromosome> selectParentChromosome() {
        // Select the first parent with the probability stored in the config either
        // from the hall of fame or the prev generation
        double selectParentFromHallOfFame1 = rand.nextDouble();
        Chromosome parent1 = null;
        if (selectParentFromHallOfFame1 < this.config.getHallOfFamePercentage())
            parent1 = this.hallOfFame.getRandomChromosome();
        else
            parent1 = this.selection.getNextChromosome();

        // Same with the second
        Chromosome parent2 = null;
        double selectParentFromHallOfFame2 = rand.nextDouble();
        if (selectParentFromHallOfFame2 < this.config.getHallOfFamePercentage())
            parent2 = this.hallOfFame.getRandomChromosome();
        else
            parent2 = this.selection.getNextChromosome();

        return new Pair<>(parent1, parent2);
    }

    /**
     * Create the initial population with random chromosomes
     */
    private void createInitialPopulation() {
        Chromosome[] chromosomes = new Chromosome[this.config.getPopulationSize()];
        for (int i = 0; i < this.config.getPopulationSize(); i++) {
            try {
                chromosomes[i] = rep.createRandomRep((IProblem) this.problem.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        Population population = new Population(chromosomes);
        this.populations.add(population);

        // Add each element of initial population into the hashset to be checked
        for (Chromosome chromosome : population.getChromosomes())
            for (Genome genome : chromosome.getGenome())
                this.elementOfChromosome.addAll(Arrays.asList(genome.getString()));
    }

    /**
     * Creates the solution from the chromosomes in this population
     * and calculates the fitness of the solution
     *
     * @param population to be calculated the fitness from
     */
    void evaluateFitnessOfPopulation(Population population) {
        Double commulativeFitness = 0.;
        Number currentFitness = config.getFitnessFunction().getWorstFitness();
        Chromosome currentBestChromosome = null;

        population.getChromosomesAsList().parallelStream().forEach(chromosome -> {
            try {
                ISolution createdSolution = rep.createSolutionFromChromosome(chromosome, (IProblem) problem.clone());
                chromosome.setCorrespondingSolution(createdSolution);
                chromosome.calculateFitness(this.fitnessFunction);
            } catch (Exception e) {
                // If something goes wrong while decoding the chromosome try to repair it
                // Otherwise penalize the chromosome by giving it the worst possible fitness
                try {
                    rep.repairChromosome(chromosome);
                    ISolution createdSolution = rep.createSolutionFromChromosome(chromosome, (IProblem) problem.clone());
                    chromosome.setCorrespondingSolution(createdSolution);
                    chromosome.calculateFitness(this.fitnessFunction);
                } catch (CannotRepairException ce) {
                    logger.log(LogLevel.ERROR, "GaManager", "Couldn't repair the chromosome");
                    chromosome.setFitness(this.fitnessFunction.getWorstFitness());
                } catch (Exception ex) {
                    logger.log(LogLevel.ERROR, "GaManager", "Tried to repair chromosome but did't work");
                    chromosome.setFitness(this.fitnessFunction.getWorstFitness());
                }
            }
        });

        for (Chromosome chromosome : population.getChromosomes()) {
            // If the fitness is already calculated
            Number fitness = chromosome.getFitness();

            this.hallOfFame.addChromosome(chromosome);
            if (this.fitnessFunction.isBetterSolution(fitness, currentFitness)) {
                currentBestChromosome = chromosome;
                currentFitness = fitness;
            }

            commulativeFitness += fitness.doubleValue();
        }

        commulativeFitness /= this.config.getPopulationSize();

        if (this.bestChromosome == null ||
                this.fitnessFunction.isBetterSolution(currentFitness, this.bestChromosome.getFitness())) {
            this.bestChromosome = currentBestChromosome;
        }

        assert this.bestChromosome != null;
        dataManager.addDataPoint("Best Fitness", this.genCounter, this.bestChromosome.getFitness());
        dataManager.addDataPoint("Hall Of Fame", this.genCounter, this.hallOfFame.getHallOfFameAverage());
//        if(this.genCounter > 5)
//            dataManager.addDataPoint("Worst Hall Of Fame", this.genCounter, this.hallOfFame.getWorstChromosome().getFitness());
        dataManager.addDataPoint("Current Fitness", this.genCounter, currentFitness, true);
        dataManager.addDataPoint("Kumulative Fitness", this.genCounter, commulativeFitness, true);
    }

    /**
     * Returns the last population in the population list
     *
     * @return Last Population
     */
    private Population getLastGeneration() {
        return this.populations.get(this.populations.size() - 1);
    }

    /**
     * Getter for the current configuration
     *
     * @return The currently used configuration
     */
    public Config getCurrentConfig() {
        return this.config;
    }
}
