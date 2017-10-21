/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;

/**
 *
 * @author kazimierz
 */
public class NumericalOptimization {

    public static ArrayList<Double> invert2by2matrix(
            ArrayList<Double> matrix, ArrayList<Double> b) {
        // matrix := [a0, a1; a2, a3]
        // b := [b0; b1]
        // x := A\b
        ArrayList<Double> x = new ArrayList<>();
        double a0 = matrix.get(0);
        double a1 = matrix.get(1);
        double a2 = matrix.get(2);
        double a3 = matrix.get(3);
        double det = a0 * a3 - a1 * a2;
        x.add((a3 * b.get(0) - a1 * b.get(1)) / det);
        x.add((-a2 * b.get(0) + a0 * b.get(1)) / det);
        return x;
    }

    public static double Halley(double x, double f, double prim, double bis) {
        // consider assigning zero to bis as to perform Newton's method
        return x - f * prim / (prim * prim - f * bis / 2);
    }
}
