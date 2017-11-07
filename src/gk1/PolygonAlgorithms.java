/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

/**
 *
 * @author kazimierz
 */
public class PolygonAlgorithms {

    private static final double Epsilon = 0.0001d;
    private static final double NegativeEpsilon = -Epsilon;
    private static final double EpsilonSquared = Epsilon * Epsilon;

    public static boolean IsSameSide(Vertex p1, Vertex p2, Segment s) {
        // dużo bardziej wydajna implementacja niż przy użyciu s.Direction(p1) oraz mnożenia
        // zadbałem, aby znaki równości/nierówności idealnie odzwierciadlały rzutowanie iloczynu wektorowego na strukturę Direction
        double cp1 = (s.getEnd().getX() - s.getBeginning().getX()) * (p1.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p1.getX() - s.getBeginning().getX());
        double cp2 = (s.getEnd().getX() - s.getBeginning().getX()) * (p2.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p2.getX() - s.getBeginning().getX());
        return (cp1 > NegativeEpsilon && cp2 > NegativeEpsilon) || (cp1 < Epsilon && cp2 < Epsilon);
    }

    static boolean IsOutside(Vertex p1, Vertex p2, Segment s) {
        double cp1 = (s.getEnd().getX() - s.getBeginning().getX()) * (p1.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p1.getX() - s.getBeginning().getX());
        double cp2 = (s.getEnd().getX() - s.getBeginning().getX()) * (p2.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p2.getX() - s.getBeginning().getX());
        // check whether boxing is done
        return cp1 >= Epsilon || cp1 <= NegativeEpsilon;
    }

    static boolean IsSameSideExclusive(Vertex p1, Vertex p2, Segment s) {
        double cp1 = (s.getEnd().getX() - s.getBeginning().getX()) * (p1.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p1.getX() - s.getBeginning().getX());
        double cp2 = (s.getEnd().getX() - s.getBeginning().getX()) * (p2.getY() - s.getBeginning().getY()) - (s.getEnd().getY() - s.getBeginning().getY()) * (p2.getX() - s.getBeginning().getX());
        return (cp1 >= Epsilon && cp2 >= Epsilon) || (cp1 <= NegativeEpsilon && cp2 <= NegativeEpsilon);
    }

    private static boolean EpsilonEqualsFast(Vertex a, Vertex b) {
        return (a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY()) < EpsilonSquared;
    }

    public static Vertex GetIntersectionVertexFast(Segment seg1, Segment seg2) {
        Vertex direction1 = new Vertex(seg1.getEnd().getX() - seg1.getBeginning().getX(), seg1.getEnd().getY() - seg1.getBeginning().getY());
        Vertex direction2 = new Vertex(seg2.getEnd().getX() - seg2.getBeginning().getX(), seg2.getEnd().getY() - seg2.getBeginning().getY());

        double t = ((seg2.getBeginning().getX() - seg1.getBeginning().getX()) * direction2.getY() - (seg2.getBeginning().getY() - seg1.getBeginning().getY()) * direction2.getX()) / ((direction1.getX() * direction2.getY()) - (direction1.getY() * direction2.getX()));

        return new Vertex(seg1.getBeginning().getX() + (t * direction1.getX()), seg1.getBeginning().getY() + (t * direction1.getY()));
    }
}
