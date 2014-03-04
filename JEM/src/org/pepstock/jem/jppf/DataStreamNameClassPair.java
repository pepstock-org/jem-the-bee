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
package org.pepstock.jem.jppf;

import javax.naming.NameClassPair;

import com.thoughtworks.xstream.XStream;

/**
 * Extends a normal NameClassPair, used on JNDI context.<br>
 * This extension is necessary to store the VALUE of key inside 
 * of JNDI context.<br>
 * Serialiazes object in XML to avoid any conflict cross classloader. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class DataStreamNameClassPair extends NameClassPair {
	
	private transient XStream stream = null;

	private static final long serialVersionUID = 1L;
	
	private String xmlObject = null;

	/**
	 * Unique constructor with key of object, class name of object and if name is relative. 
	 * 
	 * @param name name of object in JNDI context.
	 * @param className class name of object
	 * @param isRelative if name is relative
	 */
	public DataStreamNameClassPair(String name, String className, boolean isRelative) {
		super(name, className, isRelative);
		 stream = new XStream();
	}
	
	/**
	 * @return the object, after deserializing from XML
	 */
	public Object getObject() {
		return stream.fromXML(xmlObject);
	}	
	
	/**
	 * @param object the object to set, serializing in XML
	 */
	public void setObject(Object object) {
		this.xmlObject = stream.toXML(object);
	}
	/**
	 * @return the xmlObject
	 */
	public String getXmlObject() {
		return xmlObject;
	}

	/**
	 * @param xmlObject the xmlObject to set
	 */
	public void setXmlObject(String xmlObject) {
		this.xmlObject = xmlObject;
	}
}