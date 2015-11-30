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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;

/**
 * Engine to manage the proxy between hazelcast client and hazelcast cluster.<br>
 * It uses a thread pool to perform data handler.<br>
 * Every client connection uses 2 threads of pool: 1 to read and 1 to write
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class Engine extends Thread {
	
	// default port
	// FIXME to be configured
	private static final int DEFAULT_PORT = 9701;
	
	// contains all runnables data handler
	private static final BlockingQueue<Runnable> RUNNABLES = new SynchronousQueue<Runnable>();
	
	private final ThreadPoolDelegate delegate = new ThreadPoolDelegate();
	
	private final Map<String, ProxyBean> PROXY_BEANS = new ConcurrentHashMap<String, ProxyBean>();

	private boolean shutdown = false;
	
	private ServerSocket serverSocket;
	
	/**
	 * Shutdown the engine when web context is destroyed
	 */
    public void shutdown() {
    	// sets shutdown 
    	shutdown = true;
	    try {
	    	// close server socket
	    	// no further connections
	        serverSocket.close();
        } catch (IOException e) {
	        LogAppl.getInstance().ignore(e.getMessage(), e);
        }
	    // shutdown the pool
	    delegate.shutdown();
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
    @Override
    public void run() {
		try {
			// open socket in listening
			serverSocket = new ServerSocket(DEFAULT_PORT);
		} catch (IOException ioe) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG078E, ioe, String.valueOf(DEFAULT_PORT));
			return;
		}
		// if here is listening
		LogAppl.getInstance().emit(UserInterfaceMessage.JEMG077I, String.valueOf(DEFAULT_PORT));
		while (true) {
			try {
				// gets incoming requests
				Socket incoming = serverSocket.accept();
                try {
                	// creates a proxy bean
                	// with socket info
                	ProxyBean bean = ProxyBeanFactory.createProxyBean(incoming);
                	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG079I, bean);
                	// saves on a map
					PROXY_BEANS.put(bean.getId(), bean);
					// creates 2 data handlers
					IncomingDataHandler idh = new IncomingDataHandler(bean);
					OutcomingDataHandler odh = new OutcomingDataHandler(bean);
					// puts on pool to be executed
					delegate.execute(odh);
					delegate.execute(idh);
                } catch (JemException e) {
                	// if any exception to creates proxy bean
                	// it closes the incoming socket
	                incoming.shutdownInput();
	                incoming.shutdownOutput();
	                LogAppl.getInstance().emit(UserInterfaceMessage.JEMG080E, e, incoming.getRemoteSocketAddress());
                }
			} catch (IOException ioe) {
				// if is shutting down gracefully, log correctly
				if (shutdown) {
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG081I);
					return;
				} 
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG082E, ioe);
			}
		}
	}


	/**
	 * Thread pool cache which reuses the thread as possible.
	 * After execution, removes teh proxy bean from map.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 3.0
	 */
	class ThreadPoolDelegate extends ThreadPoolExecutor {
		
		private static final int CORE_POOL_SIZE = 0;
		
		private static final int MAX_POOL_SIZE = Integer.MAX_VALUE;
		
		private static final int KEEP_ALIVE = 10;

		/**
		 * Creates a thread pool with no core thread and infinite maximum.
		 */
		public ThreadPoolDelegate() {
			super(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, RUNNABLES);
		}
		
		/* (non-Javadoc)
		 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
		 */
        @Override
        protected void afterExecute(Runnable runnable, Throwable exception) {
        	// executes super
			super.afterExecute(runnable, exception);
			// if datahandler
			if (runnable instanceof DataHandler){
				// gets data handler
				DataHandler handler = (DataHandler) runnable;
				// if map contains proxy bean
				if (PROXY_BEANS.remove(handler.getBean().getId()) != null){
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG084I, handler.getBean());
				}
			} else {
				// FIXME
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG083E, runnable.getClass().getName());
			}
		}
	}

}
