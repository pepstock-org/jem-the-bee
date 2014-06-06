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
package org.pepstock.jem.node.resources;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;


/**
 * Contains all information necessary to create a <code>HTTP</code> source by JNDI.<br>
 * Here are the list of attributes to use to create a <code>HTTP</code> source by configuration:<br>
 * <br>
 * <pre>
 * HTTP_CONTENT_CHARSET = &quot;{@link CoreProtocolPNames.HTTP_CONTENT_CHARSET}&quot;;
 * PROTOCOL_VERSION = &quot;{@link CoreProtocolPNames.PROTOCOL_VERSION}&quot;;
 * USE_EXPECT_CONTINUE = &quot;{@link CoreProtocolPNames.USE_EXPECT_CONTINUE}&quot;;
 * USER_AGENT  = &quot;{@link CoreProtocolPNames.USER_AGENT}&quot;;
 * PROXY_URL = &quot;proxyUrl&quot;;
 * PROXY_PROTOCOL = &quot;proxyProtocol&quot;;
 * PROXY_PORT = &quot;proxyPort&quot;;
 * REQUEST_HOST_NAME = &quot;requestHostName&quot;;
 * REQUEST_PORT = &quot;requestPort&quot;;
 * PROTOCOL_TYPE = &quot;protocolType&quot;;
 * REQUEST_LOGIN_QUERY_STRING = &quot;requestLoginQueryString&quot;;
 * REQUEST_LOGIN_USERID = &quot;requestLoginUserid&quot;;
 * REQUEST_LOGIN_PASSWORD = &quot;requestLoginPassword&quot;;
 * REQUEST_LOGIN_PARAM_USERID = &quot;requestLoginParamUserid&quot;;
 * REQUEST_LOGIN_PARAM_PASSWORD = &quot;requestLoginParamPassword&quot;;
 * REQUEST_LOGOUT_QUERY_STRING = &quot;requestLogoutQueryString&quot;;
 * USERID = &quot;username&quot;;
 * PASSWORD = &quot;password&quot;;
 * </pre>
 * 
 * All these properties are <b>OPTIONAL</b> with the exception of {@link #REQUEST_HOST_NAME}.
 * 
 * Here are the list of applicative attributes to use to create a <code>HTTP</code> source: <br>
 * <br>
 * <pre>
 * REQUEST_PATH = &quot;requestPath&quot;;
 * REQUEST_METHOD = &quot;requestMethod&quot;;
 * REQUEST_QUERY_STRING = &quot;requestQueryString&quot;;
 * REQUEST_PARAMETERS = &quot;requestParameters&quot;;
 * </pre>
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class HttpResource extends Resource {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constant indicating that the <code>Request Method</code> to use
	 * is a {@link HttpGet}. It is the default.
	 */
	public static final String REQUEST_GET_METHOD = "GET";

	/**
	 * Constant indicating that the <code>Request Method</code> to use
	 * is a {@link HttpPost}. 
	 */
	public static final String REQUEST_POST_METHOD = "POST";

	/**
	 * Constant indicating that the <code>Protocol</code> to use
	 * is <code>HTTP</code>. It is the default.
	 */
	public static final String HTTP_PROTOCOL = "http";
	
	/**
	 * Constant indicating that the <code>Protocol</code> to use
	 * is <code>HTTPS</code>. 
	 */
	public static final String HTTPS_PROTOCOL = "https";
	

	/**
	 * Constant property containing the format of the <code>HTTP content charset</code>,
	 * for example: <code>UTF-8</code>. Default is: <code>ISO-8859-1</code>. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 * @see CoreProtocolPNames.HTTP_CONTENT_CHARSET
	 */
	public static final String HTTP_CONTENT_CHARSET = "httpContentCharset";
	
	/**
	 * Constant property containing the protocol version to use: <br>
	 * <li> {@link HttpVersion.HTTP_0_9}
	 * <li> {@link HttpVersion.HTTP_1_0} , default value
	 * <li> {@link HttpVersion.HTTP_1_1} <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 * @see HttpVersion
	 * @see CoreProtocolPNames.PROTOCOL_VERSION
	 */
	public static final String PROTOCOL_VERSION = "protocolVersion";
	
	/**
	 * Constant property containing the boolean that defines whether use <code>Expect: 100-continue</code>
	 * to improve performances. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 * @see CoreProtocolPNames.USE_EXPECT_CONTINUE
	 */
	public static final String USE_EXPECT_CONTINUE = "useExpectContinue";

	/**
	 * Constant property containing the content of the <code>User-Agent</code> header if needed. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 * @see CoreProtocolPNames.USER_AGENT
	 */
	public static final String USER_AGENT  = "userAgent";
	
	/**
	 * Constant property containing the <code>Proxy url</code> if proxy is needed. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String PROXY_URL = "proxyUrl";
	
	/**
	 * Constant property containing the <code>Proxy protocol</code> if proxy is needed. <br>
	 * As default is {@link #HTTP_PROTOCOL}. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String PROXY_PROTOCOL = "proxyProtocol";
	
	/**
	 * Constant property containing the <code>Proxy port</code> if proxy is needed. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String PROXY_PORT = "proxyPort";

	/**
	 * Constant property containing the target <code>Host Name</code> of the <code>Request</code>. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String REQUEST_HOST_NAME = "requestHostName";
	
	/**
	 * Constant property containing the target <code>PORT</code> of the <code>Request</code> if necessary. <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String REQUEST_PORT = "requestPort";
	
	/**
	 * Constant property containing the protocol type to use. It may be:
	 * <li> {@link #HTTP_PROTOCOL}
	 * <li> {@link #HTTPS_PROTOCOL} <br>
	 * Configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String PROTOCOL_TYPE = "protocolType";
	
	/**
	 * Constant property containing the optional <code>Login Query String</code>
	 * if login is necessary. <br>
	 * Configuration property. <br>
	 * Property useful to perform login if it is necessary before to connect to the
	 * <code>Http Source</code> to get the desired data.
	 */	
	public static final String REQUEST_LOGIN_QUERY_STRING = "requestLoginQueryString";
	
	/**
	 * Constant property containing the optional <code>Login Userid</code>
	 * if login is necessary. <br>
	 * If not present, {@link Resource#USERID} is used. <br>
	 * Configuration property. <br>
	 * Property useful to perform login if it is necessary before to connect to the
	 * <code>Http Source</code> to get the desired data.
	 */	
	public static final String REQUEST_LOGIN_USERID = "requestLoginUserid";

	/**
	 * Constant property containing the optional <code>Login Password</code>
	 * if login is necessary. <br>
	 * If not present, {@link Resource#PASSWORD} is used. <br>
	 * Configuration property. <br>
	 * Property useful to perform login if it is necessary before to connect to the
	 * <code>Http Source</code> to get the desired data.
	 */	
	public static final String REQUEST_LOGIN_PASSWORD = "requestLoginPassword";

	/**
	 * Constant property containing the optional <code>Login Parameter Userid</code>
	 * if login is necessary. <br>
	 * Configuration property. <br>
	 * Property useful to perform login if it is necessary before to connect to the
	 * <code>Http Source</code> to get the desired data.
	 */	
	public static final String REQUEST_LOGIN_PARAM_USERID = "requestLoginParamUserid";
	
	/**
	 * Constant property containing the optional <code>Login Parameter Password</code>
	 * if login is necessary. <br>
	 * Configuration property. <br>
	 * Property useful to perform login if it is necessary before to connect to the
	 * <code>Http Source</code> to get the desired data.
	 */	
	public static final String REQUEST_LOGIN_PARAM_PASSWORD = "requestLoginParamPassword";
	
	/**
	 * Constant property containing the optional <code>Logout Query String</code>
	 * if logout is necessary. <br>
	 * Configuration property. <br>
	 * Property useful to perform logout if it is necessary after the connection to the
	 * <code>Http Source</code> to get the desired data.
	 */
	public static final String REQUEST_LOGOUT_QUERY_STRING = "requestLogoutQueryString";
	
	/**
	 * Constant property containing the <code>Path</code> of the <code>Request URL</code> if necessary. <br>
	 * Not configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */	
	public static final String REQUEST_PATH = "requestPath";
	
	/**
	 * Constant property containing the type method of the <code>Request</code> to create. <br>
	 * It may be: 
	 * <li> {@link #REQUEST_GET_METHOD} to execute a <code>HTTP GET</code> (as default)
	 * <li> {@link #REQUEST_POST_METHOD} to execute a <code>HTTP POST</code> <br>
	 * Not configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String REQUEST_METHOD = "requestMethod";
	
	/**
	 * Constant property containing the optional query string if a {@link HttpGet} 
	 * is used. <br>
	 * Optionally {@link #REQUEST_PARAMETERS} may be used. <br>
	 * Not configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String REQUEST_QUERY_STRING = "requestQueryString";
	
	/**
	 * Constant property containing the optional <code>Request parameters</code>. <br>
	 * It may be used in case of {@link HttpGet} <code>Request</code> or {@link HttpPost} <code>Request</code> too.
	 * In case of a {@link HttpGet} <code>Request</code> optionally {@link #REQUEST_QUERY_STRING} may be used. <br>
	 * Not configuration property. <br>
	 * Property  useful to create the {@link HttpClient} to use <code>HTTP</code> source.
	 */
	public static final String REQUEST_PARAMETERS = "requestParameters";
	
	/**
	 * List of all configuration properties.
	 */
	public static final List<String> PROPERTIES_ALL = Collections.unmodifiableList(Arrays.asList(
		USERID, 
		PASSWORD, 
		PROXY_URL, 
		PROXY_PROTOCOL,
		PROXY_PORT,
		PROTOCOL_VERSION,
		HTTP_CONTENT_CHARSET,
		USER_AGENT,
		USE_EXPECT_CONTINUE,
		REQUEST_HOST_NAME,
		REQUEST_PORT,
		PROTOCOL_TYPE,
		REQUEST_LOGIN_QUERY_STRING,
		REQUEST_LOGIN_USERID,
		REQUEST_LOGIN_PASSWORD,
		REQUEST_LOGIN_PARAM_USERID,
		REQUEST_LOGIN_PARAM_PASSWORD,
		REQUEST_LOGOUT_QUERY_STRING));
	
	/**
	 * Type for <code>HTTP</code> sources
	 */
	public static final String TYPE = "http";

	/**
	 * Constructs the object adding user name, password, and the specific unchangeable final static
	 * properties.
	 */
	public HttpResource() {
		setType(TYPE);
	}
}