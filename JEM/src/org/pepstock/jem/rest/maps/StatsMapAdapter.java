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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.stats.LightMapStats;

import com.thoughtworks.xstream.XStream;

/**
 * Adapter for a Map object. Map object is not supported by REST for this reason an adapter is needed
 * 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class StatsMapAdapter extends XmlAdapter<MapType, Map<String, LightMapStats>> {

	private XStream stream = new XStream();

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public MapType marshal(Map<String, LightMapStats> pref) throws JemException {
		// to serialize, uses XStream
		MapType myMapType = new MapType();
		for (Entry<String, LightMapStats> entry : pref.entrySet()) {
			MapEntryType myMapEntryType = new MapEntryType();
			myMapEntryType.key = entry.getKey();
			String value = stream.toXML(entry.getValue());
			myMapEntryType.value = value;
			myMapType.getEntry().add(myMapEntryType);
		}
		return myMapType;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Map<String, LightMapStats> unmarshal(MapType type) throws JemException {
		// to deserialize, uses XStream
		Map<String, LightMapStats> hashMap = new HashMap<String, LightMapStats>();
		for (MapEntryType myEntryType : type.getEntry()) {
			LightMapStats up = (LightMapStats)stream.fromXML(myEntryType.value);
			hashMap.put(myEntryType.key, up);
		}
		return hashMap;
	}

}