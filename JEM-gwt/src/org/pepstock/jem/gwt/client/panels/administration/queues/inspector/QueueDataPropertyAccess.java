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
package org.pepstock.jem.gwt.client.panels.administration.queues.inspector;

import org.pepstock.jem.gwt.client.charts.DataPropertyAccess;
import org.pepstock.jem.gwt.client.panels.administration.queues.DetailedQueueData;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public interface QueueDataPropertyAccess extends DataPropertyAccess<DetailedQueueData> {

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> entries();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> puts();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> gets();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> removes();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> hits();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> locked();

	/**
	 * @return
	 */
	ValueProvider<DetailedQueueData, Long> lockWaits();
	
}