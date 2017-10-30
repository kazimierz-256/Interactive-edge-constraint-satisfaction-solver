/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

import gk1.LightSource;
import java.util.Collection;

/**
 *
 * @author kazimierz
 */
public class Texture {

    private CachedImage texture;
    private CachedImage normals;
    private CachedImage heights;

    public Texture() {
//        texture = new CachedImage("gk1/texture.jpg");
//        normals = new CachedImage("gk1/normals.jpg");
//        heights = new CachedImage("gk1/highlights.jpg");
        texture = new CachedImage(0);
        normals = new CachedImage(0x123456);
        heights = new CachedImage(0xac45e7f);
    }

    public Texture(CachedImage texture, CachedImage normals, CachedImage heights) {
        this.texture = texture;
        this.normals = normals;
        this.heights = heights;
    }

    public int getPixel(int x, int y, Collection<LightSource> lights) {
        return 0xaaffffff;
    }

    public CachedImage getTexture() {
        return texture;
    }

    public void setTexture(CachedImage texture) {
        this.texture = texture;
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
}
