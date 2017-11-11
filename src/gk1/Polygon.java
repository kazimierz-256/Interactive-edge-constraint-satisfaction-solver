/*
 * To change this license header, choose License Headers in Projection Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static gk1.GK1.model;
import gk1.areas.Area;
import gk1.textures.ArgbHelper;
import gk1.textures.Texture;
import java.awt.image.BufferedImage;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author Kazimierz
 */
public class Polygon implements Drawable {

    private Vertex rememberedMovedFirst;

    public boolean isConvex() {
        Vertex last = vertices.get(vertices.size() - 1);
        Vertex first = vertices.get(0);
        Vertex second = vertices.get(1);
        double dx1 = first.getX() - last.getX();
        double dy1 = first.getY() - last.getY();
        double dx2 = second.getX() - first.getX();
        double dy2 = second.getY() - first.getY();
        double initialCrossProduct = Math.signum(dx1 * dy2 - dy1 * dx2);

        if (initialCrossProduct == 0) {
            return false;
        }

        for (int i = 0, max = vertices.size() - 1; i < max; i++) {
            last = first;
            first = second;
            second = vertices.get((i + 2) % vertices.size());

            dx1 = first.getX() - last.getX();
            dy1 = first.getY() - last.getY();
            dx2 = second.getX() - first.getX();
            dy2 = second.getY() - first.getY();

            if (initialCrossProduct != Math.signum(dx1 * dy2 - dy1 * dx2)) {
                return false;
            }
        }

        return true;
    }

    boolean hasTouched(MouseEvent event) {
        return this.mousePressed(event).hasTouched;
    }

    Polygon clip(Polygon polygon) {
        // assuming the current polygon is convex, the polygon in the parameter is not necessarily

        ArrayList<Vertex>[] inputOutput = new ArrayList[2];

        int input = 0;
        int output = 1;
        int pIndex;
        Boolean isOutside = false;
        Vertex pp;
        Vertex p;
        Vertex inside;
        Segment e;

        // for i == 0 początkowa iteracja która różni się od właściwej tylko tym, że input jest w tej chwili polygon oraz clipVertexIndex jest równy zeru
        // ciało pętli jest lepiej wyjaśniane
        if (polygon.vertices.size() > 0 && vertices.size() > 0) {
            // odosobniona część pętli gdzie clipVertexIndex == clipPolygon.Length - 1
            e = new Segment(vertices.get(vertices.size() - 1), vertices.get(0));
            inputOutput[output] = new ArrayList<>((int) (polygon.vertices.size() * 1.2));

            pp = polygon.vertices.get(polygon.vertices.size() - 1);
            inside = vertices.get(1);
            for (pIndex = 0; pIndex < polygon.vertices.size(); pIndex++) {
                p = polygon.vertices.get(pIndex);
                if (PolygonAlgorithms.IsSameSide(p, inside, e)) {
                    isOutside = PolygonAlgorithms.IsOutside(p, inside, e);
                    if (isOutside && !PolygonAlgorithms.IsSameSide(pp, inside, e)) {
                        inputOutput[output].add(PolygonAlgorithms.GetIntersectionVertexFast(new Segment(pp, p), e));
                    }

                    inputOutput[output].add(p);
                } else if (PolygonAlgorithms.IsSameSideExclusive(pp, inside, e)) {
                    inputOutput[output].add(PolygonAlgorithms.GetIntersectionVertexFast(new Segment(pp, p), e));
                }

                pp = p;
            }

            // pętla właściwa
            for (int clipVertexIndex = 0; clipVertexIndex < vertices.size() - 1 && inputOutput[output].size() > 0; clipVertexIndex++) {
                e = new Segment(vertices.get(clipVertexIndex), vertices.get(clipVertexIndex + 1));
                // zamiana input z output bez zmiennej pomocniczej
                input = output;
                output = 1 - input;
                // zadbałem o zaalokowanie rozsądnie dużej pamięci na wynik
                inputOutput[output] = new ArrayList<>((int) (inputOutput[input].size() * 1.2));

                pp = inputOutput[input].get(inputOutput[input].size() - 1);
                // punkt w środku wielokąta wypukłego
                inside = vertices.get((clipVertexIndex < vertices.size() - 2) ? clipVertexIndex + 2 : 0);
                for (pIndex = 0; pIndex < inputOutput[input].size(); pIndex++) {
                    p = inputOutput[input].get(pIndex);
                    if (PolygonAlgorithms.IsSameSide(p, inside, e)) {
                        isOutside = PolygonAlgorithms.IsOutside(p, inside, e);
                        // isOutside gwarantuje, że p nie leży na prostej wyznaczonej przez krawędź e
                        if (isOutside && !PolygonAlgorithms.IsSameSide(pp, inside, e)) {
                            inputOutput[output].add(PolygonAlgorithms.GetIntersectionVertexFast(new Segment(pp, p), e));
                        }

                        inputOutput[output].add(p);
                    } else if (PolygonAlgorithms.IsSameSideExclusive(pp, inside, e)) {
                        // gdy pp leży na prostej wyznaczonej przez krawędź e, to już ten punkt musiałem dodać w poprzedniej iteracji w instrukcji warunkowej powyżej
                        // stąd wyspecjalizowana funkcja IsSameSideExclusive w której sprytnie i szybko sprawdzam tylko > 0 zamiast >= 0
                        inputOutput[output].add(PolygonAlgorithms.GetIntersectionVertexFast(new Segment(pp, p), e));
                    }

                    pp = p;
                }
            }

        }

        return new Polygon(
                polygon.name,
                polygon.automaticRelations,
                inputOutput[output],
                polygon.getTexture()
        );
    }

    public Boolean getAutomaticRelations() {
        return automaticRelations;
    }

    public void setAutomaticRelations(Boolean automaticRelations) {
        this.automaticRelations = automaticRelations;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    void setArtificialLight(boolean artificial) {
        isArtificialLight = artificial;
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

    private boolean isArtificialLight = false;
    private Texture texture;
    private boolean automaticRelations = false;
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

    public Reaction toggleAutomaticRelations(Boolean isAutomatic) {
        setAutomaticRelations(isAutomatic);
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

            if (upper.getYint() == lower.getYint()) {
                continue;
            } else if (lower.getY() > upper.getY()) {
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
                    new ActiveEdge(
                            upper.getYint(),
                            lower.getXint(),
                            m_inverse,
                            Math.max(upper.getX(), lower.getX())
                    )
            );
        }

        LinkedList<ActiveEdge> activeEdges = new LinkedList<>();

        ArrayList<Scanline> scanlines = new ArrayList<>(height);

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
                    int from = (int) (previousEdge.x - leftmost);
                    int to = (int) (edge.x - leftmost);
                    scanlines.add(new Scanline(from, to, localHeight));
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

        final double z = getZ();
        final double leftmostf = leftmost;
        final double bottommostf = bottommost;
        ArrayList<LightSource> lights = new ArrayList<>();

        if (isArtificialLight) {
            Color artificialLightColor = ((ColorPicker) GK1.accessScene.lookup("#lightColor")).getValue();
            lights.add(new LightSource(
                    new Vertex(0, 0, 100000),
                    ArgbHelper.fromColor(
                            artificialLightColor
                    ),
                    2d * Math.log(100000 * 100000),
                    null,
                    null
            ));
        } else {
            lights.addAll(model.getLightsList());
        }

        // twice the framerate :)
        scanlines.stream().parallel().forEach((scanline) -> {
            for (int j = scanline.from; j < scanline.to; j++) {
                pixels[scanline.localHeight * width + j] = getTexture().getPixel(
                        leftmostf, bottommostf, z, j, scanline.localHeight, lights);
            }
        });

        BufferedImage generatedTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        generatedTexture.getRaster().setDataElements(0, 0, width, height, pixels);

        // finally paint the image
        viewer.drawImage(generatedTexture, leftmost, bottommost, width, height);
    }

    public Polygon(String name, Boolean automaticRelations, Collection<Vertex> vertices, Texture texture) {
        if (vertices.size() < 3) {
            //throw new Exception("A polygon has to have at least three vertices.");
            return;
        }
        this.name = name;
        this.automaticRelations = automaticRelations;
        this.texture = texture;
        this.vertices = new ArrayList<>();
        this.segments = new ArrayList<>();

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
            viewer.draw(segment, getAutomaticRelations());
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
        reaction.hasTouched = true;
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
        if (getAutomaticRelations() && moveVertex != null) {
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
//                GK1.viewer.drawLastModel();
            });
            menu.getItems().add(menuItem);

            // horizontal segment
            if (segment.getConstraint() != Segment.segmentConstraint.horizontal
                    && segment.isSafeToRestrictHorizontal()) {
                menuItem = new MenuItem("Make horizontal if possible");
                menuItem.setOnAction((ActionEvent e) -> {
                    if (tryHorizontal(segment)) {
//                        GK1.viewer.drawLastModel();
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
//                        GK1.viewer.drawLastModel();
                    }
                });
                menu.getItems().add(menuItem);
            }

            // fixed length segment
            menuItem = new MenuItem("Fix length if possible");
            menuItem.setOnAction((ActionEvent e) -> {
                if (tryFixedLength(segment)) {
//                    GK1.viewer.drawLastModel();
                }
            });

            menu.getItems().add(menuItem);
            if (segment.getConstraint() != Segment.segmentConstraint.free) {

                // free the segment
                menuItem = new MenuItem("Free");
                menuItem.setOnAction((ActionEvent e) -> {
                    segment.restrictFree();
//                    GK1.viewer.drawLastModel();
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
//                    GK1.viewer.drawLastModel();
                });
                menu.getItems().add(menuItem);
            }

            // toggle vertex stiffness
            menuItem = new MenuItem(String.format(
                    vertex.isFixed() ? "Unfreeze" : "Freeze"));
            menuItem.setOnAction((ActionEvent e) -> {
                vertex.setFixed(!vertex.isFixed());
//                GK1.viewer.drawLastModel();
            });
            menu.getItems().add(menuItem);

            menuItems.add(menu);
        });

        if (!menuItems.isEmpty() || isInsidePolygon(position)) {
            MenuItem menuItem = new MenuItem("Remove polygon");
            menuItem.setOnAction((ActionEvent e) -> {
                GK1.model.unregisterPolygon(this);
//                GK1.viewer.drawLastModel();
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
