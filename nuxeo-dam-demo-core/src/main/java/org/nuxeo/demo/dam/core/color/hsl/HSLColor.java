package org.nuxeo.demo.dam.core.color.hsl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël on 22/05/2015.
 */
public class HSLColor {

    public static final int MIN_HUE = 0;
    public static final int MAX_HUE = 360;
    public static final int MIN_SATURATION = 0;
    public static final int MAX_SATURATION = 100;
    public static final int MIN_LIGHTNESS = 0;
    public static final int MAX_LIGHTNESS = 100;

    public static HSLColor black(int count) {
        return new HSLColor(MIN_HUE,MIN_SATURATION,MIN_LIGHTNESS,count);
    }

    public static HSLColor white(int count) {
        return new HSLColor(MIN_HUE,MIN_SATURATION,MAX_LIGHTNESS,count);
    }

    public static HSLColor gray(int count) {
        return new HSLColor(MIN_HUE,MIN_SATURATION,MAX_LIGHTNESS/2,count);
    }

    /**
     * Converts the input list to a list of hexadecimal rgb code
     * @param colors the color object list
     * @return a list of hex rgb codes
     */
    public static List<String> convertToRGB(List<HSLColor> colors) {
        List<String> rgbs = new ArrayList<>();
        for (HSLColor color : colors) {
            rgbs.add(color.toRGBHex());
        }
        return rgbs;
    }


    // Hue angle (0-360)
    protected float hue;
    // Saturation percentage (0-100)
    protected float saturation;
    // Lightness percentage (0-100)
    protected float lightness;
    //Pixel count
    protected int count;

    public HSLColor(int hue, int saturation, int lightness, int count) {
        this.saturation = saturation;
        this.lightness = lightness;
        this.hue = hue;
        this.count = count;
    }

    public HSLColor(float hue, float saturation, float lightness, int count) {
        this.saturation = (int) saturation;
        this.lightness = (int) lightness;
        this.hue = (int) hue;
        this.count = count;
    }

    public HSLColor(String rgbHex) {
        int rgb = Integer.parseInt(rgbHex,16);
        Color color = new Color(rgb);
        float hsl[] = new float[3];
        HSLRGBConverter.fromRGB(color,hsl);
        this.hue = Math.round(hsl[0]*360);
        this.saturation = Math.round(hsl[1]*100);
        this.lightness = Math.round(hsl[2]*100);
        this.count = 0;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getLightness() {
        return lightness;
    }

    public int getCount() {
        return count;
    }

    public void addToCount(int count) {
        this.count += count;
    }

    public long toRGB() {
        return HSLRGBConverter.toRGB(hue / 360.0f, saturation / 100.0f, lightness / 100.0f);
    }

    public String toRGBHex() {
        return String.format("%08X",this.toRGB()).substring(2, 8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HSLColor color = (HSLColor) o;
        if (hue != color.hue) return false;
        if (saturation != color.saturation) return false;
        return lightness == color.lightness;
    }
}
