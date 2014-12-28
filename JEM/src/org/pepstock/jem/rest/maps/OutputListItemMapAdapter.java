/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.rest.maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.log.JemException;

import com.thoughtworks.xstream.XStream;

/**
 * Adapter for a Map object. Map object is not supported by REST for this reason an adapter is needed
 * 
 * @see OutputListItem
 * @author Marco "Fuzzo" Cuccato
 * @version 1.4
 *
 */
public final class OutputListItemMapAdapter extends XmlAdapter<MapType, Map<String, List<OutputListItem>>> {

	private XStream stream = new XStream();

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public MapType marshal(Map<String, List<OutputListItem>> arg0) throws JemException {
		// to serialize, uses XStream
		MapType myMapType = new MapType();
		// scans all output list items entries
		for (Entry<String, List<OutputListItem>> entry : arg0.entrySet()) {
			MapEntryType myMapEntryType = new MapEntryType();
			// uses the key of output list item
			myMapEntryType.key = entry.getKey();
			// serializes the list item into XML
			String value = stream.toXML(entry.getValue());
			// sets value
			myMapEntryType.value = value;
			myMapType.getEntry().add(myMapEntryType);
		}
		return myMapType;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, List<OutputListItem>> unmarshal(MapType arg0) throws JemException {
		// to deserialize, uses XStream
		Map<String, List<OutputListItem>> hashMap = new LinkedHashMap<String, List<OutputListItem>>();
		// scans all maptype entries
		for (MapEntryType myEntryType : arg0.getEntry()) {
			// deserializes the lsit of outputlist item from XML
			List<OutputListItem> list = (List<OutputListItem>)stream.fromXML(myEntryType.value);
			// adds to a map
			hashMap.put(myEntryType.key, list);
		}
		return hashMap;
	}
}