package org.nuxeo.demo.dam.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos.Insert;
import com.google.api.services.youtube.model.Video;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;

public class YouTubeClient {

    private static final Log log = LogFactory.getLog(YouTubeClient.class);

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    YouTube youtube;

    Credential credential;

    public YouTubeClient(Credential credential) {
        this.credential = credential;
    }


    public YouTube getYouTube() throws IOException {
        // if credential found with an access token, invoke the user code
        if (youtube == null) {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    credential).setApplicationName("DEMO-DAM/1.0").build();
        }
        return youtube;
    }


    public Video upload(Video video, InputStream stream, String type, long length) throws IOException {
        YouTubeUploaderProgressListener progressListener = new YouTubeUploaderProgressListener() {

            @Override
            public void onStart() {
                log.info("Upload starting...");
            }

            @Override
            public void onProgress(double progress) {
                log.info("Upload percentage: " + progress);
            }

            @Override
            public void onComplete() {
                log.info("Upload complete.");
            }

            @Override
            public void onError() {
                log.info("Upload error.");
            }

        };
        return upload(video, stream, type, length, progressListener);
    }

    public Video upload(Video video, InputStream stream, String type, long length,
                        final YouTubeUploaderProgressListener uploadListener) throws IOException {

        InputStreamContent mediaContent = new InputStreamContent(type, stream);
        mediaContent.setLength(length);
        Insert insert = getYouTube().videos().insert("snippet,status", video, mediaContent);

        // Set the upload type and add event listener.
        MediaHttpUploader uploader = insert.getMediaHttpUploader();

        /*
         * Sets whether direct media upload is enabled or disabled. True = whole
         * media content is uploaded in a single request. False (default) =
         * resumable media upload protocol to upload in data chunks.
         */
        uploader.setDirectUploadEnabled(false);

        MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
            @Override
            public void progressChanged(MediaHttpUploader uploader) throws IOException {
                switch (uploader.getUploadState()) {
                    case INITIATION_STARTED:
                    case INITIATION_COMPLETE:
                        uploadListener.onStart();
                        break;
                    case MEDIA_IN_PROGRESS:
                        uploadListener.onProgress(uploader.getProgress());
                        break;
                    case MEDIA_COMPLETE:
                        uploadListener.onComplete();
                        break;
                    case NOT_STARTED:
                        log.info("Upload Not Started!");
                        break;
                }
            }
        };

        uploader.setProgressListener(progressListener);

        // Execute upload.
        Video returnedVideo = insert.execute();

        // Print out returned results.
        if (returnedVideo != null) {
            log.info("\n================== Returned Video ==================\n");
            log.info("  - Id: " + returnedVideo.getId());
            log.info("  - Title: " + returnedVideo.getSnippet().getTitle());
            log.info("  - Tags: " + returnedVideo.getSnippet().getTags());
            log.info("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
        }

        return returnedVideo;

    }

}
