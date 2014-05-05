/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.junit.test.jppf.java;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.lang.SystemUtils;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class StartDriverAndNode {
	
	static Process PROCESS = null;

	/**
	 * 
	 */
	public StartDriverAndNode() {
		try {
			Socket socket = new Socket(InetAddress.getLocalHost(), 11111);
			socket.close();
			System.out.println("====================================\nJPPF driver and node already started\n====================================");
			return;
		} catch (IOException e) {
		}
		System.out.println("====================================\nStart JPPF driver\n====================================");
		JPPFThread td = new JPPFThread("driver");
		td.setDaemon(false);
		td.start();
		
		System.out.println("====================================\nStart JPPF node\n====================================");
		JPPFThread tn = new JPPFThread("node");
		tn.setDaemon(false);
		tn.start();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new StartDriverAndNode();
	}

	class JPPFThread extends Thread {
		
		private String type = null;

		/**
		 * @param type type of JPPF server to start: "driver" or "node"
		 * 
		 */
		public JPPFThread(String type) {
			this.type = type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			ProcessBuilder builder = new ProcessBuilder();
			File dir = new File("./jppf/"+type);
			File log = new File(dir, "jppf-"+type+".log");
			log.delete();
			
			builder.directory(dir);
			
			
			
			String command = ("driver".equalsIgnoreCase(type)) ? "startDriver" : "startNode";
			
			if (SystemUtils.IS_OS_WINDOWS){
				builder.command("cmd.exe", "/C", command+".bat");
			} else {
				builder.command("bash", "-c", command+".sh");
			}
			try {
	            PROCESS = builder.start();
	            if (PROCESS.waitFor() != 0){
	            	throw new IOException("Command not started");
	            }
            } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }

		}
	}

}
