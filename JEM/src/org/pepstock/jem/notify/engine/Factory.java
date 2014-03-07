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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.AddressException;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.notify.JemEmail;
import org.pepstock.jem.notify.JemInternetAddress;
import org.pepstock.jem.notify.NotifyMessage;
import org.pepstock.jem.notify.exception.FactoryException;
import org.pepstock.jem.util.VariableSubstituter;

/**
 * This is the <code>Factory</code> class useful for {@link JemEmail}.
 * In particular this class builds a {@link JemEmail} starting from: <br>
 * - an Email template<br>
 * - the list of email addresses where the mail must be sent <br>
 * - a Map of properties ({@link Properties})
 * that can be placed inside the subject and the text of the Email. <br>
 * This Factory builds all the pieces of a <code>JemEmail</code>. <br>
 * IMPORTANT: the {@link JemEmail} contains the addresses where the mail must
 * be sent inside a {@link List} of {@link JemInternetAddress} to have a 
 * strict correctness check on the email address.
 * For details see methods Javadoc.
 * 
 * @author Alessandro Zambrini
 * @version 1.0	
 *
 */
public class Factory {
	
	/**
	 * To avoid any instantiation
	 */
	private Factory() {
		
	}

	/**
	 * This method builds {@link JemEmail} starting from a <code>JemEmail</code> template, 
	 * the list of email addresses where the mail must be sent and a Map {@link Properties}
	 * of properties 
     * that may be placed inside the subject and the text of the Email.
	 * 
	 * @param emailTemplate the starting <code>JemEmail</code> Template from which to build a <code>JemEmail</code>.
	 * @param toEmailAddresses <code>Array</code> of <code>String</code> containing the email addresses where the mail must be sent.
	 * @param properties <code>Properties</code> containing properties that may be placed in 
	 * the subject and text of the email, replacing the variables. 
	 * For example: ${jem.job.name} will be replaced with the value of the <code>name</code> of the <code>job</code> inside the parameter 
	 * properties.
	 * 
	 * @return the {@link JemEmail} created.
	 * @throws FactoryException if an error occurs.
	 */
	public static JemEmail createEmail(JemEmail emailTemplate, String[] toEmailAddresses, Properties properties) throws FactoryException{
		JemEmail resultMail = new JemEmail();
		if(emailTemplate.hasFormat()){
			resultMail.setFormat(emailTemplate.getFormat());
		}
		resultMail.addToEmailAddresses(createToEmailAddresses(toEmailAddresses));
		if(emailTemplate.hasFromUserEmailAddress()){
			resultMail.setFromUserEmailAddress(emailTemplate.getFromUserEmailAddress());
		}
		if(emailTemplate.hasFromUserName()){
			resultMail.setFromUserName(emailTemplate.getFromUserName());
		}
		if(emailTemplate.hasSubject()){
			String subject = createEmailSubject(emailTemplate, properties);
			resultMail.setSubject(subject);
		}
		if(emailTemplate.hasText()){
			String text = createEmailText(emailTemplate, properties);
			resultMail.setText(text);
		}
		return resultMail;
	}
	
	/**
	 * This method creates a {@link List} of {@link JemInternetAddress} starting
	 * from a {@link Collection} of email addresses in <code>String</code> format. If an email address
	 * in the input is repeated, in the result <code>List</code> is not repeated.
	 * 
	 * @param toEmailAddresses the <code>Collection</code> of email addresses in <code>String</code> format.
	 * @return a <code>List</code> of {@link JemInternetAddress}
	 * @throws FactoryException if an error occurs.
	 */
	public static List<JemInternetAddress> createToEmailAddresses(Collection<String> toEmailAddresses) throws FactoryException{
		String[] addressesTo = toEmailAddresses.toArray(new String[toEmailAddresses.size()]);
		return createToEmailAddresses(addressesTo);
	}
	
	/**
	 * This method creates a {@link List} of {@link JemInternetAddress} starting
	 * from an <code>Array</code> of email addresses in <code>String</code> format. If an email address
	 * in the input is repeated, in the result <code>List</code> is not repeated.
	 * 
	 * @param toEmailAddresses the <code>Array</code> of email addresses in <code>String</code> format.
	 * @return a <code>List</code> of {@link JemInternetAddress}
	 * @throws FactoryException if an error occurs.
	 */
	public static List<JemInternetAddress> createToEmailAddresses(String[] toEmailAddresses) throws FactoryException {
		List<JemInternetAddress> toEAddresses = new ArrayList<JemInternetAddress>();
		if (null != toEmailAddresses) {
			for (int i = 0; i < toEmailAddresses.length; i++) {
				try {
					JemInternetAddress address = new JemInternetAddress(toEmailAddresses[i].trim());
					toEAddresses.add(address);
				} catch (AddressException aEx) {
					LogAppl.getInstance().emit(NotifyMessage.JEMN001E, aEx, toEmailAddresses[i].trim());
				}
			}
			if (toEAddresses.isEmpty()) {
				LogAppl.getInstance().emit(NotifyMessage.JEMN002E);
				throw new FactoryException(NotifyMessage.JEMN002E.toMessage().getMessage());
			}
			return toEAddresses;
		} else {
			LogAppl.getInstance().emit(NotifyMessage.JEMN003E);
			throw new FactoryException(NotifyMessage.JEMN003E.toMessage().getMessage());
		}
	}
	
	/**
	 * This method creates the <b>text</b> of the Email. It uses a {@link Properties}
	 * containing properties that may be placed in the text of the email, replacing the variables.  <br>
	 * For example: ${jem.job.name} will be replaced with the value of the <code>name</code> of the <code>job</code> inside the parameter 
	 * properties.
	 * 
	 * @param emailTemplate the starting <code>JemEmail</code> Template from which to take the template text and build the email text.
	 * @param properties it contains the properties that may be placed in the text of the email.
	 * @return the <b>text</b> of the email.
	 * @throws FactoryException if an error occurs.
	 */
	public static String createEmailText(JemEmail emailTemplate, Properties properties) throws FactoryException{
		String textTemplate = emailTemplate.getText();
		return VariableSubstituter.substitute(textTemplate, properties);
	}
	
	/**
	 * This method creates the <b>subject</b> of the Email. It uses a {@link Properties}
	 * containing properties that may be placed in the subject of the email, replacing the variables. <br>
	 * For example: ${jem.job.name} will be replaced with the value of the <code>name</code> of the <code>job</code> inside the parameter 
	 * properties.
	 * 
	 * @param emailTemplate the starting <code>JemEmail</code> Template from which to take the template subject and build the email subject.
	 * @param properties it contains the properties that may be placed in the subject of the email.
	 * @return the <b>subject</b> of the email.
	 * @throws FactoryException if an error occurs.
	 */
	public static String createEmailSubject(JemEmail emailTemplate, Properties properties) throws FactoryException{
		String subjectTemplate = emailTemplate.getSubject();
		return VariableSubstituter.substitute(subjectTemplate, properties);
	}
}