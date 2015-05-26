/*
 * (C) Copyright ${year} Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 */

package org.nuxeo.demo.dam.core.operations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.demo.dam.core.color.adapter.ColorHolder;
import org.nuxeo.demo.dam.core.color.hsl.HSLColor;
import org.nuxeo.demo.dam.core.color.hsl.HSLNormalizer;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Operation(
        id = ParseIMColorBlobOp.ID,
        category = Constants.CAT_CONVERSION,
        label = "Parse and normalize color histogram",
        description = "Parse and normalize a list of colors generated with ImageMagick and store it in the given " +
                      "document model. Return the document model")
public class ParseIMColorBlobOp {

    public static final String ID = "Conversion.ParseIMColorBlob";

    private static final Log log = LogFactory.getLog(ParseIMColorBlobOp.class);

    @Param(name = "documentModel", required = true)
    protected DocumentModel doc;

    @OperationMethod
    public DocumentModel run(Blob blob) {
        return parseBlob(blob);
    }

    public DocumentModel parseBlob(Blob blob) {
        List<HSLColor> actualColors = extractColors(blob);
        HSLNormalizer.normalizeColor(actualColors, doc.getAdapter(ColorHolder.class));
        return doc;
    }

    /**
     *
     * @param blob A color text list extracted with ImageMagick
     * @return the list of color objects
     */
    protected List<HSLColor> extractColors(Blob blob) {
        List<HSLColor> hslColors = new ArrayList<>();
        float from255to360scale = 360 / 255.0f;
        float from255to100scale = 100 / 255.0f;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(blob.getStream()));
            String line;
            while ((line = reader.readLine()) != null && line.length()>0) {
                String segments[] = line.split(":|\\(|\\)|,|#");
                int count = Integer.parseInt(segments[0].trim());
                int hue = Integer.parseInt(segments[2].trim());
                int saturation = Integer.parseInt(segments[3].trim());
                int lightness = Integer.parseInt(segments[4].trim());
                hslColors.add(
                        new HSLColor(
                                hue*from255to360scale,
                                saturation*from255to100scale,
                                from255to100scale* lightness,
                                count));
            }
            return hslColors;
        } catch (IOException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

}
