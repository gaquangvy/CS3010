import java.util.Scanner;

public class GaussianEliminationPivot {
    private LinearEquations equations;
    private int[] l;
    private double[] s;

    public void indexVector() {
        int n = equations.getNumberEquations();
        int[] l = new int[n];
        for (int i = 0; i < n; i++)
            l[i] = i;
    }

    public void sVector() {
        int n = equations.getNumberEquations();
        double[][] e = equations.getMatrix();
        s = new double[n];

        for (int i = 0; i < n; i++) {
            double absoluteMax = 0;
            for (int j = 0; j < n; j++)
                if (Math.abs(e[i][j]) > absoluteMax) absoluteMax = Math.abs(e[i][j]);
            s[i] = absoluteMax;
        }
    }

    public void eliminationSteps() {
        int n = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();
        for (int i = 0; i < n - 1; i++) {
            System.out.println("Step " + (i+1) + ":");
            System.out.print(equations.printMatrix());
            System.out.println("Index Matrix: l = " + l.toString());
            System.out.println("S Matrix: s = " + s.toString());

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
            System.out.println(ratioFraction + " = " + ratio);
        }
    }

    public void swap(int x, int y) {
        x += y;
        y = x - y;
        x -= y;
    }
    public void swap(double x, double y) {
        x += y;
        y = x - y;
        x -= y;
    }

    public static void main(String[] args) {

    }
}

class LinearEquations {
    private double[][] matrix;
    private int numberEquations;

    LinearEquations() {
        Scanner in = new Scanner(System.in);
        System.out.print("How many are linear equations to solve? ");
        numberEquations = in.nextInt();
        if (numberEquations > 10) numberEquations = 10;
        matrix = new double[numberEquations][numberEquations+1];

        for (int i = 0; i < numberEquations; i++) {
            System.out.print("Equation #" + i + ":");
            for (int j = 0; j < numberEquations; j++)
                matrix[i][j] = in.nextDouble();
            System.out.print("b = ");
            matrix[i][numberEquations] = in.nextDouble();
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
        String matrixToString = "";
        for (int i = 0; i < numberEquations; i++) {
            for (int j = 0; j < numberEquations+1; j++)
                matrixToString += "\t" + matrix[i][j];
            matrixToString += "\n";
        }
        return matrixToString;
    }
    public String toString() {
        String matrixToString = "";
        for (int i = 0; i < numberEquations; i++) {
            matrixToString += "(" + matrix[i][0] + ")" + (char)(97);
            for (int j = 1; j < numberEquations; j++)
                matrixToString += " + (" + matrix[i][j] + ")" + (char)(j+97);
            matrixToString += " = " + matrix[i][numberEquations] + "\n";
        }
        return matrixToString;
    }
}