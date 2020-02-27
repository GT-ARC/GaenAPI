package garuntimeenv.gacomponents.geneticoperators.selections;

import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Population;
import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.Selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klass representing a sorted selection
 */
public class SortedSelection implements Selection {

    private List<Chromosome> chromosomes;
    private int index = 0;

    /**
     * Constructor for the sorted selection
     */
    public SortedSelection() {
        this.chromosomes = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewPopulation(Chromosome[] chromosomes, IFitnessFunction fitnessFunction) {
        this.chromosomes.clear();
        this.chromosomes.addAll(Arrays.asList(chromosomes));
        this.chromosomes.sort(fitnessFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Chromosome getNextChromosome() {
        return chromosomes.get(index++ % chromosomes.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Sorted Selection";
    }
}
