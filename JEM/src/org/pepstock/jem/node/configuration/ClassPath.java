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
package org.pepstock.jem.node.configuration;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Bean used inside the XML configuration to defined a custom classpath.
 * <br>
 * This bean should contains all links to JARS or paths where the java classes must be reachable.
 * <br>
 * The XML root element is named <code>pathElement</code> and the content of this element is used
 * as link to the classes.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
@XStreamAlias("pathElement")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"content"})
public class ClassPath implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
    // used by converter to read from the element instead of an attribute
	private String content = null;
	
	/**
	 * Empty constructor
	 */
	public ClassPath() {
		
	}
	
	/**
	 * @param content
	 */
	public ClassPath(String content) {
		super();
		this.content = content;
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
		return "PathElement [content=" + content + "]";
	}
}