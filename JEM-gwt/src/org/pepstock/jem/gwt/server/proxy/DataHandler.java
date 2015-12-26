/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.server.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.LogAppl;

/**
 * Is the class which manage the communication between 2 sockets, from client to Hazelcast.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
abstract class DataHandler implements Runnable {
	
	// default buffer
	private static final int BUFFERSIZE = 10000;
	
	private static final int EOF = -1;

	private ProxyBean bean = null;
	
	private Socket input;

	private Socket output;
	
	/**
	 * Saves the proxy bean with socket information
	 * @param bean bean with socket information
	 */
	public DataHandler(ProxyBean bean) {
		this.bean = bean;
	}

	/**
	 * @return the bean
	 */
	public ProxyBean getBean() {
		return bean;
	}

	/**
	 * Gets the socket for incoming data
	 * @return the socket for incoming data
	 */
	abstract Socket getIncomingSocket();
	
	/**
	 * Gets the socket for outcoming data
	 * @return the socket for outcoming data
	 */
	abstract Socket getOutcomingSocket();
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
    @Override
    public void run() {
    	// saves the sockets
		this.input = getIncomingSocket();
		this.output = getOutcomingSocket();
		try {
			// gets stream to read and writes
			InputStream inStream = input.getInputStream();
			OutputStream outStream = output.getOutputStream();
			byte[] buffer = new byte[BUFFERSIZE];
			int n = 0;
			// reads until the socket is closed
			while (n != EOF) {
				// reads buffer
				n = inStream.read(buffer);
				if (n != EOF) {
					// writes data and flush
					outStream.write(buffer, 0, n);
					outStream.flush();
				}
			}
		} catch (SocketException se) {
			LogAppl.getInstance().ignore(se.getMessage(), se);
			//do nothing
			//the tcp connection could be closed
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG085E, ioe);
		} finally {
			shutdownHandler();
		}
	}
	
    /**
     * Stops the handler
     */
	public void stopHandler() {
		shutdownHandler();
	}
	
	/**
	 * Closes Input and Output streams
	 */
	private void shutdownHandler() {
		//Try to close sockets cleanly
		try {
			if (input != null){
				input.shutdownInput();
			}
		} catch (IOException ioe) {
			LogAppl.getInstance().ignore(ioe.getMessage(), ioe);
		}
		try {
			if (output != null) {
				output.shutdownOutput();
			}
		} catch (IOException ioe) {
			LogAppl.getInstance().ignore(ioe.getMessage(), ioe);
		}
	}
}