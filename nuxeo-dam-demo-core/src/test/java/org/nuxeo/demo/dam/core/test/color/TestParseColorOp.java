package org.nuxeo.demo.dam.core.test.color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.demo.dam.core.color.hsl.HSLColor;
import org.nuxeo.demo.dam.core.color.palette.NxPalette;
import org.nuxeo.demo.dam.core.operations.ParseIMColorBlobOp;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;

/**
 * Created by Michaël on 18/05/2015.
 */

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy("studio.extensions.dam-demo-nuxeo-presales")
@LocalDeploy({
        "org.nuxeo.demo.dam.core:OSGI-INF/extensions/operations-contrib.xml",
        "org.nuxeo.demo.dam.core:OSGI-INF/extensions/adapters-contrib.xml"})
public class TestParseColorOp {

    @Inject
    CoreSession session;

    @Before
    public void studioConfigShouldBeLoaded() {
        DocumentModelImpl input = (DocumentModelImpl) session.createDocumentModel("Picture");
        Assert.assertTrue(input.hasSchema("colors"));
    }

    @Test
    public void parseSingleREDColorString() throws Exception {
        String input = "      1: (  0,255,128) #009C7A hsl(0%,100%,50%)\n";
        String expectedActual =
                new HSLColor(0,100,50,0).toRGBHex();
        String expectedNormalized =
                new HSLColor(NxPalette.HUE[0][2], NxPalette.SATURATION, NxPalette.LIGHTNESS,0).toRGBHex();
        parseAndNormalize(input,expectedActual,expectedNormalized);
    }

    @Test
    public void parseSingleBLUEColorString() throws Exception {
        String input = "      1: (  170,255,128) #009C7A hsl(60%,100%,50%)\n";
        String expectedActual =
                new HSLColor(240,100,50,0).toRGBHex();
        String expectedNormalized =
                new HSLColor(NxPalette.HUE[5][2], NxPalette.SATURATION, NxPalette.LIGHTNESS,0).toRGBHex();
        parseAndNormalize(input,expectedActual,expectedNormalized);
    }

    @Test
    public void parseSingleBLACKColorString() throws Exception {
        String input = "      1: (  0,180,0) #009C7A hsl(50%,0%,0%)\n";
        String expectedActual =
                new HSLColor(180,0,0,0).toRGBHex();
        String expectedNormalized =
                new HSLColor(HSLColor.MIN_HUE, HSLColor.MIN_SATURATION, HSLColor.MIN_LIGHTNESS,0).toRGBHex();
        parseAndNormalize(input,expectedActual,expectedNormalized);
    }

    @Test
    public void parseSingleWHITEColorString() throws Exception {
        String input = "      1: (  0,180,255) #009C7A hsl(0%,0%,100%)\n";
        String expectedActual =
                new HSLColor(0,70,100,0).toRGBHex();
        String expectedNormalized =
                new HSLColor(HSLColor.MIN_HUE, HSLColor.MIN_SATURATION, HSLColor.MAX_LIGHTNESS,0).toRGBHex();
        parseAndNormalize(input,expectedActual,expectedNormalized);
    }

    @Test
    public void parseSingleGrayColorString() throws Exception {
        String input = "      1: (  128,0,128) #009C7A hsl(50%,0%,0%)\n";
        String expectedActual =
                new HSLColor(50,0,50,0).toRGBHex();
        String expectedNormalized =
                new HSLColor(HSLColor.MIN_HUE, HSLColor.MIN_SATURATION, HSLColor.MAX_LIGHTNESS/2,0).toRGBHex();
        parseAndNormalize(input,expectedActual,expectedNormalized);
    }


    protected void parseAndNormalize(String line, String actualColor, String normalizedColor)
            throws OperationException {
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(new StringBlob(line));
        OperationChain chain = new OperationChain("TestParseColorBlob");
        DocumentModel input = session.createDocumentModel("Picture");
        chain.add(ParseIMColorBlobOp.ID).
                set("variableName", "colors").
                set("documentModel", input);
        as.run(ctx, chain);
        String[] actualColors = (String[]) input.getPropertyValue("colors:actual");
        Assert.assertTrue(actualColors!=null);
        Assert.assertEquals(1, actualColors.length);
        Assert.assertEquals("actual color",actualColor, actualColors[0]);

        String[] normalizedColors = (String[]) input.getPropertyValue("colors:normalized");
        Assert.assertTrue(normalizedColors!=null);
        Assert.assertEquals(1, normalizedColors.length);
        Assert.assertEquals("normalized color",normalizedColor,normalizedColors[0]);
    }
}
