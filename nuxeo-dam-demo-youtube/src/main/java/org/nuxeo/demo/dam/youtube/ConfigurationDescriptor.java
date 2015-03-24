package org.nuxeo.demo.dam.youtube;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import java.io.Serializable;

@XObject("configuration")
public class ConfigurationDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;

    @XNode("provider")
    private String provider;

    @XNode("clientId")
    private String clientId;

    @XNode("clientSecret")
    private String clientSecret;

	public String getProvider() {
		return provider;
	}

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }


}
