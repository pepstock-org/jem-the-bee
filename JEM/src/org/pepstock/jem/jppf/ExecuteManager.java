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
package org.pepstock.jem.jppf;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.StringRefAddr;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.server.protocol.JPPFTask;
import org.jppf.task.storage.MemoryMapDataProvider;
import org.jppf.utils.JPPFConfiguration;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.node.tasks.jndi.DataStreamReference;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

import com.thoughtworks.xstream.XStream;

/**
 * Is able to submit a job in JPPF grid and take the result.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ExecuteManager {

	private static JPPFClient JPPF_CLIENT = null;
	
	private static final List<File> TEMPORARY_FILES = new LinkedList<File>();
	
	/**
	 * To avoid any instantiation 
	 */
	private ExecuteManager() {
		
	}

	/**
	 * Submits a new job on JPPF grid
	 * 
	 * @param initialContext XML string serialization format of JNDI initialContext for JEM resources
	 * @throws JPPFMessageException if any exception occurs
	 * @throws JPPFException 
	 */
	public static synchronized void submit(String initialContext) throws JPPFMessageException{
		// clear temporary files loaded previously
		// due to is a static reference
		TEMPORARY_FILES.clear();
		try{
			JPPF_CLIENT = new JPPFClient();
			// Create a job
			JPPFJob job = createJob(initialContext);
			// execute a blocking job
			executeBlockingJob(job);
		} catch (NamingException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ030E, e);
		} catch (IOException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ031E, e);			
		} catch (JPPFException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ030E, e);
		} finally {
			JPPFConfiguration.getProperties().clear();
			if (JPPF_CLIENT != null){
				JPPF_CLIENT.close();
			}
		}
	}
	
	/**
	 * Create a JPPF job that can be submitted for execution.
	 * 
	 * @param runnable Class name of Runnable or JPPFTask to execute
	 * @param parallelTaskNumber Number of parallel task to submit
	 * @param xmlContext InitialContext, serialized in XML string
	 * @return an instance of the {@link org.jppf.client.JPPFJob JPPFJob} class.
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws JPPFException 
	 * @throws JPFFException
	 *             if an error occurs while creating the job or adding tasks.
	 */
	private static JPPFJob createJob(String xmlContext) throws JPPFMessageException, NamingException, IOException, JPPFException {
		XStream xstream = new XStream();
		// reads all properties
		String jobName =JPPFConfiguration.getProperties().getProperty(Keys.JEM_JOB_NAME);
		String taskName = JPPFConfiguration.getProperties().getProperty(Keys.JEM_TASK_NAME);
		String runnable = JPPFConfiguration.getProperties().getProperty(Keys.JEM_RUNNABLE);
		int parallelTaskNumber = JPPFConfiguration.getProperties().getInt(Keys.JEM_TASK_NUMBER);
		
		// creates a data provider to pass initial context and number of parallel task
		MemoryMapDataProvider provider = new MemoryMapDataProvider();
		provider.setValue(Keys.JEM_CONTEXT, xmlContext);
		provider.setValue(Keys.JEM_TASK_NUMBER, String.valueOf(parallelTaskNumber));
		
		// checks if CHUNK is set
		if (JPPFConfiguration.getProperties().containsKey(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION)){
			// gets data description
			String dataDescription = JPPFConfiguration.getProperties().getProperty(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION);
			File file = null;
			InitialContext ic = ContextUtils.getContext();
			
			// lookup for JNDI reference
			// needs file name because chunk is based on RandomFileAccess
			// that wants a FILE and not inputstream
			// so gets data description implementation object
			// to get REAL FILE
			NamingEnumeration<NameClassPair> list = ic.list(dataDescription);
			while(list.hasMore()){
				NameClassPair pair = list.next();
				// checks if is datastream
				// only datastreams are searched				
				if (pair.getName().equalsIgnoreCase(dataDescription) && pair instanceof DataStreamNameClassPair){
					DataStreamNameClassPair dsPair = (DataStreamNameClassPair) pair;
					DataStreamReference prevReference = (DataStreamReference)dsPair.getObject();
					// gets data description XML definition
					StringRefAddr sra = (StringRefAddr) prevReference.get(StringRefAddrKeys.DATASTREAMS_KEY);
					// creates datadescription implementatio to get file 
					DataDescriptionImpl ddImpl = (DataDescriptionImpl) xstream.fromXML(sra.getContent().toString());
					file = ddImpl.getDatasets().iterator().next().getRealFile();
					// leaves while
					break;
				}
			}
			// if file is null
			// data descritpion is not found
			if (file == null){
				throw new JPPFMessageException(JPPFMessage.JEMJ019E, dataDescription);
			}
			// calculated buffersize
			long bufferSize = file.length() / parallelTaskNumber;
			
			// using delimiter, creates chunk list
			List<ChunkDefinition> chunks = null;
			String delimiter = JPPFConfiguration.getProperties().getProperty(Keys.JEM_DELIMITER);
			String delimiterString = JPPFConfiguration.getProperties().getProperty(Keys.JEM_DELIMITER_STRING);
			
			if (delimiter != null) {
				// delimiter default LF
				char splitter = CharUtils.LF;
				// if delimiter is defined in BYTE mode
				// calculate char
				if (delimiter.toLowerCase().startsWith("0x")){
					delimiter = StringUtils.substringAfter(delimiter.toLowerCase(), "0x");
					splitter = (char)Integer.parseInt(delimiter, 16);
				} else {
					// if uses a escape java char
					splitter = StringEscapeUtils.unescapeJava(delimiter).charAt(0);
				}
				String displayDelimiter = Integer.toHexString((byte)splitter)+"("+splitter+")";
				LogAppl.getInstance().emit(JPPFMessage.JEMJ020I, displayDelimiter);

				// calculates chunks
				chunks = ChunksFactory.getChunksByDelimiter(file, bufferSize, splitter);
				provider.setValue(Keys.JEM_DELIMITER, splitter);
			} else if (delimiterString != null){
				LogAppl.getInstance().emit(JPPFMessage.JEMJ020I, delimiterString);
				// calculates chunks
				chunks = ChunksFactory.getChunksByDelimiter(file, bufferSize, delimiterString);
				provider.setValue(Keys.JEM_DELIMITER, delimiterString);
			} else {
				// if delimiter and delimiterString are missing, uses default delimiter (System.getProperty("line.separator")
				chunks = ChunksFactory.getChunksByDelimiter(file, bufferSize);
			}
			// changes parallel task
			// because chunk calculation tries to maintain the same 
			// number of task but usually there are less after
			// chunk list creation
			parallelTaskNumber = chunks.size();

			LogAppl.getInstance().emit(JPPFMessage.JEMJ021I, chunks);
			
			// serializes and saves on data provider all necessary data 
			provider.setValue(Keys.JEM_TASK_NUMBER, String.valueOf(parallelTaskNumber));
			provider.setValue(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION, dataDescription);	
			provider.setValue(Keys.JEM_CHUNKS, xstream.toXML(chunks));
		}
		
		LogAppl.getInstance().emit(JPPFMessage.JEMJ023I, parallelTaskNumber);
		
		// checks if merged data decritpion is an argument
		if (JPPFConfiguration.getProperties().containsKey(Keys.JEM_MERGED_DATA_DESCRIPTION)){
			// loads a list with all temporary files for tasks
			// to use to write partial output
			for (int i=0; i<parallelTaskNumber; i++){
				File temp = File.createTempFile("task["+i+"]-merge", ".jemjppf");
				temp.deleteOnExit();
				TEMPORARY_FILES.add(temp);
				LogAppl.getInstance().emit(JPPFMessage.JEMJ022I, temp.getAbsolutePath(), i);
			}
			// sets data provider
			provider.setValue(Keys.JEM_MERGED_DATA_DESCRIPTION, JPPFConfiguration.getProperties().getProperty(Keys.JEM_MERGED_DATA_DESCRIPTION));
			provider.setValue(Keys.JEM_TEMPORARY_FILES, xstream.toXML(TEMPORARY_FILES));
		}
		
		// create a JPPF job
		JPPFJob job = new JPPFJob(provider);

		// give this job a readable unique id that we can use to monitor and
		// manage it.
		job.setName(jobName+"."+taskName);
		
		// Checks what instance has been past to distribute on grid
		try {
			Object clazz = Class.forName(runnable).newInstance();
			if (clazz instanceof JPPFTask){
				for (int i=0; i<parallelTaskNumber; i++){
					JPPFTask t = new WrapperJPPFTask((JPPFTask)clazz);
					// add a task to the job.
					job.addTask(t);
				}
			} else if (clazz instanceof Runnable){
				for (int i=0; i<parallelTaskNumber; i++){
					JPPFTask t = new RunnableTask((Runnable)clazz);
					// add a task to the job.
					job.addTask(t);
				}
			} else {
				throw new JPPFMessageException(JPPFMessage.JEMJ002E, runnable, Runnable.class.getName(), JPPFTask.class.getName());
			}
		} catch (InstantiationException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ002E, e, runnable, Runnable.class.getName(), JPPFTask.class.getName());
		} catch (IllegalAccessException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ002E, e, runnable, Runnable.class.getName(), JPPFTask.class.getName());
		} catch (ClassNotFoundException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ002E, e, runnable, Runnable.class.getName(), JPPFTask.class.getName());
		}

		// there is no guarantee on the order of execution of the tasks,
		// however the results are guaranteed to be returned in the same order
		// as the tasks.
		return job;
	}

	/**
	 * Execute a job in blocking mode. The application will be blocked until the
	 * job execution is complete.
	 * 
	 * @param job
	 *            the JPPF job to execute.
	 * @throws Exception
	 *             if an error occurs while executing the job.
	 */
	private static void executeBlockingJob(final JPPFJob job) throws JPPFMessageException {
		// set the job in blocking mode.
		job.setBlocking(true);

		LogAppl.getInstance().emit(JPPFMessage.JEMJ003I, job.getName());
		
		// Submit the job and wait until the results are returned.
		// The results are returned as a list of JPPFTask instances,
		// in the same order as the one in which the tasks where initially added
		// the job.
		List<JPPFTask> results;
		try {
			results = JPPF_CLIENT.submit(job);
		} catch (Exception e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ028E, e);
		}

		// if merging is asked for, it does
		if (JPPFConfiguration.getProperties().containsKey(Keys.JEM_MERGED_DATA_DESCRIPTION)){
			// merges the results
			try {
				mergeExecutionResults(results);
			} catch (NamingException e) {
				throw new JPPFMessageException(JPPFMessage.JEMJ029E, e, e.getMessage());
			} catch (IOException e) {
				throw new JPPFMessageException(JPPFMessage.JEMJ029E, e, e.getMessage());
			}
		} else {
			// process the results
			processExecutionResults(results);
		}
	}

	/**
	 * Process the execution results of each submitted task.
	 * 
	 * @param results
	 *            the tasks results after execution on the grid.
	 */
	private static void processExecutionResults(final List<JPPFTask> results) throws JPPFMessageException {
		JPPFMessageException resultException = null;

		// process the results
		for (JPPFTask task : results) {
			// if the task execution resulted in an exception
			if (task.getException() != null) {
				LogAppl.getInstance().emit(JPPFMessage.JEMJ005E, task.getId(), task.getException().getMessage());
				resultException = new JPPFMessageException(JPPFMessage.JEMJ005E, task.getException(), task.getId(), task.getException().getMessage());
			} else {
				if (task.getResult() != null){
					LogAppl.getInstance().emit(JPPFMessage.JEMJ006I, task.getId(), task.getResult());
				} else {
					LogAppl.getInstance().emit(JPPFMessage.JEMJ007I, task.getId());
				}
			}
		}
		// if we have an exception
		// it will be thrown
		if (resultException != null){
			throw resultException;
		}
	}

	/**
	 * Merges the output results of all task, only if all tasks ended correctly
	 *  
	 * @param results list of tasks ended
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws Exception if any exception occurs
	 */
	private static void mergeExecutionResults(final List<JPPFTask> results) throws JPPFMessageException, NamingException, IOException {
		// sorts to be sure
		// task are ordered by position
		Collections.sort(results, new Comparator<JPPFTask>() {
			@Override
			public int compare(JPPFTask o1, JPPFTask o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		// process the results. If there is any exception, throws it
		for (JPPFTask task : results) {
			// if the task execution resulted in an exception
			if (task.getException() != null) {
				LogAppl.getInstance().emit(JPPFMessage.JEMJ005E, task.getId(), task.getException().getMessage());
				throw new JPPFMessageException(JPPFMessage.JEMJ005E, task.getException(), task.getId(), task.getException().getMessage());
			} 
		}
		
		LogAppl.getInstance().emit(JPPFMessage.JEMJ024I);
		
		// gets merge data description
		String mergedDataDescription = JPPFConfiguration.getProperties().getProperty(Keys.JEM_MERGED_DATA_DESCRIPTION);
		InitialContext ic = ContextUtils.getContext();
		// gets outputstream by JNDI
		Object object = ic.lookup(mergedDataDescription);
		OutputStream os = (OutputStream)object;
		BufferedOutputStream bos =  new BufferedOutputStream(os);
		try {
			// scans for merging
			for (JPPFTask task : results) {
				// gets temporary file written by task
				File file = TEMPORARY_FILES.get(task.getPosition());
				// reads and writes data
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileUtils.copyFile(file, baos);
				bos.write(baos.toByteArray());
				
				LogAppl.getInstance().emit(JPPFMessage.JEMJ025I, file.getAbsolutePath(), baos.size());
				baos.close();
			}
			LogAppl.getInstance().emit(JPPFMessage.JEMJ026I);
		} finally {
			// close
			if (bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
}
