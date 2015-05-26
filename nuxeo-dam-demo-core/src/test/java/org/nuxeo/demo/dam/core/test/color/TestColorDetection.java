package org.nuxeo.demo.dam.core.test.color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.demo.dam.core.color.adapter.ColorHolder;
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
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * Created by Michaël on 18/05/2015.
 */

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class})
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({"studio.extensions.dam-demo-nuxeo-presales"})
@LocalDeploy({
        "org.nuxeo.demo.dam.core:OSGI-INF/extensions/operations-contrib.xml",
        "org.nuxeo.demo.dam.core:OSGI-INF/extensions/adapters-contrib.xml"})
public class TestColorDetection {

    @Inject
    CoreSession session;

    @Before
    public void studioConfigShouldBeLoaded() {
        DocumentModelImpl input = (DocumentModelImpl) session.createDocumentModel("Picture");
        Assert.assertTrue(input.hasSchema("colors"));
    }

    @Test
    public void dontDetectLightBlue() throws Exception {
        File file = new File(getClass().getResource("/files/Small_snow-008.txt").getPath());
        Assert.assertTrue(file.exists());
        DocumentModel input = session.createDocumentModel("Picture");
        extractColors(file, input);
        ColorHolder holder = input.getAdapter(ColorHolder.class);
        List<HSLColor> principals = holder.getPrincipalColors();
        Assert.assertTrue(!principals.contains(HSLColor.white(0)));
        Assert.assertTrue(!principals.contains(new HSLColor(240, NxPalette.SATURATION,NxPalette.LIGHTNESS,0)));
    }

    @Test
    public void dontDetectWhiteWithLightRedHue() throws Exception {
        File file = new File(getClass().getResource("/files/Small_island-03-crop70pc.txt").getPath());
        Assert.assertTrue(file.exists());
        DocumentModel input = session.createDocumentModel("Picture");
        extractColors(file, input);
        ColorHolder holder = input.getAdapter(ColorHolder.class);
        List<HSLColor> principals = holder.getPrincipalColors();
        Assert.assertTrue(!principals.contains(HSLColor.white(0)));
    }

    @Test
    public void dontDetectDarkOrange() throws Exception {
        File file = new File(getClass().getResource("/files/Small_IMG_2533.txt").getPath());
        Assert.assertTrue(file.exists());
        DocumentModel input = session.createDocumentModel("Picture");
        extractColors(file, input);
        ColorHolder holder = input.getAdapter(ColorHolder.class);
        List<HSLColor> principals = holder.getPrincipalColors();
        Assert.assertTrue(principals.contains(HSLColor.black(0)));
        Assert.assertTrue(!principals.contains(new HSLColor(30,NxPalette.SATURATION,NxPalette.LIGHTNESS,0)));
    }

    @Test
    public void dontDetectWhiteWithLightYellowHue() throws Exception {
        File file = new File(getClass().getResource("/files/Small_trees-146.txt").getPath());
        Assert.assertTrue(file.exists());
        DocumentModel input = session.createDocumentModel("Picture");
        extractColors(file, input);
        ColorHolder holder = input.getAdapter(ColorHolder.class);
        List<HSLColor> principals = holder.getPrincipalColors();
        Assert.assertTrue(!principals.contains(HSLColor.white(0)));
    }

    protected void extractColors(File file, DocumentModel doc)
            throws OperationException {
        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(new FileBlob(file));
        OperationChain chain = new OperationChain("TestParseColorBlob");
        chain.add(ParseIMColorBlobOp.ID).
                set("variableName", "colors").
                set("documentModel", doc);
        as.run(ctx, chain);
    }


}
