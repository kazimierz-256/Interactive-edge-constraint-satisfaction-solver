/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.sqrt;

/**
 *
 * @author kazimierz
 */
public class EuclideanVector {

    public double x = 0;
    public double y = 0;
    public double z = 0;

    public EuclideanVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void normalize() {
        double length = getLength();
        x /= length;
        y /= length;
        z /= length;
    }

    public double getLength() {
        return sqrt(getSquareLength());
    }

    public double getSquareLength() {
        return x * x + y * y + z * z;
    }

    public void add(EuclideanVector vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
    }

    public void minus(EuclideanVector vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
    }

    public void scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    public double dotProductNormalized(EuclideanVector vector) {
        double productOfSquareLengths = this.getSquareLength() * vector.getSquareLength();
        return (this.x * vector.x + this.y * vector.y + this.z * vector.z)
                / sqrt(productOfSquareLengths);
    }

    public static EuclideanVector fromVertex(Vertex vertex) {
        return new EuclideanVector(vertex.getX(), vertex.getY(), vertex.getZ());
    }

    public void normalizeZ() {
        x /= z;
        y /= z;
        z = 1;
    }
}
