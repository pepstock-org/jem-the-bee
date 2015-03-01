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
package org.pepstock.jem.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.pepstock.jem.notify.engine.NotifierInterface;
import org.pepstock.jem.notify.exception.NotifyException;

/**
 * The <code>JemEmail</code> class is an email container. <br>
 * It contains the properties of an email to be sent, such as: Email subject,
 * email text, format (<code>text/plain</code> or <code>text/html</code>),
 * destination addresses. <br>
 * It extends {@link NotifyException}, because it has to be used inside the
 * method {@link NotifierInterface#doNotify(NotifyObject)}
 * 
 * @author Alessandro Zambrini
 * @version 1.0
 */
public class JemEmail implements NotifyObject {

	/**
	 * Default value for the field {@link #fromUserName}
	 */
	private static final String DEFAULT_FROM_USER_NAME = "JEM THE BEE";

	/**
	 * Field from user name of the email to send. The default value is:
	 * {@link #DEFAULT_FROM_USER_NAME}
	 */
	private String fromUserName = DEFAULT_FROM_USER_NAME;

	/**
	 * Field from user email address of the email to send. It has no default
	 * value.
	 */
	private String fromUserEmailAddress;

	/**
	 * Subject of the email.
	 */
	private String subject;

	/**
	 * Text of the email.
	 */
	private String text;

	/**
	 * Format of the email: <br>
	 * <code>TEXT_HTML_FORMAT</code> or <code>TEXT_PLAIN_FORMAT</code> (
	 * <code>text/plain</code> or <code>text/html</code>). <br>
	 * The default value is {@link EmailFormat#DEFAULT_FORMAT}
	 * 
	 * @see EmailFormat
	 */
	private EmailFormat format = EmailFormat.DEFAULT_FORMAT;

	/**
	 * Field <code>List</code> of the {@link JemInternetAddress}, addresses
	 * destination of the email. It uses <code>JemInternetAddress</code> to have
	 * strict check on the email syntax.
	 * 
	 * @see JemInternetAddress
	 */
	private List<InternetAddress> toEmailAddresses = null;

	/**
	 * Constructor that initializes the email addresses destination of the email
	 * with an empty {@link ArrayList}
	 */
	public JemEmail() {
		this.toEmailAddresses = new ArrayList<InternetAddress>();
	}

	/**
	 * Sets the field <code>fromUserName</code> of the email.
	 * 
	 * @param fromUserName the from user name of the email.
	 */
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	/**
	 * Returns if this <code>JemEmail</code> has a from user name.
	 * 
	 * @return true if this <code>JemEmail</code> has a from user name, false
	 *         otherwise.
	 */
	public boolean hasFromUserName() {
		return getFromUserName() != null;
	}

	/**
	 * Returns the from user name of this <code>JemEmail</code>.
	 * 
	 * @return the from user name of this <code>JemEmail</code>,
	 *         <code>null</code> if there is no from user name.
	 */
	public String getFromUserName() {
		return fromUserName;
	}

	/**
	 * Sets the field <code>fromUserEmailAddress</code> of the email.
	 * 
	 * @param fromUserEmailAddress the from user email address of the email.
	 */
	public void setFromUserEmailAddress(String fromUserEmailAddress) {
		this.fromUserEmailAddress = fromUserEmailAddress;
	}

	/**
	 * Returns if this <code>JemEmail</code> has a from user email address.
	 * 
	 * @return true if this <code>JemEmail</code> has a from user email address,
	 *         false otherwise.
	 */
	public boolean hasFromUserEmailAddress() {
		return getFromUserEmailAddress() != null;
	}

	/**
	 * Returns the from user email address of this <code>JemEmail</code>.
	 * 
	 * @return the from user email address of this <code>JemEmail</code>,
	 *         <code>null</code> if there is no from user email address.
	 */
	public String getFromUserEmailAddress() {
		return fromUserEmailAddress;
	}

	/**
	 * Sets the field <code>format</code> of the email.
	 * 
	 * @param format the format of the email.
	 * @see EmailFormat
	 */
	public void setFormat(EmailFormat format) {
		this.format = format;
	}

	/**
	 * Returns the format of this <code>JemEmail</code>.
	 * 
	 * @return the format of this <code>JemEmail</code>.
	 * @see EmailFormat
	 */
	public EmailFormat getFormat() {
		return format;
	}

	/**
	 * Returns if this <code>JemEmail</code> has a format. <br>
	 * This method is useful because the <b>Email Template</b> is
	 * <code>JemEmail</code>, and it could have no format, if in the
	 * <code>xml</code> template file it is not specified.
	 * 
	 * @return true if this <code>JemEmail</code> has a format, false otherwise.
	 */
	public boolean hasFormat() {
		return getFormat() != null;
	}

	/**
	 * Sets the field <code>subject</code> of the email.
	 * 
	 * @param subject the subject of the email.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns if this <code>JemEmail</code> has a subject.
	 * 
	 * @return true if this <code>JemEmail</code> has subject, false otherwise.
	 */
	public boolean hasSubject() {
		return getSubject() != null;
	}

	/**
	 * Returns the subject of this <code>JemEmail</code>.
	 * 
	 * @return the subject of this <code>JemEmail</code>.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the field <code>text</code> of the email.
	 * 
	 * @param text the text of the email.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Returns if this <code>JemEmail</code> has a text.
	 * 
	 * @return true if this <code>JemEmail</code> has text, false otherwise.
	 */
	public boolean hasText() {
		return getText() != null;
	}

	/**
	 * Returns the text of this <code>JemEmail</code>.
	 * 
	 * @return the text of this <code>JemEmail</code>.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the number of {@link JemInternetAddress} contained in this
	 * <code>JemEmail</code>.
	 * 
	 * @return the number of {@link JemInternetAddress} contained in this
	 *         <code>JemEmail</code>.
	 */
	public int getToEmailAddressesCount() {
		return this.toEmailAddresses.size();
	}

	/**
	 * Returns if this <code>JemEmail</code> contains {@link JemInternetAddress}
	 * .
	 * 
	 * @return true if this <code>JemEmail</code> contains at least one
	 *         {@link JemInternetAddress}, false otherwise.
	 */
	public boolean hasToEmailAddresses() {
		return !this.toEmailAddresses.isEmpty();
	}

	/**
	 * Returns if this <code>JemEmail</code> contains the parameter address
	 * <code>emailAddress</code>.
	 * 
	 * @param emailAddress the parameter that this method check if is contained
	 *            in this <code>JemEmail</code>.
	 * @return true if this <code>JemEmail</code> contains the parameter address
	 *         <code>emailAddress</code>, false otherwise.
	 */
	public boolean conatainsToEmailAddress(InternetAddress emailAddress) {
		if (null == emailAddress) {
			return false;
		}
		for (int i = 0; i < this.toEmailAddresses.size(); i++) {
			InternetAddress toEmailAddress = this.toEmailAddresses.get(i);
			if (emailAddress.toString().trim().equalsIgnoreCase(toEmailAddress.toString().trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method adds the parameter address <code>emailAddress</code> to this
	 * <code>JemEmail</code>.
	 * 
	 * @param emailAddress the {@link JemInternetAddress} that will be added to
	 *            this <code>JemEmail</code>.
	 */
	public void addToEmailAddress(InternetAddress emailAddress) {
		if (!this.conatainsToEmailAddress(emailAddress)) {
			this.toEmailAddresses.add(emailAddress);
		}
	}

	/**
	 * This method returns a <code>Collection</code> of the
	 * {@link JemInternetAddress} contained in this <code>JemEmail</code>.
	 * 
	 * @return a <code>Collection</code> of the {@link JemInternetAddress}
	 *         contained in this <code>JemEmail</code>.
	 * @see Collection
	 */
	public Collection<InternetAddress> getAllToEmailAddresses() {
		return this.toEmailAddresses;
	}

	/**
	 * This method adds the email addresses contained in the <code>List</code>
	 * parameter <code>toEmailAddresses</code> to this <code>JemEmail</code>.
	 * 
	 * @param toEmailAddresses the <code>List</code> of
	 *            {@link JemInternetAddress} that will be added to this
	 *            <code>JemEmail</code>.
	 * @see List
	 */
	public void addToEmailAddresses(List<InternetAddress> toEmailAddresses) {
		if (null != toEmailAddresses) {
			for (int i = 0; i < toEmailAddresses.size(); i++) {
				this.addToEmailAddress(toEmailAddresses.get(i));
			}
		}
	}

	/**
	 * This method adds the email addresses contained in the <code>Array</code>
	 * parameter <code>toEmailAddresses</code> to this <code>JemEmail</code>.
	 * 
	 * @param toEmailAddresses the <code>Array</code> of
	 *            {@link JemInternetAddress} that will be added to this
	 *            <code>JemEmail</code>.
	 */
	public void addToEmailAddresses(InternetAddress[] toEmailAddresses) {
		if (null != toEmailAddresses) {
			for (int i = 0; i < toEmailAddresses.length; i++) {
				this.addToEmailAddress(toEmailAddresses[i]);
			}
		}
	}

	/**
	 * This method returns the <code>String</code> representation of this
	 * <code>JemEmail</code> thats contains all the field values.
	 * 
	 * @return the <code>String</code> representation of this
	 *         <code>JemEmail</code> thats contains all the field values.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder("JEmail: \n");
		buffer.append("\tFrom User Name = " + this.fromUserName + "\n");
		buffer.append("\tFrom User Email Address = " + this.fromUserEmailAddress + "\n");
		buffer.append("\tFormat = " + this.format + "\n");
		buffer.append("\tSubject = " + this.subject + "\n");
		buffer.append("\tText = " + this.text + "\n");

		if (this.hasToEmailAddresses()) {
			buffer.append("\tAddresses to = ");
			for (int i = 0; i < this.toEmailAddresses.size(); i++) {
				if (i == 0) {
					buffer.append(this.toEmailAddresses.get(i));
				} else {
					buffer.append(", " + this.toEmailAddresses.get(i));
				}
			}
		}
		return buffer.toString();
	}
}