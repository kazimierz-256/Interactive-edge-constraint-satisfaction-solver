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
public class Euclidean2dGeometry {

    public static double getSquareDistance(Vertex a, Vertex b) {
        double xDistance = a.getX() - b.getX();
        double yDistance = a.getY() - b.getY();
        return xDistance * xDistance
                + yDistance * yDistance;
    }

    // symmetrical
    public static double getSquareDistance(Segment segment, Vertex vertex) {
        return getSquareDistance(vertex, segment);
    }

    public static double getSquareDistance(Vertex vertex, Segment segment) {
        // not implemented...
        double ax = vertex.getX() - segment.getBeginning().getX();
        double ay = vertex.getY() - segment.getBeginning().getY();
        double sx = segment.getEnd().getX() - segment.getBeginning().getX();
        double sy = segment.getEnd().getY() - segment.getBeginning().getY();
        double scale = (ax * sx + ay * sy) / (sx * sx + sy * sy);

        if (scale < 0) {
            return getSquareDistance(vertex, segment.getBeginning());
        } else if (scale * scale > 1) {
            return getSquareDistance(vertex, segment.getEnd());
        } else {
            double sPerpX = ax - scale * sx;
            double sPerpY = ay - scale * sy;
            return sPerpX * sPerpX + sPerpY * sPerpY;
        }
    }

    public static double getSquareLength(Segment segment) {
        double dx = segment.getBeginning().getX() - segment.getEnd().getX();
        double dy = segment.getBeginning().getY() - segment.getEnd().getY();
        return dx * dx + dy * dy;
    }
}
