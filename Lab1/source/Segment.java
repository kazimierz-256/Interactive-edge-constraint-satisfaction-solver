/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.abs;

/**
 *
 * @author Kazimierz
 */
public class Segment {

    Boolean isSafeToRestrictVertical() {
        return (beginning.getEndOfSegment().getConstraint() != segmentConstraint.vertical
                && end.getBeginningOfSegment().getConstraint() != segmentConstraint.vertical);
    }

    Boolean isSafeToRestrictHorizontal() {
        return (beginning.getEndOfSegment().getConstraint() != segmentConstraint.horizontal
                && end.getBeginningOfSegment().getConstraint() != segmentConstraint.horizontal);
    }

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
    private final double extremeThreshold = 20;

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

    public void restrictFree() {
        constraint = segmentConstraint.free;
    }

    public void restrictHorizontal() {
        constraint = segmentConstraint.horizontal;
    }

    public void restrictVertical() {
        constraint = segmentConstraint.vertical;
    }

    public void restrictFixedLength(double length) {
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

    public double getCenterX() {
        return getBeginning().getX() + (getEnd().getX() - getBeginning().getX()) / 2;
    }

    public double getCenterY() {
        return getBeginning().getY() + (getEnd().getY() - getBeginning().getY()) / 2;
    }

    public Vertex getCenter() {
        return new Vertex(getCenterX(), getCenterY());
    }

    public Boolean isAlmostVertical() {
        return abs(end.getY() - beginning.getY())
                > extremeThreshold * abs(end.getX() - beginning.getX());
    }

    public Boolean isAlmostHorizontal() {
        return abs(end.getY() - beginning.getY()) * extremeThreshold
                < abs(end.getX() - beginning.getX());
    }
}
