package org.nuxeo.demo.dam.youtube.work;

import com.google.api.services.youtube.model.Video;
import org.nuxeo.demo.dam.youtube.YouTubeService;
import org.nuxeo.demo.dam.youtube.YouTubeUploaderProgressListener;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.runtime.api.Framework;

/**
 * Created by Michaël on 24/03/2015.
 */

public class YoutubeUploadWork extends AbstractWork {

    public YoutubeUploadWork(DocumentModel doc) {
        super();
        setDocument(doc.getRepositoryName(),doc.getId());
        doc.setPropertyValue("youtube:workerid",getId());
        doc.getCoreSession().save();
    }

    @Override
    public void work() {
        CoreSession session = initSession();
        DocumentModel target = session.getDocument(new IdRef(docId));
        YouTubeService youTubeService = Framework.getService(YouTubeService.class);
        Video video = youTubeService.upload(target, new YouTubeUploaderProgressListener() {
            @Override
            public void onStart() {
                //nothing
            }

            @Override
            public void onProgress(double progress) {
                setProgress(new Progress((float) progress));
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError() {

            }
        });
        if (video!=null) {
            target.setPropertyValue("youtube:youtubeid",video.getId());
            target.setPropertyValue("youtube:workerid","");
            session.saveDocument(target);
        }
    }


    @Override
    public String getTitle() {
        return "Youtube Upload Work "+getId();
    }
}
