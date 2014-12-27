/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.impl.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.pepstock.jem.commands.util.HttpUtil;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.impl.AbstractObjectFactory;
import org.pepstock.jem.node.resources.impl.CommonKeys;
import org.pepstock.jem.node.tasks.jndi.JNDIException;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;

/**
 * Factory useful to create a {@link HttpClient} in particular a
 * {@link CloseableHttpClient} and the {@link InputStream} of the relative
 * request call, that is the <code>HTTP</code> datasource object to be used
 * inside the java programs. <br>
 * It uses a <code>HttpReference</code>, containing the properties of the
 * <code>HttpClient</code> object. <br>
 * The <code>HttpClient</code> object allows to connect to a <code>HTTP</code>
 * provider such as <code>Servlet</code>, or generally <code>HTTP URL</code>,
 * and call a <code>GET</CODE> or a <code>POST</code> method and it returns the
 * <code>Response</code> content inside the <code>InputStream</code>. <br>
 * In some cases <code>Login</code> and <code>Logout</code> may be necessary. <br>
 * It implements the <code>ObjectFactory</code>.
 * 
 * @see HttpReference
 * @see ObjectFactory
 * @see HttpClient
 * @see CloseableHttpClient
 * @see InputStream
 * @author Alessandro Zambrini
 * @version 1.0
 * 
 */
@SuppressWarnings("deprecation")
public class HttpFactory  extends AbstractObjectFactory {

	/**
	 * Default value of the <code>port</code> of the <code>HTTPS Schema</code>.
	 */
	public static final int DEFAULT_HTTPS_PORT = 443;

	/**
	 * It search the value of the property named <code>propertyName</code>
	 * inside the <code>Hashtable</code> in the parameter
	 * <code>environment</code>. <br>
	 * These properties are not configuration properties, but applicative.
	 * 
	 * @param environment the <code>Hashtable</code> in which it searches the
	 *            property called <code>propertyName</code>.
	 * @param propertyName the name of the property to search.
	 * @return the value of the property searched if found, <code>null</code>
	 *         otherwise.
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
	 * This method creates a {@link InputStream} for <code>HTTP</code> purposes,
	 * starting from the {@link HttpReference} in the parameter
	 * <code>object</code>. <br>
	 * The <code>InputStream</code> contains the <code>Response</code> content
	 * produced calling an <code>HTTP</code> datasource, using a
	 * {@link CloseableHttpClient} (an <code>HttpClient</code>). <br>
	 * The <code>HttpClient</code> allows to connect to a <code>HTTP</code>
	 * provider, such as <code>Servlet</code>, or generally
	 * <code>HTTP URL</code>, call a <code>GET</CODE> or a <code>POST</code>
	 * method and puts the <code>Response</code> content inside the
	 * <code>InputStream</code>. <br>
	 * In some cases <code>Login</code> and <code>Logout</code> may be
	 * necessary. <br>
	 * 
	 * @see ObjectFactory
	 */
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> environment) throws JNDIException {
		if ((object == null) || !(object instanceof Reference)) {
			return null;
		}
		Properties properties = loadProperties(object, HttpResourceKeys.PROPERTIES_ALL);
		CloseableHttpClient httpClient = (CloseableHttpClient) createHttpClient(properties);
		HttpRequestBase request = createRequestMethod(properties, environment);
		// Execute request
		return execute(httpClient, request, properties);
	}

	/**
	 * This method creates the <code>Http Request</code> that is the
	 * {@link HttpRequestBase} to be performed by the <code>HttpClient</code>. <br>
	 * The resulting <code>Http Request</code> may be a {@link HttpGet}
	 * (default) or a {@link HttpPost} depending on the value of the property
	 * {@link HttpResource#REQUEST_METHOD}. <br>
	 * It uses also the following properties to create the
	 * <code>Http Request</code>: <li> {@link HttpResource#REQUEST_PATH} : the
	 * optional path of the <code>Request URL</code> <li>
	 * {@link HttpResource#REQUEST_PARAMETERS} : the optional parameters <li>
	 * {@link HttpResource#REQUEST_QUERY_STRING} : the optional
	 * <code>Query String</code> <li>
	 * {@link CoreProtocolPNames#HTTP_CONTENT_CHARSET} : the format of the
	 * <code>HTTP content charset</code> <li>
	 * {@link HttpResource#REQUEST_HOST_NAME} : the <b>mandatory</b> host name
	 * <li> {@link HttpResource#REQUEST_HOST_PORT} : the optional host port <li>
	 * {@link HttpResource#PROTOCOL_TYPE} : the protocol type,
	 * {@link HttpResource#HTTP_PROTOCOL} (as default) or
	 * {@link HttpResource#HTTPS_PROTOCOL}.
	 * 
	 * @param httpClient the <code>HttpClient</code> containing the
	 *            {@link CoreProtocolPNames#HTTP_CONTENT_CHARSET}.
	 * @param properties the <code>Properties</code> object containing the
	 *            useful configuration properties.
	 * @param environment the <code>Hashtable</code> in which to search
	 *            applicative (not configuration) properties. It is the
	 *            <code>Environment</code> of the {@link Context}.
	 * @return <code>Http Request</code> generated.
	 * @throws JNDIException if an error occurs.
	 * @see HttpGet
	 * @see HttpPost
	 * @see Context
	 */
	private HttpRequestBase createRequestMethod(Properties properties, Map<?, ?> environment) throws JNDIException {
		String path = (String) findEnvironmentProperty(environment, HttpResourceKeys.REQUEST_PATH);
		@SuppressWarnings("unchecked")
		Map<String, String> parameters = (Map<String, String>) findEnvironmentProperty(environment, HttpResourceKeys.REQUEST_PARAMETERS);
		String queryString = (String) findEnvironmentProperty(environment, HttpResourceKeys.REQUEST_QUERY_STRING);
		String requestMethod = (String) findEnvironmentProperty(environment, HttpResourceKeys.REQUEST_METHOD);
		String parametersCharsetFormat = properties.getProperty(HttpResourceKeys.HTTP_CONTENT_CHARSET);
		String url = createRequestUrl(properties, path);
		if (null == requestMethod) {
			requestMethod = HttpResourceKeys.REQUEST_GET_METHOD;
		}
		HttpRequestBase request = null;
		if (requestMethod.equalsIgnoreCase(HttpResourceKeys.REQUEST_GET_METHOD)) {
			request = createHttpGetMethod(parameters, url, queryString);
		} else {
			request = createHttpPostMethod(parameters, url, queryString, parametersCharsetFormat);
		}
		// Request configuration
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		String useExpectContinueStr = properties.getProperty(HttpResourceKeys.USE_EXPECT_CONTINUE);
		if (null != useExpectContinueStr) {
			boolean useExpectContinue = Parser.parseBoolean(useExpectContinueStr, false);
			requestConfigBuilder.setExpectContinueEnabled(useExpectContinue);
		}
		RequestConfig requestConfig = requestConfigBuilder.build();
		request.setConfig(requestConfig);
		String protocolVersion = properties.getProperty(HttpResourceKeys.PROTOCOL_VERSION);
		if (null != protocolVersion) {
			request.setProtocolVersion(getHttpVersion(protocolVersion));
		}
		return request;
	}

	/**
	 * Return the {@link HttpVersion} corresponding to the parameter
	 * <code>httpVersionStr</code>. <br>
	 * <li>HTTP/0.9 <code>String</code> corresponds to
	 * {@link HttpVersion#HTTP_0_9} <li>HTTP/1.0 <code>String</code> corresponds
	 * to {@link HttpVersion#HTTP_1_0} <li>HTTP/1.1 <code>String</code>
	 * corresponds to {@link HttpVersion#HTTP_1_1}
	 * 
	 * @param httpVersionStr the <code>Http Version</code> in
	 *            <code>String</code> format.
	 * @return the {@link HttpVersion} corresponding to the parameter
	 *         <code>httpVersionStr</code>.
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

	/**
	 * This method creates the <code>Http Request URL</code> to be called. <br>
	 * It uses the following properties: <li>
	 * {@link HttpResource#REQUEST_HOST_NAME} : the <b>mandatory</b> host name
	 * <li> {@link HttpResource#REQUEST_HOST_PORT} : the optional host port <li>
	 * {@link HttpResource#PROTOCOL_TYPE} : the protocol type,
	 * {@link HttpResource#HTTP_PROTOCOL} (as default) or
	 * {@link HttpResource#HTTPS_PROTOCOL}.
	 * 
	 * @param properties the <code>Properties</code> object containing the
	 *            useful configuration properties.
	 * @param requestPath the path of the request.
	 * @return the resulting <code>Http Request URL</code>.
	 * @throws JNDIException if the <b>Host name</b> was not found.
	 */
	private String createRequestUrl(Properties properties, String requestPath) throws JNDIException {
		String hostName = properties.getProperty(HttpResourceKeys.REQUEST_HOST_NAME);
		if (null == hostName) {
			throw new JNDIException(NodeMessage.JEMC136E, HttpResourceKeys.REQUEST_HOST_NAME);
		}
		String port = properties.getProperty(HttpResourceKeys.REQUEST_PORT);
		String protocolType = properties.getProperty(HttpResourceKeys.PROTOCOL_TYPE);
		if (null == protocolType) {
			protocolType = HttpResourceKeys.HTTP_PROTOCOL;
		}
		URIBuilder builder = new URIBuilder();
		builder.setScheme(protocolType);
		builder.setHost(hostName);
		if (null != port) {
			builder.setPort(Parser.parseInt(port));
		} else if (protocolType == HttpResourceKeys.HTTPS_PROTOCOL) {
			builder.setPort(DEFAULT_HTTPS_PORT);
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
	 * This method creates the {@link HttpGet} that is the
	 * <code>Http Request</code> starting from the parameter <code>url</code>,
	 * the optional <code>Query String</code> (the parameter
	 * <code>queryString</code>) and the optional <code>Http Request</code>
	 * parameters <code>parameters</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param queryString the optional <code>Query String</code>.
	 * @return the {@link HttpGet} that is the <code>Http Request</code> to be
	 *         called.
	 * @throws JNDIException if an error occurs.
	 */
	private HttpGet createHttpGetMethod(Map<String, String> parameters, String url, String queryString) throws JNDIException {
		try {
			String newUrl = url;
			if (null != queryString) {
				newUrl = newUrl + "?" + queryString;
			}
			URIBuilder builder = new URIBuilder(newUrl);
			if (null != parameters && !parameters.isEmpty()) {
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
	 * This method creates the {@link HttpPost} that is the
	 * <code>Http Request</code> starting from the parameter <code>url</code>,
	 * the optional <code>Http Request</code> parameters <code>parameters</code>
	 * and the optional <code>Query String</code> (the parameter
	 * <code>queryString</code>). <br>
	 * It uses the format of the <code>HTTP content charset</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param queryString the optional <code>Query String</code>.
	 * @param parametersCharsetFormat the format of the
	 *            <code>HTTP content charset</code>.
	 * @return the {@link HttpPost} that is the <code>Http Request</code> to be
	 *         called.
	 * @throws JNDIException if an error occurs.
	 */
	private HttpPost createHttpPostMethod(Map<String, String> parameters, String url, String queryString, String parametersCharsetFormat) throws JNDIException {
		try {
			String newUrl = url;
			if (null != queryString) {
				newUrl = newUrl + "?" + queryString;
			}
			URIBuilder builder = new URIBuilder(newUrl);
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
	 * This method creates the {@link HttpPost} that is the
	 * <code>Http Request</code> starting from the parameter <code>url</code>,
	 * and the optional <code>Http Request</code> parameters
	 * <code>parameters</code> without the <code>Query String</code>. <br>
	 * It uses the format of the <code>HTTP content charset</code>.
	 * 
	 * @param parameters the optional <code>Http Request Parameters</code>.
	 * @param url the <b>mandatory</b> url.
	 * @param parametersCharsetFormat the format of the
	 *            <code>HTTP content charset</code>.
	 * @return the {@link HttpPost} that is the <code>Http Request</code> to be
	 *         called.
	 * @throws JNDIException if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	private HttpPost createHttpPostMethod(@SuppressWarnings("rawtypes") Map parameters, String url, String parametersCharsetFormat) throws JNDIException {
		return createHttpPostMethod(parameters, url, null, parametersCharsetFormat);
	}

	/**
	 * Creates and configures a {@link CloseableHttpClient} instance based on
	 * the given optional properties <code>properties</code>. <br>
	 * Configure the: <br>
	 * <li>the connection pooling configuration if <code>Login</code> and/or
	 * <code>Logout</code> is needed. <li>the optional <code>Proxy</code>
	 * configuration if needed (proxy url, port, proxy protocol, useranme,
	 * password) <li>the optional <code>SSL</code> configuration if needed
	 * (https port) <li>the content of the <code>User-Agent</code> header
	 * 
	 * @param properties the http client configuration properties
	 * @return the {@link CloseableHttpClient}
	 * @throws JNDIException if an error occurs creating the http client
	 * @see HttpResource
	 */
	private Object createHttpClient(Properties properties) throws JNDIException {
		try {
			PoolingHttpClientConnectionManager conMan = new PoolingHttpClientConnectionManager();
			String loginQueryString = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_QUERY_STRING);
			String logoutQueryString = properties.getProperty(HttpResourceKeys.REQUEST_LOGOUT_QUERY_STRING);
			CloseableHttpClient httpClient = null;

			String protocolType = properties.getProperty(HttpResourceKeys.PROTOCOL_TYPE);
			HttpClientBuilder httpClientBuilder = HttpUtil.createHttpClientByScheme(protocolType);
			
			if (null != loginQueryString || null != logoutQueryString) {
				httpClientBuilder.setConnectionManager(conMan);
			}
			httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
			String proxyUrl = properties.getProperty(HttpResourceKeys.PROXY_URL);
			String proxyPortStr = properties.getProperty(HttpResourceKeys.PROXY_PORT);
			String proxyProtocol = properties.getProperty(HttpResourceKeys.PROXY_PROTOCOL);
			int proxyPort = Parser.parseInt(proxyPortStr);
			String username = properties.getProperty(CommonKeys.USERID);
			String password = properties.getProperty(CommonKeys.PASSWORD);
			if (null != proxyUrl) {
				configureProxy(httpClientBuilder, proxyUrl, proxyPort, proxyProtocol, username, password);
			}

			String userAgent = properties.getProperty(HttpResourceKeys.USER_AGENT);
			if (null != userAgent) {
				httpClientBuilder.setUserAgent(userAgent);
			}
			httpClient = httpClientBuilder.build();
			return httpClient;
		} catch (KeyManagementException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (UnrecoverableKeyException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		} catch (KeyStoreException e) {
			throw new JNDIException(NodeMessage.JEMC156E, e, e.getMessage());
		}
	}

	/**
	 * Sets the <code>proxy</code> in the
	 * <code>HttpClientBuilder httpClientBuilder</code> with all its properties,
	 * if proxy is needed.
	 * 
	 * @param httpClientBuilder the {@link HttpClientBuilder} in which to set
	 *            the <code>proxy</code>.
	 * @param proxyUrl the <code>URL</code> of the <code>proxy</code>.
	 * @param proxyPort the <code>port</code> of the <code>proxy</code>.
	 * @param proxyProtocol the protocol of the <code>proxy</code>.
	 * @param userid the user id useful for the <code>proxy</code>.
	 * @param password the password useful for the <code>proxy</code>.
	 * @see HttpClientBuilder#setProxy(HttpHost)
	 */
	private void configureProxy(HttpClientBuilder httpClientBuilder, String proxyUrl, int proxyPort, String proxyProtocol, String userid, String password) {
		HttpHost proxy = null;
		if (null != proxyProtocol) {
			proxy = new HttpHost(proxyUrl, proxyPort, proxyProtocol);
		} else {
			proxy = new HttpHost(proxyUrl, proxyPort);
		}
		if (null != userid && null != password) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(userid, password));
			httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
		}
		httpClientBuilder.setProxy(proxy);
	}

	/**
	 * This method executes the desired <code>Http Request</code> and returns
	 * the <code>InputStream</code> containing the Body content of the
	 * <code>Response</code>. <br>
	 * In some cases <code>Login</code> and <code>Logout</code> may be necessary
	 * to perform the desired <code>Http Request</code>. <br>
	 * 
	 * @param httpClient the <code>CloseableHttpClient</code> that performs the
	 *            <code>Http Request</code>.
	 * @param request the <code>Http Request</code> to be performed.
	 * @param properties the <code>Properties</code> object containing the
	 *            useful configuration properties, such as
	 *            {@link HttpResource#REQUEST_LOGIN_QUERY_STRING} and
	 *            {@link HttpResource#REQUEST_LOGOUT_QUERY_STRING} if
	 *            <code>Login</code> and <code>Logout</code> is necessary.
	 * @return the <code>InputStream</code> containing the Body content of the
	 *         <code>Response</code>
	 * @throws JNDIException if an error occurs.
	 * 
	 * @see HttpResource#REQUEST_LOGIN_QUERY_STRING
	 * @see HttpResource#REQUEST_LOGOUT_QUERY_STRING
	 */
	private InputStream execute(CloseableHttpClient httpClient, HttpRequestBase request, Properties properties) throws JNDIException {
		String loginQueryString = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_QUERY_STRING);
		String logoutQueryString = properties.getProperty(HttpResourceKeys.REQUEST_LOGOUT_QUERY_STRING);
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
			try {
				httpClient.close();
			} catch (IOException ex) {
				LogAppl.getInstance().emit(NodeMessage.JEMC157E, ex, ex.getMessage());
			}
		}
	}

	/**
	 * This method executes the <code>Login</code> if necessary before to
	 * connect to the <code>HTTP</code> datasource and execute the desired
	 * <code>Http Request</code>.
	 * 
	 * @param httpClient httpClient the <code>HttpClient</code> that performs
	 *            the <code>Login</code>.
	 * @param properties the <code>Properties</code> object containing the
	 *            useful configuration properties: <li>
	 *            {@link HttpResource#REQUEST_LOGIN_USERID} or
	 *            {@link Resource#USERID} <li>
	 *            {@link HttpResource#REQUEST_LOGIN_PASSWORD} or
	 *            {@link Resource#PASSWORD} <li>
	 *            {@link HttpResource#REQUEST_LOGIN_PARAM_USERID} <li>
	 *            {@link HttpResource#REQUEST_LOGIN_PARAM_PASSWORD} and the
	 *            properties to create the <code>Login Url</code>
	 * 
	 * @param loginQueryString the <code>Login Query String</code>.
	 * @throws JNDIException if an error occurs.
	 */
	private void executeLogin(HttpClient httpClient, Properties properties, String loginQueryString) throws JNDIException {
		if (null != loginQueryString) {
			String userid = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_USERID);
			if (null == userid) {
				userid = properties.getProperty(CommonKeys.USERID);
			}
			if (null == userid) {
				LogAppl.getInstance().emit(NodeMessage.JEMC159E, HttpResourceKeys.REQUEST_LOGIN_USERID, CommonKeys.USERID);
				throw new JNDIException(NodeMessage.JEMC159E, HttpResourceKeys.REQUEST_LOGIN_USERID, CommonKeys.USERID);
			}
			String password = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_PASSWORD);
			if (null == password) {
				password = properties.getProperty(CommonKeys.PASSWORD);
			}
			if (null == password) {
				LogAppl.getInstance().emit(NodeMessage.JEMC159E, HttpResourceKeys.REQUEST_LOGIN_PASSWORD, CommonKeys.PASSWORD);
				throw new JNDIException(NodeMessage.JEMC159E, HttpResourceKeys.REQUEST_LOGIN_PASSWORD, CommonKeys.PASSWORD);
			}
			String paramUserId = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_PARAM_USERID);
			if (null == paramUserId) {
				LogAppl.getInstance().emit(NodeMessage.JEMC160E, HttpResourceKeys.REQUEST_LOGIN_PARAM_USERID);
				throw new JNDIException(NodeMessage.JEMC160E, HttpResourceKeys.REQUEST_LOGIN_PARAM_USERID);
			}
			String paramPassword = properties.getProperty(HttpResourceKeys.REQUEST_LOGIN_PARAM_PASSWORD);
			if (null == paramPassword) {
				LogAppl.getInstance().emit(NodeMessage.JEMC160E, HttpResourceKeys.REQUEST_LOGIN_PARAM_PASSWORD);
				throw new JNDIException(NodeMessage.JEMC160E, HttpResourceKeys.REQUEST_LOGIN_PARAM_PASSWORD);
			}
			String url = createRequestUrl(properties, loginQueryString);
			Properties parameters = new Properties();
			parameters.setProperty(paramUserId, userid);
			parameters.setProperty(paramPassword, password);
			String parametersCharsetFormat = properties.getProperty(HttpResourceKeys.HTTP_CONTENT_CHARSET);
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
	 * This method executes the <code>Logout</code> if necessary after the
	 * connection to the <code>HTTP</code> datasource and the execution of the
	 * desired <code>Http Request</code>.
	 * 
	 * @param httpClient httpClient the <code>HttpClient</code> that performs
	 *            the <code>Logout</code>.
	 * @param properties the <code>Properties</code> object containing the
	 *            useful configuration properties to create the
	 *            <code>Logout Url</code>.
	 * @param logoutQueryString the <code>Logout Query String</code>.
	 * @throws JNDIException if an error occurs.
	 */
	private void executeLogout(HttpClient httpClient, Properties properties, String logoutQueryString) throws JNDIException {
		String url = createRequestUrl(properties, logoutQueryString);
		String parametersCharsetFormat = properties.getProperty(HttpResourceKeys.HTTP_CONTENT_CHARSET);
		HttpPost request = createHttpPostMethod(null, url, parametersCharsetFormat);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			httpClient.execute(request, responseHandler);
		} catch (ClientProtocolException e) {
			throw new JNDIException(NodeMessage.JEMC231E, e, e.getMessage());
		} catch (IOException e) {
			throw new JNDIException(NodeMessage.JEMC231E, e, e.getMessage());
		}
		request.abort();
	}

	/**
	 * It executes the <code>Http Request</code> in the parameter
	 * <code>request</code> (it may be a <code>GET</code> or a <code>POST</code>
	 * ) using the <code>HttpClient</code> in the parameter
	 * <code>httpClient</code>. <br>
	 * It puts the <code>Response</code> content inside the
	 * <code>InputStream</code>. <br>
	 * 
	 * @param httpClient the <code>HttpClient</code> that performs the
	 *            <code>Http Request</code>.
	 * @param request the <code>Http Request</code> performed by the
	 *            <code>HttpClient</code> (it may be a <code>GET</code> or a
	 *            <code>POST</code>).
	 * @return the <code>InputStream</code> containing the <code>Response</code>
	 *         content.
	 * @throws JNDIException if an error occurs creating the
	 *             <code>InputStream</code>.
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

}
