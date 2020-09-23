import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class HugeSystemTest extends GaussianEliminationPivot {
    public static double[][] eliminationSteps(LinearEquations equations, double[] s) {
        int n = equations.getNumberEquations();
        double[][] matrix = equations.getMatrix();
        for (int i = 0; i < n - 1; i++) {
            //Ratios
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

    public static long test(LinearEquations equations) {
        long start = System.nanoTime();

        l = indexVector(equations); //l - index vector
        double[] s = sVector(equations); //s - vector of maximum coefficients

        LinearEquations gaussian = new LinearEquations(eliminationSteps(equations, s));
        System.out.println(gaussian.printMatrix());
        List<Double> result = backSubstitution(gaussian.getMatrix());

        long finish = System.nanoTime();
        return finish - start;
    }

    public static void main(String[] args) throws IOException {
        Random rand = new Random();
        FileWriter testRecords = new FileWriter("test_records.txt");
        testRecords.write("n\tGaussian Method with SSP\tJacobi Method\tGauss-Seidel\n");
        for (int n = 5; n < 155; n+=5) {
            //Creating matrix and equations
            testRecords.write(n + "\t");
            double[][] matrix = new double[n][n+1];
            for (int x = 0; x < n; x++) {
                for (int y = 0; y <= n; y++) {
                    matrix[x][y] = rand.nextDouble() * 10;
                }
            }
            LinearEquations equations = new LinearEquations(matrix);

            //time measurement for Gaussian with SSP
            long time = test(equations);
            testRecords.write(time + "\t");

            //time measurement for Jacobi
            double error = rand.nextDouble() / 100;
            //starting solution
            int numEquations = equations.getNumberEquations();
            double[] startingSolution = new double[numEquations];
            for (int i = 0; i < numEquations; i++)
                startingSolution[i] = 0.0;
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
}
