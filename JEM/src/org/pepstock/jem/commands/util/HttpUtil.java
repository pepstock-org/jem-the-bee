/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.commands.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitException;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.CharSet;

import com.thoughtworks.xstream.XStream;

/**
 * Utility class to call JEM by HTTP to extract group name and members list and
 * to submit a job by http.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class HttpUtil {

	/**
	 * Key used to store user id
	 */
	public static final String USER_PROPERTY_KEY = "jem.command.user";

	/**
	 * Key used to store user password
	 */
	public static final String PASSWORD_PROPERTY_KEY = "jem.command.password";
	/**
	 * Query string to get cluster group name of Hazelcast
	 */
	private static final String NAME_QUERY_STRING = "/servlet/getClusterGroupName";

	/**
	 * Query string to get all member of group name of Hazelcast
	 */
	private static final String MEMBERS_QUERY_STRING = "/servlet/getClusterMembers";

	/**
	 * Query string to log in
	 */
	private static final String LOGIN_QUERY_STRING = "/servlet/login";

	/**
	 * Query string to log out
	 */
	private static final String LOGOUT_QUERY_STRING = "/servlet/logout";

	/**
	 * Query string to submit a job
	 */
	private static final String SUBMIT_QUERY_STRING = "/servlet/submit";

	/**
	 * Query string to get a ended job by id
	 */
	private static final String ENDE_JOBID_QUERY_STRING = "/servlet/getEndedJobById";

	/**
	 * Private constructor to avoid any instantiations
	 */
	private HttpUtil() {
	}

	/**
	 * Calls a http node of JEM to get all members of group, necessary to client
	 * to connect to JEM.
	 * 
	 * @param url http URL to call
	 * @return Arrays with all members of Hazelcast cluster
	 * @throws SubmitException if errors occur
	 */
	public static String[] getMembers(String url) throws SubmitException {
		// creates a HTTP client
		HttpClient httpclient = new DefaultHttpClient();
		try {
			configureHttpClient(httpclient, url);
			// concats URL with query string
			String completeUrl = url + HttpUtil.MEMBERS_QUERY_STRING;
			// prepares GET request and basic response handler
			HttpGet httpget = new HttpGet(completeUrl);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// executes and parse the results
			// result must be
			// [ipaddress:port],[ipaddress:port],[ipaddress:port],....[ipaddress:port]
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody.trim().split(",");
		} catch (KeyManagementException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (UnrecoverableKeyException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (NoSuchAlgorithmException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (KeyStoreException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (URISyntaxException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (ClientProtocolException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} catch (IOException e) {
			throw new SubmitException(SubmitMessage.JEMW001E, e);
		} finally {
			// close http client
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Calls a http node of JEM to get group anme of Hazelcast cluster,
	 * necessary to client to connect to JEM.
	 * 
	 * @param url http URL to call
	 * @return group name of Hazelcast cluster
	 * @throws SubmitException if errors occur
	 */
	public static String getGroupName(String url) throws SubmitException {
		// creates a HTTP client
		HttpClient httpclient = new DefaultHttpClient();
		try {
			configureHttpClient(httpclient, url);

			// concats URL with query string
			String completeUrl = url + HttpUtil.NAME_QUERY_STRING;
			// prepares GET request and basic response handler
			HttpGet httpget = new HttpGet(completeUrl);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// executes and no parsing
			// result must be only a string
			String responseBody = httpclient.execute(httpget, responseHandler);
			return responseBody.trim();
		} catch (KeyManagementException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (UnrecoverableKeyException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (NoSuchAlgorithmException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (KeyStoreException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (URISyntaxException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (ClientProtocolException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} catch (IOException e) {
			throw new SubmitException(SubmitMessage.JEMW002E, e);
		} finally {
			// close http client
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Calls a http node of JEM to submit a job.
	 * 
	 * @param user user to authenticate
	 * @param password password to authenticate
	 * @param url http URL to call
	 * @param prejob job instance to submit
	 * @return job id
	 * @throws SubmitException if errors occur
	 */
	public static String submit(String user, String password, String url, PreJob prejob) throws SubmitException {
		// creates a HTTP client
		HttpClient httpclient = new DefaultHttpClient();
		boolean loggedIn = false;
		try {
			configureHttpClient(httpclient, url);
			// prepares the entity to send via HTTP
			XStream streamer = new XStream();
			String content = streamer.toXML(prejob);

			login(user, password, url, httpclient);
			loggedIn = true;
			// concats URL with query string
			String completeUrl = url + HttpUtil.SUBMIT_QUERY_STRING;
			StringEntity entity = new StringEntity(content, ContentType.create("text/xml", CharSet.DEFAULT_CHARSET_NAME));

			// prepares POST request and basic response handler
			HttpPost httppost = new HttpPost(completeUrl);
			httppost.setEntity(entity);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// executes and no parsing
			// result must be only a string
			String responseBody = httpclient.execute(httppost, responseHandler);
			return responseBody.trim();
		} catch (KeyManagementException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (UnrecoverableKeyException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (NoSuchAlgorithmException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (KeyStoreException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (URISyntaxException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (ClientProtocolException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} catch (IOException e) {
			throw new SubmitException(SubmitMessage.JEMW003E, e);
		} finally {
			if (loggedIn) {
				try {
					logout(url, httpclient);
				} catch (Exception e) {
					// debug
					LogAppl.getInstance().debug(e.getMessage(), e);
				}
			}
			// close http client
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Returns the job by its ID. If is not in output queue, normal <code>Object</code> is returned.
	 * @param user user to authenticate
	 * @param password password to authenticate
	 * @param url http URL to call
	 * @param jobId jobId
	 * @return Object Job instance of a normal <code>Object</code> is not ended
	 * @throws SubmitException if errors occur
	 */
	public static Object getEndedJobByID(String user, String password, String url, String jobId) throws SubmitException {
		// creates a HTTP client
		HttpClient httpclient = new DefaultHttpClient();
		boolean loggedIn = false;
		try {
			configureHttpClient(httpclient, url);
			// prepares the entity to send via HTTP
			XStream streamer = new XStream();

			login(user, password, url, httpclient);
			loggedIn = true;
			// concats URL with query string
			String completeUrl = url + HttpUtil.ENDE_JOBID_QUERY_STRING + "?jobId=" + jobId;
			// prepares POST request and basic response handler
			HttpGet httpget = new HttpGet(completeUrl);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// executes and no parsing
			// result must be only a string
			String responseBody = httpclient.execute(httpget, responseHandler);
			return streamer.fromXML(responseBody.trim());
		} catch (KeyManagementException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (UnrecoverableKeyException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (NoSuchAlgorithmException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (KeyStoreException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (URISyntaxException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (ClientProtocolException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);
		} catch (IOException e) {
			throw new SubmitException(SubmitMessage.JEMW004E, e);			
		} finally {
			if (loggedIn) {
				try {
					logout(url, httpclient);
				} catch (Exception e) {
					// debug
					LogAppl.getInstance().debug(e.getMessage(), e);
				}
			}
			// close http client
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Performs the login using user and password
	 * 
	 * @param user user to authenticate
	 * @param password password to authenticate
	 * @param url http URL to call
	 * @param httpclient hhtp client already created
	 * @throws ClientProtocolException if any errors occurs on calling the
	 *             servlet
	 * @throws IOException if I/O error occurs
	 */
	private static void login(String user, String password, String url, HttpClient httpclient) throws ClientProtocolException, IOException {
		// account info in a properties
		Properties properties = new Properties();
		properties.setProperty(USER_PROPERTY_KEY, user);
		properties.setProperty(PASSWORD_PROPERTY_KEY, password);
		StringWriter writer = new StringWriter();
		properties.store(writer, "Account info");
		// login

		// concats URL with query string
		String completeUrl = url + HttpUtil.LOGIN_QUERY_STRING;
		StringEntity entity = new StringEntity(writer.toString(), ContentType.create("text/plain", "UTF-8"));

		// prepares POST request and basic response handler
		HttpPost httppost = new HttpPost(completeUrl);
		httppost.setEntity(entity);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		// executes and no parsing
		// result must be only a string
		httpclient.execute(httppost, responseHandler);
	}

	/**
	 * Logs out from HTTP server.
	 * 
	 * @param url http URL to call
	 * @param httpclient http client already created
	 * @throws ClientProtocolException if any errors occurs on calling the
	 *             servlet
	 * @throws IOException if I/O error occurs
	 */
	private static void logout(String url, HttpClient httpclient) throws ClientProtocolException, IOException {
		String completeUrl = url + HttpUtil.LOGOUT_QUERY_STRING;
		// prepares POST request and basic response handler
		HttpPost httppost = new HttpPost(completeUrl);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// executes and no parsing
		httpclient.execute(httppost, responseHandler);
	}

	/**
	 * Configures SSL HTTP client, if necessary
	 * 
	 * @param client http client already created
	 * @param uri http URI to call
	 * @throws URISyntaxException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private static void configureHttpClient(HttpClient client, String uri) throws URISyntaxException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		URI uriObject = URI.create(uri);
		HttpUtil.configureHttpClient(client, uriObject);
	}

	/**
	 * Configures SSL HTTP client, if necessary
	 * 
	 * @param client http client already created
	 * @param uri http URI to call
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private static void configureHttpClient(HttpClient client, URI uri) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		// sets SSL ONLY if the scheme is HTTPS
		if (uri.getScheme() != null && "https".equalsIgnoreCase(uri.getScheme())) {
			SSLSocketFactory sf = buildSSLSocketFactory();
			int port = uri.getPort();
			if (port == -1) {
				port = 443;
			}
			Scheme https = new Scheme(uri.getScheme(), port, sf);
			SchemeRegistry sr = client.getConnectionManager().getSchemeRegistry();
			sr.register(https);
		}
	}

	private static SSLSocketFactory buildSSLSocketFactory() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy ts = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] cert, String name) throws CertificateException {
				// always true to avoid certificate unknown exception
				return true;
			}
		};
		/* build socket factory with hostname verification turned off. */
		return new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	}

}