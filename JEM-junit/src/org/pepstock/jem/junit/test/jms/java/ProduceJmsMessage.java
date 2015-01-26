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
package org.pepstock.jem.junit.test.jms.java;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * This class will show an example of how to use a JEM datasource
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class ProduceJmsMessage {

	/**
	 * Get jms resource "jem-jms" from JCL and send a message on
	 * {@value JmsServer#JUNIT_JMS_QUEUE_NAME}
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Connecting to jms server");
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.pepstock.jem.node.tasks.jndi.JemContextFactory");
		InitialContext context = new InitialContext(env);
		// get jms resource, note that jem-jms is the name of the resource
		// present in the JCL
		Object jndiObject = (Object) context.lookup("jem-jms");
		System.out.println("JNDI Object" + jndiObject);
		Context jmsContex = (Context) context.lookup("jem-jms");
		ConnectionFactory connFactory = (ConnectionFactory) jmsContex
				.lookup(JmsServer.JUNIT_JMS_CONNECTIONFACTORY_NAME);
		// Obtain a JMS connection from the factory
		Connection conn = connFactory.createConnection();
		conn.start();
		System.out.println("Connected to jms server");
		Session mySess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue myQueue = (javax.jms.Queue) jmsContex
				.lookup(JmsServer.JUNIT_JMS_QUEUE_NAME);
		MessageProducer myMsgProducer = mySess.createProducer(myQueue);
		TextMessage myTextMsg = mySess.createTextMessage();
		myTextMsg.setText(JmsServer.TEST_MESSAGE);
		System.out.println("Sending message " + myTextMsg.getText() + " to "
				+ JmsServer.JUNIT_JMS_QUEUE_NAME);
		myMsgProducer.send(myTextMsg);
		mySess.close();
		conn.close();
		System.out.println("Message sent");
	}
}
