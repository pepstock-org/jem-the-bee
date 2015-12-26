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
package org.pepstock.jem.util;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Set;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.Predicates.AbstractPredicate;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.thoughtworks.xstream.XStream;

/**
 * Generic predicate to extract data from Hazelcast maps. It serializes object 
 * using Xstream
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * 
 * @param <T> Type of object to send to hazelcast predicate
 */
public abstract class InternalAbstractPredicate<T> extends AbstractPredicate {

	private static final long serialVersionUID = 1L;
	
	private transient XStream stream = new XStream();
	
	private T object = null;

	/* (non-Javadoc)
	 * @see com.hazelcast.query.IndexAwarePredicate#filter(com.hazelcast.query.impl.QueryContext)
	 */
	@Override
	public Set<QueryableEntry> filter(QueryContext context) {
		return null;
	}
	
	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.query.Predicates.AbstractPredicate#readData(com.hazelcast.nio.ObjectDataInput)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readData(ObjectDataInput dataInput) throws IOException {
		// de-serialize the filter object
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		// reads byte from hazelcast serialization
		byte b = dataInput.readByte();
		while (b > -1) {
			try {
				ba.write(b);
				b = dataInput.readByte();
			} catch (EOFException e) {
				b = -1;
			}
		}
		String data =  new String(ba.toByteArray(), CharSet.DEFAULT);
		object = (T) stream.fromXML(data);
	}

	/* (non-Javadoc)
	 * @see com.hazelcast.query.Predicates.AbstractPredicate#writeData(com.hazelcast.nio.ObjectDataOutput)
	 */
	@Override
	public void writeData(ObjectDataOutput dataOutput) throws IOException {
		// replace \n because are not supported from serialization engine
		// of Hazelcast
		String ee = stream.toXML(getObject()).replace('\n', ' ');
		dataOutput.writeBytes(ee);
	}
}
