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
package org.pepstock.jem.jbpm;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.factories.AbstractFactory;
import org.pepstock.jem.factories.JclFactoryException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.tasks.JobTask;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This implements JemFactory interface to create, check and validate JCL for
 * JBPM. It creates JCL object.
 * 
 * @see org.pepstock.jem.factories.JemFactory
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JBpmFactory extends AbstractFactory {

    private static final long serialVersionUID = 1L;
    
	/**
	 * public key which indicates the JCL type for JBPM
	 */
	public static final String JBPM_TYPE = "jbpm";
	
	private static final String JBPM_TYPE_DESCRIPTION = "JBoss BPM";
	
	private static final String PROCESS_XML_ELEMENT = "process";

	private static final String ID_XML_ATTRIBUTE = "id";
	
	/**
	 * Empty constructor
	 */
	public JBpmFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JemFactory#getType()
	 */
	@Override
	public String getType() {
		return JBPM_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JemFactory#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return JBPM_TYPE_DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JclFactory#createJcl(java.lang.String)
	 */
	@Override
	public Jcl createJcl(String content) throws JclFactoryException {
		// creates a default JCL
		Jcl jcl = new Jcl();
		// sets type and content
		jcl.setType(JBPM_TYPE);
		jcl.setContent(content);
		try {
			// validates JCL
	        validate(jcl, content);
        } catch (JBpmException e) {
        	throw new JclFactoryException(jcl, e);
        }
		return jcl;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.factories.JobTaskFactory#createJobTask(org.pepstock.jem.Job)
	 */
	@Override
	public JobTask createJobTask(Job job) {
		return new JBpmTask(job, this);
	}
	
	/**
	 * Validates the JCL content, calling the JBPM API 
	 * @param jcl default JCL to store into job
	 * @param content JCL content
	 * @throws JBpmException if any error occurs
	 */
	private void validate(Jcl jcl, String content) throws JBpmException{
		// read the BPMN Metadata, loading in properties object
		Properties p = new Properties();
		
		// creates all readers
		StringReader reader = new StringReader(content);
		InputSource source = new InputSource(reader);
		SimpleRuntimeEnvironment jbpmEnvironment = null;
		String jobName = null;

		// SCANS JCL to get JOBNAME
		// JOBNAME = attribute ID of PROCESS element
		try {
			// creates DOM objects
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setIgnoringComments(false);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(source);
	        
	        // scan XML to read comment
	        NodeList nl = doc.getDocumentElement().getChildNodes();
	        // scans all nodes
	        for (int i = 0; i < nl.getLength(); i++) {
	        	Node node = nl.item(i);
	        	if (node.getNodeType() == Element.ELEMENT_NODE) {
	            	Element el = (Element)node;
	            	
	            	String tagName = null;
	            	// checks if name space
	            	if (el.getTagName().contains(":")){
	            		tagName = StringUtils.substringAfter(el.getTagName(),":");
	            	} else {
	            		tagName = el.getTagName();
	            	}
	            	// checks only if is a PROCESS element
	            	if (tagName.equalsIgnoreCase(PROCESS_XML_ELEMENT)){
	            		// gets ID attribute to set JOBNAME
	            		String jobNameAttr = el.getAttribute(ID_XML_ATTRIBUTE);
	            		if (jobNameAttr != null){
	            			// puts on properties
	            			p.setProperty(JBpmKeys.JBPM_JOB_NAME, jobNameAttr);
	            		} else {
	            			throw new JBpmException(JBpmMessage.JEMM031E);
	            		}
	            	}
	            }
	        }
        
			// checks if project has attribute name, if yes, uses it as JOB NAME
			// otherwise get the JEM property to define it
			jobName = p.getProperty(JBpmKeys.JBPM_JOB_NAME);

			// Anyway job name can't be null. if yes, exception occurs
			if (jobName == null){
				throw new JBpmException(JBpmMessage.JEMM031E);
			}
			// loads JCL jobname
			jcl.setJobName(jobName);

			// creates JBPM environment to check BPMN2 syntax
			SimpleRegisterableItemsFactory factory = new SimpleRegisterableItemsFactory();
			jbpmEnvironment = new SimpleRuntimeEnvironment(factory);
			jbpmEnvironment.setUsePersistence(false);
			Resource res = ResourceFactory.newReaderResource(new StringReader(content), CharSet.DEFAULT_CHARSET_NAME);
			jbpmEnvironment.addAsset(res, ResourceType.BPMN2);
			// gets process to read metadata
			org.kie.api.definition.process.Process process = jbpmEnvironment.getKieBase().getProcess(jobName);
			// if process is null, exception
			if (process == null){
				throw new JBpmException(JBpmMessage.JEMM063E);
			}
			// loads all METADATA
		    p.putAll(process.getMetaData());
		    
		    // check again because you could put the job name on metadata
	    	jcl.setJobName(p.getProperty(JBpmKeys.JBPM_JOB_NAME));
	    	
	    	// loads the JBPM process ID in a map to reuse when a JBPM task will be scheduled
	    	Map<String, Object> jclMap = new HashMap<String, Object>();
	    	jclMap.put(JBpmKeys.JBPM_JOB_NAME, jobName);
	    	jcl.setProperties(jclMap);
		
        } catch (DOMException e) {
        	throw new JBpmException(JBpmMessage.JEMM047E, e, e.getMessage());
        } catch (ParserConfigurationException e) {
        	throw new JBpmException(JBpmMessage.JEMM047E, e, e.getMessage());
        } catch (SAXException e) {
        	throw new JBpmException(JBpmMessage.JEMM047E, e, e.getMessage());
        } catch (Exception e) {
        	throw new JBpmException(JBpmMessage.JEMM047E, e, e.getMessage());
        } finally {
        	// clean up of JBPM environment
        	if (jbpmEnvironment != null && jobName != null){
        		try {
	                jbpmEnvironment.getKieBase().removeProcess(jobName);
                } catch (Exception e) {
                	LogAppl.getInstance().debug(e.getMessage(), e);
                }
        	}
        }
	    
		// if I'm here, JCL is correct

		// extracts the locking scope and checks if the value is correct
		// the value is not save in JCL because is not helpful to node but
		// at runtime so it will be read again from JBPM listener
		String lockingScopeProperty = p.getProperty(JBpmKeys.JBPM_LOCKING_SCOPE);
		if (lockingScopeProperty != null && 
				!lockingScopeProperty.equalsIgnoreCase(JBpmKeys.JBPM_JOB_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(JBpmKeys.JBPM_STEP_SCOPE) &&
				!lockingScopeProperty.equalsIgnoreCase(JBpmKeys.JBPM_TASK_SCOPE)){
			throw new JBpmException(JBpmMessage.JEMM032E, JBpmKeys.JBPM_LOCKING_SCOPE, lockingScopeProperty);		
		}
		
		// Extracts from JBPM enviroment property
		String environment = p.getProperty(JBpmKeys.JBPM_ENVIRONMENT);
		// if null, uses current environment assigned to JEM NODE
		if (environment == null) {
			environment = Main.EXECUTION_ENVIRONMENT.getEnvironment();
		}

		// Extracts from JBPM domain property
		String domain = p.getProperty(JBpmKeys.JBPM_DOMAIN);
		// if null, uses domain default
		if (domain == null) {
			domain = Jcl.DEFAULT_DOMAIN;
		}

		// Extracts from JBPM email addresses notification property
		String emailAddresses = p.getProperty(JBpmKeys.JBPM_EMAILS_NOTIFICATION);
		if(null != emailAddresses) {
			jcl.setEmailNotificationAddresses(emailAddresses);
		}
		// Extracts from JBPM affinity property
		String affinity = p.getProperty(JBpmKeys.JBPM_AFFINITY);
		// if null, uses affinity default
		if (affinity == null) {
			affinity = Jcl.DEFAULT_AFFINITY;
		}

		// Extracts from JBPM user property
		String user = p.getProperty(JBpmKeys.JBPM_USER);
		if(null != user) {
			jcl.setUser(user);
		}

		// Extracts from JBPM classpath property
		String classPath = p.getProperty(JBpmKeys.JBPM_CLASSPATH);
		// if classpath is not set, changes if some variables are in
		if (classPath != null) {
			jcl.setClassPath(super.resolvePathNames(classPath, ConfigKeys.JEM_CLASSPATH_PATH_NAME));
		}

		// Extracts from JBPM prior classpath property
		String priorClassPath = p.getProperty(JBpmKeys.JBPM_PRIOR_CLASSPATH);
		// if classpath is not set, changes if some variables are in
		if (priorClassPath != null) {
			jcl.setPriorClassPath(super.resolvePathNames(priorClassPath, ConfigKeys.JEM_CLASSPATH_PATH_NAME));
		}
		
		// Extracts from JBPM java version property
		String java = p.getProperty(JBpmKeys.JBPM_JAVA);
		if(null != java) {
			if (affinity != null && !affinity.equalsIgnoreCase(Jcl.DEFAULT_AFFINITY)){
				affinity = affinity + "," + java;
			} else {
				affinity = java;
			}
			jcl.setJava(java);
		}

		// Extracts from JBPM memory property. If missing, default is 256 
		int memory = Parser.parseInt(p.getProperty(JBpmKeys.JBPM_MEMORY), Jcl.DEFAULT_MEMORY);
		// Extracts from JBPM hold property. If missing, default is FALSE
		boolean hold = Parser.parseBoolean(p.getProperty(JBpmKeys.JBPM_HOLD), false);
		// Extracts from JBPM priority property. If missing, default is 10
		int priority = Parser.parseInt(p.getProperty(JBpmKeys.JBPM_PRIORITY), Jcl.DEFAULT_PRIORITY);

		// saves all info inside of JCL object for further computing
		
		jcl.setEnvironment(environment);
		jcl.setDomain(domain);
		jcl.setAffinity(affinity);
		jcl.setHold(hold);
		jcl.setPriority(priority);
		jcl.setMemory(memory);

	}
	

}
