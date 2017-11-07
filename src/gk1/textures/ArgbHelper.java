/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

import javafx.scene.paint.Color;

/**
 *
 * @author wojciechowskik
 */
public class ArgbHelper {

    public static int getAlpha(int color) {
        return (color >> 24) & 0x00_00_00_ff;
    }

    public static int getRed(int color) {
        return (color >> 16) & 0x00_00_00_ff;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0x00_00_00_ff;
    }

    public static int getBlue(int color) {
        return (color) & 0x00_00_00_ff;
    }

    public static int setAlpha(int color, int alpha) {
        return (color & 0x00_ff_ff_ff) | (alpha << 24);
    }

    public static int setRed(int color, int red) {
        return (color & 0xff_00_ff_ff) | (red << 16);
    }

    public static int setGreen(int color, int green) {
        return (color & 0xff_ff_00_ff) | (green << 8);
    }

    public static int setBlue(int color, int blue) {
        return (color & 0xff_ff_ff_00) | blue;
    }

    public static int fromColor(Color doubleColor) {
        int intColor = 0;
        intColor = ArgbHelper.setAlpha(intColor, ArgbHelper.toNormalizedInt(
                256 * doubleColor.getOpacity()
        ));
        intColor = ArgbHelper.setRed(intColor, ArgbHelper.toNormalizedInt(
                256 * doubleColor.getRed()
        ));
        intColor = ArgbHelper.setGreen(intColor, ArgbHelper.toNormalizedInt(
                256 * doubleColor.getGreen()
        ));
        intColor = ArgbHelper.setBlue(intColor, ArgbHelper.toNormalizedInt(
                256 * doubleColor.getBlue()
        ));
        return intColor;
    }

    private static int toNormalizedInt(double color) {
        if (color > 255d) {
            return 255;
        } else if (color < 0d) {
            return 0;
        } else {
            return (int) color;
        }
    }

    static Color toColor(int pixel) {
        return new Color(
                getRed(pixel) / 256d,
                getGreen(pixel) / 256d,
                getBlue(pixel) / 256d,
                getAlpha(pixel) / 256d
        );
    }
}
