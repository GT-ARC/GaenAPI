package garuntimeenv.gacomponents.jobshop.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Machine {

    private int id;
    private List<Operation> operations;

    public Machine(int id) {
        this.id = id;
        this.operations = new LinkedList<>();
    }

    public Machine(List<Operation> operations) {
        this.operations = operations;
    }

    public void insertOperationFirstFitting(Operation op) {
        int beginning = 0;
        int position = 0;
        int earliestPossibleBeginning = op.getPriorOperation() == null ?
                0 : op.getPriorOperation().getBeginning() + op.getPriorOperation().getDuration();

        // Get possible beginning point
        for (Operation inListOp : operations) {
            // Check if current operation starts immediately
            if (inListOp.getBeginning() == beginning || earliestPossibleBeginning > inListOp.getBeginning()) {
                beginning = inListOp.getBeginning() + inListOp.getDuration();
                position += 1;
                continue;
            }


            int tempBeginning = beginning;
            if (earliestPossibleBeginning > beginning && earliestPossibleBeginning < inListOp.getBeginning()) {
                tempBeginning = earliestPossibleBeginning;
            }

            // Check if current operation fits in between current and prev
            int timeBetween = inListOp.getBeginning() - tempBeginning;
            // it fits
            if (op.getDuration() < timeBetween)
                break;

            // Doesn't fit
            beginning = inListOp.getBeginning() + inListOp.getDuration();
            position += 1;
        }

        int startTime = beginning < earliestPossibleBeginning ? earliestPossibleBeginning : beginning;

        // Insert operation into operation list
        op.setBeginning(startTime);
        this.operations.add(position, op);

//        System.out.print("Scheduled operation:  (Job: "
//                + op.getJob().getId() + "{" + op.getOrder() + "} auf machine " + ((this.id + "").length() == 1 ? " " + this.id : this.id) +
//                "{ " + this.operations.size() + "}) ");
//        if (position != 0){
//            Operation currOperation = this.operations.get(position - 1);
//            System.out.print(" Prev: [ " + (position - 1) + ": " + currOperation.getBeginning() + " + "
//                    + currOperation.getDuration() + " -> "
//                    + (currOperation.getBeginning() + currOperation.getDuration()) + " ]");
//        }
//        System.out.print(" Curr: [ " + (position) + ": " + op.getBeginning() + " + "
//                + op.getDuration() + " -> "
//                + (op.getBeginning() + op.getDuration()) + " ]");
//
//        if (position != this.operations.size() - 1) {
//            Operation currOperation = this.operations.get(position + 1);
//            System.out.print(" Next: [ " + (position + 1) + ": " + currOperation.getBeginning() + " + "
//                    + currOperation.getDuration() + " -> "
//                    + (currOperation.getBeginning() + currOperation.getDuration()) + " ]");
//        }
//        System.out.print("\n\n");
    }

    public void addOperationAtEnd(Operation op) {
        Operation lastOp = operations.get(operations.size() - 1);
        if (op.getBeginning() == -1) {
            op.setBeginning(lastOp.getBeginning() + lastOp.getDuration());
            operations.add(op);
            return;
        }

        if (lastOp.getBeginning() + lastOp.getDuration() < op.getBeginning()) {
            operations.add(op);
        }
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        return this.operations.toString();
    }

    @Override
    public int hashCode() {
        return this.operations.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Machine machine = (Machine) o;

        if (id != machine.id) return false;
        return Objects.equals(operations, machine.operations);
    }
}
