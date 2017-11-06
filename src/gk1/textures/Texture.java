/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

import gk1.LightSource;
import gk1.Vector;
import static java.lang.Math.floor;
import java.util.ArrayList;

/**
 *
 * @author kazimierz
 */
public class Texture {

    private CachedImage background;
    private CachedImage normals;
    private CachedImage heights;

    public Texture() {
// pizza https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/320px-Pepperoni_pizza.jpg
//https://upload.wikimedia.org/wikipedia/commons/4/43/Radiosity-yes.jpg
//https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Painters_problem.svg/340px-Painters_problem.svg.png
        // pizza
        background = new CachedImage(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Pepperoni_pizza.jpg/640px-Pepperoni_pizza.jpg"
        );
        //https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Normal_map_example_-_Map.png/600px-Normal_map_example_-_Map.png
        // abstract shapes
        normals = new CachedImage(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/Normal_map_example_-_Map.png/480px-Normal_map_example_-_Map.png"
        );
        // earth
        // https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Srtm_ramp2.world.21600x10800.jpg/800px-Srtm_ramp2.world.21600x10800.jpg
        // https://upload.wikimedia.org/wikipedia/commons/5/57/Heightmap.png
        // https://upload.wikimedia.org/wikipedia/commons/c/c3/Heightmap_of_Trencrom_Hill.png
        // https://www.jpl.nasa.gov/spaceimages/images/largesize/PIA03378_hires.jpg
        heights = new CachedImage(
                "https://www.jpl.nasa.gov/spaceimages/images/largesize/PIA03378_hires.jpg"
        );
    }

    public Texture(CachedImage texture, CachedImage normals, CachedImage heights) {
        this.background = texture;
        this.normals = normals;
        this.heights = heights;
    }

    public int getPixel(double leftmost, double bottommost, double z, int x, int y, ArrayList<LightSource> lights) {
        // lambert reflectance model
        int texturePixel = background.getPixel(x, y);
        int normalPixel = normals.getPixel(x, y);
        double heightScale = 1d / 128;
        double pixelComputedHeight = ArgbHelper.getBlue(heights.getPixel(x, y)) * heightScale;

        // -y since the coordinates are flipped
        Vector N = new Vector((ArgbHelper.getRed(normalPixel) - 127) / 128d,
                -(ArgbHelper.getGreen(normalPixel) - 127) / 128d,
                ArgbHelper.getBlue(normalPixel) / 255d);

        Vector T = new Vector(1d, 0d, -N.x);
        T.scale(ArgbHelper.getBlue(heights.getPixel(x + 1, y)) * heightScale - pixelComputedHeight);
        Vector B = new Vector(0d, 1d, -N.y);
        B.scale(ArgbHelper.getBlue(heights.getPixel(x, y + 1)) * heightScale - pixelComputedHeight);

        N.add(T);
        N.add(B);
        double resultingRed = 0;
        double resultingGreen = 0;
        double resultingBlue = 0;

        for (LightSource light : lights) {
            int lightColour = light.getLightColour();

            Vector L = Vector.fromVertex(light.getPosition());
            L.minus(new Vector(leftmost + x, bottommost + y, z + pixelComputedHeight));
            double dotProduct = N.dotProductNormalized(L);

            // personal touch :)
            dotProduct *= dotProduct;
            dotProduct *= dotProduct;
            dotProduct *= light.getIntensity() / Math.max(1d, Math.log(L.getSquareLength()));

            if (dotProduct > 0d) {
                resultingRed += (dotProduct * ArgbHelper.getRed(lightColour)) * ArgbHelper.getRed(texturePixel);
                resultingGreen += (dotProduct * ArgbHelper.getGreen(lightColour)) * ArgbHelper.getGreen(texturePixel);
                resultingBlue += (dotProduct * ArgbHelper.getBlue(lightColour)) * ArgbHelper.getBlue(texturePixel);
            }
        }

        int resultingPixel = 0xff000000
                | (normalizeTo255(resultingRed) << 16)
                | (normalizeTo255(resultingGreen) << 8)
                | normalizeTo255(resultingBlue);
        return resultingPixel;

    }

    public CachedImage getBackground() {
        return background;
    }

    public void setBackground(CachedImage background) {
        this.background = background;
    }

    public CachedImage getNormals() {
        return normals;
    }

    public void setNormals(CachedImage normals) {
        this.normals = normals;
    }

    public CachedImage getHeights() {
        return heights;
    }

    public void setHeights(CachedImage heights) {
        this.heights = heights;
    }

    private int normalizeTo255(double color) {
        color /= 255;
        return color < 255 ? (int) floor(color) : 255;
    }
}
