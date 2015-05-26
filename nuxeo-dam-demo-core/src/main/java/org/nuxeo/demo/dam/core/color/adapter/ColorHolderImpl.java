package org.nuxeo.demo.dam.core.color.adapter;

import org.nuxeo.demo.dam.core.color.hsl.HSLColor;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.impl.ArrayProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël on 17/02/2015.
 */
public class ColorHolderImpl implements ColorHolder {
    
    private DocumentModel doc;
    
    public ColorHolderImpl(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public void setActualColors(List<HSLColor> colors) {
        ArrayProperty actuals = (ArrayProperty) doc.getProperty("colors:actual");
        actuals.setValue(HSLColor.convertToRGB(colors));
    }

    @Override
    public List<HSLColor> getActualColors() {
        String[] actuals = (String[]) doc.getPropertyValue("colors:actual");
        return actuals !=null ? convertToColors(actuals) : new ArrayList<>();
    }

    @Override
    public void setNormalizedColors(List<HSLColor> colors) {
        ArrayProperty normalizeds = (ArrayProperty) doc.getProperty("colors:normalized");
        normalizeds.setValue(HSLColor.convertToRGB(colors));
    }

    @Override
    public List<HSLColor> getNormalizedColors() {
        String[] normalized = (String[]) doc.getPropertyValue("colors:normalized");
        return normalized !=null ? convertToColors(normalized) : new ArrayList<>();
    }

    @Override
    public void setPrincipalColors(List<HSLColor> colors) {
        ArrayProperty normalizeds = (ArrayProperty) doc.getProperty("colors:principal");
        normalizeds.setValue(HSLColor.convertToRGB(colors));
    }

    @Override
    public List<HSLColor> getPrincipalColors() {
        String[] principals = (String[]) doc.getPropertyValue("colors:principal");
        return principals !=null ? convertToColors(principals) : new ArrayList<>();
    }


    protected List<HSLColor> convertToColors(String[] colorsStr) {
        List<HSLColor> colors = new ArrayList<>();
        for (String colorStr : colorsStr) {
            colors.add(new HSLColor(colorStr));
        }
        return colors;
    }
}
