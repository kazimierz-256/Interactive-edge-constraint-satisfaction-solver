/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public class Polygon implements Drawable {

    public enum ActionState {
        moving,
        idle
    }

    private enum MoveEntity {
        movingVertex,
        movingSegment,
        movingPolygon,
        none
    }

    private MoveEntity moveEntity;
    private Polygon backup;
    private Vertex moveVertex;
    private Segment moveSegment;

    private ActionState state = ActionState.idle;
    private MouseEvent rememberedMouseClick;

    private double clickThreshold = 200;
    private ArrayList<Vertex> vertices;
    private ArrayList<Segment> segments;
    private String name;

    public Polygon(String name, Vertex... vertices) {
        this(name, new ArrayList<>(Arrays.asList(vertices)));
    }

    public Polygon(String name, ArrayList<Vertex> vertices) {
        if (vertices.size() < 3) {
            //throw new Exception("A polygon has to have at least three vertices.");
            return;
        }

        this.name = name;
        this.vertices = new ArrayList<>();
        this.segments = new ArrayList<>();

        vertices.forEach((vertex) -> {
            this.vertices.add(vertex.cloneWithoutSegments());
        });

        int max = this.vertices.size();
        Vertex before = this.vertices.get(max - 1);
        for (int i = 0; i < max; i++) {
            Vertex now = this.vertices.get(i);
            Segment newSegment = new Segment(before, now);
            segments.add(newSegment);
            before.setBeginningOfSegment(newSegment);
            now.setEndOfSegment(newSegment);
            before = now;
        }
    }

    @Override
    public void draw(Viewer viewer) {
        segments.forEach((segment) -> {
            viewer.draw(segment);
        });
        vertices.forEach((vertex) -> {
            viewer.draw(vertex);
        });
    }

    private void makeBackupOfPolygon() {
        // be careful, line restrictions are not copied over
        // created lines will become loose!
        backup = new Polygon(name, vertices);
    }

    @Override
    public Reaction mouseMoved(MouseEvent mouseEvent) {
        Reaction reaction = new Reaction();
        Vertex position = new Vertex(mouseEvent.getX(), mouseEvent.getY());
        if (this.state == ActionState.moving) {
            switch (moveEntity) {
                case movingVertex:
                    reaction.mergeShouldRender(
                            moveVertex(moveVertex, position));
                    reaction.setDesiredCursor(Cursor.MOVE);
                    break;
                case movingPolygon:
                    reaction.mergeShouldRender(
                            movePolygon(mouseEvent));
                    reaction.setDesiredCursor(Cursor.CLOSED_HAND);
                    break;
                default:
            }
        } else {
            // check if at least the cursor is above the polygon
            if (isInsidePolygon(position)) {
                reaction.setDesiredCursor(Cursor.OPEN_HAND);
            }
            if (!getNearbyVertices(position).isEmpty()) {
                reaction.setDesiredCursor(Cursor.MOVE);
            }
            return reaction;
        }
        return reaction;
    }

    @Override
    public Reaction mousePressed(MouseEvent mouseEvent) {
        Reaction reaction = new Reaction();
        Vertex position = new Vertex(mouseEvent.getX(), mouseEvent.getY());
        ArrayList<Vertex> capturedVertices = getNearbyVertices(position);
        boolean insidePolygon = isInsidePolygon(position);
        if (capturedVertices.isEmpty() && !insidePolygon) {
            return reaction;
        }
        if (capturedVertices.size() > 0) {
            // move vertex
            moveEntity = MoveEntity.movingVertex;
            // choose the one with largest z value
            moveVertex = capturedVertices.get(0);
        } else if (insidePolygon) {
            // move whole polygon
            moveEntity = MoveEntity.movingPolygon;
        }
        rememberedMouseClick = mouseEvent;
        makeBackupOfPolygon();
        this.state = ActionState.moving;
        return reaction;
    }

    @Override
    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction reaction = new Reaction();
        Vertex position = new Vertex(mouseEvent.getX(), mouseEvent.getY());
        this.state = ActionState.idle;
        return reaction;
    }

    public boolean movePolygon(MouseEvent mouseEvent) {
        for (int i = 0, max = vertices.size(); i < max; i++) {
            vertices.get(i).setX(
                    backup.vertices.get(i).getX()
                    + mouseEvent.getX() - rememberedMouseClick.getX());
            vertices.get(i).setY(
                    backup.vertices.get(i).getY()
                    + mouseEvent.getY() - rememberedMouseClick.getY());
        }
        return true;
    }

    //returns boolean in case the moving command did not succeed
    public boolean moveVertex(Vertex vertex, Vertex targetVertex) {
        // move relevant vertices, segments, or even the polygon itself
        vertex.setX(targetVertex.getX());
        vertex.setY(targetVertex.getY());
        return true;
    }

    private boolean isInsidePolygon(Vertex position) {
        // source: https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon/2922778#2922778
        boolean isInside = false;
        int i, j, max = segments.size();
        for (i = 0, j = max - 1; i < max; j = i++) {

            if (((vertices.get(i).getY() > position.getY())
                    != (vertices.get(j).getY() > position.getY()))
                    && (position.getX() < (vertices.get(j).getX() - vertices.get(i).getX()) * (position.getY() - vertices.get(i).getY()) / (vertices.get(j).getY() - vertices.get(i).getY()) + vertices.get(i).getX())) {
                isInside = !isInside;
            }
        }
        return isInside;
    }

    private ArrayList<Segment> getNearbySegments(Vertex position) {
        ArrayList<Segment> closeSegments = new ArrayList<>();

        segments.forEach((segment) -> {
            if (Euclidean2dGeometry.getSquareDistance(segment, position)
                    <= clickThreshold) {
                closeSegments.add(segment);
            }
        });
        return closeSegments;
    }

    private ArrayList<Vertex> getNearbyVertices(Vertex position) {
        ArrayList<Vertex> closeVertices = new ArrayList<>();

        vertices.forEach((vertex) -> {
            if (Euclidean2dGeometry.getSquareDistance(vertex, position)
                    <= clickThreshold) {
                closeVertices.add(vertex);
            }
        });
        return closeVertices;
    }

    @Override

    public ArrayList<MenuItem> buildMenu(MouseEvent event) {
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        Vertex position = new Vertex(event.getX(), event.getY());

        getNearbySegments(position).forEach((segment) -> {
            // if close enough then add something to the contextmenu
            // Line ...
            // create new vertex
            // add new constraint:
            // fix length (then ask for length) (if possible)
            // make horizontal (if possible)
            // make vertical (if possible)
            // move line fix length (then ask for length) (if possible)[optional]
            Menu menu = new Menu(String.format("Segment %s", segment.toString()));
            MenuItem menuItem = new MenuItem("Add vertex in the middle");
            menuItem.setOnAction((ActionEvent e) -> {
                System.out.println("adding vertex...");
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });
        getNearbyVertices(position).forEach((vertex) -> {
// Vertex (100, 200) make a mastermenu
// add submenus: move, delete, make stiff [optional]
            Menu menu = new Menu(String.format("Vertex %s", vertex.toString()));
            MenuItem menuItem = new MenuItem("Move vertex");
            menuItem.setOnAction((ActionEvent e) -> {
                System.out.println("moving...");
            });
            menu.getItems().add(menuItem);

            menuItem = new MenuItem("Remove");
            menuItem.setOnAction((ActionEvent e) -> {
                System.out.println("deleting...");
            });
            menu.getItems().add(menuItem);

            menuItem = new MenuItem("Toggle stiffness");
            menuItem.setOnAction((ActionEvent e) -> {
                System.out.println("toggling...");
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });

        // check if inside polygon
        // add ability to delete polygon, move polygon...
        return menuItems;
    }

    @Override
    public String toString() {
        return name;
    }
}
