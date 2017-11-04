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

    public static int getAlpha(int colour) {
        return (colour >> 24) & 0x00_00_00_ff;
    }

    public static int getRed(int colour) {
        return (colour >> 16) & 0x00_00_00_ff;
    }

    public static int getGreen(int colour) {
        return (colour >> 8) & 0x00_00_00_ff;
    }

    public static int getBlue(int colour) {
        return (colour) & 0x00_00_00_ff;
    }

    public static int setAlpha(int colour, int alpha) {
        return (colour & 0x00_ff_ff_ff) | (alpha << 24);
    }

    public static int setRed(int colour, int red) {
        return (colour & 0xff_00_ff_ff) | (red << 16);
    }

    public static int setGreen(int colour, int green) {
        return (colour & 0xff_ff_00_ff) | (green << 8);
    }

    public static int setBlue(int colour, int blue) {
        return (colour & 0xff_ff_ff_00) | blue;
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
