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

import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.SubmitPreJob;
import org.pepstock.jem.protocol.Message;
import org.pepstock.jem.protocol.ObjectFactory;
import org.pepstock.jem.protocol.ResponseWorker;
import org.pepstock.jem.protocol.Session;
import org.pepstock.jem.protocol.SessionStatus;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class SubmitJobWorker extends ResponseWorker {

	/**
	 * @param session
	 * @param request
	 */
	public SubmitJobWorker(Session session, Message request) {
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
		PreJob preJob = ObjectFactory.fromMessage(getMessage(), PreJob.class);
		SubmitPreJob.submit(Main.getHazelcast(), preJob);
	}

}
