/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import gk1.Segment;

/**
 *
 * @author Kazimierz
 */
public class Vertex {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private boolean fixed = false;
    private Segment beginningOfSegment;
    private Segment endOfSegment;

    public Vertex cloneWithoutSegments() {
        return new Vertex(x, y, z, fixed);
    }

    @Override
    public String toString() {
        return String.format("(%1.0f, %1.0f)", x, y);
    }

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vertex(double x, double y, boolean fixed) {
        this.x = x;
        this.y = y;
        this.fixed = fixed;
    }

    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(double x, double y, double z, boolean fixed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fixed = fixed;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Segment getBeginningOfSegment() {
        return beginningOfSegment;
    }

    public void setBeginningOfSegment(Segment beginningOfSegment) {
        this.beginningOfSegment = beginningOfSegment;
    }

    public Segment getEndOfSegment() {
        return endOfSegment;
    }

    public void setEndOfSegment(Segment endOfSegment) {
        this.endOfSegment = endOfSegment;
    }
}
