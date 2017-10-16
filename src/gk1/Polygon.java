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
                            tryMoveVertexExactly(moveVertex, position));
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

    public void tryHorizontal(Segment segment) {
        // implement approaching from both sides and choosing the best combination
        // undone
    }

    public void tryVertical(Segment segment) {
        // implement approaching from both sides and choosing the best combination
        // undone
    }

    public void tryFixedLength(Segment segment, double targetLength) {

        // implement approaching from both sides and choosing the best combination
        // remember to set fixedLength inside the segment!
        // undone
    }

    public void tryFixedLength(Segment segment) {

        TextInputDialog dialog = new TextInputDialog();
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
    public boolean tryMoveVertexExactly(Vertex vertex, Vertex targetVertex) {
        // first, try to solve _exactly_
        // use the fact of stiffness? really?
        Area point = new Area(targetVertex);

        ArrayList<Area> cAreas = new ArrayList<>();
        Segment cSegmentIterator = vertex.getBeginningOfSegment();
        Area cLatestArea = point.generalize(cSegmentIterator);
        cAreas.add(cLatestArea);
        int cBestFound;
        Vertex cNext = cSegmentIterator.getEnd();
        if (cLatestArea.isContaining(cNext)) {
            cBestFound = 1;
        } else if (cNext.isFixed()) {
            // cannot find exactly a fixed vertex!
            System.out.println("Impossible!");
            return false;
        } else {
            int max = vertices.size();
            for (cBestFound = 2; cBestFound < max; cBestFound++) {

                cSegmentIterator = cNext.getBeginningOfSegment();
                cLatestArea = cLatestArea.generalize(cSegmentIterator);
                cAreas.add(cLatestArea);
                cNext = cSegmentIterator.getEnd();
                if (cLatestArea.isContaining(cNext)) {
                    break;
                } else if (cNext.isFixed()) {
                    // cannot find exactly a fixed vertex!
                    System.out.println("Impossible!");
                    return false;
                }
            }
        }

        ArrayList<Area> ccAreas = new ArrayList<>();
        Segment ccSegmentIterator = vertex.getEndOfSegment();
        Area ccLatestArea = point.generalize(ccSegmentIterator);
        ccAreas.add(ccLatestArea);
        int ccBestFound;
        Vertex ccNext = ccSegmentIterator.getBeginning();
        if (ccLatestArea.isContaining(ccNext)) {
            ccBestFound = 1;
        } else if (ccNext.isFixed()) {
            // cannot find exactly a fixed vertex!
            System.out.println("Should try closest possibility here!");
            return false;
        } else {
            int max = vertices.size();
            for (ccBestFound = 2; ccBestFound < max; ccBestFound++) {

                ccSegmentIterator = ccNext.getEndOfSegment();
                ccLatestArea = ccLatestArea.generalize(ccSegmentIterator);
                ccAreas.add(ccLatestArea);
                ccNext = ccSegmentIterator.getBeginning();
                if (ccLatestArea.isContaining(ccNext)) {
                    break;
                } else if (ccNext.isFixed()) {
                    // cannot find exactly a fixed vertex!
                    System.out.println("Should try closest possibility here!");
                    return false;
                }
            }
        }

        if (cBestFound + ccBestFound > vertices.size()) {
            System.out.println("Should try closest possibility here!");
            return false;
        }

        // PUSH BACK
        ArrayList<Vertex> cNewVerticesReversed = new ArrayList<>();
        Segment cBackSegment;
        Vertex cBackVertex = cNext;
        Vertex cNewVertex = cNext, cTmpPrevious;

        for (int i = cAreas.size() - 2; i >= 0; i--) {
            cBackSegment = cBackVertex.getEndOfSegment();
            cTmpPrevious = cBackSegment.getBeginning();
            cNewVertex = cAreas.get(i).getClosestPoint(
                    cNewVertex, cTmpPrevious, cBackSegment);
            if (cNewVertex == null) {
                System.out.println("This is sooooo wrong!");
                return false;
            }
            cNewVerticesReversed.add(cNewVertex);
        }

        cBackVertex = cNext;
        for (int i = 0; i < cAreas.size() - 2; i++) {
            cBackSegment = cBackVertex.getEndOfSegment();
            cTmpPrevious = cBackSegment.getBeginning();
            cNewVertex = cNewVerticesReversed.get(i);
            cTmpPrevious.setX(cNewVertex.getX());
            cTmpPrevious.setY(cNewVertex.getY());
        }

        ArrayList<Vertex> ccNewVerticesReversed = new ArrayList<>();
        Segment ccBackSegment;
        Vertex ccBackVertex = ccNext;
        Vertex ccNewVertex = ccNext, ccTmpPrevious;

        for (int i = ccAreas.size() - 2; i >= 0; i--) {
            ccBackSegment = ccBackVertex.getEndOfSegment();
            ccTmpPrevious = ccBackSegment.getBeginning();
            ccNewVertex = ccAreas.get(i).getClosestPoint(
                    ccNewVertex, ccTmpPrevious, ccBackSegment);
            if (ccNewVertex == null) {
                System.out.println("This is sooooo wrong!");
                return false;
            }
            ccNewVerticesReversed.add(ccNewVertex);
        }

        ccBackVertex = ccNext;
        for (int i = 0; i < ccAreas.size() - 2; i++) {
            ccBackSegment = ccBackVertex.getEndOfSegment();
            ccTmpPrevious = ccBackSegment.getBeginning();
            ccNewVertex = ccNewVerticesReversed.get(i);
            ccTmpPrevious.setX(ccNewVertex.getX());
            ccTmpPrevious.setY(ccNewVertex.getY());
        }

        vertex.setX(targetVertex.getX());
        vertex.setY(targetVertex.getY());
        // work my way back the chain...
        //look for exact matches and correct vertices up the way twards the target!
        //how cool is that?
        //
        // OTHERWISE
        // try to solve for nearest point and repeat the procedure with new target
//        vertex.setX(targetVertex.getX());
//        vertex.setY(targetVertex.getY());
        return true;
    }

}
