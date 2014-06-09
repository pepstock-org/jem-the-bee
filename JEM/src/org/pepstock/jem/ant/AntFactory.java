/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock " Stocchero
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
package org.pepstock.jem.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.ValidationException;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.ant.validator.transformer.TransformerValidator;
import org.pepstock.jem.factories.AbstractFactory;
import org.pepstock.jem.factories.JclFactoryException;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.tasks.JobTask;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;

/**
 * This implements JemFactory interface to create, check and validate JCL for
 * ANT. It creates JCL object.
 * 
 * @see org.pepstock.jem.factories.JemFactory
 * @author Andrea "Stock " Stocchero
 * 
 */
public class AntFactory extends AbstractFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * public key which indicates the JCL type for ANT
	 */
	public static final String ANT_TYPE = "ant";
	
	private static final String ANT_TYPE_DESCRIPTION = "Apache ANT";

	private static final String ANT_BASE_DIR = ".";
	
	private transient TransformerValidator validator = null;
	
	/**
	 * Empty constructor. Does nothing
	 */
	public AntFactory() {
		super();
	}

	@Override
	public void init(Properties properties) throws JemException {
		super.init(properties);

		// missing enable multiple validator type like xslt, java, groovy
		String xsltvalidatorFile = getProperties().getProperty(AntKeys.ANT_XSLTPROJECTVALIDATOR);
		if (xsltvalidatorFile != null && !xsltvalidatorFile.isEmpty()) {
			if (validator == null) {
				validator = new TransformerValidator();
			}
			// load a validator and initialize it with the xslt document (this is persisted in
			// memory and watched for changed)
			try {
				validator.load(xsltvalidatorFile);
			} catch (ValidationException e) {
				throw new JemException(e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * Called to create a JCL object, by the string representing source code of
	 * ANT.
	 * 
	 * @see org.pepstock.jem.factories.JclFactory#createJcl(java.lang.String)
	 * @return JCL object
	 * @throws JclFactoryException if syntax is not correct, a exception occurs
	 */
	@Override
	public Jcl createJcl(String content) throws JclFactoryException {
		// creates JCL object setting the source code
		AntJcl jcl = new AntJcl();
		jcl.setType(ANT_TYPE);
		jcl.setContent(content);

		// file reference to use to write JCL
		File file = null;
		try {
			// creates a temporary file (to delete at the end)
			file = File.createTempFile("jcl", ".jcl");

			// writes the content of JCL
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.write(jcl.getContent(), fos, CharSet.DEFAULT);
			fos.flush();
			fos.close();

			// validates jcl content
			validate(jcl, file);

		} catch (AntException e) {
			throw new JclFactoryException(jcl, e);
		} catch (IOException e) {
			throw new JclFactoryException(jcl, e);
		} finally {
			// delete anyway the JCL file (temporary)
			if (file != null){
				boolean isDeleted = file.delete();
				if (!isDeleted){
					// nop
				}
			}
		}
		return jcl;
	}

	/**
	 * It validates XML syntax of ANT source code. <br>
	 * Creates an ANT project object, extracting all information.<br>
	 * For job name, uses the <code>name</code> attribute value for property
	 * element inside of project element, to set job Name. If is missing, JEM
	 * uses <code>name</code> attribute value for project.
	 * 
	 * @param jcl JCL instance with source code
	 * @param file file reference for JCL
	 * @throws ValidationException if IO error or job name not set a exception
	 *             occurs
	 */
	private void validate(AntJcl jcl, File file) throws AntException {
		// creates an ANT empty project
		Project p = new Project();
		// initializes properties
		// we need to set ant.file prop
		p.initProperties();
		// sets base dir , current directory and set property for JCL
		// file name to use
		p.setBasedir(ANT_BASE_DIR);
		p.setUserProperty("ant.file", file.getAbsolutePath());

		// initializes it
		p.init();

		// creates the helper to parse JCL ANT file
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);

		// parse it. If there is any syntax error, throw a BuildException, 
		// but it tries to get JobName!!
		try {
			helper.parse(p, file);
		} catch (BuildException ex){
			String jobName = (p.getName() != null) ? p.getName() : p.getProperty(AntKeys.ANT_JOB_NAME);
			jcl.setJobName(jobName);
			throw ex;
		}
		// if I'm here, JCL is correct

		// checks if project has attribute name, if yes, uses it as JOB NAME
		// otherwise get the JEM property to define it
		String jobName = (p.getName() != null) ? p.getName() : p.getProperty(AntKeys.ANT_JOB_NAME);

		// Anyway job name can't be null. if yes, exception occurs
		if (jobName == null){
			throw new AntException(AntMessage.JEMA031E);
		}

		// extracts the locking scope and checks if the value is correct
		// the value is not save in JCL because is not helpful to node but
		// at runtime so it will be read again from ANT listener
		String lockingScopeProperty = p.getProperty(AntKeys.ANT_LOCKING_SCOPE);
		if (lockingScopeProperty != null && 
				!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_JOB_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_STEP_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(AntKeys.ANT_TASK_SCOPE)){
			throw new AntException(AntMessage.JEMA032E, AntKeys.ANT_LOCKING_SCOPE, lockingScopeProperty);		
		}
		
		// Extracts from ANT enviroment property
		String environment = p.getProperty(AntKeys.ANT_ENVIRONMENT);
		// if null, uses current environment assigned to JEM NODE
		if (environment == null) {
			environment = Main.EXECUTION_ENVIRONMENT.getEnvironment();
		}

		// Extracts from ANT domain property
		String domain = p.getProperty(AntKeys.ANT_DOMAIN);
		// if null, uses domain default
		if (domain == null) {
			domain = Jcl.DEFAULT_DOMAIN;
		}

		// Extracts from ANT email addresses notification property
		String emailAddresses = p.getProperty(AntKeys.ANT_EMAILS_NOTIFICATION);
		if(null != emailAddresses) {
			jcl.setEmailNotificationAddresses(emailAddresses);
		}
		// Extracts from ANT affinity property
		String affinity = p.getProperty(AntKeys.ANT_AFFINITY);
		// if null, uses affinity default
		if (affinity == null) {
			affinity = Jcl.DEFAULT_AFFINITY;
		}

		// Extracts from ANT user property
		String user = p.getProperty(AntKeys.ANT_USER);
		if(null != user) {
			jcl.setUser(user);
		}

		// Extracts from ANT classpath property
		String classPath = p.getProperty(AntKeys.ANT_CLASSPATH);
		// if classpath is not set, changes if some variables are in
		if (classPath != null) {
			jcl.setClassPath(super.resolvePathNames(classPath, ConfigKeys.JEM_CLASSPATH_PATH_NAME));
		}

		// Extracts from ANT prior classpath property
		String priorClassPath = p.getProperty(AntKeys.ANT_PRIOR_CLASSPATH);
		// if classpath is not set, changes if some variables are in
		if (priorClassPath != null) {
			jcl.setPriorClassPath(super.resolvePathNames(priorClassPath, ConfigKeys.JEM_CLASSPATH_PATH_NAME));
		}

		// Extracts from ANT memory property. If missing, default is 256 
		int memory = Parser.parseInt(p.getProperty(AntKeys.ANT_MEMORY), Jcl.DEFAULT_MEMORY);
		// Extracts from ANT hold property. If missing, default is FALSE
		boolean hold = Parser.parseBoolean(p.getProperty(AntKeys.ANT_HOLD), false);
		// Extracts from ANT priority property. If missing, default is 10
		int priority = Parser.parseInt(p.getProperty(AntKeys.ANT_PRIORITY), Jcl.DEFAULT_PRIORITY);

		// saves all info inside of JCL object for further computing
		jcl.setJobName(jobName);
		jcl.setEnvironment(environment);
		jcl.setDomain(domain);
		jcl.setAffinity(affinity);
		jcl.setHold(hold);
		jcl.setPriority(priority);
		jcl.setMemory(memory);

		// apply custom validation rules loaded from an xlst grammar
		if (validator != null) {
			try {
				validator.validate(jcl);
			} catch (ValidationException e) {
				throw new AntException(AntMessage.JEMA047E, e, e.getMessage());		
			}
		}
	}

	/**
	 * It returns new instance of AntTask.
	 * 
	 * @see AntTask
	 * @see org.pepstock.jem.node.tasks.JobTask
	 * @see org.pepstock.jem.factories.JobTaskFactory#createJobTask(org.pepstock.jem.Job)
	 * @param job job instance to execute
	 * @return Job task instance so JEM node can execute it
	 */
	@Override
	public JobTask createJobTask(Job job) {
		return new AntTask(job, this);
	}

	/**
	 * Returns the type of this factory. This is unique key (value "ant" means
	 * ANT) to search factory loaded during startup.
	 * 
	 * @see org.pepstock.jem.node.Main#FACTORIES_LIST
	 * @see org.pepstock.jem.node.StartUpSystem#run()
	 * @see org.pepstock.jem.factories.JemFactory#getType()
	 */
	@Override
	public String getType() {
		return ANT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JemFactory#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return ANT_TYPE_DESCRIPTION;
	}

}