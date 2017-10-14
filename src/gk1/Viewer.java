/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

/**
 *
 * @author Kazimierz
 */
public class Viewer {

    private GraphicsContext graphicsContext;
    private PixelWriter pixelWriter;
    private double width;
    private double height;

    public Viewer(GraphicsContext graphicsContext, double width, double height) {
        this.graphicsContext = graphicsContext;
        this.pixelWriter = graphicsContext.getPixelWriter();
        this.width = width;
        this.height = height;
    }

    public void draw(Segment segment) {
        graphicsContext.strokeLine(
                segment.getBeginning().getX(),
                segment.getBeginning().getY(),
                segment.getEnd().getX(),
                segment.getEnd().getY());
        // do skopiowania algorytm Bresenhama
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
}
