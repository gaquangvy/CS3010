import java.util.Scanner;

import static java.lang.Math.*;

/********************************************
 Name: Marvin-Calvin (Vy) Nguyen
 Course: CS 3010
 Instructor: Dr. Lajpat Rai Raheja
 Programming Project #3
 ********************************************/

public interface FindingRoot {
    double methodBisection(Functions f, double left, double right);
    double methodFalsi(Functions f, double left, double right);
    double methodNewtonRaphson(Functions f, double x);
    double methodSecant(Functions f, double x, double xLast);
    double methodModSecant(Functions f, double x, double modValue);

    public static void main(String[] args) {
        System.out.println("Choose an equation below:");
        System.out.println("\t1) 2x^3 – 11.7x^2 + 17.7x – 5 = 0");
        System.out.println("\t2) x + 10 – x*cosh(50/x) = 0");
        System.out.println("You choose ");
        Scanner in = new Scanner(System.in);
        Functions func = new Functions(in.nextInt());
        System.out.println("Your function is\n" + func);

        if (func.checkAvailable()) {
            System.out.println();
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
                result = 6 * pow(x, 2) - 11.7 * x + 17.7;
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
        return !(func == Function.NONE);
    }

    public String toString() {
        return "\tf(x) = " + funcStream + "\n\tf'(x) = " + funcPrimeStream;
    }
}

class Methods implements FindingRoot {
    //Methods
    public double methodBisection(Functions f, double left, double right) {
        return 0.0;
    }

    public double methodFalsi(Functions f, double left, double right) {
        return 0.0;
    }

    public double methodNewtonRaphson(Functions f, double x) {
        return 0.0;
    }

    public double methodSecant(Functions f, double x, double xLast) {
        return 0.0;
    }

    public double methodModSecant(Functions f, double x, double modValue) {
        return 0.0;
    }

    public static void main(String[] args) {
        
    }
}