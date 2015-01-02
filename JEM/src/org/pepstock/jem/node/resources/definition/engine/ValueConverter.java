/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Alessandro Zambrini
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
package org.pepstock.jem.node.resources.definition.engine;

import org.pepstock.jem.node.resources.definition.engine.xml.ValueTemplate;
import org.pepstock.jem.util.Parser;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Corresponding tag in the resource templates <code>xml</code> file. Converter
 * class used to convert the tag <code>value</code> of the resource templates
 * <code>xml</code> file in a {@link ValueTemplate} object.
 * 
 * @see Converter
 * @see XStream
 * @author Alessandro Zambrini
 * @version 1.0
 * 
 */
public class ValueConverter implements Converter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(ValueTemplate.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext arg2) {
		ValueTemplate v = (ValueTemplate) value;
		writer.addAttribute(ValueTemplate.SELECTED_ATTRIBUTE, Boolean.toString(v.isSelected()));
		writer.setValue(v.getContent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks
	 * .xstream.io.HierarchicalStreamReader,
	 * com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
		boolean isSelected = Parser.parseBoolean(reader.getAttribute(ValueTemplate.SELECTED_ATTRIBUTE), false);
		String content = reader.getValue();
		ValueTemplate value = new ValueTemplate();
		value.setSelected(isSelected);
		if (null != content) {
			value.setContent(content.trim());
		}
		return value;
	}
}
