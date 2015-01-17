/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node;

import javax.net.ssl.SSLServerSocketFactory;

import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.pepstock.jem.node.configuration.ConfigurationException;
import org.pepstock.jem.node.https.RequestListener;
import org.pepstock.jem.node.https.SubmitHandler;
import org.pepstock.jem.node.security.keystore.KeyStoreUtil;

/**
 * Starts a HTTPS listener to submit JOB inside job. It is being used from "submit" process no written in JAVA.<br>
 * It creates the asymmetric keys and keystore at runtime. It works ONLY on HTTPS.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class HttpsInternalSubmitter {
	
	/**
	 * To avoid any instantiation
	 */
	private HttpsInternalSubmitter() {
	}
	
	/**
	 * Starts the HTTP listener, setting the handlers and SSL factory.
	 * 
	 * @param port port to stay in listening mode
	 * @throws ConfigurationException if any errors occurs
	 */
	public static void start(int port) throws ConfigurationException {
        // Set up the HTTP protocol processor
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("Jem/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();

        // Set up request handlers
        UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register(SubmitHandler.DEFAULT_ACTION, new SubmitHandler());

        // Set up the HTTP service
        HttpService httpService = new HttpService(httpproc, reqistry);
        try {
        	// sets HTTPS ALWAYS, take a SSL server socket factory
        	SSLServerSocketFactory sf = KeyStoreUtil.getSSLServerSocketFactory();
        	// creates thread and starts it.
			Thread t = new RequestListener(port, httpService, sf);
			t.setDaemon(false);
			t.start();
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage(), e);
		}
	}
}