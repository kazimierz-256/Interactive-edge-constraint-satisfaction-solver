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
public interface Area {

    Area sumWith(Segment line);

    boolean isContaining(Vertex vertex);

    Vertex closestIntersection(Vertex ideal, Vertex startingPoint, Segment line);
}
