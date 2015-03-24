package org.nuxeo.demo.dam.youtube;

public interface YouTubeUploaderProgressListener {
    void onStart();
    void onProgress(double progress);
    void onComplete();
    void onError();
}
