import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Constraint {

    private HashMap<Integer, String> variables;
    private HashMap<Integer, ArrayList <Integer>> acceptableAssigments;

    public Constraint(int firstVarIndex, String firstVarName, int secondVarIndex, String secondVarName) {
        variables = new HashMap<>();
        variables.put(firstVarIndex, firstVarName);
        variables.put(secondVarIndex, secondVarName);

        acceptableAssigments = new HashMap<>();
        acceptableAssigments.put(firstVarIndex, new ArrayList<>());
        acceptableAssigments.put(secondVarIndex, new ArrayList<>());
    }

    public void addAssigment(int firstVarIndex, int firstValIndex, int secondVarIndex, int secondValIndex) {
        acceptableAssigments.get(firstVarIndex).add(firstValIndex);
        acceptableAssigments.get(secondVarIndex).add(secondValIndex);
    }

    public int getOtherIndex(int index){
        Integer[] keys = variables.keySet().toArray(new Integer[variables.keySet().size()]); 

        if(index == keys[0]) {
            return keys[1];
        } else if(index == keys[1]) {
            return keys[0];
        } else {
            return -1;
        }
    }

	public int getAcceptableAssigment(int assigmentIndex, int varIndex) {
		return acceptableAssigments.get(varIndex).get(assigmentIndex);
	}

	public boolean isLegal(List<Integer> assigment) {
        Integer[] keys = variables.keySet().toArray(new Integer[variables.keySet().size()]); 
        if (assigment.get(keys[0]) != null && assigment.get(keys[1]) != null){
            Boolean legal = false;
            for(int i = 0; i < acceptableAssigments.get(keys[0]).size(); i++) {
                if(acceptableAssigments.get(keys[0]).get(i).equals(assigment.get(keys[0])) && acceptableAssigments.get(keys[1]).get(i).equals(assigment.get(keys[1]))) {
                    legal = true;
                    break;
                }
            }
            return legal;
        } else {
            return true;
        }
	}

    


}
