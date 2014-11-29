/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.pepstock.jem.rest;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.HttpClient;
import org.pepstock.jem.commands.util.HttpUtil;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.impl.http.HttpResourceKeys;
import org.pepstock.jem.util.UtilMessage;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;

/**
 * Abstract Client to access to JEM by REST protocol. Uses Apache to avoid to close the HTTP configuration, mandatory
 * to maintain the security management in web app.
 *  
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class RestClient {

	private URI baseURI = null;
	
	/**
	 * Creates the object using the base URL of rest
	 * @param uriString URL to access to JEM by HTTP
	 * @throws Exception if any SSL errors occurs
	 */
	public RestClient(String uriString){
		baseURI = UriBuilder.fromUri(uriString).build();
	}
	
	/**
	 * Creates a base web resource to use to call remotely the method of REST services
	 * @return a web resource instance
	 */
	public abstract WebResource getBaseWebResource();
	
	/**
	 * @return the baseURI
	 */
	public URI getBaseURI() {
		return baseURI;
	}
	
	/**
	 * Returns a Apache HTTP client, ready to use 
	 * @return Apache HTTP client instance
	 */
	ApacheHttpClient4 initialHttpClient() {
	    ClientConfig config = new DefaultClientConfig();
	    config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
	    
	    ApacheHttpClient4 client = null;
	    if (HttpResourceKeys.HTTPS_PROTOCOL.equalsIgnoreCase(baseURI.getScheme())){
	    	try {
	    		HttpClient hc = HttpUtil.createHttpClient(baseURI);
	    		client = new ApacheHttpClient4(new ApacheHttpClient4Handler(hc, null, false), config);
	    	} catch (KeyManagementException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (UnrecoverableKeyException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (NoSuchAlgorithmException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (KeyStoreException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	}
	    } else {
	    	client = ApacheHttpClient4.create(config);
	    }
	    // to add log, use addFilter method with LoggingFilter to std output
	   	return client;
	}
}