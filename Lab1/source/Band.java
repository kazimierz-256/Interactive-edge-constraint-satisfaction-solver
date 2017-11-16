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
public class Band {

    private Vertex center;
    private double bandWidth;
    private double bandHeight;

    public Band(Vertex vertex, double bandWidth, double bandHeight) {
        this.center = vertex;
        this.bandWidth = bandWidth;
        this.bandHeight = bandHeight;
    }

    public Vertex getCenter() {
        return center;
    }

    public void setCenter(Vertex center) {
        this.center = center;
    }

    public double getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(double bandWidth) {
        this.bandWidth = bandWidth;
    }

    public double getBandHeight() {
        return bandHeight;
    }

    public void setBandHeight(double bandHeight) {
        this.bandHeight = bandHeight;
    }
}
