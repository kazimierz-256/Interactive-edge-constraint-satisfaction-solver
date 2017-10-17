/*
 * To change this license header, choose License Headers in Projection Properties.
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

    private double clickThreshold = 300;
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
                            tryMoveVertexExactly(moveVertex, position, true));
                    reaction.setDesiredCursor(Cursor.MOVE);
                    break;
                case movingPolygon:
                    reaction.mergeShouldRender(
                            movePolygon(mouseEvent));
                    reaction.setDesiredCursor(Cursor.CLOSED_HAND);
                    break;
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
        // source: stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon/2922778#2922778
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

    public ArrayList<MenuItem> buildContextMenu(MouseEvent event) {
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        Vertex position = new Vertex(event.getX(), event.getY());

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

            if (segment.getConstraint() == Segment.segmentConstraint.free) {
                // horizontal segment
                menuItem = new MenuItem("Make horizontal");
                menuItem.setOnAction((ActionEvent e) -> {
                    tryHorizontal(segment);
                    GK1.model.draw(GK1.viewer);
                });
                menu.getItems().add(menuItem);

                // vertical segment
                menuItem = new MenuItem("Make vertical");
                menuItem.setOnAction((ActionEvent e) -> {
                    tryVertical(segment);
                    GK1.model.draw(GK1.viewer);
                });
                menu.getItems().add(menuItem);

                // fixed length segment
                menuItem = new MenuItem("Fix length");
                menuItem.setOnAction((ActionEvent e) -> {
                    tryFixedLength(segment);
                    GK1.model.draw(GK1.viewer);
                });
                menu.getItems().add(menuItem);
            } else {

                // free the segment
                menuItem = new MenuItem("Free");
                menuItem.setOnAction((ActionEvent e) -> {
                    segment.restrictFree();
                    GK1.model.draw(GK1.viewer);
                });
                menu.getItems().add(menuItem);

            }

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

        if (!menuItems.isEmpty() || isInsidePolygon(position)) {
            MenuItem menuItem = new MenuItem("Delete polygon");
            menuItem.setOnAction((ActionEvent e) -> {
                GK1.model.unregisterDrawable(this);
                GK1.model.draw(GK1.viewer);
            });
            menuItems.add(menuItem);
        }

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
        beginningSegment.restrictFree();
    }

    public void addVertexInbetween(Segment segment) {
        int segmentIndex = segments.indexOf(segment);
        int vertexIndex = vertices.indexOf(segment.getBeginning());
        Vertex newVertex = segment.getCenter();
        Segment newSegment = new Segment(newVertex, segment.getEnd());
        newVertex.setBeginningOfSegment(newSegment);
        newVertex.setEndOfSegment(segment);
        segment.getEnd().setEndOfSegment(newSegment);
        segment.setEnd(newVertex);
        segment.restrictFree();
        vertices.add(vertexIndex + 1, newVertex);
        segments.add(segmentIndex + 1, newSegment);
    }

    private void tryHorizontal(Segment segment) {
        // implement approaching from both sides and choosing the best combination
        // undone
        // i suspect the situation is symmetrical
        // therefore freeze one of the vertices & try to complete the cycle with the other one
    }

    private void tryVertical(Segment segment) {
        // implement approaching from both sides and choosing the best combination
        // undone
    }

    private boolean tryFixedLength(Segment segment, double targetLength) {
        segment.restrictFixedLength(targetLength);
        boolean result = tryMoveVertexExactly(
                segment.getBeginning(), segment.getBeginning(), false);
        if (result) {
            return true;
        } else {
            segment.restrictFree();
            return false;
        }
        // implement approaching from both sides and choosing the best combination
        // remember to set fixedLength inside the segment!
        // undone
        //fix the beginning
//        // buildup area towards the flexible vertex
//        ArrayList<Area> cAreas = new ArrayList<>();
//        Vertex cNext = segment.getEnd();
//        Segment cSegmentIterator;
//        Area cLatestArea = new Area(segment.getEnd());
//        int cBestFound = 1;
//        for (int max = vertices.size() - 1; cBestFound < max; cBestFound++) {
//            cSegmentIterator = cNext.getBeginningOfSegment();
//            cLatestArea = cLatestArea.generalize(cSegmentIterator);
//            cAreas.add(cLatestArea);
//            cNext = cSegmentIterator.getEnd();
//            if (cNext.isFixed()) {
//                System.out.println("Reached a stiff vertex.");
//                return false;
//            }
//        }
//
//        //check if vertical line is possible to achieve
//        Vertex bestIntersection = cLatestArea.mostAccurateFixedLength(
//                segment.getBeginning(), segment.getEnd(), targetLength);
//        if (bestIntersection == null) {
//            return false;
//        }
//        // solve myself in reverse order
//        cNext = segment.getBeginning();
//        ArrayList<Vertex> cNewVerticesReversed = new ArrayList<>();
//        Segment cBackSegment;
//        Vertex cBackVertex = cNext;
//        Vertex cNewVertex = cNext, cTmpPrevious;
//
//        for (int i = cAreas.size() - 2; i >= 0; i--) {
//            cBackSegment = cBackVertex.getEndOfSegment();
//            cTmpPrevious = cBackSegment.getBeginning();
//            cNewVertex = cAreas.get(i).getClosestPoint(
//                    cNewVertex, cTmpPrevious, cBackSegment, false);
//
//            if (cNewVertex == null) {
//                System.out.println("Numerical errors blew up");
//                return false;
//            }
//            cNewVerticesReversed.add(cNewVertex);
//            cBackVertex = cTmpPrevious;
//        }
//
//        cBackVertex = cNext;
//        for (int i = 0, max = cAreas.size() - 1; i < max; i++) {
//            cBackSegment = cBackVertex.getEndOfSegment();
//            cTmpPrevious = cBackSegment.getBeginning();
//            cTmpPrevious.setX(cNewVerticesReversed.get(i).getX());
//            cTmpPrevious.setY(cNewVerticesReversed.get(i).getY());
//            cBackVertex = cTmpPrevious;
//        }
//        return true;
    }

    private void tryFixedLength(Segment segment) {

        TextInputDialog dialog = new TextInputDialog();
        // should try .initOwner(primaryStage)
        dialog.setHeaderText(String.format(
                "Please enter the desired length of %s.\nCurrent length is about %2.1f\nLeave blank to restrict to current length",
                segment.toString(), sqrt(Euclidean2dGeometry.getSquareLength(segment))));
        dialog.setTitle("Fixing segment length");
        dialog.showAndWait();
        double result;
        try {
            result = Double.parseDouble(dialog.getResult());
            tryFixedLength(segment, result);
        } catch (NullPointerException | NumberFormatException e) {
            result = sqrt(Euclidean2dGeometry.getSquareLength(segment));
        }
        segment.restrictFixedLength(result);
    }

    //returns boolean in case the moving command did not succeed
    private boolean tryMoveVertexExactly(Vertex vertex, Vertex targetVertex, boolean preferCloser) {

        //find containing areas
        ArrayList<Area> cAreas = new ArrayList<>();
        Vertex cNext = vertex;
        Segment cSegmentIterator;
        Area cLatestArea = new Area(targetVertex);
        int cBestFound = 1;
        for (int max = vertices.size(); cBestFound < max; cBestFound++) {
            cSegmentIterator = cNext.getBeginningOfSegment();
            cLatestArea = cLatestArea.generalize(cSegmentIterator);
            cAreas.add(cLatestArea);
            cNext = cSegmentIterator.getEnd();
            if (cLatestArea.isContaining(cNext)) {
                break;
            } else if (cNext.isFixed()) {
                System.out.println("Reached a stiff vertex.");
                return false;
            }
        }

        ArrayList<Area> ccAreas = new ArrayList<>();
        Vertex ccNext = vertex;
        Segment ccSegmentIterator;
        Area ccLatestArea = new Area(targetVertex);
        int ccBestFound = 1;
        for (int max = vertices.size(); ccBestFound < max; ccBestFound++) {
            ccSegmentIterator = ccNext.getEndOfSegment();
            ccLatestArea = ccLatestArea.generalize(ccSegmentIterator);
            ccAreas.add(ccLatestArea);
            ccNext = ccSegmentIterator.getBeginning();
            if (ccLatestArea.isContaining(ccNext)) {
                break;
            } else if (ccNext.isFixed()) {
                System.out.println("Reached a stiff vertex.");
                return false;
            }
        }

        if (ccBestFound + ccBestFound > vertices.size()) {
            System.out.println("Too many moving vertices (> n)");
            return false;
        }

        // solve in reverse order
        ArrayList<Vertex> cNewVerticesReversed = new ArrayList<>();
        Segment cBackSegment;
        Vertex cBackVertex = cNext;
        Vertex cNewVertex = cNext, cTmpPrevious;

        for (int i = cAreas.size() - 2; i >= 0; i--) {
            cBackSegment = cBackVertex.getEndOfSegment();
            cTmpPrevious = cBackSegment.getBeginning();
            cNewVertex = cAreas.get(i).getClosestPoint(
                    cNewVertex, cTmpPrevious, cBackSegment, preferCloser);

            if (cNewVertex == null) {
                System.out.println("Numerical errors blew up");
                return false;
            }
            cNewVerticesReversed.add(cNewVertex);
            cBackVertex = cTmpPrevious;
        }

        ArrayList<Vertex> ccNewVerticesReversed = new ArrayList<>();
        Segment ccBackSegment;
        Vertex ccBackVertex = ccNext;
        Vertex ccNewVertex = ccNext, ccTmpPrevious;

        for (int i = ccAreas.size() - 2; i >= 0; i--) {
            ccBackSegment = ccBackVertex.getBeginningOfSegment();
            ccTmpPrevious = ccBackSegment.getEnd();
            ccNewVertex = ccAreas.get(i).getClosestPoint(
                    ccNewVertex, ccTmpPrevious, ccBackSegment, preferCloser);
            if (ccNewVertex == null) {
                System.out.println("Numerical errors blew up");
                return false;
            }
            ccNewVerticesReversed.add(ccNewVertex);
            ccBackVertex = ccTmpPrevious;
        }

        // succsessfully solved, applying changes
        cBackVertex = cNext;
        for (int i = 0, max = cAreas.size() - 1; i < max; i++) {
            cBackSegment = cBackVertex.getEndOfSegment();
            cTmpPrevious = cBackSegment.getBeginning();
            cTmpPrevious.setX(cNewVerticesReversed.get(i).getX());
            cTmpPrevious.setY(cNewVerticesReversed.get(i).getY());
            cBackVertex = cTmpPrevious;
        }

        ccBackVertex = ccNext;
        for (int i = 0, max = ccAreas.size() - 1; i < max; i++) {
            ccBackSegment = ccBackVertex.getBeginningOfSegment();
            ccTmpPrevious = ccBackSegment.getEnd();
            ccTmpPrevious.setX(ccNewVerticesReversed.get(i).getX());
            ccTmpPrevious.setY(ccNewVerticesReversed.get(i).getY());
            ccBackVertex = ccTmpPrevious;
        }

        vertex.setX(targetVertex.getX());
        vertex.setY(targetVertex.getY());

        return true;
    }

}
