/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

import gk1.Euclidean2dGeometry;
import gk1.EuclideanVector;
import gk1.Vertex;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/**
 *
 * @author kazimierz
 */
public class CachedImage {

    private BufferedImage image;
    private int[] pixels;
    private int width;
    private int height;
    private fillMethod method;
    private String url;
    private textureType type;
    // should create seperate classes but there's no time
    private Vertex mousePosition;

    public String getUrl() {
        return url;
    }

    public Color getColor() {
        if (type == textureType.color) {
            return ArgbHelper.toColor(pixels[0]);
        } else {
            return Color.BLACK;
        }
    }

    public textureType getType() {
        return type;
    }

    public Vertex getMousePosition() {
        return mousePosition;
    }

    public void setMousePosition(Vertex mousePosition) {
        this.mousePosition = mousePosition;
    }

    public enum fillMethod {

        flip,
        repeat,
        stretch
    }

    public enum textureType {

        color,
        urlStream,
        mouseBump
    }

    public CachedImage(String filesrc) {
        BufferedImage temporaryImage = null;
        type = textureType.urlStream;
        url = filesrc;
        File possibleFile = new File(filesrc);
        try {

            if (possibleFile.exists() && !possibleFile.isDirectory()) {
                temporaryImage = ImageIO.read(possibleFile);
            } else {
                temporaryImage = ImageIO.read(new URL(filesrc));
            }
        } catch (IOException e) {
            CreateSinglePixel(0xff_ff_ff_ff);
            return;
        }
        image = temporaryImage;
        width = image.getWidth();
        height = image.getHeight();

        pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[j * width + i] = image.getRGB(i, j);
            }
        }

        this.method = fillMethod.repeat;
    }

    public CachedImage(int color) {
        CreateSinglePixel(color);
    }

    public CachedImage(Vertex mousePosition) {
        this.type = textureType.mouseBump;
        this.mousePosition = mousePosition;
    }

    private void CreateSinglePixel(int color) {
        type = textureType.color;
        width = height = 1;
//        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        pixels = new int[]{color};
        method = fillMethod.repeat;
    }

    public int getPixel(int x, int y) {
        // I hope this is much faster than the switch statement

        if (method == fillMethod.repeat) {
//                    return image.getRaster().getDataBuffer().getElem(element);
//                return image.getRGB(x % image.getWidth(), y % image.getHeight());
            return pixels[(y % height) * width + (x % width)];
        } else {
            return 0;
        }
    }

    public EuclideanVector getNormal(int x, int y, double leftmost, double bottommost) {
        if (type == textureType.color || type == textureType.urlStream) {

            int normalPixel = getPixel(x, y);
            return new EuclideanVector(
                    (ArgbHelper.getRed(normalPixel) - 127) / 128d,
                    (127 - ArgbHelper.getGreen(normalPixel)) / 128d,
                    ArgbHelper.getBlue(normalPixel) / 255d
            );
        } else if (type == textureType.mouseBump) {
            double radiusSquared = 10000d;
            Vertex position = new Vertex(leftmost + x, bottommost + y);
            double squareDistance = Euclidean2dGeometry.getSquareDistance(mousePosition, position);
            if (squareDistance > radiusSquared) {
                return new EuclideanVector(0, 0, 1);
            }

            return new EuclideanVector(
                    position.getX() - mousePosition.getX(),
                    position.getY() - mousePosition.getY(),
                    Math.sqrt(radiusSquared - squareDistance)
            );
        }
        return new EuclideanVector(0, 0, 1);
    }
}
