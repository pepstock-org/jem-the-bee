/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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
package org.pepstock.jem.util.filters.predicates;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.pepstock.jem.util.filters.Filter;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates.AbstractPredicate;
import com.thoughtworks.xstream.XStream;

/**
 * Root class of Hazelcast custom {@link Predicate}s
 * @author Marco "Cuc" Cuccato
 * @version 1.0	
 *
 * @param <T>
 */
public abstract class JemFilterPredicate<T> extends AbstractPredicate implements Serializable {

	private static final long serialVersionUID = -113037417624987942L;

	private transient XStream stream = new XStream();
	
	private Filter filter = null;

	/**
	 * Empty constructor
	 */
	public JemFilterPredicate() {
	}
	
	/**
	 * Build a {@link JemFilterPredicate}
	 * @param filter the {@link Filter} associated with this {@link Predicate}
	 */
	public JemFilterPredicate(Filter filter) {
		this.filter = filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public abstract boolean apply(MapEntry arg0);

	@Override
	public void readData(DataInput dataInput) throws IOException {
		String filterString = dataInput.readLine();
		filter = (Filter) stream.fromXML(filterString);
	}

	@Override
	public void writeData(DataOutput dataOutput) throws IOException {
		// replace \n beacause are not supported from serialize engine
		String ee = stream.toXML(filter).replace('\n', ' ');
		dataOutput.writeBytes(ee);
	}

	/**
	 * @return the associated {@link Filter}
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Set the associated filter
	 * @param filter the associated filter
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}