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
public class Segment {

    public enum segmentConstraint {
        vertical,
        horizontal,
        fixedLength,
        free
    }
    private segmentConstraint constraint = segmentConstraint.free;
    private double constraintLength;
    private Vertex beginning;
    private Vertex end;

    @Override
    public String toString() {
        return String.format("%s -> %s", beginning.toString(), end.toString());
    }

    public Segment(Vertex beginning, Vertex end) {
        this.beginning = beginning;
        this.end = end;
    }

    public segmentConstraint getConstraint() {
        return constraint;
    }

    public void makeFree() {
        constraint = segmentConstraint.free;
    }

    public void makeHorizontal() {
        constraint = segmentConstraint.horizontal;
    }

    public void makeVertical() {
        constraint = segmentConstraint.vertical;
    }

    public void makeFixedLength(double length) {
        constraint = segmentConstraint.fixedLength;
        constraintLength = length;
    }

    public Vertex getBeginning() {
        return beginning;
    }

    public void setBeginning(Vertex beginning) {
        this.beginning = beginning;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public double getConstraintLength() {
        return constraintLength;
    }
}
