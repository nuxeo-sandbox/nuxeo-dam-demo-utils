package org.nuxeo.demo.dam.core.color.hsl;

import org.nuxeo.demo.dam.core.color.adapter.ColorHolder;
import org.nuxeo.demo.dam.core.color.palette.NxPalette;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël on 24/05/2015.
 */
public class HSLNormalizer {

    /**
     * The normalize the actual colors and store the normalized value in the color holder
     * @param actualColors
     * @param colorHolder
     */
    public static void normalizeColor(List<HSLColor> actualColors, ColorHolder colorHolder) {

        int totalPixelCount = getPixelCount(actualColors);
        int normalizeThreshold = (int) (totalPixelCount *0.01f);
        int principalThreshold = (int) (totalPixelCount *0.25f);
        List<HSLColor> normalizedColors = new ArrayList<>();
        List<HSLColor> principalColors = new ArrayList<>();

        // Normalize
        for (HSLColor color : actualColors) {
            if (color.getCount()<normalizeThreshold) continue;
            HSLColor normalizedColor = normalizeColor(color);
            if (normalizedColor!=null) addColorToList(normalizedColors, normalizedColor);
        }

        //Get principal colors
        for (HSLColor color : normalizedColors) {
            if (color.getCount()<principalThreshold) continue;
            addColorToList(principalColors, color);
        }

        colorHolder.setActualColors(actualColors);
        colorHolder.setNormalizedColors(normalizedColors);
        colorHolder.setPrincipalColors(principalColors);
    }


    /**
     * @param colors A list of color objects
     * @return the total pixel count
     */
    protected static int getPixelCount(List<HSLColor> colors) {
        int count = 0;
        for (HSLColor color : colors) {
            count += color.getCount();
        }
        return count;
    }


    /**
     * @param hue
     * @return the closest hue in NxPalette
     */
    protected static float getNormalizeHue(float hue) {
        int i=0;
        hue = hue % 360;
        while (i< NxPalette.HUE.length && !(hue >= NxPalette.HUE[i][0] && hue < NxPalette.HUE[i][1])) {
            i++;
        }
        return NxPalette.HUE[i][2];
    }


    /**
     * @param color An HSL color
     * @return the color object corresponding to the closest color defined in {@link NxPalette}
     */
    protected static HSLColor normalizeColor(HSLColor color) {

        // Handle black
        if (color.getLightness()<= NxPalette.BLACK_LIGHTNESS_UPPER_THRESHOLD) {
            return HSLColor.black(color.getCount());
        }

        // Handle white
        if (color.getLightness() >= NxPalette.WHITE_LIGHTNESS_LOWER_THRESHOLD) {
            return HSLColor.white(color.getCount());
        }

        // Handle gray
        if (color.getSaturation()<= NxPalette.GRAY_SATURATION_LOWER_THRESHOLD) {
            return HSLColor.gray(color.getCount());
        }

        // Handle desaturated color
        if (color.getSaturation()> NxPalette.GRAY_SATURATION_LOWER_THRESHOLD
                && color.getSaturation()<25) {
            return null;
        }

        // Handle very light color
        if (color.getLightness()< NxPalette.WHITE_LIGHTNESS_LOWER_THRESHOLD
                && color.getLightness()>= 65) {
            return null;
        }

        // Handle very dark color
        if (color.getLightness()> NxPalette.BLACK_LIGHTNESS_UPPER_THRESHOLD
                && color.getLightness()<=30 ) {
            return null;
        }

        return new HSLColor(getNormalizeHue(color.getHue()), NxPalette.SATURATION, NxPalette.LIGHTNESS,color.getCount());
    }


    /**
     * Adds the color to the list if not already present, otherwise just adds the pixel count
     * @param colors the list of color objects
     * @param color the color object to add
     */
    protected static void addColorToList(List<HSLColor> colors, HSLColor color) {
        HSLColor sameColor = null;
        for(HSLColor current : colors) {
            if (current.equals(color)) sameColor = current;
        }
        if (sameColor==null) {
            colors.add(color);
        } else {
            sameColor.addToCount(color.getCount());
        }
    }


}
