/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
package org.pepstock.jem.node.events;

import java.util.Properties;

import org.pepstock.jem.Job;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.notify.JemEmail;
import org.pepstock.jem.notify.NotifyMessage;
import org.pepstock.jem.notify.engine.EmailNotifier;
import org.pepstock.jem.notify.engine.EmailTemplateReader;
import org.pepstock.jem.notify.engine.Factory;
import org.pepstock.jem.notify.exception.NotifyException;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.VariablesFactory;

/**
 * This <code>Listener</code> notifies the end of the execution of a
 * <code>Job</code> sending an email. <br>
 * The addresses to which the emails are sent are inside the <code>Jcl</code> of
 * the <code>Job</code> executed. <br>
 * It extends {@link EmailNotifier} and implements {@link JobLifecycleListener}
 * 
 * @see JobLifecycleListener
 * @see EmailNotifier
 * @author Alessandro Zambrini
 * @version 1.0
 */
public class EmailEndJobListener extends EmailNotifier implements JobLifecycleListener {

	/**
	 * It is the separator for the email addresses inside the Jcl
	 */
	private static final String EMAIL_ADDRESSES_SEPARATOR = ";";

	/**
	 * It is the <code>name</code> of the <code>Email Server</code> property
	 * inside the <code>EmailEndJobListener</code> configuration, in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the mandatory <code>Email Server</code> property value.
	 */
	private static final String EMAIL_SERVER_PROPERTY = "jem.emailServer";

	/**
	 * It is the <code>name</code> of the <code>Bounce Address</code> property
	 * inside the <code>EmailEndJobListener</code> configuration, in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>Bounce Address</code> property value.
	 */
	private static final String BOUNCE_ADDRESS_PROPERTY = "jem.bounceAddress";

	/**
	 * It is the <code>name</code> of the <code>Email template file</code>
	 * property inside the <code>EmailEndJobListener</code> configuration, in
	 * the <code>jem-node.xml</code>. <br>
	 * Useful to load the mandatory <code>Email template file</code> property
	 * value.
	 */
	private static final String EMAIL_TEMPLATE_FILE_PROPERTY = "jem.emailTemplateFile";

	/**
	 * It is the <code>name</code> of the <code>SMTP port</code> property inside
	 * the <code>EmailEndJobListener</code> configuration, in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>SMTP port</code> property value, if
	 * necessary. The default value is No port.
	 */
	private static final String SMTP_PORT_PROPERTY = "jem.smtpPort";

	/**
	 * It is the <code>name</code> of the <code>IS SSL protocol</code> property
	 * inside the <code>EmailEndJobListener</code> configuration, in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>IS SSL protocol</code> property value,
	 * if necessary. The default value is <code>false</code>, that is no
	 * <code>SSL</code> protocol is used by the <code>EmailEndJobListener</code>
	 * .
	 */
	private static final String IS_SSL_PROTOCOL_PROPERTY = "jem.isSSLProtocol";

	/**
	 * It is the <code>name</code> of the <code>IS TLS protocol</code> property
	 * inside the <code>EmailEndJobListener</code> configuration in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>IS TLS protocol</code> property value,
	 * if necessary. The default value is <code>false</code>, that is no
	 * <code>TLS</code> protocol is used by the <code>EmailEndJobListener</code>
	 * .
	 */
	private static final String IS_TLS_PROTOCOL_PROPERTY = "jem.isTLSProtocol";

	/**
	 * It is the <code>name</code> of the
	 * <code>SMTP authentication userid</code> property inside the
	 * <code>EmailEndJobListener</code> configuration in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>SMTP authentication userid</code>
	 * property value, in case of TLS or SSL protocol.
	 */
	private static final String SMTP_AUTHENTICATION_USERID_PROPERTY = "jem.smtp.authentication.userid";

	/**
	 * It is the <code>name</code> of the
	 * <code>SMTP authentication password</code> property inside the
	 * <code>EmailEndJobListener</code> configuration in the
	 * <code>jem-node.xml</code>. <br>
	 * Useful to load the optional <code>SMTP authentication password</code>
	 * property value, in case of TLS or SSL protocol.
	 */
	private static final String SMTP_AUTHENTICATION_PASSWORD_PROPERTY = "jem.smtp.authentication.password";

	/**
	 * Reader of the template email file. <br>
	 * It reads the file and builds a <code>JemEmail</code> object that contains
	 * the email template.
	 * 
	 * @see JemEmail
	 * @see EmailTemplateReader
	 */
	private EmailTemplateReader emailTemplateReader = null;

	/**
	 * In this method the Listener initializes all necessary fields to send an
	 * email: <br>
	 * - the email server <br>
	 * - an optional bounce address used to as target email address for the
	 * emails that cannot be sent for any problem. <br>
	 * - the optional <code>SMTP</code> port to use <br>
	 * - the optional property that indicates whether is a <code>SSL</code>
	 * protocol. <br>
	 * - the optional property that indicates whether is a <code>TLS</code>
	 * protocol. <br>
	 * - the {@link #emailTemplateReader} useful to read the email template file
	 * that describes the email to send (subject, text, from use name, from
	 * email address, email format), loading a template {@link JemEmail}. <br>
	 * 
	 * @param properties properties
	 * @see EmailTemplateReader
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 * @Override
	 */
	public void init(Properties properties) {
		try {
			// load Email server (mandatory)
			String emailServer = properties.getProperty(EMAIL_SERVER_PROPERTY);
			// load Bounce address (optional)
			String bounceAddress = properties.getProperty(BOUNCE_ADDRESS_PROPERTY);
			// load email template file (mandatory)
			String emailTemplateFileStr = properties.getProperty(EMAIL_TEMPLATE_FILE_PROPERTY);
			String authenticationUserId = properties.getProperty(SMTP_AUTHENTICATION_USERID_PROPERTY);
			String authenticationPassword = properties.getProperty(SMTP_AUTHENTICATION_PASSWORD_PROPERTY);
			boolean isSSL = DEFAULT_IS_SSL;
			boolean isTLS = DEFAULT_IS_TLS;
			int smtpPort = NO_SMTP_PORT;
			// loads is SSL property
			if (null != properties.getProperty(IS_SSL_PROTOCOL_PROPERTY)) {
				String isSslStr = properties.getProperty(IS_SSL_PROTOCOL_PROPERTY);
				isSSL = Parser.parseBoolean(isSslStr, DEFAULT_IS_SSL);
			}
			// loads is TLS property
			if (null != properties.getProperty(IS_TLS_PROTOCOL_PROPERTY)) {
				String isTlsStr = properties.getProperty(IS_TLS_PROTOCOL_PROPERTY);
				isTLS = Parser.parseBoolean(isTlsStr, DEFAULT_IS_TLS);
			}
			// loads is SMTP port property
			if (null != properties.getProperty(SMTP_PORT_PROPERTY)) {
				String smtpPortStr = properties.getProperty(SMTP_PORT_PROPERTY);
				smtpPort = Parser.parseInt(smtpPortStr, NO_SMTP_PORT);
			}
			if (null == emailTemplateFileStr) {
				LogAppl.getInstance().emit(NotifyMessage.JEMN012E, EMAIL_TEMPLATE_FILE_PROPERTY);
			} else {
				super.init(emailServer, bounceAddress, isSSL, isTLS, smtpPort, authenticationUserId, authenticationPassword);
				LogAppl.getInstance().emit(NotifyMessage.JEMN017I, this.toString());
				// create the template reader
				this.emailTemplateReader = new EmailTemplateReader(emailTemplateFileStr);
				// initialize the email template reader that read the email
				// template file and builds a JemEmail
				this.emailTemplateReader.initialize();
			}
			LogAppl.getInstance().emit(NodeMessage.JEMC037I, this.getClass().getName());
		} catch (NotifyException nEx) {
			LogAppl.getInstance().emit(NodeMessage.JEMC035E, nEx, this.getClass().getName());
		}
	}

	/**
	 * Sends emails to notify the end of the <code>Job</code> execution. <br>
	 * - Checks if the <code>Job</code> has email addresses to notify the end of
	 * the <code>Job</code>. <br>
	 * - Gets the {@link NodeInfoBean}. <br>
	 * - Starting from the <code>Job</code> and the <code>NodeInfoBean</code>
	 * builds a {@link MapVariableDereferencer}. <br>
	 * - Takes from the <code>Job</code> the addresses to which to send the
	 * emails. <br>
	 * - Builds the email to send using the email template, the addresses and
	 * the MapVariableDereferencer. <br>
	 * - Sends the email. <br>
	 * 
	 * @param job <code>Job</code> instance
	 * @see MapVariableDereferencer
	 * @Override
	 */
	public void ended(Job job) {
		// Checks if the job has email addresses to notify the end of the job
		if (job.getJcl().hasEmailNotificationAddresses()) {
			try {
				// Gets the NodeInfoBean
				NodeInfoBean nodeInfo = Main.getNode().getNodeInfoBean();
				// Starting from the job and the node info bean builds a
				// MapVariableDereferencer
				Properties variables = VariablesFactory.createVariables(job, nodeInfo);
				// Takes from the job the addresses to which to send the emails
				String emailAddressesStr = job.getJcl().getEmailNotificationAddresses();
				String[] emailAddresses = emailAddressesStr.split(EMAIL_ADDRESSES_SEPARATOR);
				// Builds the email to send
				JemEmail email = Factory.createEmail(this.emailTemplateReader.getEmailTemplate(), emailAddresses, variables);
				// Sends the email
				super.doNotify(email);
			} catch (Exception ex) {
				LogAppl.getInstance().emit(NodeMessage.JEMC074E, ex);
			}
		} else {
			LogAppl.getInstance().emit(NotifyMessage.JEMN018W);
		}
	}

	/**
	 * Empty.
	 * 
	 * @param job <code>Job</code> instance
	 * @Override
	 */
	public void queued(Job job) {
		// do nothing
	}

	/**
	 * Empty.
	 * 
	 * @param job <code>Job</code> instance
	 * @Override
	 */
	public void running(Job job) {
		// do nothing
	}
}