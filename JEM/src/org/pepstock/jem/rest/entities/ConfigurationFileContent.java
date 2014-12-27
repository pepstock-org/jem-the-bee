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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.node.ConfigurationFile;
import org.pepstock.jem.node.affinity.Result;

/**
 * POJO container of configuration services of nodes.<br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class ConfigurationFileContent extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String what = null;
    
    private String content = null;
    
    private ConfigurationFile file = null;

	private NodeInfoBean node = null;
	
	private Result result = null;

	/**
	 * Empty constructor
	 */
	public ConfigurationFileContent() {
	}

	/**
	 * @return the what
	 */
	public String getWhat() {
		return what;
	}

	/**
	 * @param what the what to set
	 */
	public void setWhat(String what) {
		this.what = what;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the file
	 */
	public ConfigurationFile getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(ConfigurationFile file) {
		this.file = file;
	}

	/**
	 * @return the node
	 */
	public NodeInfoBean getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(NodeInfoBean node) {
		this.node = node;
	}

	/**
	 * @return the result
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigurationFileContent [what=" + what + ", content=" + content + ", file=" + file + ", node=" + node + ", result=" + result + "]";
	}
}