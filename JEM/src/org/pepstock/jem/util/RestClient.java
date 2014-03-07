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
package org.pepstock.jem.util;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.pepstock.jem.log.LogAppl;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

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
	    ApacheHttpClient4 client = ApacheHttpClient4.create(config);
	    HttpClient hc = client.getClientHandler().getHttpClient();
	    if ("https".equalsIgnoreCase(baseURI.getScheme())){
	    	try {
	    		configureSSLHandling(hc, baseURI);
	    	} catch (KeyManagementException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (UnrecoverableKeyException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (NoSuchAlgorithmException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	} catch (KeyStoreException e) {
	    		LogAppl.getInstance().emit(UtilMessage.JEMB008E, e);
	    	}
	    }
		return client;
	}

	
	/**
	 * Configures SSL HTTP connections
	 * @param hc http client
	 * @param uri URI based
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private void configureSSLHandling(HttpClient hc, URI uri) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		SSLSocketFactory sf = buildSSLSocketFactory();
		Scheme https = new Scheme(uri.getScheme(), uri.getPort(), sf);
		SchemeRegistry sr = hc.getConnectionManager().getSchemeRegistry();
		sr.register(https);
	}
 
	private SSLSocketFactory buildSSLSocketFactory() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy ts = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// always true to avoif certicateunknow excpetion
				return true;
			}
		};

		/* build socket factory with hostname verification turned off. */
		return  new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	}
}