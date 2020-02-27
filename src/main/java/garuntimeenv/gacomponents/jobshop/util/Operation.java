package garuntimeenv.gacomponents.jobshop.util;

/**
 * Represents a operation in the job shop scheduling problem
 */
public class Operation {

    private int order;
    private int duration;
    private int machine;
    private int beginning;

    private boolean isScheduled = false;

    private Job parentJob;

    public Operation(Job parentJob, int order, int duration, int machine) {
        this.order = order;
        this.duration = duration;
        this.machine = machine;
        this.parentJob = parentJob;
        this.beginning = -1;
    }

    public Operation(Job parentJob, int order, int duration, int machine, int beginning) {
        this.order = order;
        this.duration = duration;
        this.machine = machine;
        this.beginning = beginning;
        this.parentJob = parentJob;
    }

    public Operation getPriorOperation() {
        if (order == 0)
            return null;
        else
            return this.parentJob.getOperations().get(this.order - 1);
    }

    @Override
    public String toString() {
        return "m" + machine + ":" + parentJob.getId() + "_" + order;
    }

    @Override
    public int hashCode() {
        return beginning ^ machine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (order != operation.order) return false;
        if (duration != operation.duration) return false;
        if (machine != operation.machine) return false;
        if (beginning != operation.beginning) return false;
        if (isScheduled != operation.isScheduled) return false;
        return parentJob.getId() != operation.parentJob.getId();
    }

    public Job getJob() {
        return parentJob;
    }

    public void setJob(Job parentJob) {
        this.parentJob = parentJob;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMachine() {
        return machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public int getBeginning() {
        return beginning;
    }

    public void setBeginning(int beginning) {
        this.beginning = beginning;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }
}
