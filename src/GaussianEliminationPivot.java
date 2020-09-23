/********************************************
Name: Vy Nguyen
 Course: CS 3010
 Instructor: Dr. Lajpat Rai Raheja
 Programming Assignment #1
********************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GaussianEliminationPivot {
    protected static int[] l;

    public static int[] indexVector(LinearEquations equations) {
        int n = equations.getNumberEquations();
        l = new int[n];
        for (int i = 0; i < n; i++)
            l[i] = i;
        return l;
    }

    public static double[] sVector(LinearEquations equations) {
        double[] s;

        int n = equations.getNumberEquations();
        double[][] e = equations.getMatrix();
        s = new double[n];

        for (int i = 0; i < n; i++) {
            double absoluteMax = 0;
            for (int j = 0; j < n; j++)
                if (Math.abs(e[i][j]) > absoluteMax) absoluteMax = Math.abs(e[i][j]);
            s[i] = absoluteMax;
        }

        return s;
    }

    public static double[][] eliminationSteps(LinearEquations equations, double[] s) {
        int n = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();
        for (int i = 0; i < n - 1; i++) {
            System.out.println("\n*************Step k = " + i + "*************");
            System.out.print(equations.printMatrix());
            System.out.println("Index Matrix: l = " + Arrays.toString(l));
            System.out.println("S Matrix: s = " + Arrays.toString(s));

            //Ratios
            System.out.print("Ratio: ");
            double[] ratio = new double[n];
            String[] ratioFraction = new String[n];
            double maxRatio = 0.0;
            int maxRatioIndex = i+1;
            for (int j = 0; j < n; j++) {
                int index = l[j];
                ratio[j] = Math.abs(matrix[index][i]) / s[index];
                if (ratio[j] > maxRatio) {
                    maxRatio = ratio[j];
                    maxRatioIndex = j;
                }
                ratioFraction[j] = Math.abs(matrix[index][i]) + "/" + s[index];
            }
            System.out.println(Arrays.toString(ratioFraction) + " = " + Arrays.toString(ratio));
            System.out.println("Maximum ratio is " + maxRatio + " at j = " + maxRatioIndex);

            //Trading
            if (i != maxRatioIndex) {
                l[maxRatioIndex] += l[i];
                l[i] = l[maxRatioIndex] - l[i];
                l[maxRatioIndex] -= l[i];
            }
            for (int j = i+1; j < n; j++) {
                double x = matrix[l[j]][i] / matrix[l[i]][i];
                for (int k = i; k < n+1; k++) matrix[l[j]][k] -= x * matrix[l[i]][k];
            }
        }

        return matrix;
    }

    public static List<Double> backSubstitution(double[][] matrix) {
        List<Double> result = new ArrayList<>();
        for (int i = l.length - 1; i >= 0; i--) {
            int index = l[i];
            double solution = matrix[index][l.length];
            for (int j = i + 1; j < l.length; j++) solution -= matrix[index][j] * result.get(j - i - 1);
            solution /= matrix[index][i];
            result.add(0, solution);
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner in = new Scanner(System.in);
        System.out.print("Please choose your source for inputs (\'f\'-file or other character-console stream): ");
        boolean fileInput = in.nextLine().charAt(0) == 'f';

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

        l = indexVector(equations); //l - index vector
        double[] s = sVector(equations); //s - vector of maximum coefficients

        LinearEquations gaussian = new LinearEquations(eliminationSteps(equations, s));
        System.out.println("\n*************Last Step: Back Substitution*************");
        System.out.println(gaussian.printMatrix());
        List<Double> result = backSubstitution(gaussian.getMatrix());
        System.out.println("Solutions (by alphabet):");
        for (int i = 0; i < result.size(); i++) {
            System.out.print("\t" + (char)(i + 97) + " = ");
            System.out.printf("%6.3f\n", result.get(i));
        }
    }
}

class LinearEquations {
    private final double[][] matrix;
    private int numberEquations;

    LinearEquations() {
        Scanner in = new Scanner(System.in);
        System.out.print("How many are linear equations to solve? ");
        numberEquations = in.nextInt();
        if (numberEquations > 10) numberEquations = 10;
        matrix = new double[numberEquations][numberEquations+1];

        for (int i = 0; i < numberEquations; i++) {
            System.out.print("Equation #" + i + ": ");
            for (int j = 0; j < numberEquations; j++)
                matrix[i][j] = in.nextDouble();
            matrix[i][numberEquations] = in.nextDouble();
        }
    }

    LinearEquations(double[][] matrix) {
        this.matrix = matrix;
        numberEquations = matrix.length;
    }

    LinearEquations(File filename) throws FileNotFoundException {
        Scanner fin = new Scanner(filename);
        String[] equationInString = fin.nextLine().trim().split(" ");
        numberEquations = equationInString.length - 1;
        matrix = new double[numberEquations][equationInString.length];
        int i = 0;
        for (int j = 0; j < equationInString.length; j++)
            matrix[0][j] = Double.parseDouble(equationInString[j]);

        while (fin.hasNextLine()) {
            ++i;
            equationInString = fin.nextLine().trim().split(" ");
            for (int j = 0; j < equationInString.length; j++)
                matrix[i][j] = Double.parseDouble(equationInString[j]);
        }
    }

    //getters
    public double[][] getMatrix() {
        return matrix;
    }
    public int getNumberEquations() {
        return numberEquations;
    }

    //print equations
    public String printMatrix() {
        StringBuilder matrixToString = new StringBuilder();
        for (int i = 0; i < numberEquations; i++) {
            for (int j = 0; j <= numberEquations; j++)
                matrixToString.append("\t").append(String.format("%6.3f", matrix[i][j]));
            matrixToString.append("\n");
        }
        return matrixToString.toString();
    }
    public String toString() {
        StringBuilder matrixToString = new StringBuilder();
        for (int i = 0; i < numberEquations; i++) {
            matrixToString.append("\t(").append(String.format("%6.3f", matrix[i][0])).append(")").append((char) (97));
            for (int j = 1; j < numberEquations; j++)
                matrixToString.append(" + (").append(String.format("%6.3f", matrix[i][j])).append(")").append((char) (j + 97));
            matrixToString.append(" = ").append(String.format("%6.3f", matrix[i][numberEquations])).append("\n");
        }
        return matrixToString.toString();
    }
}