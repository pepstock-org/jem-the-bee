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
package org.pepstock.jem.notify;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages about JEM Notification. <br>
 * It is a list of <code>NotifyMessage</code>. <br> 
 * Each <code>NotifyMessage</code> in the list corresponds to a <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public enum NotifyMessage implements MessageInterface{	
	
	/**
	 * "Wrong email address \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "Email address defined in JCL for notification is not well-formed")
	JEMN001E(1, "Wrong email address \"{0}\".", MessageLevel.ERROR),
	
	/**
	 * "No email address found: all mail addresses are wrong.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if in the JCL all the email addresses (the recipients of the notification email of the end of the job) are not well formed. <br>An exception is thrown.<br> Check all the email addresses in the JCL and correct them.")
	JEMN002E(2, "No email address found: all mail addresses are wrong.", MessageLevel.ERROR),

	/**
	 * "No email address found as destination of the email.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if the email to send has no email address as destination recipients. <br>It is used inside the email Factory and inside the EmailNotifier. <br>An exception is thrown.<br>Now this log will not occur, because for now only the EmailEndJobListener extends EmailNotifier.<br>EmailEndJobListener immediately checks if in the JCL there is at least one email addresses, if there is none it only<br>logs there is no email address and does nothing.")
	JEMN003E(3, "No email address found as destination of the email.", MessageLevel.ERROR),

	/**
	 * "Error creating email text with text template: [{0}].", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is a problem substituting the values of the fields of the Job and of the NodeInfo inside the text of the notification email of the end of the job.<br>An exception is thrown.<br>Check the email template file. Check if the email text template is well formed. The variables inside the template are so done: ${variable name}, for example ${jem.job.startedTime}.")
	JEMN004E(4, "Error creating email text with text template: [{0}].", MessageLevel.ERROR),

	/**
	 * "Error creating email subject with subject template: [{0}].",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "<br>It occurs if there is a problem substituting the values of the fields of the Job and of the NodeInfo inside the subject of the notification email of the end of the job.<br>An exception is thrown.<br>Check the email template file. Check if the email subject template is well formed. The variables inside the template are so done: ${variable name}, for example ${jem.job.startedTime}.")
	JEMN005E(5, "Error creating email subject with subject template: [{0}].", MessageLevel.ERROR),

	/**
	 * "Wrong email template file directory: \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if the email template file for the Job end notification is configured not properly inside EmailEndJobListener configuration.<br>In particular it means that the directory of the template file (specified in the log: {0}) does not exist or is the root directory, for example '/emailTempateFile.xml' or 'emailTempateFile.xml' or '/dir/emailTempateFile.xml' if 'dir' doesn't exist.<br>An exception is thrown.<br>Check the EmailEndJobListener configuration, in particular the property: 'jem.emailTemplateFile'. Correct it.")
	JEMN006E(6, "Wrong email template file directory: \"{0}\".", MessageLevel.ERROR),

	/**
	 * "Configuration email template file \"{0}\" does not exist. Create new email template file \"{0}\" inside directory: \"{1}\"."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs if the email template file for the Job end notification (specified in the log: {0}) inside EmailEndJobListener configuration does not exist.<br>No exception is thrown, but this warning is logged.<br>Check the EmailEndJobListener configuration, in particular the property: 'jem.emailTemplateFile'. Write an existing email template file, and automatically it will be loaded, so no node restart is needed.")
	JEMN007W(7, "Configuration email template file \"{0}\" does not exist. Create new email template file \"{0}\" inside directory: \"{1}\".", MessageLevel.WARNING),

	/**
	 * "Error loading EmailTemplate.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is a problem loading the email template file for the Job end notification.<br>No exception is thrown, but this error is logged.<br>Check if the email template file is an XML well formed and correct it. Automatically it will be reloaded, so no node restart is needed.")
	JEMN008E(8, "Error loading EmailTemplate.", MessageLevel.ERROR),

	/**
	 * "Error starting automatic control for email template file changes. The template file is: \"{0}\"."
	 * , MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is an error trying to start the automatic control for email template file changes.<br>It may occurs if the automatic control is already started, or if there is a problem initializing the directory Observer that checks directory changes. It is a system error.")
	JEMN009E(9, "Error starting automatic control for email template file changes. The template file is: \"{0}\".", MessageLevel.ERROR),

	/**
	 * "Error reading email template file \"{0}\".", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs if there is a problem reading the email template file for the Job end notification (specified in the log: {0}).<br>An exception is thrown.<br>Check if the email template file is an XML well formed and correct it. Automatically it will be reloaded, so no node restart is needed.")
	JEMN010E(10, "Error reading email template file \"{0}\".", MessageLevel.ERROR),

	/**
	 * "The email template xml file \"{0}\" has been deleated, using previous EmailTemplate. Create new email template file \"{0}\" inside directory: {1}."
	 * , MessageLevel.WARNING
	 */
	@Description(explanation = "<br>It occurs if someone delete the email template file for the Job end notification (specified in the log: {0}).<br>No exception is thrown, but this warning is logged.<br>Create a new email template xml file in the correct directory, and automatically it will be reloaded, so no node restart is needed.")
	JEMN011W(11, "The email template xml file \"{0}\" has been deleated, using previous EmailTemplate. Create new email template file \"{0}\" inside directory: {1}.", MessageLevel.WARNING),

	/**
	 * "{0} not configured. It must have a value.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the following things (specified in the log: {0}) are not configured:<br>- the email template file for the Job end notification: property 'jem.emailTemplateFile' inside EmailEndJobListener configuration<br>- the from user address of the email of the Job end notification: the tag 'from-user-email-address' inside the email template file<br>- the Email Server: property 'jem.emailServer' inside EmailEndJobListener configuration<br>In case of 'email template file' missing no exception is thrown, but this error is logged. In the other cases<br>an exception is thrown.<br>To correct the problem, respectively:<br>- add or fill the property 'jem.emailTemplateFile' inside EmailEndJobListener configuration and restart the node<br>- add or fill the the tag 'from-user-email-address' inside the email template file no node restart is needed<br>- add or fill the property 'jem.emailServer' inside EmailEndJobListener configuration and restart the node")
	JEMN012E(12, "{0} not configured. It must have a value.", MessageLevel.ERROR),

	/**
	 * "{0} is null.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the following thing (specified in the log: {0}) is null:<br>- the email to send: some problem occurred before the process of email sending, it depends on other errors.<br>An exception is thrown.<br>Check the log and the configuration. In the log the cause is written, so it is possible to correct the error.")
	JEMN013E(13, "{0} is null.", MessageLevel.ERROR),

	/**
	 * "The email has no {0}.", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs when the following things (specified in the log: {0}) are not configured:<br>- the subject of the email of the Job end notification: the tag 'subject' inside the email template file<br>- the text of the email of the Job end notification: the tag 'text' inside the email template file<br>No exception is thrown, but this warning is logged.<br>To correct the problem, respectively:<br>- add or fill the the tag 'subject' inside the email template file<br>- add or fill the the tag 'text' inside the email template file")
	JEMN014W(14, "The email has no {0}.", MessageLevel.WARNING),

	/**
	 * "Email {0} \n sent properly.", MessageLevel.INFO
	 */
	@Description(explanation = "This log informs you that the email of the Job end notification was successfully sent.<br>It writes in the log all the properties of the email sent.<br>No exception is thrown, but this information is logged.")
	JEMN015I(15, "Email {0} \nsent properly.", MessageLevel.INFO),

	/**
	 * "Problems sending email: {0} \n.", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is a problem sending the email of the Job end notification, with the cause of the error specified in the log: {0}.<br>For example it may occurs if the email server is down or wrong, or if SMTP port is not configured properly or if the configuration is not correct:<br>email server need SSL or TLS protocol, but they are not configured or there is no user configured(properties 'jem.isSSLProtocol', 'jem.isTLSProtocol', 'jem.authorization.userid' and 'jem.authorization.password' inside EmailEndJobListener configuration)<br>An exception is thrown.<br>The cause of the error specified in the log may suggest how to solve the problem.<br>If it is a configuration problem check the EmailEndJobListener configuration properties:<br>- jem.emailServer<br>- jem.smtpPort<br>- jem.isSSLProtocol<br>- jem.isTLSProtocol<br>- jem.authorization.userid<br>- jem.authorization.password")
	JEMN016E(16, "Problems sending email: {0} \n.", MessageLevel.ERROR),

	/**
	 * "{0}", MessageLevel.INFO
	 */
	@Description(explanation = "This logs is used to write all the properties of the EmailNotifier:<br>- Email Server<br>- SMTP Port<br>- Is SSL<br>- Is TLS<br>- Bounce Address<br>No exception is thrown, but this information is logged.<br>It is general because it may be used for different purposes.")
	JEMN017I(17, "{0}", MessageLevel.INFO),
	 
	/**
	 * "The Jcl of the Job has no notification email addresses", MessageLevel.WARNING
	 */
	@Description(explanation = "It occurs if in the JCL there is no email address as destination of the Job end notification.<br>No exception is thrown, but this warning is logged.<br>Add at least one email address in the JCL if job end notification is needed.")
	JEMN018W(18, "The Jcl of the Job has no notification email addresses", MessageLevel.WARNING);
	
	/**
	 * The {@link Message} created in the constructor corresponding to an instance of <code>NotifyMessage</code>. 
	 * @see Message
	 */
	private Message message;
	
	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code> constructor
	 * and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string do display. Could contain variables, resolved at runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private NotifyMessage(int code, String messageContent, MessageLevel level){
		this.message = new Message(code, MessageCode.NOTIFY.getCode(), messageContent, level);
	}
	
	/**
	 * It returns the {@link Message} corresponding to an <code>NotifyMessage</code> instance.
	 * @return the {@link Message} corresponding to an <code>NotifyMessage</code> instance.
	 */
	@Override
	public Message toMessage(){
		return this.message;
	}

}