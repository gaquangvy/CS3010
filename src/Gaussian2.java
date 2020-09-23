import java.util.Scanner;

/********************************************
 Name: Vy Nguyen
 Course: CS 3010
 Instructor: Dr. Lajpat Rai Raheja
 Programming Project #2
 ********************************************/
//LinearEquations class taken from GaussianEliminationPivot.java

public class Gaussian2 {
    public static double[] methodJacobi(LinearEquations equations, double criterionError) {
        int numEquations = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();

        //starting solution
        double[] startingSolution = new double[numEquations];
        System.out.print("Please input starting solutions: ");
        Scanner in = new Scanner(System.in);
        for (int i = 0; i < numEquations; i++)
            startingSolution[i] = in.nextDouble();

        //Calculation
        double[][] tableIterations = new double[51][numEquations];
        tableIterations[0] = startingSolution;
        for (int i = 1; i <= 50; i++)
            for (int j = 0; j < numEquations; j++) {
                tableIterations[i][j] = matrix[j][numEquations];
                for (int k = 0; k < j; k++) {
                    tableIterations[i][j] -= matrix[j][k] * tableIterations[i-1][k];
                }
                for (int k = i+1; k < numEquations; k++) {
                    tableIterations[i][j] -= matrix[j][k] * tableIterations[i-1][k];
                }
                tableIterations[i][j] /= matrix[j][j];
            }

        return tableIterations[50];
    }

    public static double[] methodGaussSeidel(LinearEquations equations, double criterionError, double sor) {
        int numEquations = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();

        //starting solution
        double[] startingSolution = new double[numEquations];
        System.out.print("Please input starting solutions: ");
        Scanner in = new Scanner(System.in);
        for (int i = 0; i < numEquations; i++)
            startingSolution[i] = in.nextDouble();
    }

    public static void main(String[] args) {

    }
}