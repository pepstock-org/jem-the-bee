package org.pepstock.jem.junit.test.jms.java;

import java.io.File;
import java.io.IOException;

import net.timewalker.ffmq3.FFMQServerLauncher;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author Simone "Busy" Businaro
 * 
 *         Start a java process that is a jms server that will be used to test
 *         jms functionality
 * 
 */
public class JmsServer {

	/**
	 * 
	 */
	public static String JUNIT_JMS_QUEUE_NAME = "queue/JUNIT_JMS_QUEUE_NAME";

	/**
	 * 
	 */
	public static String JUNIT_JMS_CONNECTIONFACTORY_NAME = "factory/ConnectionFactory";

	/**
	 * 
	 */
	public static String PROVIDER_URL = "tcp://localhost:10002";
	/**
	 * 
	 */
	public static String TEST_MESSAGE = "Jem Queue Test Message";

	/**
	 * Run a Thread that will start a new process
	 * 
	 * @throws Exception
	 */
	public void start() {
		JMSThread jmsThread = new JMSThread();
		jmsThread.setDaemon(false);
		jmsThread.start();
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		JmsServer jmsServer = new JmsServer();
		jmsServer.start();
	}


	class JMSThread extends Thread {

		public JMSThread() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			File logsDir = new File("./jms/logs");
			File dataDir = new File("./jms/data");
			File destinationDir = new File("./jms/destinations");
			try {
				FileUtils.deleteDirectory(logsDir);
				logsDir.mkdir();
				FileUtils.deleteDirectory(dataDir);
				dataDir.mkdir();
				FileUtils.deleteDirectory(destinationDir);
				destinationDir.mkdir();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.setProperty("FFMQ_HOME","./jms");
			FFMQServerLauncher.main(new String[]{"-conf","./jms/conf/ffmq-server.properties"});
		}
	}
}
