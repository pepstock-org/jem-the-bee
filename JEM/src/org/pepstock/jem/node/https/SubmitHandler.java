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
package org.pepstock.jem.node.https;

import java.io.IOException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.commands.SubmitMessage;
import org.pepstock.jem.commands.SubmitParameters;
import org.pepstock.jem.commands.util.Factory;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.security.keystore.KeyStoreUtil;
import org.pepstock.jem.node.security.keystore.KeyStoresInfo;
import org.pepstock.jem.node.security.keystore.KeysUtil;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;

import com.hazelcast.core.IQueue;
import com.hazelcast.core.IdGenerator;

/**
 * Is the HTTP request handler when you use NO java clients to submit jobs.
 * It takes care about the information sent from the client.<br>
 * Accepts ONLY POST requests where in the body needs:<br>
 * <br>
 * FirstRow (delimiter is 'n'): <code>wait=wait&jcl=jcl&user=user&type=type&env=env&password=password&callback=localPort&printOutput=printOutput&signature=signature</code><br>
 * Rest of the body: JCL content to submit<br>
 * <br>
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class SubmitHandler implements HttpRequestHandler {
	
	/**
	 * URL action to use to submit job
	 */
	public static final String DEFAULT_ACTION = "/submit";
	
	static final String JOB_SUBMIT_IP_ADDRESS_KEY = "jem-submit-client-address";
	
	private static final String JOB_SUBMIT_CALLBACK_PORT_KEY = "callback";
	
	private static final String USER_SIGNATURE_KEY = "signature";
	
	private static final String POST = "POST";
	
	private static final String RESPONSE_MIME_TYPE = "text/plain";
	
	private static final String DELIMITER = "\n";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.
	 * HttpRequest, org.apache.http.HttpResponse,
	 * org.apache.http.protocol.HttpContext)
	 */
	@Override
	public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
		// extracts the host
		String host = context.getAttribute(JOB_SUBMIT_IP_ADDRESS_KEY).toString();
		// gets HTTP method (uses locale ENGLISH to be sure to have POST)
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		// if NOT post, exception!
		if (!method.equals(POST)) {
			LogAppl.getInstance().emit(NodeMessage.JEMC284W, method, host);
			throw new MethodNotSupportedException(NodeMessage.JEMC284W.toMessage().getFormattedMessage(method, host));
		}
		// gets the URI or the request
		String target = request.getRequestLine().getUri();
		// if is not the same , accepts, exception!
		if (!target.equalsIgnoreCase(DEFAULT_ACTION)){
			LogAppl.getInstance().emit(NodeMessage.JEMC285W, target, host);
			throw new MethodNotSupportedException(NodeMessage.JEMC285W.toMessage().getFormattedMessage(target, host));
		}
		// checks the HTTP request
		if (request instanceof HttpEntityEnclosingRequest) {
			// gets the entity of the request
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			// gets the body in string format
			String result = EntityUtils.toString(entity, CharSet.DEFAULT);
			// reads the first line,
			// with all URL encoded variables  
			String vars = StringUtils.substringBefore(result, DELIMITER);
			// loads a map with all parms
			Map<String, String> parms = loadParametersMap(URLEncodedUtils.parse(vars, CharSet.DEFAULT));
			// gets the USER
			String user = parms.get(SubmitParameters.USER.getName());
			// if JEM is configured to have the Socket Interceptor on HC
			// the client MUST provide a SIGNATURE (using own private key) with
			// the user crypted inside
			if (Main.getHazelcastConfig().getNetworkConfig().getSocketInterceptorConfig().isEnabled()){
				// checks if there is the signature
				if (parms.containsKey(USER_SIGNATURE_KEY)){
					// gets the signature in HEX format
					String cryptedUserString = parms.get(USER_SIGNATURE_KEY);
					// gets keys stores
					KeyStoresInfo keyStoresInfo = KeyStoreUtil.getKeyStoresInfo();
					try {
						// extracts from the USER key store the PUBLIC KEY (upload by UI) for the user  
						PublicKey publicKey = KeysUtil.getPublicKeyByAlias(keyStoresInfo.getUserKeystoreInfo(), user);
						// creates tne SIGNATURE verifying steps
						Signature signature = Signature.getInstance("SHA256withRSA");
						// sets public key
						signature.initVerify(publicKey);
						// sets content to check. It uses USER
						signature.update(user.getBytes(CharSet.DEFAULT_CHARSET_NAME));
						// checks if is verified
						if (!signature.verify(Hex.decodeHex(cryptedUserString.toCharArray()))){
							// if not, log and EXCEPTION
							LogAppl.getInstance().emit(NodeMessage.JEMC286W, user, host);
							throw new HttpException(NodeMessage.JEMC286W.toMessage().getFormattedMessage(user, host));
						}
					} catch (MessageException e) {
						LogAppl.getInstance().emit(NodeMessage.JEMC286W, user, host);
						throw new ProtocolException(e.getMessage(), e);
					} catch (KeyException e) {
						throw new ProtocolException(e.getMessage(), e);
					} catch (DecoderException e) {
						throw new ProtocolException(e.getMessage(), e);
					} catch (NoSuchAlgorithmException e) {
						throw new ProtocolException(e.getMessage(), e);
					} catch (SignatureException e) {
						throw new ProtocolException(e.getMessage(), e);
					}
				} else {
					LogAppl.getInstance().emit(NodeMessage.JEMC287W, user, host);
					// if here, the signature is missing
					throw new HttpException(NodeMessage.JEMC287W.toMessage().getFormattedMessage(user, host));
				}
			}
			// gets JEM environemnt name and its passwrod
			String env = parms.get(SubmitParameters.ENV.getName());
			String password = parms.get(SubmitParameters.PASSWORD.getName());
			// checks if password and env are same, 
			// comparing with the HC configuration
			if (!Main.getHazelcastConfig().getGroupConfig().getName().equalsIgnoreCase(env) ||
					!Main.getHazelcastConfig().getGroupConfig().getPassword().equalsIgnoreCase(password)){
				// if not equals, exception
				LogAppl.getInstance().emit(NodeMessage.JEMC288W, host);
				throw new HttpException(NodeMessage.JEMC288W.toMessage().getFormattedMessage(host));
			}
			// gets JCL type, OPTIONAL
			String type = parms.get(SubmitParameters.TYPE.getName());
			// gets filename of JCL, used as JOB NAME when the JCL factory
			// is not able to parse the JCL
			String fileName = parms.get(SubmitParameters.JCL.getName());
			// gets the HTTP port of the HTTP server used by the client 
			// to wait the answer, OPTIONAL
			String callbackPort = parms.get(JOB_SUBMIT_CALLBACK_PORT_KEY);
			// gets if the client is waiting for the end of the job 
			boolean isWait = Parser.parseBoolean(parms.get(SubmitParameters.WAIT.getName()), false);
			// gets if the client wants to have back the output of the job
			String printOutput = parms.get(SubmitParameters.PRINT_OUTPUT.getName());
			
			// reads teh second row of the body, with the JCL
			String jcl = StringUtils.substringAfter(result, DELIMITER);
			
			// sets the entity to send back, submitting the job.
			// it returns the JOBID
			StringEntity resultEntity = new StringEntity(submit(fileName, jcl, type, user, isWait, host, callbackPort, printOutput), ContentType.create(RESPONSE_MIME_TYPE, CharSet.DEFAULT_CHARSET_NAME));
			// sets STATUS code and entity 
			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(resultEntity);
		} else {
			// if here, the request is not correct
			LogAppl.getInstance().emit(NodeMessage.JEMC284W, method, host);
			throw new MethodNotSupportedException(NodeMessage.JEMC284W.toMessage().getFormattedMessage(method, host));
		}
	}
	
	/**
	 * Loads all parms inserted in the body with all necessary info to submit the job.
	 * @param params list of pair values with all parms
	 * @return a map, key-value with the parameters
	 */
	private Map<String, String> loadParametersMap(List<NameValuePair> params){
		// scans the pairs and creates a map
		Map<String, String> map = new HashMap<String, String>();
		for (NameValuePair param : params) {
			map.put(param.getName(), param.getValue());
		}
		return map;
	}

	/**
	 * Submits the JCL as JOB inside the JEM.
	 * It uses all information passed by client.
	 * 
	 * @param content JCL content
	 * @param type JCL type (Optional)
	 * @param user user who submit the job
	 * @param isWait if the client is waiting for the result of job
	 * @throws HttpException if any errors occurs 
	 */
	private String submit(String fileName, String content, String type, String user, boolean isWait, String host, String callbackPort, String printOutput) throws HttpException {
		// creates a pre job using the JCL
		PreJob preJob = new PreJob();
		preJob.setJclContent(content);
		// sets JCL type
		if (type.trim().length()>0){
			preJob.setJclType(type);
		}
		// creates a job
		Job job = new Job();
		// sets user and group
		job.setUser(user);
		job.setOrgUnit(user);
		
		// uses file name as job name, if file name exists
		if (fileName != null){
			job.setName(fileName);
		}
		
		// creates a job ID asking to Hazelcast for a new long value
		IdGenerator generator = Main.getHazelcast().getIdGenerator(Queues.JOB_ID_GENERATOR);
		long id = generator.newId();
		// Pads the value with "0"
		String jobId = Factory.createJobId(job, id);
		job.setId(jobId);
		// loads all line arguments (the -D properties).
		// could be useful to factories, listeners and during job execution to
		// job itself
		
		job.setInputArguments(Arrays.asList(host, callbackPort, printOutput, JOB_SUBMIT_IP_ADDRESS_KEY));
		// set nowait
		job.setNowait(!isWait);
		// loads prejob with job
		preJob.setJob(job);		

		// puts the pre job in a queue for validating and moving to right QUEUE
		// (input if is correct, output if is wrong)
		IQueue<PreJob> jclCheckingQueue = Main.getHazelcast().getQueue(Queues.JCL_CHECKING_QUEUE);
		try {
			jclCheckingQueue.put(preJob);
		} catch (InterruptedException e) {
			throw new HttpException(SubmitMessage.JEMW003E.toMessage().getFormattedMessage(), e);
		}
		// returns job id
		return jobId;
	}
}
