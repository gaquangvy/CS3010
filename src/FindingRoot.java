import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.*;

/********************************************
 Name: Marvin-Calvin (Vy) Nguyen
 Course: CS 3010
 Instructor: Dr. Lajpat Rai Raheja
 Programming Project #3
 ********************************************/

public class FindingRoot {
    static final int max_iteration = 100;
    static final String inputMessage = "Please enter starting point: ";

    public static List<double[]> methodBisection(Functions f, double left, double right, double error) {
        //setup
        int i = 0;
        List<double[]> result = new ArrayList<>();
        double a = left, b = right, newError = error, root = a;

        //loop - never divergent
        while (i < max_iteration && f.f(a) != 0 && f.f(b) != 0 && newError >= error) {
            double fa = f.f(a), fb = f.f(b);
            double c = (a + b) / 2, fc = f.f(c);

            newError = abs((root-c)/c);
            result.add(new double[]{a, b, fa, fb, c, fc, 0.0, newError});
            if (fa * fc < 0) b = c;
            else if (fa * fc > 0) a = c;
            else {
                a = c;
                b = c;
            }

            ++i;
            root = c;
        }

        int resultLen = result.size();
        for (int j = 0; j < resultLen; j++) {
            double[] array = result.get(j);
            array[6] = abs((root - array[4]) / root);
            result.set(j, array);
        }

        return result;
    }

    static List<double[]> methodFalsi(Functions f, double left, double right, double error) {
        //setup
        int i = 0;
        List<double[]> result = new ArrayList<>();
        double a = left, b = right, newError = error, root = 0.0;

        while (i < max_iteration && f.f(a) != 0 && f.f(b) != 0 && newError >= error) {
            double fa = f.f(a), fb = f.f(b);
            double c = (a * fb - b * fa) / (fb - fa), fc = f.f(c);
            newError = abs((root-c)/c);
            result.add(new double[]{a, b, fa, fb, c, fc, 0.0, newError});

            if (fa * fc < 0) b = c;
            else if (fa * fc > 0) a = c;
            else {
                a = c;
                b = c;
            }

            ++i;
            root = c;
        }

        int resultLen = result.size();
        for (int j = 0; j < resultLen; j++) {
            double[] array = result.get(j);
            array[6] = abs((root - array[4]) / root);
            result.set(j, array);
        }

        return result;
    }

    static List<double[]> methodNewtonRaphson(Functions f, double x, double error) {
        //setup
        int i = 0;
        List<double[]> result = new ArrayList<>();
        double xi = x, newError = error;

        while (i < max_iteration && f.f(xi) != 0 && f.fp(xi) != 0 && newError >= error) {
            double fx = f.f(xi), fpx = f.fp(xi);
            double xPlus = xi - fx / fpx, fxPlus = f.f(xPlus), fpxPlus = f.fp(xPlus);
            newError = abs((xi-xPlus)/xPlus);
            result.add(new double[]{xi, fx, fpx, xPlus, fxPlus, fpxPlus, 0.0, newError});

            //warning message and this will stop the loop till the end
            if (fxPlus != 0 && fpxPlus == 0) System.out.println("Divergent");

            ++i;
            xi = xPlus;
        }

        int resultLen = result.size();
        for (int j = 0; j < resultLen; j++) {
            double[] array = result.get(j);
            array[6] = abs((xi - array[3]) / xi);
            result.set(j, array);
        }

        return result;
    }

    static List<double[]> methodSecant(Functions f, double x, double xLast, double error) {
        //setup
        int i = 0;
        List<double[]> result = new ArrayList<>();
        double newError = error;
        double xi = x, xMinus = xLast;

        while (i < max_iteration && f.f(xi) != 0 && f.fp(xi) != 0 && newError >= error) {
            double fxi = f.f(xi), fxMinus = f.f(xMinus);
            double xPlus = xi - (xi - xMinus) / (1 - fxMinus / fxi), fxPlus = f.f(xPlus);
            newError = abs((xi-xPlus)/xPlus);

            ++i;
            result.add(new double[]{xi, xMinus, fxi, fxMinus, xPlus, fxPlus, 0.0, newError});
            xMinus = xi;
            xi = xPlus;
        }

        int resultLen = result.size();
        for (int j = 0; j < resultLen; j++) {
            double[] array = result.get(j);
            array[6] = abs((xi - array[4]) / xi);
            result.set(j, array);
        }

        return result;
    }

    static List<double[]> methodModSecant(Functions f, double x, double modValue, double error) {
        //setup
        int i = 0;
        List<double[]> result = new ArrayList<>();
        double newError = error, xi = x;

        while (i < max_iteration && f.f(xi) != 0 && xi != 0 && newError >= error) {
            double fxi = f.f(xi), fxMod = f.f(xi + modValue * xi);
            double xPlus = xi - fxi * modValue * xi /(fxMod - fxi), fxPlus = f.f(xPlus);
            newError = abs((xi-xPlus)/xPlus);

            ++i;
            result.add(new double[]{xi, fxi, fxMod, xPlus, fxPlus, 0.0, newError});
            xi = xPlus;
        }

        int resultLen = result.size();
        for (int j = 0; j < resultLen; j++) {
            double[] array = result.get(j);
            array[5] = abs((xi - array[3]) / xi);
            result.set(j, array);
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Choose an equation below:");
        System.out.println("\t1) 2x^3 – 11.7x^2 + 17.7x – 5 = 0");
        System.out.println("\t2) x + 10 – x*cosh(50/x) = 0");
        System.out.print("You choose ");
        Scanner in = new Scanner(System.in);
        Functions func = new Functions(in.nextInt());
        System.out.println("Your function is\n" + func);

        if (func.checkAvailable()) {
            System.out.print("Please enter an error to stop loop iteration: ");
            double error = in.nextDouble();
            long timePunch = System.currentTimeMillis();

            //initialize file and bisection method
            FileWriter file = new FileWriter("table_" + timePunch + ".txt");

            System.out.println("\n===================Bisection Method===================\n");
            System.out.print(inputMessage + "a = ");
            double a = in.nextDouble();
            System.out.print(inputMessage + "b = ");
            double b = in.nextDouble();
            List<double[]> result = methodBisection(func, a, b, error);
            int n = result.size();

            file.write("Bisections\tTrue\tError\n");
            for (int i = 0; i < n; ++i) {
                double[] arr = result.get(i);
                file.write((i+1) + "\t");
                file.write(arr[6] + "\t");
                file.write(arr[7] + "\n");
            }
            file.write("\n");

            double root = result.get(n-1)[4];
            System.out.println("Data has written to bisection.txt!");
            System.out.println("Number of iterations: " + n);
            System.out.printf("Root is %.5f%n", root);

            //falsi method
            System.out.println("\n===================False Position Method===================\n");
            System.out.print(inputMessage + "a = ");
            a = in.nextDouble();
            System.out.print(inputMessage + "b = ");
            b = in.nextDouble();
            result = methodFalsi(func, a, b, error);
            n = result.size();

            file.write("Falsi\tTrue\tError\n");
            for (int i = 0; i < n; ++i) {
                double[] arr = result.get(i);
                file.write((i+1) + "\t");
                file.write(arr[6] + "\t");
                file.write(arr[7] + "\n");
            }
            file.write("\n");

            root = result.get(n-1)[4];
            System.out.println("Data has written to bisection.txt!");
            System.out.println("Number of iterations: " + n);
            System.out.printf("Root is %.5f%n", root);

            //Newton Raphson method
            System.out.println("\n===================Newton Raphson Method===================\n");
            System.out.print(inputMessage + "x(0) = ");
            a = in.nextDouble();
            result = methodNewtonRaphson(func, a, error);
            n = result.size();

            file.write("NewtonRaphson\tTrue\tError\n");
            for (int i = 0; i < n; ++i) {
                double[] arr = result.get(i);
                file.write((i+1) + "\t");
                file.write(arr[6] + "\t");
                file.write(arr[7] + "\n");
            }
            file.write("\n");

            root = result.get(n-1)[3];
            System.out.println("Data has written to bisection.txt!");
            System.out.println("Number of iterations: " + n);
            System.out.printf("Root is %.5f%n", root);

            //Secant method
            System.out.println("\n===================Secant Method===================\n");
            System.out.print(inputMessage + "x(0) = ");
            a = in.nextDouble();
            System.out.print(inputMessage + "x(1) = ");
            b = in.nextDouble();
            result = methodSecant(func, a, b, error);
            n = result.size();

            file.write("Secant\tTrue\tError\n");
            for (int i = 0; i < n; ++i) {
                double[] arr = result.get(i);
                file.write((i+1) + "\t");
                file.write(arr[6] + "\t");
                file.write(arr[7] + "\n");
            }
            file.write("\n");

            root = result.get(n-1)[4];
            System.out.println("Data has written to bisection.txt!");
            System.out.println("Number of iterations: " + n);
            System.out.printf("Root is %.5f%n", root);

            //secant mod method
            System.out.println("\n===================Modified Secant Method===================\n");
            System.out.print(inputMessage + "x(0) = ");
            a = in.nextDouble();
            System.out.print(inputMessage + "mod = ");
            b = in.nextDouble();
            result = methodModSecant(func, a, b, error);
            n = result.size();

            file.write("ModSecant\tTrue\tError\n");
            for (int i = 0; i < n; ++i) {
                double[] arr = result.get(i);
                file.write((i+1) + "\t");
                file.write(arr[5] + "\t");
                file.write(arr[6] + "\n");
            }
            file.write("\n");

            root = result.get(n-1)[3];
            System.out.println("Data has written to bisection.txt!");
            System.out.println("Number of iterations: " + n);
            System.out.printf("Root is %.5f%n", root);
            file.close();
        }
    }
}

class Functions {
    enum Function {
        NONE,
        FIRST,
        SECOND
    }
    private final Function func;
    private final String funcStream;
    private final String funcPrimeStream;

    Functions(int funcNum) {
        switch (funcNum) {
            case 1:
                func = Function.FIRST;
                funcStream = "2x^3 – 11.7x^2 + 17.7x – 5";
                funcPrimeStream = "6x^2 - 23.4x + 17.7";
                break;
            case 2:
                func = Function.SECOND;
                funcStream = "x + 10 – x*cosh(50/x)";
                funcPrimeStream = "1 + cosh(50/x) - (50/x)sinh(50/x)";
                break;
            default:
                func = Function.NONE;
                funcStream = "No function found!";
                funcPrimeStream = "No prime function found!";
        }
    }

    public double f(double x) {
        double result;
        switch (func) {
            case FIRST:
                result = 2 * pow(x, 3) - 11.7 * pow(x, 2) + 17.7 * x - 5;
                break;
            case SECOND:
                result = x + 10 - x * cosh(50/x);
                break;
            default:
                result = Double.NaN;
        }
        return result;
    }

    public double fp(double x) {
        double result;
        switch (func) {
            case FIRST:
                result = 6 * pow(x, 2) - 23.4 * x + 17.7;
                break;
            case SECOND:
                result = 1 + cosh(50/x) - (50/x)*sinh(50/x);
                break;
            default:
                result = Double.NaN;
        }
        return result;
    }

    public boolean checkAvailable() {
        return func != Function.NONE;
    }

    public String toString() {
        return "\tf(x) = " + funcStream + "\n\tf'(x) = " + funcPrimeStream;
    }
}