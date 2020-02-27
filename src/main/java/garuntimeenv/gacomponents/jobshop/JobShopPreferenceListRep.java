package garuntimeenv.gacomponents.jobshop;

import garuntimeenv.envcomponents.TestManager;
import garuntimeenv.gacomponents.Chromosome;
import garuntimeenv.gacomponents.Genome;
import garuntimeenv.gacomponents.jobshop.util.Job;
import garuntimeenv.gacomponents.jobshop.util.Machine;
import garuntimeenv.gacomponents.jobshop.util.Operation;
import garuntimeenv.interfaces.IProblem;
import garuntimeenv.interfaces.IProblemRepresentation;
import garuntimeenv.interfaces.ISolution;
import garuntimeenv.runtimeexceptions.CannotRepairException;
import garuntimeenv.utils.MyLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

public class JobShopPreferenceListRep implements IProblemRepresentation {

    final static MyLogger logger = MyLogger.getLogger(JobShopPreferenceListRep.class);
    final static Random rand = new Random(TestManager.getSeed());

    @Override
    public ISolution createSolutionFromChromosome(Chromosome rep, IProblem inProblem) throws Exception {

        // Check the correct type of the inserted problem
        if (!(inProblem instanceof JobShopProblem)) {
            throw new IllegalArgumentException("Wrong parameter Type");
        }

        JobShopProblem problem = (JobShopProblem) inProblem;

        // Reset the info
//        problem.getJobs().forEach(job -> job.getOperations().forEach(operation -> operation.setScheduled(false)));
        JobShopSolution solution = new JobShopSolution(problem.getNr_machines());


        List<String>[] chromosome = rep.getGenomeAsListArray();
        int tempCounter = 0;

        boolean allScheduled;

        do {
            int skipPosition = tempCounter / problem.getNr_machines();
            allScheduled = true;
            List<Operation> scheduledOperation = new ArrayList<>();
            for (int i = 0; i < chromosome.length; i++) {
                if (chromosome[i].isEmpty() || skipPosition >= chromosome[i].size())
                    continue;
                // Get the dna of the current chromosome and the first position or if tried previously the next one
                String dna = chromosome[i].get(skipPosition);
                // The dna represents the job thats supposed to be scheduled on the current machine i
                // Go to the problem and check for job (dna) what position the operation is to be scheduled
                Job currentJob = problem.getJobById(Integer.parseInt(dna));
                // Get from the job the to be scheduled operation
                Operation toBeScheduledOperation = currentJob.getOperationOfMachine(i);

                // Check if prior operation is scheduled
                if (toBeScheduledOperation.getPriorOperation() != null &&
                        !toBeScheduledOperation.getPriorOperation().isScheduled()) {
                    tempCounter++;
                    allScheduled = false;
                    continue;
                }

                // The Operation is schedulable and will be put into the first available position in the machine
                Machine machine = solution.getMachine(i);
                machine.insertOperationFirstFitting(toBeScheduledOperation);
                // Only after the current cycle is finished the isScheduled parameter should be set
                scheduledOperation.add(toBeScheduledOperation);
                chromosome[i].remove(skipPosition);
                tempCounter = 0;

                // Check if all elements of this genome is scheduled
                if (!chromosome[i].isEmpty())
                    allScheduled = false;
            }

            for (Operation op : scheduledOperation) {
                op.setScheduled(true);
            }

        } while (!allScheduled);

        return solution;
    }

    /**
     * Randomly generates chromosome that decoded represent the {@code inProblem}
     *
     * @param inProblem The problem that
     */
    @Override
    public <T extends IProblem> Chromosome createRandomRep(T inProblem) {

        if (!(inProblem instanceof JobShopProblem)) {
            throw new IllegalArgumentException("Wrong parameter Type");
        }

        JobShopProblem problem = (JobShopProblem) inProblem;

        int nrMachines = problem.getNr_machines();
        int nrJobs = problem.getNr_jobs();

        List<ArrayList<Operation>> operations = problem.getJobs().stream()
                .map(job -> new ArrayList<>(job.getOperations()))
                .collect(toList());

        Chromosome retChromosome = new Chromosome(nrMachines, nrJobs);
        Genome[] genomes = retChromosome.getGenome();

        for (int i = 0; i < nrMachines; i++) {
            for (int c = 0; c < nrJobs; c++) {
                int selectMachine = rand.nextInt(operations.size());
//                int selectJob = rand.nextInt(operations.get(selectMachine).size());
                int selectJob = 0;
                Operation op = operations.get(selectMachine).remove(selectJob);
                if (operations.get(selectMachine).size() == 0)
                    operations.remove(selectMachine);
                genomes[op.getMachine()].addToEnd(op.getJob().getId() + "");
            }
        }


        return retChromosome;
    }

    /**
     * Inplace repair of the {@Code chromosome}
     *
     * @param chromosome
     * @throws CannotRepairException
     */
    @Override
    public void repairChromosome(Chromosome chromosome) throws CannotRepairException {
        throw new CannotRepairException();
    }
}
