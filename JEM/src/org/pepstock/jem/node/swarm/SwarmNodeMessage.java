/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Alessandro Zambrini
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
package org.pepstock.jem.node.swarm;

import org.pepstock.jem.log.Description;
import org.pepstock.jem.log.Message;
import org.pepstock.jem.log.MessageCode;
import org.pepstock.jem.log.MessageInterface;
import org.pepstock.jem.log.MessageLevel;

/**
 * It is an enumeration containing all the messages related to the swarm. <br>
 * It is a list of <code>UserInterfaceMessage</code>. <br>
 * Each <code>UserInterfaceMessage</code> in the list corresponds to a
 * <code>Message</code>. <br>
 * It implements {@link MessageInterface}
 * 
 * @see Message
 * @author Simone Businaro
 * @version 1.0
 */
public enum SwarmNodeMessage implements MessageInterface {

	/**
	 * "Swarm node is starting", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the Swarm Node is starting. A Swarm node is a node that belong to different environments")
	JEMO001I(1, "Swarm node is starting", MessageLevel.INFO),

	/**
	 * "The configuration of Swarm Environment is not enable.", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when active configuration for the Swarm Environment is not enable.")
	JEMO002I(2, "The configuration of Swarm Environment is not enable.", MessageLevel.INFO),
	/**
	 * "Swarm node is shutting down", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the Swarm Node is shutting down. A Swarm node is a node that belong to different environments")
	JEMO003I(3, "Swarm node is shutting down", MessageLevel.INFO),

	/**
	 * "Swarm node shutdown is completed", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the Swarm Node is shut down. A Swarm node is a node that belong to different environments")
	JEMO004I(4, "Swarm node shutdown is completed", MessageLevel.INFO),

	/**
	 * "Error while trying to notify the end of the routed job: {0}",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is an exception during the notification of the end of a job that was routed")
	JEMO005E(5, "Error while trying to notify the end of the routed job: {0}", MessageLevel.ERROR),

	/**
	 * "Notifying the of the execution for routed job {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the notification for the end of a routed job start.")
	JEMO006I(6, "Notifying the of the execution for routed job {0}", MessageLevel.INFO),

	/**
	 * "End of execution of routed job {0} is been notified", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the end of a routed job is been notified to the environment that route it")
	JEMO007I(7, "End of execution of routed job {0} is been notified", MessageLevel.INFO),

	/**
	 * "Member is null cannot process job {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when a ditributed task find a member down")
	JEMO008E(8, "Hazelcast instance is null cannot process job {0}", MessageLevel.ERROR),

	/**
	 * "Routing job {0}", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the routing process start.")
	JEMO009I(9, "Routing job {0}", MessageLevel.INFO),

	/**
	 * "Job {0} is been routed", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the routing process end.")
	JEMO010I(10, "Job {0} is been routed", MessageLevel.INFO),

	/**
	 * "Error while routing job: {0}", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is an exception during the routing process of a job")
	JEMO011E(11, "Error while routing job: {0}", MessageLevel.ERROR),

	/**
	 * "Removed job {0} from ROUTING QUEUE", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the system remove a job from ROUTING QUEUE")
	JEMO012I(12, "Removed job {0} from ROUTING QUEUE", MessageLevel.INFO),

	/**
	 * "Inserted job {0} in ROUTED QUEUE", MessageLevel.INFO
	 */
	@Description(explanation = "It occurs when the system insert the job in the ROUTED QUEUE")
	JEMO013I(13, "Inserted job {0} in ROUTED QUEUE", MessageLevel.INFO),

	/**
	 * "Unable to execute the service for nodes", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when there is an exception inside of swarm nodes service.<br>Contact JEM administrator.")
	JEMO014I(14, "Unable to execute the service for swarm nodes", MessageLevel.ERROR),

	/**
	 * "Unexpeted Exception", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when an unespected exception occurs")
	JEMO015E(15, "Unexpeted Exception", MessageLevel.ERROR),

	/**
	 * ""Unable to store node information in memory data map", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the swarm node is not able to store its data in Hazelcast.<br>Please contact your JEM administrators.")
	JEMO016E(16, "Unable to store swarm node information in memory data map", MessageLevel.ERROR),

	/**
	 * "Member removed \"{0}\" is not in {1} queue", MessageLevel.ERROR
	 */
	@Description(explanation = "Display the member which is not present on queue as expected. Internal error.")
	JEMO017E(17, "Member removed \"{0}\" is not in {1} queue", MessageLevel.ERROR),

	/**
	 * "Unable to store node information in memory data map", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when the web node is not able to store its data in Swarm Hazelcast.<br>Please contact your JEM administrators.")
	JEMO018E(18, "Unable to store node information in swarm memory data map", MessageLevel.ERROR),

	/**
	 * "Unable to start swarm because it's disable by configuration",
	 * MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs you try to start a node but the configuration is enable to false.<br>Please change the configuration and start again.")
	JEMO019E(19, "Unable to start swarm because it's disable by configuration", MessageLevel.ERROR),

	/**
	 * ""Unable to store node information in memory data map", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs you try to start a node but the configuration has no members defined.<br>Please change the configuration and start again.")
	JEMO020E(20, "Unable to start swarm because members of swarm configuration is empty", MessageLevel.ERROR),
	
	/**
	 * "Unable to get swarm configuration", MessageLevel.ERROR
	 */
	@Description(explanation = "It occurs when web app is not able to get sarm configuration to manage.<br>Please contact your JEM administrators.")
	JEMO021E(21, "Unable to get swarm configuration", MessageLevel.ERROR);

	/**
	 * The {@link Message} created in the constructor corresponding to an
	 * instance of <code>UserInterfaceMessage</code>.
	 * 
	 * @see Message
	 */
	private Message message;

	/**
	 * Constructor. It builds a <code>Message</code>. <br>
	 * This method uses the same parameter of the <code>Message</code>
	 * constructor and the specific ID: {@link #MESSAGE_ID}.
	 * 
	 * @param code identifier ID
	 * @param msg string to display. Could contain variables, resolved at
	 *            runtime
	 * @param level severity of log message
	 * @see Message
	 */
	private SwarmNodeMessage(int code, String messageContent, MessageLevel level) {
		this.message = new Message(code, MessageCode.SWARM_NODE.getCode(), messageContent, level);
	}

	/**
	 * It returns the {@link Message} corresponding to an
	 * <code>UserInterfaceMessage</code> instance.
	 * 
	 * @return the {@link Message} corresponding to an
	 *         <code>UserInterfaceMessage</code> instance.
	 */
	@Override
	public Message toMessage() {
		return this.message;
	}
}