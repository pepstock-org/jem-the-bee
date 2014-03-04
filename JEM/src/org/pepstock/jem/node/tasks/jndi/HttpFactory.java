/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.node.tasks.jndi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.HttpResource;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;



/**
 * Factory useful to create a {@link HttpClient} and the {@link InputStream} of the relative request call,
 * that is the <code>HTTP</code> datasource object to be used inside the java programs. <br>
 * It uses a <code>HttpReference</code>, containing the properties of the <code>HttpClient</code> object. <br>
 * The <code>HttpClient</code> object allows to connect to a <code>HTTP</code> provider
 * such as <code>Servlet</code>, or generally <code>HTTP URL</code>, and call a <code>GET</CODE>
 * or a <code>POST</code> method and it returns the <code>Response</code> content inside the <code>InputStream</code>. <br>
 * In some cases <code>Login</code> and <code>Logout</code> may be necessary. <br>
 * It implements the <code>ObjectFactory</code>.
 * 
 * @see HttpReference
 * @see ObjectFactory
 * @see HttpClient
 * @see InputStream
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class HttpFactory implements ObjectFactory {
		
	/**
	 * Default value of the <code>port</code> of the <code>HTTPS Schema</code>.
	 */
	public static final int DEFAULT_HTTPS_PORT = 443;
	
	/**
	 * This method creates a {@link InputStream} for <code>HTTP</code> purposes, starting from
	 * the {@link HttpReference} in the parameter <code>obj</code>. <br>
	 * The <code>InputStream</code> contains the <code>Response</code> content produced
	 * calling an <code>HTTP</code> datasource, using an {@link HttpClient}. <br>
	 * The <code>HttpClient</code> allows to connect to a <code>HTTP</code> provider, 
	 * such as <code>Servlet</code>, or generally <code>HTTP URL</code>, call a <code>GET</CODE>
	 * or a <code>POST</code> method and puts the <code>Response</code> content inside the <code>InputStream</code>. <br>
	 * In some cases <code>Login</code> and <code>Logout</code> may be necessary. <br>
	 * 
	 * @see ObjectFactory
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> environment) throws JNDIException {
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		Reference ref = (Reference) object;
		Properties properties = new Properties();
		for (String propertyName : HttpResource.PROPERTIES_ALL) {
			RefAddr ra = ref.get(propertyName);
			if (ra != null) {
				String propertyValue = ra.getContent().toString().trim();
				properties.setProperty(propertyName, propertyValue);
			}
		}
		HttpClient httpClient = (HttpClient) createHttpClient(properties);
		HttpRequestBase request = createRequestMethod(httpClient, properties, environment);
		// Execute request
		return execute(httpClient, request, properties);
	}
	
	/**
	 * This method executes the desired <code>Http Request</code> and returns the <code>InputStream</code>
	 * containing the Body content of the <code>Response</code>. <br>
	 * In some cases <code>Login</code> and <code>Logout</code> may be necessary to
	 * perform the desired <code>Http Request</code>. <br>
	 * 
	 * @param httpClient the <code>HttpClient</code> that performs the <code>Http Request</code>.
	 * @param request the <code>Http Request</code> to be performed.
	 * @param properties the <code>Properties</code> object containing the useful configuration properties,
	 * such as {@link HttpResource#REQUEST_LOGIN_QUERY_STRING} and {@link HttpResource#REQUEST_LOGOUT_QUERY_STRING}
	 * if <code>Login</code> and <code>Logout</code> is necessary.
	 * @return the <code>InputStream</code> containing the Body content of the <code>Response</code>
	 * @throws Exception if an error occurs.
	 * 
	 * @see HttpResource#REQUEST_LOGIN_QUERY_STRING
	 * @see HttpResource#REQUEST_LOGOUT_QUERY_STRING
	 */
	private InputStream execute(HttpClient httpClient, HttpRequestBase request, Properties properties) throws JNDIException{
		String loginQueryString = properties.getProperty(HttpResource.REQUEST_LOGIN_QUERY_STRING);
		String logoutQueryString = properties.getProperty(HttpResource.REQUEST_LOGOUT_QUERY_STRING);
		try {
			if (null != loginQueryString) {
				executeLogin(httpClient, properties, loginQueryString);
			}
			InputStream inputStream = executeRequestMethod(httpClient, request);
			if (null != logoutQueryString) {
				executeLogout(httpClient, properties, logoutQueryString);
			}
			return inputStream;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	
	/**
	 * This method executes the <code>Login</code> if necessary before to connect to the 
	 * <code>HTTP</code> datasource and execute the desired <code>Http Request</code>.
	 * 
	 * @param httpClient httpClient the <code>HttpClient</code> that performs the <code>Login</code>.
	 * @param properties the <code>Properties</code> object containing the useful configuration properties:
	 * <li> {@link HttpResource#REQUEST_LOGIN_USERID} or {@link Resource#USERID}
	 * <li> {@link HttpResource#REQUEST_LOGIN_PASSWORD} or {@link Resource#PASSWORD}
	 * <li> {@link HttpResource#REQUEST_LOGIN_PARAM_USERID}
	 * <li> {@link HttpResource#REQUEST_LOGIN_PARAM_PASSWORD}
	 * and the properties to create the <code>Login Url</code>
	 * 
	 * @param loginQueryString the <code>Login Query String</code>.
	 * @throws Exception if an error occurs.
	 */
	private void executeLogin(HttpClient httpClient, Properties properties, String loginQueryString) throws JNDIException {
		if (null != loginQueryString) {
			String userid = properties.getProperty(HttpResource.REQUEST_LOGIN_USERID);
			if (null == userid) {
				userid = properties.getProperty(HttpResource.USERID);
			}
			if (null == userid) {
				LogAppl.getInstance().emit(NodeMessage.JEMC159E, HttpResource.REQUEST_LOGIN_USERID, HttpResource.USERID);
				throw new JNDIException(NodeMessage.JEMC159E, HttpResource.REQUEST_LOGIN_USERID, HttpResource.USERID);
			}
			String password = properties.getProperty(HttpResource.REQUEST_LOGIN_PASSWORD);
			if (null == password) {
				password = properties.getProperty(HttpResource.PASSWORD);
			}
			if (null == password) {
				LogAppl.getInstance().emit(NodeMessage.JEMC159E, HttpResource.REQUEST_LOGIN_PASSWORD, HttpResource.PASSWORD);
				throw new JNDIException(NodeMessage.JEMC159E, HttpResource.REQUEST_LOGIN_PASSWORD, HttpResource.PASSWORD);
			}
			String paramUserid = properties.getProperty(HttpResource.REQUEST_LOGIN_PARAM_USERID);
			if (null == paramUserid) {
				LogAppl.getInstance().emit(NodeMessage.JEMC160E, HttpResource.REQUEST_LOGIN_PARAM_USERID);
				throw new JNDIException(NodeMessage.JEMC160E, HttpResource.REQUEST_LOGIN_PARAM_USERID);
			}
			String paramPassword = properties.getProperty(HttpResource.REQUEST_LOGIN_PARAM_PASSWORD);
			if (null == paramPassword) {
				LogAppl.getInstance().emit(NodeMessage.JEMC160E, HttpResource.REQUEST_LOGIN_PARAM_PASSWORD);
				throw new JNDIException(NodeMessage.JEMC160E, HttpResource.REQUEST_LOGIN_PARAM_PASSWORD);
			}
			String url = createRequestUrl(properties, loginQueryString);
			Properties parameters = new Properties();
			parameters.setProperty(paramUserid, userid);
			parameters.setProperty(paramPassword, password);
			String parametersCharsetFormat = (String) httpClient.getParams().getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET);
			HttpPost request = createHttpPostMethod(parameters, url, parametersCharsetFormat);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			try {
				httpClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				throw new JNDIException(NodeMessage.JEMC230E, e, e.getMessage());
			} catch (IOException e) {
				throw new JNDIException(NodeMessage.JEMC230E, e, e.getMessage());
			}
			request.abort();
		}
	}
	
	/**
	 * This method executes the <code>Logout</code> if necessary after the connection to the 
	 * <code>HTTP</code> datasource and the execution of the desired <code>Http Request</code>.
	 * 
	 * @param httpClient httpClient the <code>HttpClient</code> that performs the <code>Logout</code>.
	 * @param properties the <code>Properties</code> object containing the useful configuration properties
	 * to create the <code>Logout Url</code>.
	 * @param logoutQueryString the <code>Logout Query String</code>.
	 * @throws Exception if an error occurs.
	 */
	private void executeLogout(HttpClient httpClient, Properties properties, String logoutQueryString) throws JNDIException {
		String url = createRequestUrl(properties, logoutQueryString);
		String parametersCharsetFormat = (String) httpClient.getParams().getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET);
		HttpPost request = createHttpPostMethod(null, url, parametersCharsetFormat);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			httpClient.execute(request, responseHandler);
		} catch (ClientProtocolException e) {
			throw new JNDIException(NodeMessage.JEMC231E, e, e.getMessage());
		} catch (IOException e) {
			throw new JNDIException(NodeMessage.JEMC230E, e, e.getMessage());
		}
		request.abort();
	}
	
	/**
	 * It executes the <code>Http Request</code> in the parameter <code>request</code> 
	 * (it may be a <code>GET</code> or a <code>POST</code>)
	 * using the <code>HttpClient</code> in the parameter <code>httpClient</code>. <br>
	 * It puts the <code>Response</code> content inside the <code>InputStream</code>. <br>
	 * 
	 * @param httpClient the <code>HttpClient</code> that performs the <code>Http Request</code>.
	 * @param request the <code>Http Request</code> performed by the <code>HttpClient</code> 
	 * (it may be a <code>GET</code> or a <code>POST</code>).
	 * @return the <code>InputStream</code> containing the <code>Response</code> content.
	 * @throws Exception if an error occurs creating the <code>InputStream</code>.
	 * @see HttpClient
	 * @see HttpRequestBase
	 */
	private InputStream executeRequestMethod(HttpClient httpClient, HttpRequestBase request) throws JNDIException {
		InputStream instream = null;
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			String responseBody = httpClient.execute(request, responseHandler);
			instream = new ByteArrayInputStream(responseBody.getBytes(CharSet.DEFAULT));
			request.abort();
			return instream;
		} catch (Exception ex) {
			request.abort();
			if (null != instream) {
				try {
					instream.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			throw new JNDIException(NodeMessage.JEMC157E, ex, ex.getMessage());
		}
	}
	
	/**
	 * It search the value of the property named <code>propertyName</code> inside the
	 * <code>Hashtable</code> in the parameter <code>environment</code>. <br>
	 * These properties are not configuration properties, but applicative.
	 * @param environment the <code>Hashtable</code> in which it searches the property called <code>propertyName</code>.
	 * @param propertyName the name of the property to search.
	 * @return the value of the property searched if found, <code>null</code> otherwise.
	 * @see Hashtable
	 */
	private Object findEnvironmentProperty(Map<?, ?> environment, String propertyName) {
		Object properyValue = null;
		try {
			return environment.get(propertyName);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(NodeMessage.JEMC158W, ex, propertyName);
		}
		return properyValue;
	}
	
	/**
	 * This method creates the <code>Http Request</code> that is the {@link HttpRequestBase} to be performed by
	 * the <code>HttpClient</code>. <br>
	 * The resulting <code>Http Request</code> may be a {@link HttpGet} (default) or a {@link HttpPost} depending
	 * on the value of the property {@link HttpResource#REQUEST_METHOD}. <br>
	 * It uses also the following properties to create the <code>Http Request</code>:
	 * <li> {@link HttpResource#REQUEST_PATH} : the optional path of the <code>Request URL</code>
	 * <li> {@link HttpResource#REQUEST_PARAMETERS} : the optional parameters
	 * <li> {@link HttpResource#REQUEST_QUERY_STRING} : the optional <code>Query String</code>
	 * <li> {@link CoreProtocolPNames#HTTP_CONTENT_CHARSET} : the format of the <code>HTTP content charset</code>
	 * <li> {@link HttpResource#REQUEST_HOST_NAME} : the <b>mandatory</b> host name
	 * <li> {@link HttpResource#REQUEST_HOST_PORT} : the optional host port
	 * <li> {@link HttpResource#PROTOCOL_TYPE} : the protocol type, {@link HttpResource#HTTP_PROTOCOL}
	 * (as default) or {@link HttpResource#HTTPS_PROTOCOL}.
	 * 
	 * @param httpClient the <code>HttpClient</code> containing the {@link CoreProtocolPNames#HTTP_CONTENT_CHARSET}.
	 * @param properties  the <code>Properties</code> object containing the useful configuration properties.
	 * @param environment the <code>Hashtable</code> in which to search applicative (not configuration) properties. 
	 * It is the <code>Environment</code> of the {@link Context}.
	 * @return <code>Http Request</code> generated.
	 * @throws Exception if an error occurs.
	 * @see HttpGet
	 * @see HttpPost
	 * @see Context
	 */
	private HttpRequestBase createRequestMethod(HttpClient httpClient, Properties properties, Map<?, ?> environment) throws JNDIException {
		String path = (String) findEnvironmentProperty(environment, HttpResource.REQUEST_PATH);
		@SuppressWarnings("unchecked")
		Map<String, String> parameters = (Map<String, String>) findEnvironmentProperty(environment, HttpResource.REQUEST_PARAMETERS);
		String queryString = (String) findEnvironmentProperty(environment, HttpResource.REQUEST_QUERY_STRING);
		String requestMethod = (String) findEnvironmentProperty(environment, HttpResource.REQUEST_METHOD);
		String parametersCharsetFormat = (String) httpClient.getParams().getParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET);
		String url = createRequestUrl(properties, path);
		if (null == requestMethod) {
			requestMethod = HttpResource.REQUEST_GET_METHOD;
		}
		
		if (requestMethod.equalsIgnoreCase(HttpResource.REQUEST_GET_METHOD)) {
			return createHttpGetMethod(parameters, url, queryString);
		} else {
			return createHttpPostMethod(parameters, url, queryString, parametersCharsetFormat);
		}
	}
	
	/**
	 * This method creates the {@link HttpGet} that is the <code>Http Request</code>
	 * starting from the parameter <code>url</code>, the optional <code>Query String</code>
	 * (the parameter <code>queryString</code>) and the optional <code>Http Request</code>
	 * parameters <code>parameters</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param queryString the optional <code>Query String</code>.
	 * @return the {@link HttpGet} that is the <code>Http Request</code> to be called.
	 * @throws Exception if an error occurs.
	 */
	private HttpGet createHttpGetMethod(Map<String, String> parameters, String url, String queryString) throws JNDIException {
		try {
			URIBuilder builder = new URIBuilder(url);
			if (null != queryString) {
				builder.setQuery(queryString);
			} else if (null != parameters && !parameters.isEmpty()) {
				for (Entry<String, String> entry : parameters.entrySet()) {
					builder.setParameter(entry.getKey(), entry.getValue());
				}
			}
			URI uri = builder.build();
			return new HttpGet(uri.toString());
		} catch (URISyntaxException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		}
	}
	
	/**
	 * This method creates the {@link HttpPost} that is the <code>Http Request</code>
	 * starting from the parameter <code>url</code>, the optional <code>Http Request</code>
	 * parameters <code>parameters</code> and the optional <code>Query String</code>
	 * (the parameter <code>queryString</code>). <br>
	 * It uses the format of the <code>HTTP content charset</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param queryString the optional <code>Query String</code>.
	 * @param parametersCharsetFormat the format of the <code>HTTP content charset</code>.
	 * @return the {@link HttpPost} that is the <code>Http Request</code> to be called.
	 * @throws Exception if an error occurs.
	 */
	private HttpPost createHttpPostMethod(Map<String, String> parameters, String url, String queryString, String parametersCharsetFormat) throws JNDIException {
		try {
			URIBuilder builder = new URIBuilder(url);
			if (null != queryString) {
				builder.setQuery(queryString);
			}
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri.toString());
			if (null != parameters && !parameters.isEmpty()) {
				List<NameValuePair> parametersList = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : parameters.entrySet()) {
					parametersList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				if (null == parametersCharsetFormat) {
					((HttpPost) request).setEntity(new UrlEncodedFormEntity(parametersList));
				} else {
					((HttpPost) request).setEntity(new UrlEncodedFormEntity(parametersList, parametersCharsetFormat));
				}
			}
			return request;
		} catch (UnsupportedEncodingException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (URISyntaxException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		}
	}

	/**
	 * This method creates the {@link HttpPost} that is the <code>Http Request</code>
	 * starting from the parameter <code>url</code>, and the optional <code>Http Request</code>
	 * parameters <code>parameters</code> without the <code>Query String</code>. <br>
	 * It uses the format of the <code>HTTP content charset</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param parametersCharsetFormat the format of the <code>HTTP content charset</code>.
	 * @return the {@link HttpPost} that is the <code>Http Request</code> to be called.
	 * @throws Exception if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	private HttpPost createHttpPostMethod(@SuppressWarnings("rawtypes") Map parameters, String url, String parametersCharsetFormat) throws JNDIException{
		return createHttpPostMethod(parameters, url, null, parametersCharsetFormat);
	}
	
	/**
	 * This method creates the <code>Http Request URL</code> to be called. <br>
	 * It uses the following properties:
	 * <li> {@link HttpResource#REQUEST_HOST_NAME} : the <b>mandatory</b> host name
	 * <li> {@link HttpResource#REQUEST_HOST_PORT} : the optional host port
	 * <li> {@link HttpResource#PROTOCOL_TYPE} : the protocol type, {@link HttpResource#HTTP_PROTOCOL}
	 * (as default) or {@link HttpResource#HTTPS_PROTOCOL}.
	 * 
	 * @param properties the <code>Properties</code> object containing the useful configuration properties.
	 * @param requestPath the path of the request.
	 * @return the resulting <code>Http Request URL</code>.
	 * @throws JNDIException if the <b>Host name</b> was not found.
	 */
	public String createRequestUrl(Properties properties, String requestPath) throws JNDIException {
		String hostName = properties.getProperty(HttpResource.REQUEST_HOST_NAME);
		if (null == hostName) {
			throw new JNDIException(NodeMessage.JEMC136E, HttpResource.REQUEST_HOST_NAME);
		}
		String port = properties.getProperty(HttpResource.REQUEST_PORT);
		String protocolType = properties.getProperty(HttpResource.PROTOCOL_TYPE);
		if (null == protocolType) {
			protocolType = HttpResource.HTTP_PROTOCOL;
		}
		URIBuilder builder = new URIBuilder();
		builder.setScheme(protocolType);
		builder.setHost(hostName);
		if (null != port) {
			builder.setPort(Parser.parseInt(port));
		}
		if (null != requestPath) {
			builder.setPath(requestPath);
		}
		try {
			return builder.build().toString();
		} catch (URISyntaxException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		}
	}
	
	/**
	 * Creates and configures a {@link HttpClient} instance based on the given optional properties <code>properties</code>. <br>
	 * Configure the: <br>
	 * <li> the connection pooling configuration if <code>Login</code> and/or <code>Logout</code> is needed.
	 * <li> the optional <code>Proxy</code> configuration if needed (proxy url, port, proxy protocol, useranme, password, credential charset)
	 * <li> the optional <code>SSL</code> configuration if needed (https port)
	 * <li> the <code>Protocol Version</code>
	 * <li> the format for encoding <code>HTTP protocol elements</code>
	 * <li> the format of the <code>HTTP content charset</code>
	 * <li> the content of the <code>User-Agent</code> header
	 * <li> the boolean that defines whether responses with an invalid 
	 * <code>Transfer-Encoding</code> header should be rejected
	 * <li> the configuration whether use <code>Expect: 100-continue</code>
	 * 
	 * @param properties the http client configuration properties
	 * @return the {@link HttpClient}
	 * @throws Exception if an error occurs creating the http client
	 * @see HttpResource
	 */
	private Object createHttpClient(Properties properties) throws JNDIException {
		try {
			PoolingClientConnectionManager conMan = new PoolingClientConnectionManager(SchemeRegistryFactory.createDefault());
			String loginQueryString = properties.getProperty(HttpResource.REQUEST_LOGIN_QUERY_STRING);
			String logoutQueryString = properties.getProperty(HttpResource.REQUEST_LOGOUT_QUERY_STRING);
			DefaultHttpClient httpClient = null;
			if (null == loginQueryString && null == logoutQueryString) {
				httpClient = new DefaultHttpClient();
			} else {
				httpClient = new DefaultHttpClient(conMan);
			}
			httpClient.setRedirectStrategy(new LaxRedirectStrategy());
			InetAddress originAddress = InetAddress.getLocalHost();
			httpClient.getParams().setParameter(CoreProtocolPNames.ORIGIN_SERVER, originAddress);
			String proxyUrl = properties.getProperty(HttpResource.PROXY_URL);
			String proxyPortStr = properties.getProperty(HttpResource.PROXY_PORT);
			String proxyProtocol = properties.getProperty(HttpResource.PROXY_PROTOCOL);
			int proxyPort = Parser.parseInt(proxyPortStr);
			String username = properties.getProperty(HttpResource.USERID);
			String password = properties.getProperty(HttpResource.PASSWORD);
			String credentialCharset = properties.getProperty(HttpResource.CREDENTIAL_CHARSET);
			if (null != proxyUrl) {
				configureProxy(httpClient, proxyUrl, proxyPort, proxyProtocol, username, password, credentialCharset);
			}
			String protocolType = properties.getProperty(HttpResource.PROTOCOL_TYPE);
			String port = properties.getProperty(HttpResource.REQUEST_PORT);
			if (null != protocolType) {
				configureSSL(httpClient, protocolType, port);
			}
			String protocolVersion = properties.getProperty(HttpResource.PROTOCOL_VERSION);
			if (null != protocolVersion) {
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, getHttpVersion(protocolVersion));
			}
			String httpElementCharset = properties.getProperty(HttpResource.HTTP_ELEMENT_CHARSET);
			if (null != httpElementCharset) {
				httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, httpElementCharset);
			}
			String httpContentCharset = properties.getProperty(HttpResource.HTTP_CONTENT_CHARSET);
			if (null != httpContentCharset) {
				httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, httpContentCharset);
			}
			String userAgent = properties.getProperty(HttpResource.USER_AGENT);
			if (null != userAgent) {
				httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
			}
			String strictTransferEncodingStr = properties.getProperty(HttpResource.STRICT_TRANSFER_ENCODING);
			if (null != strictTransferEncodingStr) {
				boolean strictTransferEncoding = Parser.parseBoolean(strictTransferEncodingStr, false);
				httpClient.getParams().setParameter(CoreProtocolPNames.STRICT_TRANSFER_ENCODING, Boolean.valueOf(strictTransferEncoding));
			}
			String useExpectContinueStr = properties.getProperty(HttpResource.USE_EXPECT_CONTINUE);
			String waitForContinueStr = properties.getProperty(HttpResource.WAIT_FOR_CONTINUE);
			if (null != useExpectContinueStr && null != waitForContinueStr) {
				boolean useExpectContinue = Parser.parseBoolean(useExpectContinueStr, false);
				int waitForContinue = Parser.parseInt(waitForContinueStr);
				setExpectContinue(httpClient, useExpectContinue, waitForContinue);
			}
			return httpClient;
		} catch (KeyManagementException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (UnrecoverableKeyException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (UnknownHostException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (KeyStoreException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		}
	}
	
	/**
	 * Sets the <code>proxy</code> in the <code>DefaultHttpClient httpClient</code> 
	 * with all its properties, if proxy is needed.
	 * 
	 * @param httpClient the {@link DefaultHttpClient} in which to set the <code>proxy</code>.
	 * @param proxyUrl the <code>URL</code> of the <code>proxy</code>.
	 * @param proxyPort the <code>port</code> of the <code>proxy</code>.
	 * @param proxyProtocol the protocol of the <code>proxy</code>.
	 * @param userid the user id useful for the <code>proxy</code>.
	 * @param password the password useful for the <code>proxy</code>.
	 * @param credentialCharset the <code>Credential Charset</code> value 
	 * if specific charset must be used when encoding Credentials.
	 * @see ConnRoutePNames#DEFAULT_PROXY
	 */
	private void configureProxy(DefaultHttpClient httpClient, String proxyUrl, int proxyPort, String proxyProtocol, String userid, String password, String credentialCharset) {
		HttpHost proxy = null;
		if (null != proxyProtocol) {
			proxy = new HttpHost(proxyUrl, proxyPort, proxyProtocol);
		} else {
			proxy = new HttpHost(proxyUrl, proxyPort);
		}

		if (null != userid && null != password) {
			httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(userid, password));
		}
		if (null != credentialCharset) {
			httpClient.getParams().setParameter(AuthPNames.CREDENTIAL_CHARSET, credentialCharset);
		}
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	/**
	 * Configures SSL in the parameter <code>httpClient</code>, if necessary,
	 * that is if the <code>protocolType</code> is {@link HttpResource#HTTPS_PROTOCOL}. <br>
	 * If <code>port</code> is <code>null</code>, {@link #DEFAULT_HTTPS_PORT} is used.
	 * 
	 * @param httpClient http client already created in which to set SSL property.
	 * @param protocolType the protocol type:
	 * <li> {@link HttpResource#HTTP_PROTOCOL}
	 * <li> {@link HttpResource#HTTPS_PROTOCOL}
	 * @param port the <code>HTTPS Port</code>.
	 * @throws KeyStoreException if an error occurs
	 * @throws NoSuchAlgorithmException if an error occurs
	 * @throws UnrecoverableKeyException if an error occurs
	 * @throws KeyManagementException if an error occurs
	 */
	private void configureSSL(HttpClient httpClient, String protocolType, String port) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		// sets SSL ONLY if the scheme is HTTP
		if (protocolType.equalsIgnoreCase(HttpResource.HTTPS_PROTOCOL)) {
				SSLSocketFactory sf = buildSSLSocketFactory();
				int httpsPort = DEFAULT_HTTPS_PORT;
				if (null != port) {
					httpsPort = Parser.parseInt(port);
				}
				Scheme https = new Scheme(HttpResource.HTTPS_PROTOCOL, httpsPort, sf);
				SchemeRegistry sr = httpClient.getConnectionManager().getSchemeRegistry();
				sr.register(https);
		}
	}
 
	/**
	 * It builds a {@link SSLSocketFactory} if SSL is needed.
	 * @return the {@link SSLSocketFactory} for SSL purposes.
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @see SSLSocketFactory
	 */
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
	
	/**
	 * Sets in the <code>DefaultHttpClient httpClient</code> the {@link CoreProtocolPNames#USE_EXPECT_CONTINUE} property 
	 * and the {@link CoreProtocolPNames#WAIT_FOR_CONTINUE} property
	 * if necessary.
	 * 
	 * @param httpClient the {@link DefaultHttpClient} in which to set the <code>Expect: 100-continue</code> property.
	 * @param useExpectContinue  the <code>boolean</code> value. <code>true</code> if 100-continue must be used.
	 * @param waitForContinue the maximum waiting time
	 * @see HttpResource#USE_EXPECT_CONTINUE
	 * @see HttpResource#WAIT_FOR_CONTINUE
	 */
	private void setExpectContinue(DefaultHttpClient httpClient, boolean useExpectContinue, int waitForContinue){
		httpClient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.valueOf(useExpectContinue));
		httpClient.getParams().setParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, Integer.valueOf(waitForContinue));
	}

	/**
	 * Return the {@link HttpVersion} corresponding to the parameter <code>httpVersionStr</code>. <br>
	 * <li> HTTP/0.9 <code>String</code> corresponds to {@link HttpVersion#HTTP_0_9}
	 * <li> HTTP/1.0 <code>String</code> corresponds to {@link HttpVersion#HTTP_1_0}
	 * <li> HTTP/1.1 <code>String</code> corresponds to {@link HttpVersion#HTTP_1_1}
	 * 
	 * @param httpVersionStr the <code>Http Version</code> in <code>String</code> format.
	 * @return the {@link HttpVersion} corresponding to the parameter <code>httpVersionStr</code>.
	 * @see HttpVersion
	 */
	private static HttpVersion getHttpVersion(String httpVersionStr) {
		HttpVersion version = HttpVersion.HTTP_1_0;
		if (null != httpVersionStr) {
			if (httpVersionStr.trim().equalsIgnoreCase(HttpVersion.HTTP_0_9.toString())) {
				return HttpVersion.HTTP_0_9;
			} else if (httpVersionStr.trim().equalsIgnoreCase(HttpVersion.HTTP_1_1.toString())) {
				return HttpVersion.HTTP_1_1;
			}
		}
		return version;
	}
}