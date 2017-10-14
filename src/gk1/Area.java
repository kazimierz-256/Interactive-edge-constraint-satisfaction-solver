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

    public Area generalize(Segment.segmentConstraint constraint) {
        // anything + free = band
        // ring + length = ring
        // ring + horiz/vert = band
        // band + length = band
        // band + horiz/vert = band
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

    public Vertex getClosestPoint(Area areaToIntersect, Vertex target) {
        // a little bit tough...
        // implement later...
        return null;
    }
}
