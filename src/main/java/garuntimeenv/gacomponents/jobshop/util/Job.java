package garuntimeenv.gacomponents.jobshop.util;

import java.util.*;

public class Job {

    private int id;
    private List<Operation> operations;
    private Map<Integer, Operation> operationMap;

    public Job(int id) {
        this.id = id;
        this.operationMap = new HashMap<>();
    }

    public Job(int id, Operation[] operations) {
        this.id = id;
        this.operations = new ArrayList<>(Arrays.asList(operations));

        // Create an machine to operation hash map for O(1) access time
        this.operationMap = new HashMap<>();
        for (Operation op : operations)
            operationMap.put(op.getMachine(), op);
    }

    public Operation getOperationOfMachine(int machineId) {
        return operationMap.get(machineId);
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void setOperations(Operation[] operations) {
        this.operations = new ArrayList<>(Arrays.asList(operations));
        for (Operation op : operations)
            operationMap.put(op.getMachine(), op);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
