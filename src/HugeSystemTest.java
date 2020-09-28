import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class HugeSystemTest {
    public static void main(String[] args) throws IOException {
        Random rand = new Random();
        FileWriter testRecords = new FileWriter("test_records.txt");
        testRecords.write("n\tGaussian Method with SSP\tJacobi Method\tGauss-Seidel\n");
        for (int n = 50; n < 1001; n+=10) {
            //Creating matrix and equations
            testRecords.write(n + "\t");
            double[][] matrix = new double[n][n+1];
            for (int x = 0; x < n; x++) {
                double sum = 0.0;
                for (int y = 0; y <= n; y++) {
                    matrix[x][y] = (rand.nextBoolean() ? -1 : 1) * rand.nextDouble() * 10; //significantly low chance to get 0 while finding a random value
                    if (y != x && y != n) sum += abs(matrix[x][y]);
                }

                if (abs(matrix[x][x]) < abs(sum)) matrix[x][x] += ((matrix[x][x] < 0) ? -1 : 1) * sum;
            }
            //matrix must be convergent
            GaussianTest g = new GaussianTest(matrix);

            //time measurement for Gaussian with SSP
            testRecords.write(g.testElimination() + " \t");
            //time measurement for Jacobi
            testRecords.write(g.testJacobi() + " \t");
            //time measurement for Gauss Seidel with SOR value = 1.0
            testRecords.write(g.testGaussSeidel() + " \n");
        }
        testRecords.close();
    }
}

class GaussianTest {
    private final LinearEquations equations;
    private double[] startingSolution;
    private final double criterionError;
    private final double sor;

    GaussianTest(double [][] matrix) {
        equations = new LinearEquations(matrix);
        startingSolution = new double[equations.getNumberEquations()];
        criterionError = 0.0;
        sor = 1.0;
    }

    public long testElimination() {
        int n = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();
        long start = System.nanoTime() / 1000;

        //Creating s vector
        double[] s;
        s = new double[n];
        for (int i = 0; i < n; i++) {
            double absoluteMax = 0;
            for (int j = 0; j < n; j++)
                if (abs(matrix[i][j]) > absoluteMax) absoluteMax = abs(matrix[i][j]);
            s[i] = absoluteMax;
        }

        //Creating index vector
        int[] l = new int[n];
        for (int i = 0; i < n; i++)
            l[i] = i;

        //Elimination Steps
        for (int i = 0; i < n - 1; i++) {
            //Ratios
            double[] ratio = new double[n];
            double maxRatio = 0.0;
            int maxRatioIndex = i+1;
            for (int j = 0; j < n; j++) {
                int index = l[j];
                ratio[j] = abs(matrix[index][i]) / s[index];
                if (ratio[j] > maxRatio) {
                    maxRatio = ratio[j];
                    maxRatioIndex = j;
                }
            }

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

        //Back Substitution
        List<Double> result = new ArrayList<>();
        for (int i = l.length - 1; i >= 0; i--) {
            int index = l[i];
            double solution = matrix[index][l.length];
            for (int j = i + 1; j < l.length; j++) solution -= matrix[index][j] * result.get(j - i - 1);
            solution /= matrix[index][i];
            result.add(0, solution);
        }

        return System.nanoTime() / 1000 - start;
    }

    public long testJacobi() {
        long start = System.nanoTime() / 1000;

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

        return System.nanoTime() / 1000 - start;
    }

    public long testGaussSeidel() {
        long start = System.nanoTime() / 1000;

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

        return System.nanoTime() / 1000 - start;
    }
}