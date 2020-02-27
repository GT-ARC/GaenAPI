package garuntimeenv.gacomponents.jobshop;

import garuntimeenv.gacomponents.jobshop.util.Machine;
import garuntimeenv.interfaces.ISolution;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JobShopSolution implements ISolution {
    private Map<Integer, Machine> machines;

    public JobShopSolution(int machineAmount) {
        machines = new HashMap<>();
        for (int i = 0; i < machineAmount; i++) {
            this.machines.put(i, new Machine(i));
        }
    }

    public Machine getMachine(int i) {
        return this.getMachines().get(i);
    }

    public Map<Integer, Machine> getMachines() {
        return machines;
    }

    public void setMachines(Map<Integer, Machine> machines) {
        this.machines = machines;
    }

    @Override
    public String toString() {
        return this.machines.values().toString();
    }

    @Override
    public int hashCode() {
        return this.machines.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobShopSolution that = (JobShopSolution) o;

        return Objects.equals(machines, that.machines);
    }
}
