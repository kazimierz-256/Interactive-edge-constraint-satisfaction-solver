/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

/**
 *
 * @author Kazimierz
 */
public class Ring {

    private Vertex center;
    private double smallerRadius;
    private double largerRadius;

    public Ring(Vertex vertex, double smallerRadius, double largerRadius) {
        this.center = vertex;
        this.smallerRadius = smallerRadius;
        this.largerRadius = largerRadius;
    }

    public Ring(Vertex vertex, double radius) {
        this(vertex, radius, radius);
    }

    public Vertex getCenter() {
        return center;
    }

    public void setCenter(Vertex center) {
        this.center = center;
    }

    public double getSmallerRadius() {
        return smallerRadius;
    }

    public void setSmallerRadius(double smallerRadius) {
        this.smallerRadius = smallerRadius;
    }

    public double getLargerRadius() {
        return largerRadius;
    }

    public void setLargerRadius(double largerRadius) {
        this.largerRadius = largerRadius;
    }
}
