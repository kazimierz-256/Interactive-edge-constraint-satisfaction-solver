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
public class Area {

    private boolean promotedToBand = false;
    private Ring ring;
    private Band band;

    public Area(Vertex center) {
        ring = new Ring(center, 0, 0);
    }

    public Area(Ring ring) {
        this.ring = ring;
    }

    public Area(Band band) {
        promotedToBand = true;
        this.band = band;
    }

    public Area generalize(Segment segment) {
        if (promotedToBand) {
            switch (segment.getConstraint()) {
                case free:
                    return new Area(new Band(new Vertex(0, 0),
                            Double.POSITIVE_INFINITY,
                            Double.POSITIVE_INFINITY));
                case horizontal:
                    return new Area(new Band(band.getCenter(),
                            Double.POSITIVE_INFINITY,
                            band.getBandHeight()));
                case vertical:
                    return new Area(new Band(band.getCenter(),
                            band.getBandWidth(),
                            Double.POSITIVE_INFINITY));
                case fixedLength:
                    double width = band.getBandWidth()
                            + segment.getConstraintLength();
                    double height = band.getBandHeight()
                            + segment.getConstraintLength();
                    return new Area(new Band(band.getCenter(), width, height));
            }
        } else {
            switch (segment.getConstraint()) {
                case free:
                    return new Area(new Band(new Vertex(0, 0),
                            Double.POSITIVE_INFINITY,
                            Double.POSITIVE_INFINITY));
                case horizontal:
                    return new Area(new Band(ring.getCenter(),
                            Double.POSITIVE_INFINITY,
                            ring.getLargerRadius()));
                case vertical:
                    return new Area(new Band(ring.getCenter(),
                            ring.getLargerRadius(),
                            Double.POSITIVE_INFINITY));
                case fixedLength:
                    if (promotedToBand) {
                        double R = segment.getConstraintLength();
                        double r = ring.getSmallerRadius();
                        double d = ring.getLargerRadius();
                        if (R < ring.getSmallerRadius()) {
                            return new Area(new Ring(ring.getCenter(),
                                    r - R, d + R));
                        } else {
                            if (R < r + d) {
                                return new Area(new Ring(ring.getCenter(),
                                        0, d + R));
                            } else {
                                return new Area(new Ring(ring.getCenter(),
                                        R - d - r, d + R));
                            }
                        }
                    }
            }
        }
        // should never reach this line
        return null;
    }

    public boolean isContaining(Vertex vertex) {
        if (promotedToBand) {
            boolean withinX = abs(band.getCenter().getX() - vertex.getX()) <= band.getBandWidth();
            boolean withinY = abs(band.getCenter().getY() - vertex.getY()) <= band.getBandHeight();
            return withinX && withinY;
        } else {
            boolean outsideInnerShell = Euclidean2dGeometry.getSquareDistance(ring.getCenter(), vertex) >= ring.getSmallerRadius() * ring.getSmallerRadius();
            boolean insideOuterShell = Euclidean2dGeometry.getSquareDistance(ring.getCenter(), vertex) <= ring.getLargerRadius() * ring.getLargerRadius();
            return outsideInnerShell && insideOuterShell;
        }
    }

    public Vertex getClosestPoint(Vertex starting, Vertex target, Segment segment) {
        //choose closest intersection to target
        return null;
    }

    public Vertex getClosestPoint(Area areaToIntersect, Vertex target) {
        // a little bit tough...
        // implement later...
        return null;
    }
}
