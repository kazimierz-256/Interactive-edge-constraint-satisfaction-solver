/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import static java.lang.Math.abs;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author Kazimierz
 */
public class Viewer {

    private Canvas drawing;
    private GraphicsContext graphicsContext;
    private PixelWriter pixelWriter;
    private double width;
    private double height;

    public Viewer(Canvas drawing, double width, double height) {
        this.drawing = drawing;
        this.graphicsContext = drawing.getGraphicsContext2D();
        this.pixelWriter = graphicsContext.getPixelWriter();
        this.width = width;
        this.height = height;
    }

    public void draw(Segment segment) {
//        graphicsContext.strokeLine(
//                segment.getBeginning().getX(),
//                segment.getBeginning().getY(),
//                segment.getEnd().getX(),
//                segment.getEnd().getY());
        Vertex b = segment.getBeginning();
        Vertex e = segment.getEnd();
        boolean steep = abs(e.getY() - b.getY()) > abs(e.getX() - b.getX());
        if (b.getX() > e.getX()) {
            if (b.getY() > e.getY()) {
                if (steep) {
                    BresenhamSteepUp(e, b);
                } else {
                    BresenhamUp(e, b);
                }
            } else {
                if (steep) {
                    BresenhamSteepDown(e, b);
                } else {
                    BresenhamDown(e, b);
                }
            }
        } else {
            if (e.getY() > b.getY()) {
                if (steep) {
                    BresenhamSteepUp(b, e);
                } else {
                    BresenhamUp(b, e);
                }
            } else {
                if (steep) {
                    BresenhamSteepDown(b, e);
                } else {
                    BresenhamDown(b, e);
                }
            }
        }
        double x, y;

        switch (segment.getConstraint()) {
            case vertical:
                x = segment.getCenterX() + 4;
                y = segment.getCenterY() - 10;
                graphicsContext.strokeRect(x, y, 4, 20);
                break;
            case horizontal:
                x = segment.getCenterX() - 10;
                y = segment.getCenterY() - 8;
                graphicsContext.strokeRect(x, y, 20, 4);
                break;
            case fixedLength:
                x = segment.getCenterX() + 4;
                y = segment.getCenterY() + 4;
//                graphicsContext.strokeRect(x, y, 10, 10);
                graphicsContext.strokeText(
                        String.format("[%2.1f]", segment.getConstraintLength()), x, y);
                break;
        }
        // do skopiowania algorytm Bresenhama
    }

    private void BresenhamDown(Vertex left, Vertex right) {
        Color color = Color.RED;

        int x1 = (int) left.getX();
        int x2 = (int) right.getX();
        int y1 = (int) left.getY();
        int y2 = (int) right.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;
        int d = dy * 2 - dx; //initial value of d
        int incrE = dy * 2; //increment used for move to E
        int incrSE = (dy + dx) * 2; //increment used for move to NE
        int twiceDy = dy * 2;
        int twiceNegativeDx = (-dx) * 2;
        pixelWriter.setColor(x1, y1, color);
        int x = x1;
        int y = y1;
        while (x < x2) {

            if (d > 0) {
                d += incrE;
                x++;
            } else {
                d += incrSE;
                x++;
                y--;
            }
            pixelWriter.setColor(x, y, color);
        }
    }

    private void BresenhamSteepDown(Vertex left, Vertex right) {
        Color color = Color.CORAL;

        int x1 = (int) left.getX();
        int x2 = (int) right.getX();
        int y1 = (int) left.getY();
        int y2 = (int) right.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;
        int d = dy * 2 - dx; //initial value of d
        int incrS = dx * 2; //increment used for move to E
        int incrSE = (dy + dx) * 2; //increment used for move to NE
        int twiceDy = dy * 2;
        int twiceNegativeDx = (-dx) * 2;
        pixelWriter.setColor(x1, y1, color);
        int x = x1;
        int y = y1;
        while (y > y2) {

            if (d > 0) {
                d += incrSE;
                x++;
                y--;
            } else {
                d += incrS;
                y--;
            }
            pixelWriter.setColor(x, y, color);
        }
    }

    private void BresenhamUp(Vertex left, Vertex right) {
        Color color = Color.CRIMSON;

        int x1 = (int) left.getX();
        int x2 = (int) right.getX();
        int y1 = (int) left.getY();
        int y2 = (int) right.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;
        int d = dy * 2 - dx; //initial value of d
        int incrE = dy * 2; //increment used for move to E
        int incrNE = (dy - dx) * 2; //increment used for move to NE
        int twiceDy = dy * 2;
        int twiceNegativeDx = (-dx) * 2;
        pixelWriter.setColor(x1, y1, color);
        int x = x1;
        int y = y1;
        while (x < x2) {

            if (d < 0) {
                d += incrE;
                x++;
            } else {
                d += incrNE;
                x++;
                y++;
            }
            pixelWriter.setColor(x, y, color);
        }
    }

    private void BresenhamSteepUp(Vertex left, Vertex right) {
        Color color = Color.SADDLEBROWN;

        int x1 = (int) left.getX();
        int x2 = (int) right.getX();
        int y1 = (int) left.getY();
        int y2 = (int) right.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;
        int d = dy * 2 - dx; //initial value of d
        int incrN = -dx * 2; //increment used for move to E
        int incrNE = (dy - dx) * 2; //increment used for move to NE
        int twiceDy = dy * 2;
        int twiceNegativeDx = (-dx) * 2;
        pixelWriter.setColor(x1, y1, color);
        int x = x1;
        int y = y1;
        while (y < y2) {

            if (d < 0) {
                d += incrNE;
                x++;
                y++;
            } else {
                d += incrN;
                y++;
            }
            pixelWriter.setColor(x, y, color);
        }
    }

    public void draw(Vertex vertex) {
        double x = vertex.getX();
        double y = vertex.getY();
        double w, h;
        w = h = 6;
        graphicsContext.fillOval(x - w / 2, y - h / 2, w, h);

        if (vertex.isFixed()) {
            w = h = 10;
            graphicsContext.strokeOval(x - w / 2, y - h / 2, w, h);
        }

        graphicsContext.strokeText(vertex.toString(), x, y - 10);
    }

    public void clear() {
        graphicsContext.clearRect(0, 0, width, height);
    }

    public void setWidth(double width) {
        this.width = width;
        drawing.setWidth(width);
    }

    public void setHeight(double height) {
        this.height = height;
        drawing.setHeight(height);
    }

}
