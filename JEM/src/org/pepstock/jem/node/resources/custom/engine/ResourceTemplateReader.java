/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.resources.custom.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.resources.custom.ResourceDefinitionException;
import org.pepstock.jem.node.resources.custom.ResourceDescriptor;
import org.pepstock.jem.node.resources.custom.ResourceMessage;
import org.pepstock.jem.node.resources.custom.XmlConfigurationResourceDefinition;
import org.pepstock.jem.node.resources.custom.engine.xml.AbstractFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.CheckBoxFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.MultiSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.PasswordFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ResourceTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.SectionTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.SingleSelectableListFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.SingleValueFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.TextFieldTemplate;
import org.pepstock.jem.node.resources.custom.engine.xml.ValueTemplate;

import com.thoughtworks.xstream.XStream;

/**
 * <code>ResourceTemplateReader</code> receives (in the constructor) the path of
 * the <code>xml</code> file that describes the custom resources template useful
 * to define new resource type User interface. <br>
 * It reads the file and builds a {@link ResourceTemplate} and saves it in the
 * field {@link #resourceTemplate} and a {@link ResourceDescriptor} and saves it
 * in the field {@link #resourceDescriptor}. <br>
 * It may receive a {@link XmlConfigurationResourceDefinition} in which to save
 * the <code>ResourceDescriptor</code> every time it changes.
 * 
 * <code>XStream</code> library is used. <br>
 * <code>ResourceTemplateReader</code> uses also the
 * <code>org.apache.commons.io.monitor</code> library to check automatically if
 * the file is changed: in this case <code>ResourceTemplateReader</code> reloads
 * it and build a new {@link ResourceTemplate}. <br>
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
public class ResourceTemplateReader extends FileAlterationListenerAdaptor {

	/**
	 * The interval in millisecond between each file modification automatic
	 * control. <br>
	 * <b>60</b> seconds.
	 */
	private static final long CHECK_INTERVAL = 60000;

	/**
	 * This field contains the the resource template ({@link ResourceTemplate})
	 * produced reading the template <code>xml</code> file that describes the
	 * custom resources template User interface.
	 */
	private ResourceTemplate resourceTemplate = null;

	/**
	 * This field contains the the resource descriptor (
	 * {@link ResourceDescriptor}) produced reading the template
	 * <code>xml</code> file that describes the custom resources template User
	 * interface.
	 */
	private ResourceDescriptor resourceDescriptor = null;

	/**
	 * The <code>File</code> that maps the resource template <code>xml</code>
	 * file that describes the custom resource template useful to define new
	 * resource type User interface.
	 * 
	 * @see File
	 */
	private File resourceTemplateFile;

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
	 * Field that contains {@link XmlConfigurationResourceDefinition} in which
	 * to save the <code>ResourceDescriptor</code> every time it changes.
	 */
	private XmlConfigurationResourceDefinition xmlConfigurationResourceDefinition = null;

	/**
	 * Constructor. It receives the path of the <code>xml</code> file that
	 * describes the custom resources template User interface and a
	 * {@link XmlConfigurationResourceDefinition} in which to save the
	 * <code>ResourceDescriptor</code> every time it changes.
	 * 
	 * @param resourceTemplateFile the resource template file path.
	 * @param xmlConfigurationResourceDefinition the
	 *            {@link XmlConfigurationResourceDefinition} in which to save
	 *            the <code>ResourceDescriptor</code> every time it changes.
	 * @throws ResourceTemplateException if the resource template file directory
	 *             does not exist, or is not a directory, or is the File System
	 *             root directory.
	 * @see XStream
	 */
	public ResourceTemplateReader(String resourceTemplateFile, XmlConfigurationResourceDefinition xmlConfigurationResourceDefinition) throws ResourceTemplateException {
		// Sets the resource template xml File field
		this.resourceTemplateFile = new File(resourceTemplateFile);
		this.xmlConfigurationResourceDefinition = xmlConfigurationResourceDefinition;
		if (null == this.resourceTemplateFile.getParentFile() || !this.resourceTemplateFile.getParentFile().exists() || !this.resourceTemplateFile.getParentFile().isDirectory()
				|| this.resourceTemplateFile.getParentFile().toString().equalsIgnoreCase(File.separator)) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR001E, this.resourceTemplateFile.getParentFile());
			throw new ResourceTemplateException(ResourceMessage.JEMR001E.toMessage().getFormattedMessage(this.resourceTemplateFile.getParentFile()));
		}
		if (!this.resourceTemplateFile.exists()) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR002W, new Object[] { this.resourceTemplateFile, this.resourceTemplateFile.getParentFile() });
			throw new ResourceTemplateException(ResourceMessage.JEMR002W.toMessage().getFormattedMessage(this.resourceTemplateFile, this.resourceTemplateFile.getParentFile()));
		}
		this.xstream = new XStream();
	}

	/**
	 * Constructor. It receives the path of the <code>xml</code> file that
	 * describes the custom resources template User interface.
	 * 
	 * @param resourceTemplateFile the resource template file path. changes.
	 * @throws ResourceTemplateException if the resource template file directory
	 *             does not exist, or is not a directory, or is the File System
	 *             root directory.
	 * @see XStream
	 */
	public ResourceTemplateReader(String resourceTemplateFile) throws ResourceTemplateException {
		this(resourceTemplateFile, null);
	}

	/**
	 * This method returns the {@link #resourceTemplate} created reading the
	 * resource template <code>xml</code> file. <br>
	 * If in the initialization phase occurred problems starting automatic
	 * control, this method read the file, because it could be changed. <br>
	 * If the automatic control is started but always occurred problems reading
	 * the file, return a {@link ResourceTemplateException} and the field
	 * {@link #resourceTemplate} is <code>null</code>. If the automatic control
	 * is started correctly, it returns field {@link #resourceTemplate} updated.
	 * 
	 * @return The resource template created reading the resource template
	 *         <code>xml</code> file for User interface.
	 * @see ResourceTemplate
	 * 
	 * @throws ResourceTemplateException throwed if a reading exception
	 *             occurred.
	 */
	private ResourceTemplate getResourceTemplate() throws ResourceTemplateException {
		if (!this.automaticControlStarted) {
			this.readResourceTemplate();
		}
		if (null == this.resourceTemplate) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR005E, this.resourceTemplateFile);
			throw new ResourceTemplateException(ResourceMessage.JEMR005E.toMessage().getFormattedMessage(this.resourceTemplateFile));
		}
		return this.resourceTemplate;
	}

	/**
	 * This method returns the {@link #resourceDescriptor} created reading the
	 * resource template <code>xml</code> file. <br>
	 * If in the initialization phase occurred problems starting automatic
	 * control, this method read the file, because it could be changed. <br>
	 * If the automatic control is started but always occurred problems reading
	 * the file, return a {@link ResourceTemplateException} and the field
	 * {@link #resourceDescriptor} is <code>null</code>. If the automatic
	 * control is started correctly, it returns field
	 * {@link #resourceDescriptor} updated.
	 * 
	 * @return The resource descriptor created reading the resource template
	 *         <code>xml</code> file for User interface.
	 * @see ResourceTemplate
	 * @see ResourceDescriptor
	 * 
	 * @throws ResourceTemplateException throwed if a reading exception
	 *             occurred.
	 */
	public ResourceDescriptor getResourceDescriptor() throws ResourceTemplateException {
		if (!this.automaticControlStarted) {
			this.readResourceTemplate();
		}
		if (null == this.resourceDescriptor) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR005E, this.resourceTemplateFile);
			throw new ResourceTemplateException(ResourceMessage.JEMR005E.toMessage().getFormattedMessage(this.resourceTemplateFile));
		}
		return this.resourceDescriptor;
	}

	/**
	 * This method initializes the <code>ResourceTemplateReader</code> <br>
	 * It sets the alias, for the <code>xml</code> resource template file. <br>
	 * It initializes the field <code>xstream</code> using {@link XStream}. <br>
	 * Reads and loads the <code>xml</code> resource template file <br>
	 * It initializes the components to check if someone modifies the resource
	 * template <code>xml</code> file: <br>
	 * <dd>- Initializes the field <code>resourceTemplateFileObserver</code>
	 * with a new {@link FileAlterationObserver} using the directory in which is
	 * placed the resource template file (the field
	 * <code>resourceTemplateFile</code>). <dd>- Add
	 * <code>this ResourceTemplateReader</code> as a
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

		this.aliasXml(xstream);
		// This field is a FileAlterationObserver. It checks every change of
		// the template xml file that describes the Custom Resource
		// User interface. 
		// It's important because if someone modifies the file,
		// ResourceTemplateReader reloads it.
		// Initializes the field this.resourceTemplateFileObserver with a
		// new new FileAlterationObserver using the resource template xml file
		// directory
		FileAlterationObserver resourceTemplateFileObserver = new FileAlterationObserver(this.resourceTemplateFile.getParentFile());
		// Add a listener (this) that listens the file changes
		resourceTemplateFileObserver.addListener(this);
		// Creates a FileAlterationMonitor that every CHECK_INTERVAL
		// milliseconds
		// checks the template xml file
		FileAlterationMonitor fileMonitor = new FileAlterationMonitor(CHECK_INTERVAL);
		fileMonitor.addObserver(resourceTemplateFileObserver);
		try {
			this.readResourceTemplate();
		} catch (ResourceTemplateException ex) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR003E, ex);
		}
		try {
			fileMonitor.start();
			this.automaticControlStarted = true;
		} catch (Exception ex) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR004E, ex, this.resourceTemplateFile);
		}
	}

	/**
	 * Sets all the aliases in the {@link XStream} parameter. It is necessary
	 * for correct mapping from the <code>xml</code> file to the created
	 * {@link ResourceTemplate} object.
	 * 
	 * @param xstream the <code>XStream</code> object in which to set aliases.
	 * @see XStream
	 */
	private void aliasXml(XStream xstream) {
		xstream.registerConverter(new ValueConverter());

		// Sets alias for the root xml tag
		xstream.alias(ResourceTemplate.MAPPED_XML_TAG, ResourceTemplate.class);
		xstream.alias(SectionTemplate.MAPPED_XML_TAG, SectionTemplate.class);
		xstream.alias(TextFieldTemplate.MAPPED_XML_TAG, TextFieldTemplate.class);

		xstream.alias(PasswordFieldTemplate.MAPPED_XML_TAG, PasswordFieldTemplate.class);
		xstream.alias(CheckBoxFieldTemplate.MAPPED_XML_TAG, CheckBoxFieldTemplate.class);

		xstream.alias(SingleSelectableListFieldTemplate.MAPPED_XML_TAG, SingleSelectableListFieldTemplate.class);
		xstream.alias(MultiSelectableListFieldTemplate.MAPPED_XML_TAG, MultiSelectableListFieldTemplate.class);
		xstream.alias(ListFieldTemplate.MAPPED_XML_TAG, ListFieldTemplate.class);
		xstream.alias(ValueTemplate.MAPPED_XML_TAG, ValueTemplate.class);

		xstream.useAttributeFor(ResourceTemplate.class, ResourceTemplate.TYPE_ATTRIBUTE);
		xstream.aliasField(SectionTemplate.MAPPED_XML_TAG, ResourceTemplate.class, ResourceTemplate.SECTIONS_FIELD);
		xstream.addImplicitCollection(ResourceTemplate.class, ResourceTemplate.SECTIONS_FIELD);

		xstream.useAttributeFor(SectionTemplate.class, SectionTemplate.NAME_ATTRIBUTE);
		xstream.aliasField(TextFieldTemplate.MAPPED_XML_TAG, SectionTemplate.class, SectionTemplate.FIELDS_FIELD);
		xstream.aliasField(SingleSelectableListFieldTemplate.MAPPED_XML_TAG, SectionTemplate.class, SectionTemplate.FIELDS_FIELD);
		xstream.aliasField(MultiSelectableListFieldTemplate.MAPPED_XML_TAG, SectionTemplate.class, SectionTemplate.FIELDS_FIELD);
		xstream.aliasField(ListFieldTemplate.MAPPED_XML_TAG, SectionTemplate.class, SectionTemplate.FIELDS_FIELD);
		xstream.addImplicitCollection(SectionTemplate.class, SectionTemplate.FIELDS_FIELD);

		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.KEY_ATTRIBUTE);
		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.LABEL_ATTRIBUTE);
		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.DESCRIPTION_ATTRIBUTE);
		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.MANDATORY_ATTRIBUTE);

		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.OVERRIDE_ATTRIBUTE);
		xstream.useAttributeFor(AbstractFieldTemplate.class, AbstractFieldTemplate.VISIBLE_ATTRIBUTE);

		xstream.useAttributeFor(SingleValueFieldTemplate.class, SingleValueFieldTemplate.DEFAULT_VALUE_ATTRIBUTE);

		xstream.useAttributeFor(TextFieldTemplate.class, TextFieldTemplate.REG_EX_VALIDATOR_ATTRIBUTE);

		xstream.useAttributeFor(ListFieldTemplate.class, ListFieldTemplate.REG_EX_VALIDATOR_ATTRIBUTE);

		xstream.useAttributeFor(SingleSelectableListFieldTemplate.class, SingleSelectableListFieldTemplate.RENDER_AS_RADIO_ATTRIBUTE);
		xstream.aliasField(ValueTemplate.MAPPED_XML_TAG, SingleSelectableListFieldTemplate.class, SingleSelectableListFieldTemplate.VALUES_FIELD);
		xstream.addImplicitCollection(SingleSelectableListFieldTemplate.class, SingleSelectableListFieldTemplate.VALUES_FIELD);

		xstream.aliasField(ValueTemplate.MAPPED_XML_TAG, MultiSelectableListFieldTemplate.class, MultiSelectableListFieldTemplate.VALUES_FIELD);
		xstream.addImplicitCollection(MultiSelectableListFieldTemplate.class, MultiSelectableListFieldTemplate.VALUES_FIELD);

		xstream.useAttributeFor(ValueTemplate.class, ValueTemplate.SELECTED_ATTRIBUTE);
	}

	/**
	 * Method that reads the resource template file, and creates a
	 * <code>ResourceTemplate</code> end a <code>ResourceDescriptor</code>
	 * object. It saves the <code>ResourceTemplate</code> in the field
	 * {@link #resourceTemplate} and the <code>ResourceDescriptor</code> in the
	 * field {@link #resourceDescriptor}. <br>
	 * If it reads a resource already existing and the resource type has
	 * changed, removes the old type resources.
	 * 
	 * @see ResourceTemplate
	 * @see ResourceDescriptor
	 * 
	 * @throws ResourceTemplateException throwed if a reading exception
	 *             occurred.
	 */
	private void readResourceTemplate() throws ResourceTemplateException {
		ResourceTemplate template = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(this.resourceTemplateFile);
			template = (ResourceTemplate) this.xstream.fromXML(fis);
			fis.close();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR005E, ex, this.resourceTemplateFile);
			throw new ResourceTemplateException(ResourceMessage.JEMR005E.toMessage().getFormattedMessage(this.resourceTemplateFile), ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
		ResourceDescriptor newResourceDescriptor = ResourceTemplatesFactory.buildResourceDescriptor(template);
		LogAppl.getInstance().emit(ResourceMessage.JEMR021I, newResourceDescriptor.getType(), this.resourceTemplateFile);
		if (null != this.xmlConfigurationResourceDefinition) {
			this.xmlConfigurationResourceDefinition.setResourceDescriptor(newResourceDescriptor);
			if (null != this.resourceDescriptor && !this.resourceDescriptor.getType().equalsIgnoreCase(newResourceDescriptor.getType())) {
				// resource changed type
				try {
					Main.CUSTOM_RESOURCE_DEFINITION_MANAGER.changeResourceType(this.xmlConfigurationResourceDefinition, this.resourceDescriptor.getType());
					this.resourceDescriptor = newResourceDescriptor;
					this.resourceTemplate = template;
				} catch (ResourceDefinitionException rdEx) {
					// debug
					LogAppl.getInstance().debug(rdEx.getMessage(), rdEx);
					// in case of error go back to the old type
					this.xmlConfigurationResourceDefinition.setResourceDescriptor(this.resourceDescriptor);
				}
			} else {
				this.resourceDescriptor = newResourceDescriptor;
				this.resourceTemplate = template;
			}
		} else {
			this.resourceDescriptor = newResourceDescriptor;
			this.resourceTemplate = template;
		}
	}

	/**
	 * Returns the {@link #resourceTemplate} to xml String.
	 * 
	 * @return the {@link #resourceTemplate} to xml String.
	 */
	public String resourceTemplateToString() {
		try {
			ResourceTemplate currResourceTemplate = this.getResourceTemplate();
			return this.xstream.toXML(currResourceTemplate);
		} catch (ResourceTemplateException rtEx) {
			// debug
			LogAppl.getInstance().debug(rtEx.getMessage(), rtEx);
			return "<null/>";
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is changed. <br>
	 * If the file changed is the resource template file, reloads the resource
	 * template file.
	 * 
	 * @param file the <code>File</code> changed.
	 * @see FileAlterationListener#onFileChange(File)
	 * @override
	 */
	public void onFileChange(File file) {
		if (file.equals(this.resourceTemplateFile)) {
			try {
				this.readResourceTemplate();
			} catch (ResourceTemplateException ex) {
				LogAppl.getInstance().emit(ResourceMessage.JEMR003E, ex);
			}
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is created. <br>
	 * If the file created is the resource template file, reloads the resource
	 * template.
	 * 
	 * @param file the <code>File</code> created.
	 * @see FileAlterationListener#onFileCreate(File)
	 * @override
	 */
	public void onFileCreate(File file) {
		if (file.equals(this.resourceTemplateFile)) {
			try {
				this.readResourceTemplate();
			} catch (ResourceTemplateException ex) {
				LogAppl.getInstance().emit(ResourceMessage.JEMR003E, ex);
			}
		}
	}

	/**
	 * Called if the parameter file <code>file</code> is deleted. <br>
	 * If the file deleted is the resource template file, this method warns the
	 * deletion.
	 * 
	 * @param file the <code>File</code> deleted.
	 * @see FileAlterationListener#onFileDelete(File)
	 * @override
	 */
	public void onFileDelete(File file) {
		if (file.equals(this.resourceTemplateFile)) {
			LogAppl.getInstance().emit(ResourceMessage.JEMR006W, new Object[] { this.resourceTemplateFile, this.resourceTemplateFile.getParentFile() });
		}
	}
}
