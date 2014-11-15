/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import com.google.gwt.user.client.rpc.GwtTransient;

/**
 * JCL (Job Control Language) is the class which contains all statements to
 * describe and execute jobs. It is possible to have different languages to
 * control jobs and for this reason is abstract about that.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class Jcl extends AbstractJcl implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Unknown label used for all undefined attributes
	 */
	public static final String UNKNOWN = "UNKNOWN";
	
	/**
	 * Describes the language 
	 */
	public static final String DEFAULT_MODE = "xml";

	/**
	 * Constant if JCL is not available. Happens often when you're looking at
	 * job in execution and it ends
	 */
	public static final String CONTENT_NOT_AVAILABLE = "JCL not available";

	private String mode = DEFAULT_MODE;
	
	private String type = null;

	private String classPath = null;
	
	private String priorClassPath = null;
	
	private Map<String, Object> properties = null;

	/**
	 * Put @GWTTransinet to improve serialization performance. Do not use java transient because 
	 * JCL content must be serialize in Hazelcast but not in GWT
	 */
	@GwtTransient
	private String content = null;
	
	/**
	 * Put @GWTTransinet to improve serialization performance. Do not use java transient because 
	 * JCL content must be serialize in Hazelcast but not in GWT
	 */
	@GwtTransient
	private String contentToBeExecuted = null;

	/**
	 * Constructor without any arguments
	 */
	public Jcl() {
	}

	/**
	 * Sets the type of language (and then the factory) to parse and check the
	 * content of JCL by a short name defined on configuration.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @param type type of job control language
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the type of language of control jobs, or null if none.
	 * 
	 * @see org.pepstock.jem.factories#createJcl(String) factory creation method
	 * @return type of job control language
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the language type used for this JCL
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Sets the language type used for this JCL
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * @return the classPath
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * @param classPath the classPath to set
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return the priorClassPath
	 */
	public String getPriorClassPath() {
		return priorClassPath;
	}

	/**
	 * @param priorClassPath the priorClassPath to set
	 */
	public void setPriorClassPath(String priorClassPath) {
		this.priorClassPath = priorClassPath;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Sets the source code representing the JCL, by a string.
	 * 
	 * @param content the string representing source code
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the source code string, representing the JCL.
	 * 
	 * @return the string representing source code
	 */
	@XmlTransient
	public String getContent() {
		return content;
	}
	
	/**
	 * Returns the source code string, representing the JCL, to be executed.
	 * 
	 * @param contentToBeExecuted the contentToBeExecuted to set
	 */
	public void setContentToBeExecuted(String contentToBeExecuted) {
		this.contentToBeExecuted = contentToBeExecuted;
	}

	/**
	 * Returns the source code string, representing the JCL, to be executed.
	 * 
	 * @return the string representing source code
	 */
	@XmlTransient
	public String getContentToBeExecuted() {
		// if thre isn't any content to be submitted
		// that means content attribute must be used 
		// to be executed
		if (contentToBeExecuted == null){
			return content;
		}
		return contentToBeExecuted;
	}
	
	/**
	 * Creates a Unknown jcl, when JEM is not able to parse completely a JCL
	 * @return a unknown JCL
	 */
	public static final Jcl createUnknownJcl(){
		Jcl unknown = new Jcl();
		unknown.setType(UNKNOWN);
		unknown.setJobName(UNKNOWN);
		unknown.setEnvironment(UNKNOWN);
		unknown.setDomain(UNKNOWN);
		unknown.setAffinity(UNKNOWN);
		return unknown;
	}
}