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
package org.pepstock.jem.node.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pepstock.jem.node.configuration.ConfigKeys;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for Xstream to read and write XML custom resource property.
 * <br>
 * It is used to read and write properties as following:<br>
 * <br>
 * <pre>
 * &lt;property name="name"&gt;value&lt;/property&gt; 
 * </pre> 
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ResourceCustomPropertyConverter implements Converter {
	
	/**
	 * Constant for HASH attribute
	 */
	public static final String HASH_FIELD = "hash";
	
	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(HashMap.class);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext marsh) {
	     @SuppressWarnings("unchecked")
	     // writes a map as properties
		Map<String, String> map = (Map<String, String>)value;
	     for (Entry<String, String> entry : map.entrySet()){
		     writer.startNode(ConfigKeys.PROPERTY_ATTRIBUTE_ALIAS);
	         writer.addAttribute(ConfigKeys.NAME_FIELD, entry.getKey());
	         // writes the element content as property value
	         writer.setValue(entry.getValue());
	         writer.endNode();
	     }
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarsh) {
		Map<String, String> map = new HashMap<String, String>();
		while(reader.hasMoreChildren()){
			// scans all nodes
			reader.moveDown();
			// reads the attribute
			String name = reader.getAttribute(ConfigKeys.NAME_FIELD);
			// and reads the element content
			String value = reader.getValue();
			// puts on the map
			map.put(name, value);
			// return up
			reader.moveUp();
		}
        return map;
	}
}