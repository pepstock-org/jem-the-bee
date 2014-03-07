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
package org.pepstock.jem.notify.engine;

import java.util.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.EmailEndJobListener;
import org.pepstock.jem.notify.EmailFormat;
import org.pepstock.jem.notify.JemEmail;
import org.pepstock.jem.notify.NotifyMessage;
import org.pepstock.jem.notify.NotifyObject;
import org.pepstock.jem.notify.exception.EmailConfigurationException;
import org.pepstock.jem.notify.exception.NotifyException;
import org.pepstock.jem.notify.exception.SendMailException;

/**
 * <tt>EmailNotifier</tt> implements {@link NotifierInterface} and it has the
 * methods to send emails of type html or only text too. <br>
 * It may be extended to other classes that need to send emails, for example
 * {@link EmailEndJobListener}.
 * 
 * @author Alessandro Zambrini
 * @version 1.0
 * 
 */
public class EmailNotifier implements NotifierInterface {

	/**
	 * Default value in the case the there is no <code>SMTP</code> port.
	 */
	protected static final int NO_SMTP_PORT = -1;

	/**
	 * Default value for the {@link #isSSL} property if it is not configured:
	 * <code>false</code>.
	 */
	protected static final boolean DEFAULT_IS_SSL = false;

	/**
	 * Default value for the {@link #isTLS} property if it is not configured:
	 * <code>false</code>.
	 */
	protected static final boolean DEFAULT_IS_TLS = false;

	/**
	 * It is the email server for the email.
	 */
	private String emailServer = null;

	/**
	 * Field that contains the value of the <code>SMTP</code> port if necessary.
	 */
	private int smtpPort = NO_SMTP_PORT;

	/**
	 * Address to which are sent emails that cannot be delivered to a specified
	 * recipient.
	 */
	private String bounceAddress = null;

	/**
	 * Field that indicates whether is a SSL protocol for the email. <br>
	 * The default value is {@link #DEFAULT_IS_SSL}
	 */
	private boolean isSSL = DEFAULT_IS_SSL;

	/**
	 * Field that indicates whether is a TLS protocol for the email. <br>
	 * The default value is {@link #DEFAULT_IS_TLS}
	 */
	private boolean isTLS = DEFAULT_IS_TLS;

	/**
	 * It is the the userid useful for <code>SMTP</code> email server
	 * authentication.
	 */
	private String authenticationUserId = null;

	/**
	 * It is the the password useful for <code>SMTP</code> email server
	 * authentication.
	 */
	private String authenticationPassword = null;

	/**
	 * This method initialize <code>EmailNotifier</code> with the email server,
	 * the bounce address, the property that indicates whether is a
	 * <code>SSL</code> protocol and a <code>TLS</code> protocol and the value
	 * of the <code>SMTP</code> port if necessary.
	 * 
	 * @param emailServer Server for the email, mandatory cannot be
	 *            <code>null</code>.
	 * @param bounceAddress the bounce address to which are sent emails that
	 *            cannot be delivered to a specified recipient. Optional.
	 * @param isSSL the <code>boolean</code> that indicates whether is a
	 *            <code>SSL</code> protocol. Optional.
	 * @param isTLS the <code>boolean</code> that indicates whether is a
	 *            <code>TLS</code> protocol. Optional.
	 * @param smtpPort the <code>SMTP</code> port. Optional.
	 * @param authenticationUserId the <code>userid</code> useful for
	 *            <code>SMTP</code> server authentication.
	 * @param authenticationPassword the <code>password</code> useful for
	 *            <code>SMTP</code> server authentication.
	 * @throws EmailConfigurationException if the parameter
	 *             <code>emailServer</code> is <code>null</code>.
	 */
	public void init(String emailServer, String bounceAddress, boolean isSSL, boolean isTLS, int smtpPort, String authenticationUserId, String authenticationPassword) throws EmailConfigurationException {
		if (null == emailServer) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "Email Server");
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("Email Server"));
		}
		if (isTLS && (authenticationPassword == null || authenticationUserId == null)) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "SMTP server authentication userid or password with TLS protocol");
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("SMTP server authentication userid or password with TLS protocol"));
		}
		if (isSSL && (authenticationPassword == null || authenticationUserId == null)) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "SMTP server authentication userid or password with SSL protocol");
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("SMTP server authentication userid or password with SSL protocol"));
		}
		if (null != authenticationUserId && null == authenticationPassword) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "SMTP server authentication password with authentication userid");
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("SMTP server authentication password with authentication userid"));
		}
		if (null == authenticationUserId && null != authenticationPassword) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "SMTP server authentication userid with authentication password");
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("SMTP server authentication userid with authentication password"));
		}
		this.emailServer = emailServer;
		if (null != bounceAddress) {
			this.bounceAddress = bounceAddress;
		}
		this.isSSL = isSSL;
		this.isTLS = isTLS;
		this.smtpPort = smtpPort;
		if (null != authenticationUserId) {
			this.authenticationUserId = authenticationUserId;
		}
		if (null != authenticationPassword) {
			this.authenticationPassword = authenticationPassword;
		}
	}

	/**
	 * This method initialize <code>EmailNotifier</code> with the email server,
	 * the bounce address, the property that indicates whether is a
	 * <code>SSL</code> protocol and a <code>TLS</code> protocol and the value
	 * of the <code>SMTP</code> port if necessary.
	 * 
	 * @param emailServer Server for the email, mandatory cannot be
	 *            <code>null</code>.
	 * @param bounceAddress the bounce address to which are sent emails that
	 *            cannot be delivered to a specified recipient. Optional.
	 * @param isSSL the <code>boolean</code> that indicates whether is a
	 *            <code>SSL</code> protocol. Optional.
	 * @param isTLS the <code>boolean</code> that indicates whether is a
	 *            <code>TLS</code> protocol. Optional.
	 * @param smtpPort the <code>SMTP</code> port. Optional.
	 * @throws EmailConfigurationException if the parameter
	 *             <code>emailServer</code> is <code>null</code>.
	 */
	public void init(String emailServer, String bounceAddress, boolean isSSL, boolean isTLS, int smtpPort) throws EmailConfigurationException {
		this.init(emailServer, bounceAddress, isSSL, isTLS, smtpPort, null, null);
	}

	/**
	 * This method initialize <code>EmailNotifier</code> with the email server
	 * and the bounce address.
	 * 
	 * @param emailServer Server for the email, mandatory cannot be
	 *            <code>null</code>.
	 * @param bounceAddress the bounce address to which are sent emails that
	 *            cannot be delivered to a specified recipient. Optional.
	 * @throws EmailConfigurationException if the parameter
	 *             <code>emailServer</code> is <code>null</code>.
	 */
	public void init(String emailServer, String bounceAddress) throws EmailConfigurationException {
		this.init(emailServer, bounceAddress, DEFAULT_IS_SSL, DEFAULT_IS_TLS, NO_SMTP_PORT);
	}

	/**
	 * This method initialize <code>EmailNotifier</code> with the email server.
	 * 
	 * @param emailServer Server for the email, mandatory cannot be
	 *            <code>null</code>.
	 * @throws EmailConfigurationException if the parameter
	 *             <code>emailServer</code> is null.
	 */
	public void init(String emailServer) throws EmailConfigurationException {
		this.init(emailServer, null);
	}

	/**
	 * It returns the {@link #bounceAddress}: the address to which are sent
	 * emails that cannot be delivered to a specified recipient.
	 * 
	 * @return the <code>bounceAddress</code>
	 */
	public String getBounceAddress() {
		return bounceAddress;
	}

	/**
	 * It sets the {@link #bounceAddress}: the address to which are sent emails
	 * that cannot be delivered to a specified recipient.
	 * 
	 * @param bounceAddress the <code>bounceAddress</code> to set
	 */
	public void setBounceAddress(String bounceAddress) {
		this.bounceAddress = bounceAddress;
	}

	/**
	 * It sets the {@link #isSSL}: the field that indicates whether is a
	 * <code>SSL</code> protocol.
	 * 
	 * @param isSSL the <code>boolean</code> to set that indicates whether is a
	 *            <code>SSL</code> protocol.
	 */
	public void setIsSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	/**
	 * It sets the {@link #isTLS}: the field that indicates whether is a
	 * <code>TLS</code> protocol.
	 * 
	 * @param isTLS the <code>boolean</code> to set that indicates whether is a
	 *            <code>TLS</code> protocol.
	 */
	public void setIsTLS(boolean isTLS) {
		this.isTLS = isTLS;
	}

	/**
	 * It sets the {@link #smtpPort} the <code>SMTP</code> port.
	 * 
	 * @param smtpPort the <code>SMTP</code> port to set.
	 */
	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	/**
	 * It returns the {@link #emailServer}.
	 * 
	 * @return the Email Server
	 */
	public String getEmailServer() {
		return emailServer;
	}

	/**
	 * It sets the {@link #emailServer}.
	 * 
	 * @param emailServer the Email Server to set
	 */
	public void setEmailServer(String emailServer) {
		this.emailServer = emailServer;
	}

	/**
	 * This method make the notification. It overrides
	 * {@link NotifierInterface#doNotify(NotifyObject)} In this case it sends an
	 * email. It calls {@link #doMailNotify(JemEmail)}
	 * 
	 * @param notifyObject the <code>NotifyObject</code>, in this case an
	 *            {@link JemEmail}
	 * @throws NotifyException if an error occurs.
	 * @see NotifyObject
	 * @override
	 */
	public void doNotify(NotifyObject notifyObject) throws NotifyException {
		this.doMailNotify((JemEmail) notifyObject);
	}

	/**
	 * This method makes the Email notification. <br>
	 * It checks if the email to send is not <code>null</code> and if the email
	 * has destination addresses. <br>
	 * Depending on the email format creates or a {@link HtmlEmail} (for
	 * {@link EmailFormat#TEXT_HTML}) or a {@link SimpleEmail} (for
	 * {@link EmailFormat#TEXT_PLAIN}). <br>
	 * It calls the method {@link #sendEmail(JemEmail, Email)}.
	 * 
	 * @param email the <code>JemEmail</code> to be sent.
	 * @see JemEmail
	 * @throws SendMailException if an error occurs.
	 */
	private void doMailNotify(JemEmail email) throws SendMailException {
		if (null == email) {
			// log email null
			LogAppl.getInstance().emit(NotifyMessage.JEMN013E, "Email to send");
			throw new SendMailException(NotifyMessage.JEMN013E.toMessage().getFormattedMessage("Email to send"));
		}
		if (!email.hasToEmailAddresses()) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN003E);
			throw new SendMailException(NotifyMessage.JEMN003E.toMessage().getMessage());
		}
		if (!email.hasFromUserEmailAddress()) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, "From User Email Address");
			throw new SendMailException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage("From User Email Address"));
		}
		Email sendingEmail = null;
		if (email.getFormat() == EmailFormat.TEXT_HTML) {
			sendingEmail = new HtmlEmail();
		} else {
			sendingEmail = new SimpleEmail();
		}
		sendEmail(email, sendingEmail);
	}

	/**
	 * This method sends an <code>Email</code>. <br>
	 * It sets in the parameter <code>Email</code> the properties of the
	 * parameter <code>JemEmail</code>: From User Email Address, From User Name,
	 * subject, text, email destination addresses. <br>
	 * It sets in the parameter <code>Email</code> the Email Server property and
	 * the optional <code>SMTP</code> port, the properties that indicates if it
	 * must use <code>SSL</code> and <code>TLS</code> protocol, the useirid and
	 * password for <code>SMTP</code> server authentication if needed, the
	 * optional bounce address, the subject, the text, and the recipients of the
	 * email.
	 * 
	 * @param email the <code>JemEmail</code> with the properties of the email
	 *            to be sent.
	 * @param sendingEmail the real <code>Email</code> that will be sent.
	 * @see JemEmail
	 * @see Email
	 * @throws SendMailException if an error occurs.
	 */
	private void sendEmail(JemEmail email, Email sendingEmail) throws SendMailException {
		try {
			sendingEmail.setHostName(this.emailServer);
			if (this.smtpPort != NO_SMTP_PORT) {
				sendingEmail.setSmtpPort(this.smtpPort);
			}
			sendingEmail.setFrom(email.getFromUserEmailAddress(), email.getFromUserName());
			if (email.hasSubject()) {
				sendingEmail.setSubject(email.getSubject());
			} else {
				// log no subject
				LogAppl.getInstance().emit(NotifyMessage.JEMN014W, "Subject");
			}
			if (email.hasText()) {
				sendingEmail.setMsg(email.getText());
			} else {
				// log no text message
				LogAppl.getInstance().emit(NotifyMessage.JEMN014W, "Text Message");
			}
			sendingEmail.setTo(email.getAllToEmailAddresses());
			if (null != this.bounceAddress) {
				sendingEmail.setBounceAddress(this.bounceAddress);
			}
			sendingEmail.setSentDate(new Date());
			sendingEmail.setSSL(this.isSSL);
			sendingEmail.setTLS(this.isTLS);
			if (null != this.authenticationUserId && null != this.authenticationPassword) {
				sendingEmail.setAuthenticator(new DefaultAuthenticator(this.authenticationUserId, this.authenticationPassword));
			}
			sendingEmail.send();
			LogAppl.getInstance().emit(NotifyMessage.JEMN015I, email);
		} catch (EmailException eEx) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN016E, eEx, email);
			throw new SendMailException(NotifyMessage.JEMN016E.toMessage().getFormattedMessage(email), eEx);
		}
	}

	/**
	 * This method returns the <code>String</code> representation of this
	 * <code>EmailNotifier</code> thats contains all the field values.
	 * 
	 * @return the <code>String</code> representation of this
	 *         <code>EmailNotifier</code> thats contains all the field values.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder("EmailNotifier: \n");
		buffer.append("\tEmail Server = " + this.emailServer + "\n");
		buffer.append("\tSMTP Port = " + this.smtpPort + "\n");
		buffer.append("\tIs SSL = " + this.isSSL + "\n");
		buffer.append("\tIs TLS = " + this.isTLS + "\n");
		buffer.append("\tBounce Address = " + this.bounceAddress + "\n");
		if (null != this.authenticationUserId && null != this.authenticationPassword) {
			buffer.append("\tSMTP Authorization needed = true.");
		} else {
			buffer.append("\tSMTP Authorization needed = false.");
		}
		return buffer.toString();
	}

}