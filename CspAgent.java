import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class CspAgent {

    public static void main(String[] args) {
        ConstraintNetwork cn = new ConstraintNetwork();
        parseBcn(cn, args[0]);
        ArrayList<Integer> config = new ArrayList<>(Collections.nCopies(cn.getNumVars(), null));

        if(solveCspBacktracking(cn, config) == null){
            System.out.println("No Solution");
        }
        System.out.println("Solution: " + configToString(cn, config));
    }

    private static String configToString(ConstraintNetwork cn, ArrayList<Integer> config) {
        String configStr = "(";
        for(int i = 0; i < config.size(); i++){
            if(config.get(i) != null){
                configStr += cn.getVarByIndex(i).getName() + "=" + cn.getVarByIndex(i).getValues().get(config.get(i)) + ", ";
            }
        }
        //cleaning up trailing commma
        if(configStr.length() > 2) {
            configStr = configStr.substring(0, configStr.length() - 2);
        }
        configStr += ")";
        return configStr;
    }

    private static Variable getVariable(ConstraintNetwork cn, ArrayList<Integer> config){
        int currentMinimumValue = Integer.MAX_VALUE;
        Variable currentMinLegalValuesVar = null;
        for(int i = 0; i < config.size(); i++) {
            Variable tempVar = cn.getVarByIndex(i);
            if(config.get(i) == null && tempVar.getNumLegalValues() < currentMinimumValue){
                currentMinimumValue = tempVar.getNumLegalValues();
                currentMinLegalValuesVar = tempVar;
            }
        }
        return currentMinLegalValuesVar;
    }

    //removing illegal values from unassigned neighbors
    private static void removeIllegalValues(ConstraintNetwork cn, ArrayList<Integer> config, Variable recentlyAssignedVar) {
        for(int i = 0; i < recentlyAssignedVar.getNumNeighbors(); i++) {
            //checks if neighbor is unassigned
            if(config.get(recentlyAssignedVar.getNeighborIndexes().get(i)) == null) {
                Variable neighborVar = cn.getVarByIndex(recentlyAssignedVar.getNeighborIndexes().get(i));
                ArrayList<String> neighborValues = (ArrayList<String>) neighborVar.getValues();
                ArrayList<String> removedValues = (ArrayList<String>) neighborVar.getRemovedValues();
                for(String val : neighborVar.getValues()) {
                    if(val != null) {
                        config.set(neighborVar.getIndex(), neighborValues.indexOf(val));
                        if(!cn.isConsistent(config)) {
                            removedValues.set(neighborValues.indexOf(val), val);
                            neighborValues.set(neighborValues.indexOf(val), null);
                            neighborVar.setNumLegalValues(neighborVar.getNumLegalValues() - 1);
                        }
                        config.set(neighborVar.getIndex(), null);
                    }
                }
            }
        }
    }

    //recovering values of adjacent variables that were removed due to x assignment
    private static void restoreValues(ConstraintNetwork cn, ArrayList<Integer> config, Variable recentlyBacktrackedVar) {
        for(int i = 0; i < recentlyBacktrackedVar.getNumNeighbors(); i++) {
            //checks if neighbor is unassigned
            if(config.get(recentlyBacktrackedVar.getNeighborIndexes().get(i)) == null) {
                Variable neighborVar = cn.getVarByIndex(recentlyBacktrackedVar.getNeighborIndexes().get(i));
                ArrayList<String> neighborValues = (ArrayList<String>) neighborVar.getValues();
                ArrayList<String> removedValues = (ArrayList<String>) neighborVar.getRemovedValues();
                for(String val : removedValues) {
                    if(val != null) {
                        config.set(neighborVar.getIndex(), removedValues.indexOf(val));

                        if(cn.isConsistent(config)){
                            neighborValues.set(removedValues.indexOf(val), val);
                            removedValues.set(removedValues.indexOf(val), null);
                            neighborVar.setNumLegalValues(neighborVar.getNumLegalValues() + 1);
                        }
                        config.set(neighborVar.getIndex(), null);
                    }
                }
            }
        }
    }

    //config must be an arraylist of null values with the same length as the number of vars in the  constraint network
    private static ArrayList<Integer> solveCspBacktracking(ConstraintNetwork cn, ArrayList<Integer> config) {
        System.out.println("Forward: config = " + configToString(cn, config));
        if(cn.isComplete(config)) {
            return config;
        }

        Variable var = getVariable(cn, config);
        if(var == null) {
            return null;
        }
        System.out.println("At " + var.getName());

        ArrayList<String> values = (ArrayList<String>) var.getValues();
        for(int i = 0; i < values.size(); i++) {
            if(values.get(i) != null) {
                System.out.print("    Check value " + values.get(i) + ": ");
                config.set(var.getIndex(), values.indexOf(values.get(i)));
                if(cn.isConsistent(config)) {
                    //leaving var assigned if legal 
                    System.out.println("assign " + values.get(i));

                    //removing illegal values from unassigned neighbors
                    removeIllegalValues(cn, config, var);

                    ArrayList<Integer> result = solveCspBacktracking(cn, config);
                    if(result != null) {
                        return result;
                    }
                    config.set(var.getIndex(), null);
                    System.out.println("Backtrack to " + var.getName() + ": config = " + configToString(cn, config));
                    //recovering values of adjacent variables that were removed due to x assignment
                    restoreValues(cn, config, var);
                } else {
                    config.set(var.getIndex(), null);
                }
            } else {
                System.out.print("    Check value " + var.getRemovedValues().get(i) + ": ");
                System.out.println("illegal.");
            }
        }

        return null;
    }

    private static void parseBcn(ConstraintNetwork cn, String fileName) {
        try(Scanner scanner = new Scanner(new File(fileName))){
            String line = scanner.nextLine();

            //get number of variables
            cn.setNumVars(Integer.parseInt(line.split(" ")[0]));

            //read in each variable paragraph
            for(int i = 0; i < cn.getNumVars(); i++) {
                parseVariable(cn, scanner, i);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private static void parseVariable(ConstraintNetwork cn, Scanner scanner, int varIndex) {
        String line; 

        scanner.nextLine(); //eat empty line

        //get size of state space for this var
        line = scanner.nextLine();
        int numValues = Integer.parseInt(line.split(" ")[0]);
        
        //get name of variable
        line = scanner.nextLine();
        String name = line.split(" ")[0];

        //get values of variable 
        ArrayList<String> values = new ArrayList<>();
        for(int i = 0; i < numValues; i++) {
            line = scanner.nextLine();
            values.add(line);
        }
        //get neighboring variables
        ArrayList<Integer> neighborIndexes = new ArrayList<>();
        line = scanner.nextLine();
        int numNeighbors = Integer.parseInt(line.split(" ")[0]);
        for(int i = 0; i < numNeighbors; i++) {
            neighborIndexes.add(Integer.parseInt(line.split(" ")[i + 2])); //offset accounts for number of spaces and the first number specifying number of neighbors
        }

        Variable var = new Variable(name, numValues, values, numNeighbors, varIndex, neighborIndexes);
        cn.addVar(var);

        //get constraints
        line = scanner.nextLine();
        int numConstraints = Integer.parseInt(line.split(" ")[0]);
        for(int i = 0; i < numConstraints; i++) {
            parseConstraint(cn, scanner, var);
        }

        scanner.nextLine(); //eat unused coordinate line
    }

    private static void parseConstraint(ConstraintNetwork cn, Scanner scanner, Variable var) {
        String line;

        //parsing index of var with constraint and # of acceptable assigments
        line = scanner.nextLine();
        int indexOfVarConstrainedWith = Integer.parseInt(line.split(" ")[0]);
        int numAcceptableAssigments = Integer.parseInt(line.split(" ")[2]);
        int numLinesOfAssigments = (int) Math.ceil((double) numAcceptableAssigments / 5);


        Constraint constraint = new Constraint(var.getIndex(), var.getName(), indexOfVarConstrainedWith, cn.getVarByIndex(indexOfVarConstrainedWith).getName());

        //reads the list of valid assigments into the constraint
        for(int i = 0; i < numLinesOfAssigments; i++) {
            line = scanner.nextLine();
            for(int j = 0; (j < 5) && (j + 5 * i < numAcceptableAssigments); j++){
                constraint.addAssigment(var.getIndex(), Integer.parseInt(line.split(" ; ")[j].split(" ")[1]), indexOfVarConstrainedWith, Integer.parseInt(line.split(" ; ")[j].split(" ")[0]));
            }
        }
        cn.addConstraint(constraint);
    }
}