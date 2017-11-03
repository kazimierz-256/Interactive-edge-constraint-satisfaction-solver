/*
 * To change this license header, choose License Headers in Projection Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static gk1.GK1.model;
import gk1.areas.Area;
import gk1.textures.Texture;
import java.awt.image.BufferedImage;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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

    private Vertex rememberedMovedFirst;

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

    private Texture texture = new Texture();
    private Boolean automaticRelations;
    private MoveEntity moveEntity;
//    private Polygon backup;
    private Vertex moveVertex;
    private Segment moveSegment;

    private ActionState state = ActionState.idle;
    private MouseEvent rememberedMouseClick;

    private double clickThreshold = 300;
    private ArrayList<Vertex> vertices;
    private ArrayList<Segment> segments;
    private String name;

    @Override
    public double getZ() {
        return vertices.get(0).getZ();
    }

    @Override
    public Reaction toggleAutomaticRelations(Boolean isAutomatic) {
        automaticRelations = isAutomatic;
        return new Reaction(true, false, null);
    }

    private boolean trySnapEdges() {
        Segment segment;
        for (int i = 0, max = segments.size(); i < max; i++) {
            segment = segments.get(i);
            if (segment.getConstraint() == Segment.segmentConstraint.free) {
                // todo: only if surrounding segments are not extreme
                if (segment.isAlmostHorizontal()
                        && segment.isSafeToRestrictHorizontal()) {
                    segment.restrictHorizontal();
                } else if (segment.isAlmostVertical()
                        && segment.isSafeToRestrictVertical()) {
                    segment.restrictVertical();
                }
            }
        }
        return true;
    }

    private void drawTexture(Viewer viewer, Model context) {
        // start already with the first vertex
        double leftmost = vertices.get(0).getXint();
        double topmost = vertices.get(0).getYint();
        double rightmost = leftmost;
        double bottommost = topmost;
        double x, y;

        Vertex lower, upper, tmp;
        for (int i = 1, max = vertices.size(); i < max; i++) {
            tmp = vertices.get(i);
            x = tmp.getXint();
            y = tmp.getYint();

            if (x < leftmost) {
                leftmost = x;
            } else if (x > rightmost) {
                rightmost = x;
            }

            if (y > topmost) {
                topmost = y;
            } else if (y < bottommost) {
                bottommost = y;
            }
        }

        int width = (int) (rightmost - leftmost);
        int height = (int) (topmost - bottommost);

        if (width <= 0 || height <= 0) {
            return;
        }

        int[] pixels = new int[width * height];

        // bucketsort edges
        LinkedList<ActiveEdge>[] edgeTable = new LinkedList[height + 1];
        // initialize the array
        for (int i = 0; i < edgeTable.length; i++) {
            edgeTable[i] = new LinkedList<>();
        }

        for (int i = 0, max = segments.size(); i < max; i++) {
            lower = segments.get(i).getBeginning();
            upper = segments.get(i).getEnd();
            if (lower.getY() > upper.getY()) {
                tmp = lower;
                lower = upper;
                upper = tmp;
            }
            // now the following is true: lower.y <= upper.y

            // make sure the index is always correctly assigned
            int indexAboveBottommost = (int) (lower.getYint() - bottommost);
            // watch out for division by zero
            //dx/dy
            double m_inverse = ((double) (upper.getXint() - lower.getXint()))
                    / (upper.getYint() - lower.getYint());

            edgeTable[indexAboveBottommost].add(
                    new ActiveEdge(upper.getYint(), lower.getXint(), m_inverse));
        }

        LinkedList<ActiveEdge> activeEdges = new LinkedList<>();

        // fill a bufferedimage with pixels using the texture field
        for (int localHeight = 0; localHeight < height; localHeight++) {
            if (!edgeTable[localHeight].isEmpty()) {
                //https://docs.oracle.com/javase/7/docs/api/java/util/LinkedList.html#fields_inherited_from_class_java.util.AbstractList
                // constant time insertion
                activeEdges.addAll(edgeTable[localHeight]);
            }
            activeEdges.sort((edge1, edge2) -> Double.compare(edge1.x, edge2.x));

            // sorted, now paint!
            boolean seekingPair = false;
            ActiveEdge previousEdge = null;

            for (ActiveEdge edge : activeEdges) {
                if (seekingPair) {
                    if (Double.isInfinite(edge.x)) {
                        return;
                    }

                    // paint all the way from previous to current edge
                    for (int j = (int) (previousEdge.x - leftmost),
                            max = (int) (edge.x - leftmost);
                            j < max;
                            j++) {

                        pixels[localHeight * width + j] = texture.getPixel(
                                leftmost, bottommost, getZ(), j, localHeight, model.getLights());
                    }

                    seekingPair = false;
                } else {
                    previousEdge = edge;
                    seekingPair = true;
                }
            }

            // increment x coordinates and remove edges below the scanline!
            LinkedList<ActiveEdge> toSpare = new LinkedList<>();

            for (ActiveEdge edge : activeEdges) {
                // jeśli edge.y_max <= 1 + localHeight + bottommost
                // to należy usunąć
                if (edge.y_max > 1 + localHeight + bottommost) {
                    edge.x += edge.m_inverse;
                    toSpare.add(edge);
                }

            }

            activeEdges = toSpare;

        }

        BufferedImage generatedTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        generatedTexture.getRaster().setDataElements(0, 0, width, height, pixels);

        // finally paint the image
        viewer.drawImage(generatedTexture, leftmost, bottommost, width, height);
    }

    public Polygon(String name, Boolean automaticRelations, Collection<Vertex> vertices) {
        if (vertices.size() < 3) {
            //throw new Exception("A polygon has to have at least three vertices.");
            return;
        }
        this.name = name;
        this.vertices = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.automaticRelations = automaticRelations;

        vertices.forEach((vertex) -> {
            Vertex cloned = vertex.cloneWithoutSegments();
            this.vertices.add(cloned);
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
    public void draw(Viewer viewer, Model context) {
        drawTexture(viewer, model);

        segments.forEach((segment) -> {
            viewer.draw(segment, automaticRelations);
        });

        vertices.forEach((vertex) -> {
            viewer.draw(vertex);
        });

    }

//    private void makeBackupOfPolygon() {
//        // be careful, line restrictions are not copied over
//        // created lines will become loose!
//        backup = new Polygon(name, automaticRelations, vertices);
//    }
    @Override
    public Reaction mouseMoved(MouseEvent mouseEvent) {
        Reaction reaction = new Reaction();
        Vertex position = new Vertex(mouseEvent.getX(), mouseEvent.getY());
        if (this.state == ActionState.moving) {
            switch (moveEntity) {
                case movingVertex:
                    // TODO: draw snappable edges
                    reaction.mergeShouldRender(
                            tryMoveVertexExactly(moveVertex, position, true));
                    reaction.setDesiredCursor(Cursor.CROSSHAIR);
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
                reaction.setDesiredCursor(Cursor.CROSSHAIR);
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
//        makeBackupOfPolygon();
        rememberedMovedFirst = vertices.get(0).cloneWithoutSegments();
        this.state = ActionState.moving;
        return reaction;
    }

    @Override
    public Reaction mouseReleased(MouseEvent mouseEvent) {
        Reaction reaction = new Reaction();
        this.state = ActionState.idle;

        // TODO: create a backup of polygon in case there is no possibility
        if (automaticRelations && moveVertex != null) {
            reaction.mergeShouldRender(
                    trySnapEdges());

            reaction.mergeShouldRender(
                    tryMoveVertexExactly(moveVertex, moveVertex, true));
        }

        return reaction;
    }

    public boolean movePolygon(MouseEvent mouseEvent) {
        double displacementX = rememberedMovedFirst.getX() - vertices.get(0).getX();
        double displacementY = rememberedMovedFirst.getY() - vertices.get(0).getY();

        for (int i = 0, max = vertices.size(); i < max; i++) {
            vertices.get(i).setX(
                    displacementX + vertices.get(i).getX()
                    + mouseEvent.getX() - rememberedMouseClick.getX()
            );
            vertices.get(i).setY(
                    displacementY + vertices.get(i).getY()
                    + mouseEvent.getY() - rememberedMouseClick.getY()
            );
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
                GK1.viewer.drawLastModel();
            });
            menu.getItems().add(menuItem);

            // horizontal segment
            if (segment.getConstraint() != Segment.segmentConstraint.horizontal
                    && segment.isSafeToRestrictHorizontal()) {
                menuItem = new MenuItem("Make horizontal if possible");
                menuItem.setOnAction((ActionEvent e) -> {
                    if (tryHorizontal(segment)) {
                        GK1.viewer.drawLastModel();
                    }
                });
                menu.getItems().add(menuItem);
            }

            // vertical segment
            if (segment.getConstraint() != Segment.segmentConstraint.vertical
                    && segment.isSafeToRestrictVertical()) {
                menuItem = new MenuItem("Make vertical if possible");
                menuItem.setOnAction((ActionEvent e) -> {
                    if (tryVertical(segment)) {
                        GK1.viewer.drawLastModel();
                    }
                });
                menu.getItems().add(menuItem);
            }

            // fixed length segment
            menuItem = new MenuItem("Fix length if possible");
            menuItem.setOnAction((ActionEvent e) -> {
                if (tryFixedLength(segment)) {
                    GK1.viewer.drawLastModel();
                }
            });

            menu.getItems().add(menuItem);
            if (segment.getConstraint() != Segment.segmentConstraint.free) {

                // free the segment
                menuItem = new MenuItem("Free");
                menuItem.setOnAction((ActionEvent e) -> {
                    segment.restrictFree();
                    GK1.viewer.drawLastModel();
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
                    GK1.viewer.drawLastModel();
                });
                menu.getItems().add(menuItem);
            }

            // toggle vertex stiffness
            menuItem = new MenuItem(String.format(
                    vertex.isFixed() ? "Unfreeze" : "Freeze"));
            menuItem.setOnAction((ActionEvent e) -> {
                vertex.setFixed(!vertex.isFixed());
                GK1.viewer.drawLastModel();
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });

        if (!menuItems.isEmpty() || isInsidePolygon(position)) {
            MenuItem menuItem = new MenuItem("Remove polygon");
            menuItem.setOnAction((ActionEvent e) -> {
                GK1.model.unregisterPolygon(this);
                GK1.viewer.drawLastModel();
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

    private boolean tryHorizontal(Segment segment) {
        if (!segment.isSafeToRestrictHorizontal()) {
            return false;
        }
        segment.restrictHorizontal();
        boolean result = tryMoveVertexExactly(
                segment.getBeginning(), segment.getBeginning(), true);
        if (result) {
            return true;
        }
        result = tryMoveVertexExactly(
                segment.getEnd(), segment.getEnd(), true);
        if (result) {
            return true;
        } else {
            segment.restrictFree();
            return false;
        }
    }

    private boolean tryVertical(Segment segment) {
        if (!segment.isSafeToRestrictVertical()) {
            return false;
        }
        segment.restrictVertical();
        boolean result = tryMoveVertexExactly(
                segment.getBeginning(), segment.getBeginning(), true);
        if (result) {
            return true;
        }
        result = tryMoveVertexExactly(
                segment.getEnd(), segment.getEnd(), true);
        if (result) {
            return true;
        } else {
            segment.restrictFree();
            return false;
        }
    }

    private boolean tryFixedLength(Segment segment, double targetLength) {
        segment.restrictFixedLength(targetLength);
        boolean result = tryMoveVertexExactly(
                segment.getBeginning(), segment.getBeginning(), true);
        if (result) {
            return true;
        }
        result = tryMoveVertexExactly(
                segment.getEnd(), segment.getEnd(), true);
        if (result) {
            return true;
        } else {
            segment.restrictFree();
            return false;
        }
    }

    private boolean tryFixedLength(Segment segment) {

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
            if (tryFixedLength(segment, result)) {
                segment.restrictFixedLength(result);
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException | NumberFormatException e) {
            result = sqrt(Euclidean2dGeometry.getSquareLength(segment));
            segment.restrictFixedLength(result);
            return true;
        }
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

        if (cBestFound + ccBestFound > vertices.size()) {
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
                java.awt.Toolkit.getDefaultToolkit().beep();
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
                java.awt.Toolkit.getDefaultToolkit().beep();
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
