package garuntimeenv.gacomponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Genome {

    private String[] genome;

    private int currentEndPosition = 0;

    public Genome(String[] genome) {
        this.genome = genome;
    }

    public Genome(int genomeLength) {
        genome = new String[genomeLength];
    }

    public void insertIntoGenome(int genomeId, String element) {
        genome[genomeId] = element;
    }

    public boolean addToEnd(String element) {
        if (currentEndPosition >= genome.length)
            return false;

        genome[currentEndPosition++] = element;
        return true;
    }

    public List<String> getMutableListOfGenome() {
        return new ArrayList<>(Arrays.asList(genome));
    }

    public String[] getString() {
        return genome;
    }

    public String getElement(int element) {
        return this.genome[element];
    }

    public void setElement(int pos, String element) {
        if (pos < this.genome.length) {
            this.genome[pos] = element;
        }
    }

    public void setGenome(String[] genome) {
        this.genome = genome;
    }

    public int getGenomeSize() {
        return this.genome.length;
    }

    @Override
    public String toString() {
        return "[" + String.join(",", genome) + "]";
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.genome);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genome genome1 = (Genome) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(genome, genome1.genome);
    }
}
