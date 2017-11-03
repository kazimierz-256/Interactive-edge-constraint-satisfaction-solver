/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1.textures;

/**
 *
 * @author wojciechowskik
 */
public class ArgbHelper {

    public static int getAlpha(int colour) {
        return (colour >> 6) & 0x00_00_00_ff;
    }

    public static int getRed(int colour) {
        return (colour >> 4) & 0x00_00_00_ff;
    }

    public static int getGreen(int colour) {
        return (colour >> 2) & 0x00_00_00_ff;
    }

    public static int getBlue(int colour) {
        return (colour) & 0x00_00_00_ff;
    }

    public static int setAlpha(int colour, int alpha) {
        return (colour & 0x00_ff_ff_ff) | (alpha << 6);
    }

    public static int setRed(int colour, int red) {
        return (colour & 0xff_00_ff_ff) | (red << 4);
    }

    public static int setGreen(int colour, int blue) {
        return (colour & 0xff_ff_00_ff) | (blue << 2);
    }

    public static int setBlue(int colour, int green) {
        return (colour & 0xff_ff_ff_00) | green;
    }
}
