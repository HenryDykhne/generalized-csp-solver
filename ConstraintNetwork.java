import java.util.ArrayList;
import java.util.List;

public class ConstraintNetwork {
    private int numVars;
    private ArrayList<Variable> variables;
    private ArrayList<Constraint> constraints;


    public ConstraintNetwork() {
        variables = new ArrayList<>();
        constraints = new ArrayList<>();

    }     

	public int getNumVars() {
		return numVars;
    }
    
    public void setNumVars(int numVars) {
		this.numVars = numVars;
	}

	public void addVar(Variable var) {
        variables.add(var);
        //there is probably more to this method than you think 
        //TODO
    }
    
    public Variable getVarByIndex(int index) {
        return variables.get(index);
    }

	public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
	}

    public Boolean isComplete(List<Integer> assigment) {
        Boolean complete = false;
        if(assigment.size() == this.numVars) {
            for(int i = 0; i < this.numVars; i++){
                if(assigment.get(i) == null){
                    return false;
                }
            }
            complete = true;
        }
        return complete;
    }

    public Boolean isConsistent(List<Integer> assigment) {

        for(int i = 0; i < constraints.size(); i++) {
            if(!constraints.get(i).isLegal(assigment)) {
                return false;
            }
        }
        return true;
    }
}
