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
package org.pepstock.jem.node.persistence.mongo;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.pepstock.jem.Job;

/**
 * Map manager based on MONGO for jobs.
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class JobMongoManager extends AbstractMongoManager<Job> {
	
	private static final String FIELD_KEY = "id";

	/**
	 * Creates the object setting queue and field key of JSON
	 * @param queueName job queue name to manage
	 */
	public JobMongoManager(String queueName) {
		this(queueName, false);
	}
	
	/**
	 * Creates the object setting queue and field key of JSON
	 * @param queueName job queue name to manage
	 * @param canBeEvicted if the HC map can be evicted
	 */
	public JobMongoManager(String queueName, boolean canBeEvicted) {
		super(queueName, FIELD_KEY, canBeEvicted);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.DataBaseManager#getKey(java.lang.Object)
	 */
	@Override
	public String getKey(Job item) {
		return item.getId();
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.mongo.AbstractMongoManager#createObject(org.codehaus.jackson.map.ObjectMapper, java.lang.String)
	 */
	@Override
	public Job createObject(ObjectMapper mapper, String objFound) throws IOException {
		return mapper.readValue(objFound, Job.class);
	}

}
