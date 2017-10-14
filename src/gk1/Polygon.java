/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Kazimierz
 */
public class Polygon implements Drawable {

    @Override
    public double getZ() {
        return z;
    }

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
    private double z;

    public Polygon(String name, double z, Vertex... vertices) {
        this(name, z, new ArrayList<>(Arrays.asList(vertices)));
    }

    public Polygon(String name, double z, ArrayList<Vertex> vertices) {
        if (vertices.size() < 3) {
            //throw new Exception("A polygon has to have at least three vertices.");
            return;
        }

        this.name = name;
        this.vertices = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.z = z;

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
        backup = new Polygon(name, z, vertices);
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

        if (isInsidePolygon(position)) {
            MenuItem menuItem = new MenuItem("Delete polygon");
            menuItem.setOnAction((ActionEvent e) -> {
                GK1.model.unregisterDrawable(this);
                GK1.model.draw(GK1.viewer);
            });
            menuItems.add(menuItem);
        }

        // add segments contextmenu
        getNearbySegments(position).forEach((segment) -> {
            Menu menu = new Menu(String.format("Segment %s", segment.toString()));

            // vertex in the middle
            MenuItem menuItem = new MenuItem("Add vertex in the middle");
            menuItem.setOnAction((ActionEvent e) -> {
                addVertexInbetween(segment);
                GK1.model.draw(GK1.viewer);
            });
            menu.getItems().add(menuItem);

            // horizontal segment
            menuItem = new MenuItem("Make horizontal");
            menuItem.setOnAction((ActionEvent e) -> {
                makeHorizontal(segment);
                GK1.model.draw(GK1.viewer);
            });
            menu.getItems().add(menuItem);

            // vertical segment
            menuItem = new MenuItem("Make vertical");
            menuItem.setOnAction((ActionEvent e) -> {
                makeVertical(segment);
                GK1.model.draw(GK1.viewer);
            });
            menu.getItems().add(menuItem);

            // fixed length segment
            menuItem = new MenuItem("Fix length");
            menuItem.setOnAction((ActionEvent e) -> {
                makeFixedLength(segment);
                GK1.model.draw(GK1.viewer);
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });

        // add vertices contextmenu
        getNearbyVertices(position).forEach((vertex) -> {
            Menu menu = new Menu(String.format("Vertex %s", vertex.toString()));
            MenuItem menuItem;

            // vertex removal
            if (vertices.size() > 3) {
                menuItem = new MenuItem("Remove");
                menuItem.setOnAction((ActionEvent e) -> {
                    removeVertex(vertex);
                    GK1.model.draw(GK1.viewer);
                });
                menu.getItems().add(menuItem);
            }

            // toggle vertex stiffness
            menuItem = new MenuItem(String.format(
                    vertex.isFixed() ? "Unfreeze" : "Freeze"));
            menuItem.setOnAction((ActionEvent e) -> {
                vertex.setFixed(!vertex.isFixed());
                GK1.model.draw(GK1.viewer);
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });

        return menuItems;
    }

    @Override
    public String toString() {
        return name;
    }

    public void removeVertex(Vertex vertex) {
        Segment beginningSegment = vertex.getEndOfSegment();
        Segment endSegment = vertex.getBeginningOfSegment();
        endSegment.getEnd().setEndOfSegment(beginningSegment);
        beginningSegment.setEnd(endSegment.getEnd());
        vertices.remove(vertex);
        segments.remove(endSegment);
    }

    public void addVertexInbetween(Segment segment) {
        int segmentIndex = segments.indexOf(segment);
        int vertexIndex = vertices.indexOf(segment.getBeginning());
        double newX = segment.getBeginning().getX()
                + (segment.getEnd().getX() - segment.getBeginning().getX()) / 2;
        double newY = segment.getBeginning().getY()
                + (segment.getEnd().getY() - segment.getBeginning().getY()) / 2;
        Vertex newVertex = new Vertex(newX, newY);
        Segment newSegment = new Segment(newVertex, segment.getEnd());
        newVertex.setBeginningOfSegment(newSegment);
        newVertex.setEndOfSegment(segment);
        segment.getEnd().setEndOfSegment(newSegment);
        segment.setEnd(newVertex);
        segment.makeFree();
        vertices.add(vertexIndex + 1, newVertex);
        segments.add(segmentIndex + 1, newSegment);
    }

    public void makeHorizontal(Segment segment) {

        // undone
    }

    public void makeVertical(Segment segment) {

        // undone
    }

    public void makeFixedLength(Segment segment) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(String.format(
                "Please enter the desired length of %s.\nCurrent length is about %2.1f",
                segment.toString(), sqrt(Euclidean2dGeometry.getSquareLength(segment))));
        dialog.setTitle("Fixing segment length");
        dialog.showAndWait();
        try {
            double result = Double.parseDouble(dialog.getResult());
            segment.makeFixedLength(result);
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }

        // undone
    }

    //returns boolean in case the moving command did not succeed
    public boolean moveVertex(Vertex vertex, Vertex targetVertex) {
        // move relevant vertices, segments, or even the polygon itself
        vertex.setX(targetVertex.getX());
        vertex.setY(targetVertex.getY());
        return true;
    }
}
