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
package org.pepstock.jem.node.persistence.mongo.module;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.pepstock.jem.node.resources.ResourceProperties;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.util.CharSet;

/**
 * Jackson module to serialize Resource properties MAP instance.
 * This is mandatory because in MONGODB field with "." or starting with "$" are not supported
 * and we want to have keys with ".".
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
class ResourcePropertiesSerializer extends JsonSerializer<ResourceProperties> {
	
	/* (non-Javadoc)
	 * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator, org.codehaus.jackson.map.SerializerProvider)
	 */
	@Override
	public void serialize(ResourceProperties object, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		// starts write object
		generator.writeStartObject();
		// scans all properties
		for (Entry<String, ResourceProperty> entry : object.entrySet()){
			// encodes the key of map (base64)
			String newField = new String(Base64.encodeBase64(entry.getKey().getBytes(CharSet.DEFAULT)), CharSet.DEFAULT);
			// sets new field name
			generator.writeFieldName(newField);
			// writes object using the standard JSON serializer
			generator.writeObject(entry.getValue());
		}
		// ends object
		generator.writeEndObject();
	}
}
