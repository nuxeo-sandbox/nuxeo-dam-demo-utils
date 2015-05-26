package org.nuxeo.demo.dam.core.color.adapter;

import org.nuxeo.demo.dam.core.color.hsl.HSLColor;

import java.util.List;

/**
 * Created by Michaël on 16/02/2015.
 */
public interface ColorHolder {
    
    void setActualColors(List<HSLColor> colors);

    List<HSLColor> getActualColors();

    void setNormalizedColors(List<HSLColor> colors);

    List<HSLColor> getNormalizedColors();

    void setPrincipalColors(List<HSLColor> colors);

    List<HSLColor> getPrincipalColors();
    
}
