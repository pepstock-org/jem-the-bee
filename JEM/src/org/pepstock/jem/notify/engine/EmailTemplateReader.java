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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.notify.JemEmail;
import org.pepstock.jem.notify.NotifyMessage;
import org.pepstock.jem.notify.exception.EmailConfigurationException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * <code>EmailTemplateReader</code> receives (in the constructor) the path of
 * the <code>xml</code> file that describes the email to send, (an email
 * template, for example <code>config/emailTemplate.xml</code>). <br>
 * It reads the file, builds a {@link JemEmail} and saves it in the field
 * {@link #emailTemplate}. <br>
 * <code>XStream</code> library is used. <br>
 * <code>EmailTemplateReader</code> uses also the
 * <code>org.apache.commons.io.monitor</code> library to check automatically if
 * the file is changed: in this case <code>EmailTemplateReader</code> reloads it
 * and build a new {@link JemEmail}. <br>
 * The interval between each check is {@link #CHECK_INTERVAL} milliseconds. <br>
 * It extends {@link FileAlterationListenerAdaptor} to listen the file changes.
 * 
 * @see XStream
 * @see FileAlterationObserver
 * @see FileAlterationListener
 * @see FileAlterationListenerAdaptor
 * 
 * @author Alessandro Zambrini
 */
public class EmailTemplateReader extends FileAlterationListenerAdaptor {

	/**
	 * The interval in millisecond between each file modification automatic
	 * control. <br>
	 * <b>60</b> seconds.
	 */
	private static final long CHECK_INTERVAL = 60000;

	/**
	 * Root tag in the email template <code>xml</code> file.
	 * 
	 * @see XStream
	 */
	private static final String EMAIL_TEMPLATE_ROOT_TAG = "email-template";

	/**
	 * Mapping of the Root tag in the email template <code>xml</code> file.
	 * 
	 * @see JemEmail
	 * @see XStream
	 */
	private static final Class<JemEmail> EMAIL_TEMPLATE_ROOT_ALIAS = JemEmail.class;

	/**
	 * The <code>File</code> that maps the template <code>xml</code> file that
	 * describes the email to send.
	 * 
	 * @see File
	 */
	private File emailTemplateFile;

	/**
	 * This field contains the {@link JemEmail} produced reading the template
	 * <code>xml</code> file that describes the email to send.
	 */
	private JemEmail emailTemplate;

	/**
	 * <code>Xstream</code> field used to read <code>xml</code>.
	 * 
	 * @see XStream
	 */
	private XStream xstream;

	/**
	 * Field that indicates whether the automatic file modifications control is
	 * started: the value is <code>true</code> if the control is started
	 * correctly, <code>false</code> otherwise.
	 */
	private boolean automaticControlStarted = false;

	/**
	 * <code>Enumeration</code> containing the mapping alias for
	 * <code>xml</code> template file. <br>
	 * It is possible to write the email template file using, for example: <br>
	 * <dd> <code>from-user-name</code> or <code>fromUserName</code> <br> <dd>
	 * <code>from-user-email-address</code> or <code>fromUserEmailAddress</code>
	 * <br>
	 * The <code>Class</code> used for alias mapping is the implicit
	 * {@link JemEmail} class.
	 */
	private static enum FieldAlias {

		/**
		 * Constant that defines the <code>xml</code> alias value tag
		 * (from-user-name) for the <code>JemEmail</code> field
		 * <code>fromUserName</code>.
		 */
		FROM_USER_NAME_ALIAS("from-user-name", "fromUserName"),

		/**
		 * Constant that defines the <code>xml</code> alias value tag
		 * (from-user-email-address) for the <code>JemEmail</code> field
		 * <code>fromUserEmailAddress</code>.
		 */
		FROM_USER_EMAIL_ADDRESS_ALIAS("from-user-email-address", "fromUserEmailAddress");

		/**
		 * Field that contains the alias <code>xml</code> tag.
		 */
		private String aliasXmlTag;

		/**
		 * Field that contains the <code>JemMail</code> field name corresponding
		 * to the alias <code>xml</code> tag.
		 */
		private String fieldName;

		/**
		 * Constructor. It build an <code>FieldAlias</code> using the alias
		 * <code>xml</code> tag and the corresponding field name inside class
		 * {@link JemEmail}.
		 * 
		 * @param aliasXmlTag the value of the alias tag in the <code>xml</code>
		 *            template.
		 * @param fieldName the corresponding name of the field inside the class
		 *            <code>JemMail</code>.
		 */
		private FieldAlias(String aliasXmlTag, String fieldName) {
			this.aliasXmlTag = aliasXmlTag;
			this.fieldName = fieldName;
		}

		/**
		 * Return the value of the alias <code>xml</code> tag for the
		 * <code>FieldAlias</code>.
		 * 
		 * @return alias <code>xml</code> tag value for the
		 *         <code>FieldAlias</code>.
		 */
		public String getAliasXmlTag() {
			return this.aliasXmlTag;
		}

		/**
		 * Return the value of the original name of the <code>JemMail</code>
		 * field corresponding to the <code>FieldAlias</code>.
		 * 
		 * @return the value of the original name of the <code>JemMail</code>
		 *         field for the <code>FieldAlias</code>.
		 */
		public String getFieldName() {
			return this.fieldName;
		}
	}

	/**
	 * Constructor. It receives the path of the <code>xml</code> file that
	 * describes the email to send (email template).
	 * 
	 * @see XStream
	 * @param emailTemplateFile the email template file path.
	 * @throws EmailConfigurationException if the email template file directory
	 *             does not exist, or is not a directory, or is the File System
	 *             root directory.
	 */
	public EmailTemplateReader(String emailTemplateFile) throws EmailConfigurationException {
		// Sets the email template xml File field
		this.emailTemplateFile = new File(emailTemplateFile);
		if (null == this.emailTemplateFile.getParentFile() || !this.emailTemplateFile.getParentFile().exists() || !this.emailTemplateFile.getParentFile().isDirectory() || this.emailTemplateFile.getParentFile().toString().equalsIgnoreCase(File.separator)) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN006E, this.emailTemplateFile.getParentFile());
			throw new EmailConfigurationException(NotifyMessage.JEMN006E.toMessage().getFormattedMessage(this.emailTemplateFile.getParentFile()));
		}
		if (!this.emailTemplateFile.exists()) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN007W, new Object[] { this.emailTemplateFile, this.emailTemplateFile.getParentFile() });
		}
	}

	/**
	 * This method initializes the <code>EmailTemplateReader</code> <br>
	 * It sets the alias, for the <code>xml</code> email template file root and
	 * for the the user name and user email address. <br>
	 * It initializes the field <code>xstream</code> using {@link XStream}. <br>
	 * Reads and loads the <code>xml</code> email template file <br>
	 * It initializes the components to check if someone modifies the email
	 * template <code>xml</code> file: <br>
	 * <dd>- Initializes the field <code>emailTemplateFileObserver</code> with a
	 * new {@link FileAlterationObserver} using the directory in which is placed
	 * the email template file (the field <code>emailTemplateFile</code>). <dd>-
	 * Add <code>this EmailTemplateReader</code> as a
	 * {@link FileAlterationListener} that listens the file changes. <dd>-
	 * Creates a <code>FileAlterationMonitor</code> that checks the template
	 * <code>xml</code> file changes every {@link #CHECK_INTERVAL} milliseconds.
	 * If the automatic file modifications control doesn't start correctly, the
	 * field <code>automaticControlStarted</code> is set to <code>false</code>.
	 * 
	 * @see FileAlterationObserver
	 * @see FileAlterationListener
	 * @see FileAlterationMonitor
	 */
	public void initialize() {
		// Initializes a XStream object
		this.xstream = new XStream(new DomDriver());
		// Sets alias for the root xml tag
		xstream.alias(EMAIL_TEMPLATE_ROOT_TAG, EMAIL_TEMPLATE_ROOT_ALIAS);
		// Sets alias for the other tags
		FieldAlias[] aliases = FieldAlias.values();
		for (int i = 0; i < aliases.length; i++) {
			xstream.aliasField(aliases[i].getAliasXmlTag(), JemEmail.class, aliases[i].getFieldName());
		}

		// This field is a FileAlterationObserver. It checks every change of
		// the template xml file that describes the Email. 
		// It's important because if someone modifies the file,
		// EmailTemplateReader reloads it.
		// Initializes the field this.emailTemplateFileObserver with a
		// new new FileAlterationObserver using the email template xml file
		// directory
		FileAlterationObserver emailTemplateFileObserver = new FileAlterationObserver(this.emailTemplateFile.getParentFile());
		// Add a listener (this) that listens the file changes
		emailTemplateFileObserver.addListener(this);
		// Creates a FileAlterationMonitor that every CHECK_INTERVAL
		// milliseconds
		// checks the template xml file
		FileAlterationMonitor fileMonitor = new FileAlterationMonitor(CHECK_INTERVAL);
		fileMonitor.addObserver(emailTemplateFileObserver);
		try {
			this.readEmailTemplate();
		} catch (EmailConfigurationException ex) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN008E, ex);
		}
		try {
			fileMonitor.start();
			this.automaticControlStarted = true;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN009E, ex, this.emailTemplateFile);
		}
	}

	/**
	 * This method returns the email template created reading the email template
	 * <code>xml</code> file. <br>
	 * If in the initialization phase occurred problems starting automatic
	 * control, this method read the file, because it could be changed. <br>
	 * If the automatic control is started but always occurred problems reading
	 * the file, return a {@link EmailConfigurationException} and the field
	 * {@link #emailTemplate} is <code>null</code>.
	 * 
	 * @return The email template created reading the email template
	 *         <code>xml</code> file.
	 * @see JemEmail
	 * 
	 * @throws EmailConfigurationException throwed if a reading exception
	 *             occurred.
	 */
	public JemEmail getEmailTemplate() throws EmailConfigurationException {
		if (!this.automaticControlStarted) {
			this.readEmailTemplate();
		}
		if (null == this.emailTemplate) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN010E, this.emailTemplateFile);
			throw new EmailConfigurationException(NotifyMessage.JEMN010E.toMessage().getFormattedMessage(this.emailTemplateFile));
		}
		return this.emailTemplate;
	}

	/**
	 * Method that reads the email template file, and creates a
	 * <code>JemMail</code> object. It saves this <code>JemMail</code> in the
	 * field {@link #emailTemplate}. <br>
	 * It checks that the email template file has a from user email.
	 * 
	 * @see JemEmail
	 * 
	 * @throws EmailConfigurationException throwed if a reading exception
	 *             occurred or if the email template file has no from user
	 *             email.
	 */
	private void readEmailTemplate() throws EmailConfigurationException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(emailTemplateFile);
			JemEmail currEmailTemplate = (JemEmail) this.xstream.fromXML(fis);
			this.emailTemplate = currEmailTemplate;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN010E, ex, this.emailTemplateFile);
			throw new EmailConfigurationException(NotifyMessage.JEMN010E.toMessage().getFormattedMessage(this.emailTemplateFile), ex);
		} finally {
			if (fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		if (null != this.emailTemplate && !this.emailTemplate.hasFromUserEmailAddress()) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN012E, FieldAlias.FROM_USER_EMAIL_ADDRESS_ALIAS.getFieldName());
			throw new EmailConfigurationException(NotifyMessage.JEMN012E.toMessage().getFormattedMessage(FieldAlias.FROM_USER_EMAIL_ADDRESS_ALIAS.getFieldName()));
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is changed. <br>
	 * If the file changed is the email template file, reloads the email
	 * template file.
	 * 
	 * @param file the <code>File</code> changed.
	 * @see FileAlterationListener#onFileChange(File)
	 * @override
	 */
	public void onFileChange(File file) {
		if (file.equals(this.emailTemplateFile)) {
			try {
				this.readEmailTemplate();
			} catch (EmailConfigurationException ex) {
				LogAppl.getInstance().emit(NotifyMessage.JEMN008E, ex);
			}
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is created. <br>
	 * If the file created is the email template file, reloads the email
	 * template file.
	 * 
	 * @param file the <code>File</code> created.
	 * @see FileAlterationListener#onFileCreate(File)
	 * @override
	 */
	public void onFileCreate(File file) {
		if (file.equals(this.emailTemplateFile)) {
			try {
				this.readEmailTemplate();
			} catch (EmailConfigurationException ex) {
				LogAppl.getInstance().emit(NotifyMessage.JEMN008E, ex);
			}
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is deleated. <br>
	 * If the file deleted is the email template file, this method warns the
	 * deletion.
	 * 
	 * @param file the <code>File</code> deleted.
	 * @see FileAlterationListener#onFileDelete(File)
	 * @override
	 */
	public void onFileDelete(File file) {
		if (file.equals(this.emailTemplateFile)) {
			LogAppl.getInstance().emit(NotifyMessage.JEMN011W, new Object[] { this.emailTemplateFile, this.emailTemplateFile.getParentFile() });
		}
	}
}