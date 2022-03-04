import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Variable {

    private String name;
    private int numValues;
    private int numLegalValues;
    private List<String> values;
    private List<String> removedValues;
    private int numNeighbors;
    private List<Integer> neighborIndexes;
    private int index;

    public Variable(String name, int numValues, List<String> values, int numNeighbors, int index, List<Integer> neighborIndexes){
        this.name = name;
        this.numValues = numValues;
        this.numLegalValues = numValues;
        this.values = values;
        this.removedValues = new ArrayList<>(Collections.nCopies(numValues, null));
        this.numNeighbors = numNeighbors;
        this.neighborIndexes = neighborIndexes;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

	public List<String> getValues() {
		return values;
	}

	public int getNumLegalValues() {
		return numLegalValues;
    }
    
    public void setNumLegalValues(int newNumLegalValues) {
		this.numLegalValues = newNumLegalValues;
    }

	public int getNumNeighbors() {
		return numNeighbors;
    }
    
    public List<Integer> getNeighborIndexes() {
		return neighborIndexes;
	}

	public int getNumValues() {
		return numValues;
    }
    
    public List<String> getRemovedValues(){
        return removedValues;
    }
}
