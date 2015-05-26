package org.nuxeo.demo.dam.core.color.palette;

/**
 * Created by Michaël on 22/05/2015.
 */
public class NxPalette {

    public static final String COLOR_NAMES[] = {
            "red","orange","yellow","green","cyan","blue","purple","pink","white","gray","black"};

    public static final float HUE[][] = {
            {0,15, 0},
            {15,45, 30},
            {45, 60, 52.5f},
            {60,165, 112.5f},
            {165, 200, 182.5f},
            {200,270, 235},
            {270,285, 277.5f},
            {285, 345, 305},
            {345,360, 0}};

    public static final int SATURATION = 80;

    public static final int LIGHTNESS = 50;

    public static final int WHITE_LIGHTNESS_LOWER_THRESHOLD = 95;

    public static final int BLACK_LIGHTNESS_UPPER_THRESHOLD = 5;

    public static final int GRAY_SATURATION_LOWER_THRESHOLD = 10;

}
