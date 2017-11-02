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
        this.method = fillMethod.repeat;
    }

    public CachedImage(int colour) {
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        System.out.println(colour);
        image.getRaster().getDataBuffer().setElem(0, colour);
        method = fillMethod.repeat;
    }

    public int getPixel(int x, int y) {
        switch (method) {
            case repeat:
//                    return image.getRaster().getDataBuffer().getElem(element);
                return image.getRGB(x % image.getWidth(), y % image.getHeight());
            default:
                return 0;
        }
    }
}
