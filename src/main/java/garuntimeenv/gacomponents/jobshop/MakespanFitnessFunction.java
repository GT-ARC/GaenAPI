package garuntimeenv.gacomponents.jobshop;

import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.jobshop.util.Machine;
import garuntimeenv.gacomponents.jobshop.util.Operation;
import garuntimeenv.interfaces.IFitnessFunction;
import garuntimeenv.interfaces.ISolution;

public class MakespanFitnessFunction implements IFitnessFunction<Integer> {

    @Override
    public Integer calculateFitness(ISolution solution) {
        JobShopSolution jsp;
        if (!(solution instanceof JobShopSolution)) {
            throw new IllegalArgumentException("Wrong parameter Type");
        }
        jsp = (JobShopSolution) solution;
        int makespan = 0;
        for (Machine m : jsp.getMachines().values()) {
            Operation lastOperation = m.getOperations().get(m.getOperations().size() - 1);
            int end = lastOperation.getDuration() + lastOperation.getBeginning();
            if (end > makespan) {
                makespan = end;
            }
        }
        return makespan;
    }

    @Override
    public boolean isBetterSolution(Integer firstFitness, Integer secondFitness) {
        if (secondFitness == -1)
            return true;
        return firstFitness < secondFitness;
    }

    @Override
    public boolean isBetterSolution(Double firstFitness, Double secondFitness) {
        return firstFitness < secondFitness;
    }

    @Override
    public Integer getWorstFitness() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        return Double.compare(o2.getFitness().doubleValue(), o1.getFitness().doubleValue());
    }
}
