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
package org.pepstock.jem.node.sgm;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Bean used by XStream to store the configuration about the paths to use in JEM.
 * <br>
 * It uses a Stream converter, because it uses <code>name</code> as attribute to assign to the path.
 * and the content of the element as path value.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
@XStreamAlias("path")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"content"})
public class Path implements Serializable {

    private static final long serialVersionUID = 1L;

    // uses name as attribute on element
    @XStreamAsAttribute
	private String name = null;
	
    // content is not used as attribute
    // but read as content of element
	private String content = null;
	
	/**
	 * Empty constructor
	 */
	public Path() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Path [name=" + name + ", content=" + content + "]";
	}
}
