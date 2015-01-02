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
package org.pepstock.jem.jppf;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;

import org.jppf.server.protocol.JPPFTask;
import org.pepstock.jem.annotations.SetFields;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;

import com.thoughtworks.xstream.XStream;

/**
 * IS a JPPF task that wraps a Runnable object to execute on JPPF grid.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 *
 */
public class RunnableTask extends JPPFTask {
	
	private static final long serialVersionUID = 1L;

	private Runnable runnable = null; 
	
	/**
	 * Constructs task using the runnable object to execute
	 * 
	 * @param runnable using the runnable object to execute
	 * 
	 */
	public RunnableTask(Runnable runnable) {
		this.runnable = runnable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		// sets standard error and output to remote output stream, with charset
		try {
			System.setErr(new PrintStream(new RemoteStandardOutputStream(this), true, CharSet.DEFAULT_CHARSET_NAME));
			System.setOut(new PrintStream(new RemoteStandardOutputStream(this), true, CharSet.DEFAULT_CHARSET_NAME));
		} catch (UnsupportedEncodingException e) {
			// debug
			LogAppl.getInstance().ignore(e.getMessage(), e);
			setException(e);
			return;
		}

		// Sets task id. necessry to log the results
		setId("Task["+getPosition()+"]");
		// loads TASK DATA 
		TaskData tData = new TaskData();
		// sets Index
		tData.setIndex(getPosition());
		// takes from data provider the task count
		Object count = null;
		try {
			count = getDataProvider().getValue(Keys.JEM_TASK_NUMBER);
		} catch (Exception e) {
			// debug
			LogAppl.getInstance().ignore(e.getMessage(), e);
			setException(e);
			return;
		}
		// sets total task
		int tasks = Parser.parseInt(count.toString(), 1);
		tData.setTotal(tasks);
		// stores on ThreadLocal
		TaskInfo.setTaskData(tData);
		
		// serializes this JPPFtask
		// because it must be passed to reference
		// to perform remote I/O
		XStream xstream = new XStream();
		String thisXml = xstream.toXML(this);


		// de-serializes JNDI initial context 
		InitialContext ic = null;
		try {
			// reads data from data provider
			String chunks = getValueFromDataProvider(Keys.JEM_CHUNKS);
			String dataDescription = getValueFromDataProvider(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION);
			String mergedDataDescription = getValueFromDataProvider(Keys.JEM_MERGED_DATA_DESCRIPTION);

			// extract temporary file
			// if there is the complete list
			File temporayFile = null;
			String temporaryFilesXml = getValueFromDataProvider(Keys.JEM_TEMPORARY_FILES);
			if (temporaryFilesXml !=null){
				@SuppressWarnings("unchecked")
				List<File> temporayFiles = (LinkedList<File>) xstream.fromXML(temporaryFilesXml);
				temporayFile = temporayFiles.get(getPosition());
			}

			// initial context is in data provider
			String context =  getValueFromDataProvider(Keys.JEM_CONTEXT).toString();
			ic = (InitialContext)xstream.fromXML(context);

			// gets all references because is to change the definition
			// for data descriptions. This change is mandatory to set the remote
			// stream and to add on reference the JPPF task (necessary to call remotely task
			// to read and write files.
			NamingEnumeration<NameClassPair> list = ic.list("");
			while(list.hasMore()){
				NameClassPair pair = list.next();
				// checks if is datastream
				// only datastreams are changed
				if (pair instanceof DataStreamNameClassPair){
					DataStreamNameClassPair dsPair = (DataStreamNameClassPair) pair;
					DataStreamReference prevReference = (DataStreamReference)dsPair.getObject();
					// gets data description XML defintion
					// adding it to a new reference, for remote access
					StringRefAddr sra = (StringRefAddr) prevReference.get(StringRefAddrKeys.DATASTREAMS_KEY);
					RemoteDataStreamReference reference = new RemoteDataStreamReference();
					reference.add(sra);
					// adds task
					reference.add(new StringRefAddr(StringRefAddrKeys.JPPFTASK_KEY, thisXml));
					// if there is chunk data, adds chunk data
					if (chunks != null){
						reference.add(new StringRefAddr(StringRefAddrKeys.CHUNKS_KEY, chunks));
						reference.add(new StringRefAddr(StringRefAddrKeys.CHUNKABLE_DATA_DESCRIPTION_KEY, dataDescription));
					}
					// if there is merging and temporary data, adds data for merging
					if ((mergedDataDescription != null) && (temporayFile != null)){
						reference.add(new StringRefAddr(StringRefAddrKeys.MERGED_DATA_DESCRIPTION_KEY, mergedDataDescription));
						reference.add(new StringRefAddr(StringRefAddrKeys.TEMPORARY_FILE_KEY, temporayFile.getAbsolutePath()));
					}
					// re-bind the object inside the JNDI context
					ic.rebind(dsPair.getName(), reference);
				}
			}
			// sets this threadlocal to have
			// a unique reference for thread of JNDI context
			UniqueInitialContext.setContext(ic);

			System.out.println(JPPFMessage.JEMJ004I.toMessage().getFormattedMessage("Task["+getPosition()+"]", InetAddress.getLocalHost().getHostAddress()));
			// sets fields using annotation
			SetFields.applyByAnnotation(runnable, true);
			// executes the runnable
			runnable.run();
		} catch (UnknownHostException e) {
			// debug
			LogAppl.getInstance().ignore(e.getMessage(), e);
			saveException(e);
		} catch (NamingException e) {
			// debug
			LogAppl.getInstance().ignore(e.getMessage(), e);
			saveException(e);
		} catch (Exception e) {
			// debug
			LogAppl.getInstance().ignore(e.getMessage(), e);
			saveException(e);
		}
		// flush all output and error
		System.out.flush();
		System.err.flush();
	}
	
	/**
	 * Saves generated Exception to JPPF task to forward afterwards
	 * @param e Exception to save
	 */
	private void saveException(Exception e){
		if (getException() == null){
			setException(e);
		}
	}
	
	/**
	 * Extracts String value from data provider by key.
	 * @param key key to search
	 * @return string value
	 * @throws Exception if any exception occurs accessing data provider
	 */
	private String getValueFromDataProvider(String key) throws JemException{
		try {
			Object value = getDataProvider().getValue(key);
			if (value != null){
				return value.toString();
			}
			return null;
		} catch (Exception e) {
			throw new JemException(e);
		}
	}
}
