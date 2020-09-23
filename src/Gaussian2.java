import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/********************************************
 Name: Vy Nguyen
 Course: CS 3010
 Instructor: Dr. Lajpat Rai Raheja
 Programming Project #2
 ********************************************/
//LinearEquations class taken from GaussianEliminationPivot.java

public class Gaussian2 {
    public static List<double[]> methodJacobi(LinearEquations equations, double[] startingSolution, double criterionError) {
        int numEquations = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();

        //Calculation
        List<double[]> tableIterations = new ArrayList<>();
        int i = 1;
        tableIterations.add(startingSolution);
        double error;
        do {
            double[] newSolution = new double[numEquations];
            double l2NormNumerator = 0.0;
            double l2NormDominator = 0.0;
            for (int j = 0; j < numEquations; j++) {
                newSolution[j] = matrix[j][numEquations];
                for (int k = 0; k < j; k++) {
                    newSolution[j] -= matrix[j][k] * startingSolution[k];
                }
                for (int k = i+1; k < numEquations; k++) {
                    newSolution[j] -= matrix[j][k] * startingSolution[k];
                }
                newSolution[j] /= matrix[j][j];
                l2NormNumerator += Math.pow(newSolution[j] - startingSolution[j], 2);
                l2NormDominator += Math.pow(newSolution[j], 2);
            }
            l2NormNumerator = Math.sqrt(l2NormNumerator);
            l2NormDominator = Math.sqrt(l2NormDominator);
            error = l2NormNumerator/l2NormDominator;
            ++i;
            startingSolution = newSolution;
            tableIterations.add(startingSolution);
        } while (i <= 50 && error >= criterionError);

        return tableIterations;
    }

    public static List<double[]> methodGaussSeidel(LinearEquations equations, double[] startingSolution, double criterionError, double sor) {
        int numEquations = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();

        //Calculation
        List<double[]> tableIterations = new ArrayList<>();
        int i = 1;
        tableIterations.add(startingSolution);
        double error;
        do {
            double[] newSolution = new double[numEquations];
            double l2NormNumerator = 0.0;
            double l2NormDominator = 0.0;
            for (int j = 0; j < numEquations; j++) {
                newSolution[j] = matrix[j][numEquations];
                for (int k = 0; k < j; k++) {
                    newSolution[j] -= matrix[j][k] * newSolution[k];
                }
                for (int k = i+1; k < numEquations; k++) {
                    newSolution[j] -= matrix[j][k] * startingSolution[k];
                }
                newSolution[j] /= matrix[j][j];
                newSolution[j] *= sor;
                newSolution[j] += (1 - sor) * startingSolution[j];
                l2NormNumerator += Math.pow(newSolution[j] - startingSolution[j], 2);
                l2NormDominator += Math.pow(newSolution[j], 2);
            }
            l2NormNumerator = Math.sqrt(l2NormNumerator);
            l2NormDominator = Math.sqrt(l2NormDominator);
            error = l2NormNumerator/l2NormDominator;
            ++i;
            startingSolution = newSolution;
            tableIterations.add(startingSolution);
        } while (i <= 50 && error >= criterionError);

        return tableIterations;
    }

    public static String printArray(double[] arr) {
        StringBuilder matrixToString = new StringBuilder();
        int n = arr.length;
        matrixToString.append("[").append(String.format("%8.4f", arr[0]));
        for (int i = 1; i < n; i++)
            matrixToString.append("\t").append(String.format("%8.4f", arr[i]));
        matrixToString.append("]");
        return matrixToString.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        System.out.print("Please choose your source for inputs ('f'-file or other character-console stream): ");
        boolean fileInput = in.nextLine().charAt(0) == 'f';

        //Equation Inputs
        LinearEquations equations;
        if (fileInput) {
            System.out.print("Enter your file name: ");
            File fin = new File(in.next());
            if (fin.exists()) equations = new LinearEquations(fin);
            else {
                System.out.println("Cannot find a file with the name!!!");
                equations = new LinearEquations(); //create new linear equations
            }
        }
        else equations = new LinearEquations();
        System.out.print("\nEquations:\n" + equations + "\n");

        //Desired Error
        System.out.print("How much error at most to stop iterations? ");
        double error = in.nextDouble();
        //starting solution
        int numEquations = equations.getNumberEquations();
        double[] startingSolution = new double[numEquations];
        System.out.print("Please input starting solutions: ");
        for (int i = 0; i < numEquations; i++)
            startingSolution[i] = in.nextDouble();
        //Jacobi Method
        System.out.println("\n==================Jacobi Method==================\n");
        List<double[]> results = methodJacobi(equations, startingSolution, error);
        for (int i = 0; i < results.size(); i++)
            System.out.println("\tIteration k = " + (i+1) + ":\t" + printArray(results.get(i)));
        System.out.printf("\nThe solution with error <%5.2f is\n", error);
        System.out.println(printArray(results.get(results.size() - 1)));

        //SOR value
        System.out.print("\nHow much sor? ");
        double sor = in.nextDouble();
        //Gauss-Seidel Method (same error)
        System.out.println("\n==================Gauss-Seidel Method==================\n");
        results = methodGaussSeidel(equations, startingSolution, error, sor);
        for (int i = 0; i < results.size(); i++)
            System.out.println("\tIteration k = " + (i+1) + ":\t" + printArray(results.get(i)));
        System.out.printf("\nThe solution with error <%5.2f is\n", error);
        System.out.println(printArray(results.get(results.size() - 1)));
    }
}