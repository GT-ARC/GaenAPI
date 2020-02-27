package garuntimeenv.gacomponents.jobshop;

import garuntimeenv.gacomponents.jobshop.util.Job;
import garuntimeenv.gacomponents.jobshop.util.Operation;
import garuntimeenv.interfaces.IProblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobShopProblem extends IProblem {

    private int nr_jobs;
    private int nr_machines;

    private Integer[][] processing_time;
    private Integer[][] machine_sequence;

    private List<Job> jobs;
    private Map<Integer, Job> jobMap;

    @Override
    protected void initProblem() {
        this.setJobs();
    }

    public void setJobs() {
        jobs = new ArrayList<>();
        jobMap = new HashMap<>();
        int machineAmount = this.machine_sequence.length;

        for (int i = 0; i < machineAmount; i++) {

            int jobAmount = machine_sequence[i].length;
            Operation[] operations = new Operation[jobAmount];
            Job currJob = new Job(i);
            for (int c = 0; c < jobAmount; c++) {

                int order = c;
                int duration = processing_time[i][c];
                int machine = machine_sequence[i][c] - 1;

                Operation op = new Operation(currJob, order, duration, machine);
                operations[c] = op;
            }
            currJob.setOperations(operations);
            jobs.add(currJob);
            jobMap.put(currJob.getId(), currJob);
        }
    }

    public Job getJobById(int id) {
        return this.jobMap.get(id);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public int getNr_machines() {
        return nr_machines;
    }

    public void setNr_machines(int nr_machines) {
        this.nr_machines = nr_machines;
    }

    public int getNr_jobs() {
        return nr_jobs;
    }

    public void setNr_jobs(int nr_jobs) {
        this.nr_jobs = nr_jobs;
    }

    public Map<Integer, Job> getJobMap() {
        return jobMap;
    }

    public void setJobMap(Map<Integer, Job> jobMap) {
        this.jobMap = jobMap;
    }
}
