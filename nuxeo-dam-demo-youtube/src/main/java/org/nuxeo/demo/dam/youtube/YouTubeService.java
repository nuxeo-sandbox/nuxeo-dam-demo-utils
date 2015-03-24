package org.nuxeo.demo.dam.youtube;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.oauth2.providers.NuxeoOAuth2ServiceProvider;
import org.nuxeo.ecm.platform.oauth2.providers.OAuth2ServiceProviderRegistry;
import org.nuxeo.ecm.platform.video.TranscodedVideo;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YouTubeService extends DefaultComponent {

    private static final Log log = LogFactory.getLog(YouTubeService.class);

    public static final String CONFIGURATION_EP = "configuration";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private String providerName;

    private String clientId;

    private String clientSecret;

    private NuxeoOAuth2ServiceProvider oauth2Provider;


    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        if (CONFIGURATION_EP.equals(extensionPoint)) {
            ConfigurationDescriptor config = (ConfigurationDescriptor) contribution;
            providerName = config.getProvider();
            clientId = config.getClientId();
            clientSecret = config.getClientSecret();
        }
    }

    @Override
    public void applicationStarted(ComponentContext context) {
        getOAuth2ServiceProvider();
    }


    /**
     * @param userId the username
     * @return the credential for the given user
     */
    protected Credential getCredential(String userId) {
        // Use system wide OAuth2 provider
        if (getOAuth2ServiceProvider() == null) {
            throw new ClientException("Could not find service provider");
        }

        try {
            AuthorizationCodeFlow flow =
                    getOAuth2ServiceProvider().
                            getAuthorizationCodeFlow(HTTP_TRANSPORT, JSON_FACTORY);
            return flow.loadCredential(userId);
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }
    }


    protected NuxeoOAuth2ServiceProvider getOAuth2ServiceProvider() throws ClientException {

        // Register the system wide OAuth2 provider
        if (oauth2Provider != null) return oauth2Provider;

        OAuth2ServiceProviderRegistry oauth2ProviderRegistry =
                Framework.getLocalService(OAuth2ServiceProviderRegistry.class);

        if (oauth2ProviderRegistry == null) {
            log.info("Please start the authorization flow");
            return null;
        }

        oauth2Provider = oauth2ProviderRegistry.getProvider(providerName);

        if (oauth2Provider == null) {
            try {
                oauth2Provider = oauth2ProviderRegistry.addProvider(
                        providerName,
                        GoogleOAuthConstants.TOKEN_SERVER_URL,
                        GoogleOAuthConstants.AUTHORIZATION_SERVER_URL,
                        clientId, clientSecret,
                        Arrays.asList(YouTubeScopes.YOUTUBE));
            } catch (Exception e) {
               throw new ClientException(e.getMessage());
            }
        } else {
            log.warn("Provider "
                    + providerName
                    + " is already in the Database, XML contribution  won't overwrite it");
        }

        return oauth2Provider;
    }


    public YouTubeClient getYouTubeClient(String userId) throws ClientException {
        Credential credential = getCredential(userId);
        if (credential != null && credential.getAccessToken() != null) {
            return new YouTubeClient(credential);
        } else {
            throw new ClientException("Failed to get YouTube credentials");
        }
    }


    public Video upload(DocumentModel doc, YouTubeUploaderProgressListener listener) {
        YouTubeClient youtube = getYouTubeClient("system");
        VideoDocument videoDocument = doc.getAdapter(VideoDocument.class);

        Video youtubeVideo = new Video();
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(doc.getTitle());
        List<String> tags = new ArrayList<String>();
        snippet.setTags(tags);

        youtubeVideo.setSnippet(snippet);

        TranscodedVideo transcoded = videoDocument.getTranscodedVideo("MP4 480p");

        try {
            return youtube.upload(
                    youtubeVideo,
                    transcoded.getBlob().getStream(),
                    transcoded.getBlob().getMimeType(),
                    transcoded.getBlob().getLength(),
                    listener);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


}
