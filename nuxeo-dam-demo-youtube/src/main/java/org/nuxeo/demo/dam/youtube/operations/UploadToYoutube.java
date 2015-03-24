package org.nuxeo.demo.dam.youtube.operations;

import org.nuxeo.demo.dam.youtube.work.YoutubeUploadWork;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.api.Framework;


/**
 * @author nelson.silva
 */
@Operation(id = UploadToYoutube.ID, category = Constants.CAT_DOCUMENT, label = "UploadToYouTube", description = "This operation will upload given video document to YouTube")
public class UploadToYoutube {

	public static final String ID = "UploadToYouTube";

	@Context
	public CoreSession coreSession;

	@Param(name = "document")
	public DocumentModel doc;


	@OperationMethod
	public void run() throws Exception {
        WorkManager wm = Framework.getService(WorkManager.class);
        wm.schedule(new YoutubeUploadWork(doc));
	}

}
