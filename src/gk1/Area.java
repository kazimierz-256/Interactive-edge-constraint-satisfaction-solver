/*
 * To change this license header, choose License Headers in Projection Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author Kazimierz
 */
public class Area {

    // eps co najmniej 2^-30
    private static final double eps = pow(2, -10);
    private boolean isPromotedToBand = false;
    private Ring ring;
    private Band band;

    public Area(Vertex center) {
        ring = new Ring(center, 0, 0);
    }

    public Area(Ring ring) {
        this.ring = ring;
    }

    public Area(Band band) {
        isPromotedToBand = true;
        this.band = band;
    }

    public Area generalize(Segment segment) {
        if (isPromotedToBand) {
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
                    double newWidth = band.getBandWidth()
                            + segment.getConstraintLength();
                    double newHeight = band.getBandHeight()
                            + segment.getConstraintLength();
                    return new Area(new Band(band.getCenter(), newWidth, newHeight));
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
        // should never reach this line
        return null;
    }

    public double containingError(Vertex vertex) {
        double totalError = 0;
        if (isPromotedToBand) {
            double offsetX = abs(band.getCenter().getX() - vertex.getX());
            totalError += offsetX > band.getBandWidth()
                    ? offsetX - band.getBandWidth() : 0;
            double offsetY = abs(band.getCenter().getY() - vertex.getY());
            totalError += offsetY > band.getBandHeight()
                    ? offsetY - band.getBandHeight() : 0;
        } else {
            double distance = Euclidean2dGeometry.getSquareDistance(
                    ring.getCenter(), vertex);
            double smallRSquared = ring.getSmallerRadius() * ring.getSmallerRadius();
            double largeRSquared = ring.getLargerRadius() * ring.getLargerRadius();
            totalError += distance < smallRSquared ? smallRSquared - distance : 0;
            totalError += distance > largeRSquared ? distance - largeRSquared : 0;
        }
        return totalError;
    }

    public boolean isContaining(Vertex vertex) {
        return containingError(vertex) < eps;
    }

    public ArrayList<Vertex> Projection(Vertex vertex) {
        if (isPromotedToBand) {
            return Projection(vertex, band);
        } else {
            return Projection(vertex, ring);
        }
    }

    private static ArrayList<Vertex> Projection(Vertex vertex, Band band) {

        ArrayList<Vertex> list = new ArrayList<>();
        if (Double.isFinite(band.getBandWidth())) {
            // vertical
            double x = vertex.getX() > band.getCenter().getX()
                    ? band.getCenter().getX() + band.getBandWidth()
                    : band.getCenter().getX() - band.getBandWidth();
            list.add(new Vertex(x, vertex.getY()));
        } else if (Double.isFinite(band.getBandHeight())) {
            // horizontal
            double y = vertex.getX() > band.getCenter().getX()
                    ? band.getCenter().getY() + band.getBandHeight()
                    : band.getCenter().getY() - band.getBandHeight();
            list.add(new Vertex(vertex.getX(), y));
        } else {
            // all R
            list.add(vertex);
        }
        return list;
    }

    private static ArrayList<Vertex> Projection(Vertex vertex, Ring ring) {
        ArrayList<Vertex> list = new ArrayList<>();
        // danger if vertex is close to ring's center!
        double quotient = sqrt(Euclidean2dGeometry.getSquareDistance(vertex, ring.getCenter()));
        double cSmaller = ring.getSmallerRadius() / quotient;
        double cLarger = ring.getLargerRadius() / quotient;

        double cx = ring.getCenter().getX();
        double cy = ring.getCenter().getY();
        list.add(new Vertex(cx + cSmaller * (vertex.getX() - cx),
                cy + cSmaller * (vertex.getY() - cy)));
        list.add(new Vertex(cx - cSmaller * (vertex.getX() - cx),
                cy - cSmaller * (vertex.getY() - cy)));
        list.add(new Vertex(cx + cLarger * (vertex.getX() - cx),
                cy + cLarger * (vertex.getY() - cy)));
        list.add(new Vertex(cx - cLarger * (vertex.getX() - cx),
                cy - cLarger * (vertex.getY() - cy)));
        return list;
    }

    private static ArrayList<Vertex> IntersectVertical(double x, Band band) {
        // horizontal finite-height band
        ArrayList<Vertex> list = new ArrayList<>();
        list.add(new Vertex(x, band.getCenter().getY() + band.getBandHeight()));
        list.add(new Vertex(x, band.getCenter().getY() - band.getBandHeight()));
        return list;
    }

    private static ArrayList<Vertex> IntersectHorizontal(double y, Band band) {
        // vertical finite-width band
        ArrayList<Vertex> list = new ArrayList<>();
        list.add(new Vertex(band.getCenter().getX() + band.getBandWidth(), y));
        list.add(new Vertex(band.getCenter().getX() - band.getBandWidth(), y));
        return list;
    }

    private static ArrayList<Vertex> IntersectVertical(double x, Ring ring) {

        ArrayList<Vertex> list = new ArrayList<>();
        double difX = x - ring.getCenter().getX();

        double underSqrt = zeroIfNegligible(
                ring.getSmallerRadius() * ring.getSmallerRadius() - difX * difX);
        if (underSqrt >= 0) {
            double sqrtResult = sqrt(underSqrt);
            list.add(new Vertex(x, ring.getCenter().getY() + sqrtResult));
            list.add(new Vertex(x, ring.getCenter().getY() - sqrtResult));
        }

        underSqrt = zeroIfNegligible(
                ring.getLargerRadius() * ring.getLargerRadius() - difX * difX);
        if (underSqrt >= 0) {
            double sqrtResult = sqrt(underSqrt);
            list.add(new Vertex(x, ring.getCenter().getY() + sqrtResult));
            list.add(new Vertex(x, ring.getCenter().getY() - sqrtResult));
        }

        return list;
    }

    private static ArrayList<Vertex> IntersectHorizontal(double y, Ring ring) {

        ArrayList<Vertex> list = new ArrayList<>();
        double difY = y - ring.getCenter().getY();

        double underSqrt = zeroIfNegligible(
                ring.getSmallerRadius() * ring.getSmallerRadius() - difY * difY);
        if (underSqrt >= 0) {
            double sqrtResult = sqrt(underSqrt);
            list.add(new Vertex(ring.getCenter().getX() + sqrtResult, y));
            list.add(new Vertex(ring.getCenter().getX() - sqrtResult, y));
        }

        underSqrt = zeroIfNegligible(
                ring.getLargerRadius() * ring.getLargerRadius() - difY * difY);
        if (underSqrt >= 0) {
            double sqrtResult = sqrt(underSqrt);
            list.add(new Vertex(ring.getCenter().getX() + sqrtResult, y));
            list.add(new Vertex(ring.getCenter().getX() - sqrtResult, y));
        }

        return list;
    }

    private static ArrayList<Vertex> Intersect(Ring ring, Band band) {
        // either entirely vertical or entirely horizontal band
        ArrayList<Vertex> possibilities = new ArrayList<>();
        if (Double.isFinite(band.getBandWidth())) {
            //vertical band
            possibilities.addAll(IntersectVertical(
                    band.getCenter().getX() + band.getBandWidth(), ring));
            possibilities.addAll(IntersectVertical(
                    band.getCenter().getX() - band.getBandWidth(), ring));
        } else {
            // horizontal band
            possibilities.addAll(IntersectHorizontal(
                    band.getCenter().getY() + band.getBandHeight(), ring));
            possibilities.addAll(IntersectHorizontal(
                    band.getCenter().getY() - band.getBandHeight(), ring));
        }
        return possibilities;
    }

    private static ArrayList<Vertex> IntersectCircle(
            Vertex starting, double constraintLength, Ring ring) {

        ArrayList<Vertex> possibilities = new ArrayList<>();
        possibilities.addAll(IntersectRingsSubroutine(starting, ring.getCenter(),
                constraintLength, ring.getSmallerRadius()));
        possibilities.addAll(IntersectRingsSubroutine(starting, ring.getCenter(),
                constraintLength, ring.getLargerRadius()));
        return possibilities;
    }

    public ArrayList<Vertex> Intersect(Area area) {
        ArrayList<Vertex> possibilities = new ArrayList<>();
        possibilities.addAll(isPromotedToBand
                ? area.Intersect(band) : area.Intersect(ring));
        return possibilities;
    }

    public ArrayList<Vertex> Intersect(Band band) {
        return isPromotedToBand ? Intersect(this.band, band) : Intersect(ring, band);
    }

    public ArrayList<Vertex> Intersect(Ring ring) {
        return isPromotedToBand ? Intersect(ring, band) : Intersect(ring, this.ring);
    }

    private static ArrayList<Vertex> Intersect(Band band1, Band band2) {
        ArrayList<Vertex> possibilities = new ArrayList<>();

        if (Double.isFinite(band1.getBandWidth()) && Double.isFinite(band2.getBandHeight())) {
            //band1 is vertical band2 is horizontal
            possibilities.addAll(IntersectVertical(
                    band1.getCenter().getX() + band1.getBandWidth(), band2));
            possibilities.addAll(IntersectVertical(
                    band1.getCenter().getX() - band1.getBandWidth(), band2));
        } else if (Double.isFinite(band1.getBandHeight()) && Double.isFinite(band2.getBandWidth())) {
            //band1 is horizontal band2 is vertical

            possibilities.addAll(IntersectHorizontal(
                    band1.getCenter().getY() + band1.getBandHeight(), band2));
            possibilities.addAll(IntersectHorizontal(
                    band1.getCenter().getY() - band1.getBandHeight(), band2));
        }
        return possibilities;
    }

    private static ArrayList<Vertex> Intersect(Ring ring1, Ring ring2) {

        ArrayList<Vertex> possibilities = new ArrayList<>();
        Vertex c1 = ring1.getCenter();
        Vertex c2 = ring2.getCenter();
        possibilities.addAll(IntersectRingsSubroutine(c1, c2,
                ring1.getSmallerRadius(), ring2.getSmallerRadius()));
        possibilities.addAll(IntersectRingsSubroutine(c1, c2,
                ring1.getSmallerRadius(), ring2.getLargerRadius()));
        possibilities.addAll(IntersectRingsSubroutine(c1, c2,
                ring1.getLargerRadius(), ring2.getSmallerRadius()));
        possibilities.addAll(IntersectRingsSubroutine(c1, c2,
                ring1.getLargerRadius(), ring2.getLargerRadius()));
        return possibilities;
    }

    private static ArrayList<Vertex> IntersectRingsSubroutine(
            Vertex c1, Vertex c2, double r1, double r2) {

        ArrayList<Vertex> possibilities = new ArrayList<>();
        double a = 2 * (c1.getX() - c2.getX());
        double b = 2 * (c1.getY() - c2.getY());
        double c = c1.getX() * c1.getX() - c2.getX() * c2.getX()
                + c1.getY() * c1.getY() - c2.getY() * c2.getY()
                + r2 * r2 - r1 * r1;
        double q = c - a * c1.getX();
        double a2plusb2 = a * a + b * b;
        double bbq = b * c1.getY() - q;
        double underSqrt = zeroIfNegligible(a2plusb2 * r1 * r1 - bbq * bbq);
        if (underSqrt < 0) {
            return possibilities;
        }
        double sqrtResult = sqrt(underSqrt);
        double y1 = (b * q + c1.getY() * a * a + a * sqrtResult) / a2plusb2;
        double y2 = (b * q + c1.getY() * a * a - a * sqrtResult) / a2plusb2;
        double x1 = (c - b * y1) / a;
        double x2 = (c - b * y2) / a;
        possibilities.add(new Vertex(x1, y1));
        possibilities.add(new Vertex(x2, y2));
        return possibilities;
    }

    public Vertex getClosestPoint(
            Vertex starting, Vertex target, Segment segment, boolean preferCloser) {
        //choose closest intersection to target
        ArrayList<Vertex> possibilities = new ArrayList<>();
        possibilities.add(target);

        if (isPromotedToBand) {
            possibilities.addAll(Projection(target, band));

            switch (segment.getConstraint()) {
                case horizontal:
                    if (Double.isFinite(band.getBandWidth())) {
                        possibilities.addAll(
                                IntersectHorizontal(starting.getY(), band));
                    }
                    break;
                case vertical:
                    if (Double.isFinite(band.getBandHeight())) {
                        possibilities.addAll(
                                IntersectVertical(starting.getX(), band));
                    }
                    break;

                case fixedLength:
                    possibilities.addAll(Intersect(
                            new Ring(starting, segment.getConstraintLength()), band));
                    break;
                case free:
                    possibilities.add(target);
            }
        } else {
            possibilities.addAll(Projection(target, ring));

            switch (segment.getConstraint()) {
                case horizontal:
                    possibilities.addAll(
                            IntersectHorizontal(starting.getY(), ring));
                    break;
                case vertical:
                    possibilities.addAll(
                            IntersectVertical(starting.getX(), ring));
                    break;

                case fixedLength:
                    possibilities.addAll(IntersectCircle(
                            starting, segment.getConstraintLength(), ring));
                    break;
                case free:
                    possibilities.add(target);
            }
        }

        possibilities = filterValidVertices(possibilities, starting, segment);
        return preferCloser ? getClosestPossibility(possibilities, target)
                : getMostAccuratePossibility(possibilities, starting, segment);
    }

    public static Vertex getClosestPossibility(ArrayList<Vertex> possibilities, Vertex target) {
        Vertex best = null;
        double smallestSquareDistance = Double.POSITIVE_INFINITY;
        for (int i = 0, max = possibilities.size(); i < max; i++) {
            // check if vertex is valid!!
            double currentSquareDistance = Euclidean2dGeometry.getSquareDistance(
                    possibilities.get(i), target);
            if (currentSquareDistance < smallestSquareDistance) {
                smallestSquareDistance = currentSquareDistance;
                best = possibilities.get(i);
            }
        }
        return best;
    }

    private Vertex getMostAccuratePossibility(
            ArrayList<Vertex> possibilities, Vertex starting, Segment segment) {
        Vertex best = null;
        double smallestError = Double.POSITIVE_INFINITY;
        for (int i = 0, max = possibilities.size(); i < max; i++) {
            // check if vertex is valid!!
            double currentError = satisfabilityTotalError(
                    possibilities.get(i), starting, segment);
            if (currentError < smallestError) {
                smallestError = currentError;
                best = possibilities.get(i);
            }
        }
        return best;
    }

    private double satisfabilityTotalError(
            Vertex vertex, Vertex starting, Segment segment) {
        double totalError = containingError(vertex);
        switch (segment.getConstraint()) {
            case horizontal:
                totalError += abs(starting.getY() - vertex.getY());
                break;
            case vertical:
                totalError += abs(starting.getX() - vertex.getX());
                break;
            case fixedLength:

                totalError += abs(Euclidean2dGeometry.getSquareDistance(vertex, starting)
                        - segment.getConstraintLength()
                        * segment.getConstraintLength());
                break;
        }
        return totalError;
    }

    private ArrayList<Vertex> filterValidVertices(
            ArrayList<Vertex> list, Vertex starting, Segment segment) {
        ArrayList<Vertex> filteredList = new ArrayList<>();
        for (int i = 0, max = list.size(); i < max; i++) {
            if (satisfabilityTotalError(list.get(i), starting, segment) < eps) {
                filteredList.add(list.get(i));
            }
        }
        return filteredList;
    }

    public ArrayList<Vertex> thatContains(ArrayList<Vertex> possibilities) {
        ArrayList<Vertex> containing = new ArrayList<>();
        for (int i = 0, max = possibilities.size(); i < max; i++) {
            if (isContaining(possibilities.get(i))) {
                containing.add(possibilities.get(i));
            }
        }
        return containing;
    }

    private static double zeroIfNegligible(double number) {
        if (abs(number) < eps) {
            return 0;
        } else {
            return number;
        }
    }
//    public Vertex getClosestPoint(Area areaToIntersect, Vertex target) {
//        // a little bit tough...
//        // implement later...
//        return null;
//    }

}
