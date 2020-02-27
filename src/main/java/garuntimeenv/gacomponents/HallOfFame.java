package garuntimeenv.gacomponents;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.utils.MyLogger;

import java.util.*;

/**
 * Class representing the hall of fame data structure
 */
public class HallOfFame {

    final static MyLogger logger = MyLogger.getLogger(HallOfFame.class);

    private static Random rand = new Random(TestManager.getSeed());

    private PriorityQueue<Chromosome> hallOfFame;       // Priority queue to hold the chromosomes ordered
    private ArrayList<Chromosome> quickAccess;          // HashSet for O(1) request time
    private HashSet<Chromosome> checkForExisting;       // HashSet for O(1) existence check
    private double cumulativeSum = 0;                   // Sum of all chromosomes in the hall of fame
    private final int hallOfFameSize;                   // The size of the data structures used in the hall of fame

    /**
     * Constructor initializing all used data structure representing the hall of fame
     *
     * @param comparator The comparator in most cases the fitness function to keep the prio queue ordered
     * @param hallOfFameSize The size of the hall of fame
     */
    public HallOfFame(Comparator<? super Chromosome> comparator, int hallOfFameSize) {
        this.hallOfFame = new PriorityQueue<>(comparator);
        this.hallOfFameSize = hallOfFameSize;
        this.quickAccess = new ArrayList<>();
        this.checkForExisting = new HashSet<>();
    }

    /**
     * Returns the average fitness of the hall of fame
     *
     * @return The average value of each chromosome in the hall of fame
     */
    public double getHallOfFameAverage() {
        return this.cumulativeSum / this.hallOfFame.size();
    }

    /**
     * Returns a random chromosome from the hall of fame
     *
     * @return The random chromosome
     */
    public Chromosome getRandomChromosome() {
        if (this.quickAccess.size() > 0) {
            return this.quickAccess.get(rand.nextInt(this.quickAccess.size() - 1));
        }
        return null;
    }

    /**
     * Returns the worst chromosome from the hall of fame
     *
     * @return The worst chromosome
     */
    public Chromosome getWorstChromosome() {
        return this.hallOfFame.peek();
    }

    /**
     * Adds the {@code chromosome} if it is better then the worse chromosome in the hall of fame
     *
     * @param chromosome The new chromosome to the hall of fame data structures
     */
    public void addChromosome(Chromosome chromosome) {
        // If chromosome or solution of chromosome is already in the hall of fame dont add it.
        if (this.checkForExisting.contains(chromosome)) {
            logger.info("hallOfFame", "Hall of fame hit");
            return;
        }

        // Check if the hall of fame is full
        if (this.hallOfFame.size() <= hallOfFameSize) {
            this.hallOfFame.add(chromosome);
            this.quickAccess.add(chromosome);
            this.checkForExisting.add(chromosome);
            this.cumulativeSum += chromosome.getFitness().doubleValue();
            return;
        }

        // If the hall of fame isn't full check if the proposed chromosome is better then the worst chromosome
        if (this.hallOfFame.peek() != null &&
                chromosome.getFitness().doubleValue() < this.hallOfFame.peek().getFitness().doubleValue()) {

            // Remove old worst chromosome
            Chromosome toBeRemoved = this.hallOfFame.poll();    // Get the worst
            this.cumulativeSum -= toBeRemoved.getFitness().doubleValue();    // Alter the cumulativeSum
            this.quickAccess.remove(toBeRemoved);
            this.checkForExisting.remove(toBeRemoved);

            // Add new chromosome
            this.hallOfFame.add(chromosome);
            this.quickAccess.add(chromosome);
            this.checkForExisting.add(chromosome);
            this.cumulativeSum += chromosome.getFitness().doubleValue();

//            logger.debug("hallOfFame", "Hall of Fame added : " +
//                    "Size: " + this.hallOfFame.size() + " Sum: " + this.cumulativeSum);
        }
    }
}
