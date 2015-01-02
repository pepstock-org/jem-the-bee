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
package org.pepstock.jem.node.resources;

import org.pepstock.jem.node.configuration.ConfigKeys;
import org.pepstock.jem.node.resources.definition.engine.xml.AbstractFieldTemplate;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for Xstream to read and write XML resource property.
 * <br>
 * It is used to read and write properties as following:<br>
 * <br>
 * <pre>
 * &lt;property name="name" visible="true" override="true"&gt;value&lt;/property&gt; 
 * </pre> 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ResourcePropertyConverter implements Converter {
	
	/**
	 * Constant for HASH attribute
	 */
	public static final String HASH_FIELD = "hash";
	
	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(ResourceProperty.class);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext marsh) {
		// casts resource property
	     ResourceProperty p = (ResourceProperty)value;
	     // writes attributes
         writer.addAttribute(ConfigKeys.NAME_FIELD, p.getName());
         writer.addAttribute(AbstractFieldTemplate.VISIBLE_ATTRIBUTE, String.valueOf(p.isVisible()));
         writer.addAttribute(AbstractFieldTemplate.OVERRIDE_ATTRIBUTE, String.valueOf(p.isOverride()));
         // only if it has got hash it writes hash
         if (p.getHash() != null) {
        	 writer.addAttribute(HASH_FIELD, p.getHash());
         }
         // sets value of property as content of element
         writer.setValue(p.getValue());
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarsh) {
		// reads all attributes
		String name = reader.getAttribute(ConfigKeys.NAME_FIELD);
		String visible = reader.getAttribute(AbstractFieldTemplate.VISIBLE_ATTRIBUTE);
		String override = reader.getAttribute(AbstractFieldTemplate.OVERRIDE_ATTRIBUTE);
		String hash = reader.getAttribute(HASH_FIELD);
		// reads value as content of element
		String value = reader.getValue();
		// creates resource property
		ResourceProperty property = new ResourceProperty();
		// setting all attributes
		property.setName(name);
		property.setOverride(Boolean.valueOf(override));
		property.setVisible(Boolean.valueOf(visible));
		if (hash != null){
			property.setHash(hash);
		}
		property.setValue(value);
		return property;
	}
}