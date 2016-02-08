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
package org.pepstock.jem.protocol.worker;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.hazelcast.IdGenerators;
import org.pepstock.jem.protocol.Message;
import org.pepstock.jem.protocol.ObjectFactory;
import org.pepstock.jem.protocol.ResponseWorker;
import org.pepstock.jem.protocol.Session;
import org.pepstock.jem.protocol.SessionStatus;
import org.pepstock.jem.util.JobIdGenerator;

import com.hazelcast.core.IdGenerator;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class JobIdWorker extends ResponseWorker {

	/**
	 * @param session
	 * @param request
	 */
	public JobIdWorker(Session session, Message request) {
		super(session, request);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.protocol.Worker#execute()
	 */
	@Override
	public void execute(Session session) throws JemException {
		if (!session.getSessionStatus().equals(SessionStatus.CONNECTED)) {
			// TODO fare nuovo messaggio
			throw new JemException("Invalid protocol at the current status " + session.getSessionStatus().name());
		}
		// creates a job ID asking to Hazelcast for a new long value
		IdGenerator generator = Main.getHazelcast().getIdGenerator(IdGenerators.JOB);
		long id = generator.newId();
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.leftPad(String.valueOf(id), JobIdGenerator.LONG_LENGTH, "0")).append('-').append(StringUtils.leftPad(String.valueOf(System.currentTimeMillis()), JobIdGenerator.LONG_LENGTH, "0"));
		
		Message msg = ObjectFactory.createMessage(getMessage(), sb.toString(), String.class);
		add(msg);
	}

}
