/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
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

    public enum fillMethod {
        flip,
        repeat,
        stretch
    }

    public CachedImage(String filesrc) {

        BufferedImage temporaryImage = null;
        try {
            temporaryImage = ImageIO.read(new URL(filesrc));
        } catch (IOException e) {
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

    public CachedImage(int colour) {
        width = height = 1;
//        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        pixels = new int[]{colour};
        method = fillMethod.repeat;
    }

    public int getPixel(int x, int y) {
        switch (method) {
            case repeat:
//                    return image.getRaster().getDataBuffer().getElem(element);
//                return image.getRGB(x % image.getWidth(), y % image.getHeight());
                return pixels[(y % height) * width + (x % width)];
            default:
                return 0;
        }
    }
}
