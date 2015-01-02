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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.jppf.server.protocol.JPPFTask;
import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.tasks.jndi.StringRefAddrKeys;

import com.thoughtworks.xstream.XStream;

/**
 * JNDI object factory. It looks inside the context all information save for data description
 * and returns a stream (input or output) using the definition found on JCL.<br>
 * It is used inside of JPPF grid and uses Remote stream to read and write data.<br>
 * It needs a JPPF task to execute callable on client.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class RemoteDataStreamFactory implements ObjectFactory {
	private XStream xstream = new XStream();
	
	private JPPFTask task = null;
	
	private List<ChunkDefinition> chunks = null;
	
	private String chunkableDataDescription = null;
	
	private String mergedDataDescription = null;

	private String temporaryFile = null;
	
	/**
	 * Searches inside the reference the data description implementation object,
	 * serialized in XML format.<br>
	 * Using the the attributes of Data Description, returns a InputStream or
	 * OutputStream, based on the disposition used.
	 * 
	 * @param object - The possibly null object containing location or reference
	 *            information that can be used in creating an object.
	 * @param name The name of this object relative to nameCtx, or null if no
	 *            name is specified.
	 * @param ctx The context relative to which the name parameter is specified,
	 *            or null if name is relative to the default initial context.
	 * @param env The possibly null environment that is used in creating the
	 *            object.
	 * @return a stream to use to write or read
	 * @throws Exception occurs if there is any error
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JPPFMessageException {
		try {
			// checks if object passed is a Reference
			if (object instanceof Reference) {
				Reference reference = (Reference) object;

				// reads from context JPPFTask serialized in XML
				String taskXml = getValue(reference, StringRefAddrKeys.JPPFTASK_KEY);
				task = (JPPFTask) xstream.fromXML(taskXml);

				// reads list of chunk, if exist
				// it is in XML format
				String chunksXml = getValue(reference, StringRefAddrKeys.CHUNKS_KEY);
				if (chunksXml != null){
					chunks = (LinkedList<ChunkDefinition>)xstream.fromXML(chunksXml);
				}

				// reads data description for inputstream
				chunkableDataDescription = getValue(reference, StringRefAddrKeys.CHUNKABLE_DATA_DESCRIPTION_KEY);
				// reads data description for outputstream
				mergedDataDescription = getValue(reference, StringRefAddrKeys.MERGED_DATA_DESCRIPTION_KEY);
				// temporary file (on JEM machine) that it must write remotely if it wants
				// merges result of all tasks.
				temporaryFile = getValue(reference, StringRefAddrKeys.TEMPORARY_FILE_KEY);


				// get XML string representation of data description impl,
				// wrapped by a data stream
				RefAddr datastreamsAddr = reference.get(StringRefAddrKeys.DATASTREAMS_KEY);

				// creates DataDescritpionImpl object using XStream (used to
				// serialize)
				DataDescriptionImpl ddImpl = (DataDescriptionImpl) xstream.fromXML((String) datastreamsAddr.getContent());

				if (ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)) {
					// is SHR then is a INPUTSTREAM
					if (ddImpl.getDatasets().size() > 1) {
						// if more than 1 datasets, then prepares a
						// SequenceInputStream
						List<InputStream> inputStreams = new ArrayList<InputStream>();
						for (DataSetImpl dstream : ddImpl.getDatasets()) {
							inputStreams.add(getInputStream(dstream, ctx));
						}
						return new SizableSequenceInputStream(inputStreams);
					} else {
						// if only 1 datasets, then prepares a InputStream
						DataSetImpl dstream = ddImpl.getDatasets().get(0);
						// checks if is equals to chuck data description
						if (chunkableDataDescription != null && ddImpl.getName().equalsIgnoreCase(chunkableDataDescription)){
							return getInputStream(dstream, ctx, true);
						}
						return getInputStream(dstream, ctx);
					}
				} else {
					// is not SHR then return OutputStream
					// multi datasets not implemented in output (means with
					// disposition OLD, MOD or NEW)
					DataSetImpl dstream = ddImpl.getDatasets().get(0);
					// checks if is equals to merged data description
					if (mergedDataDescription != null && ddImpl.getName().equalsIgnoreCase(mergedDataDescription)){
						return getOutputStream(ddImpl, dstream, ctx, temporaryFile);
					}
					return getOutputStream(ddImpl, dstream, ctx);
				}
			}
			// if we arrive here, object is not a reference
			throw new JPPFMessageException(NodeMessage.JEMC133E, object.getClass().getName());
		} catch (IOException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ033E, e);
		} catch (NamingException e) {
			throw new JPPFMessageException(JPPFMessage.JEMJ032E, e);
		}
	}

	/**
	 * Returns a RemoteInputStream, using the real name of DataSetImpl.<br>
	 * RemoteInputStream is not chunkable.
	 * 
	 * @param ds Dataset implementation
	 * @param ctx JNDI context
	 * @return a remote input stream or remote chunk input stream
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws Exception occurs if there is any error
	 */
	private InputStream getInputStream(DataSetImpl ds, Context ctx) throws JPPFMessageException, IOException, NamingException {
		return getInputStream(ds, ctx, false);
	}
	
	/**
	 * Returns a RemoteInputStream, using the real name of DataSetImpl.<br>
	 * Passing true, defines the RemoteInputStream as chunkable
	 * 
	 * @param ds Dataset implementation
	 * @param ctx JNDI context
	 * @param isChunkable if true, is a input stream chunkable 
	 * @return a remote input stream or remote chunk input stream
	 * @throws IOException 
	 * @throws NamingException 
	 * @throws Exception occurs if there is any error
	 */
	private InputStream getInputStream(DataSetImpl ds, Context ctx, boolean isChunkable) throws JPPFMessageException, IOException, NamingException {
		// use real file, created to manage GDG
		if (ds.getType() != DataSetType.RESOURCE){
			// if is chunkable
			// returns a chunkable input stream
			if ((chunks != null) && (isChunkable)){
				ChunkDefinition chunk = chunks.get(task.getPosition());
				return new RemoteChunkedInputStream(task, ds.getRealFile(), chunk);
			}
			return new RemoteInputStream(task, ds.getRealFile());
		} else {
			Object object = ctx.lookup(ds.getDataSource());
			if (object instanceof InputStream){
				return (InputStream)object;
			}
			throw new JPPFMessageException(NodeMessage.JEMC132E, ds.getDataSource());
		}
	}

	/**
	 * Returns a RemoteOutputStream, using the real name of DataSetImpl.<br>
	 * Sets in append mode if disposition equals a MOD, otherwise not.<br>
	 * Is a NOT mergable output stream.
	 * 
	 * @param dd data description instance
	 * @param ds dataset instance
	 * @return a RemoteOutputStream
	 * @throws NamingException 
	 * @throws Exception occurs if there is any error
	 */
	private OutputStream getOutputStream(DataDescriptionImpl dd, DataSetImpl ds, Context ctx) throws JPPFMessageException, NamingException {
		return getOutputStream(dd, ds, ctx, null);
	}

	/**
	 * Returns a RemoteOutputStream, using the real name of DataSetImpl.<br>
	 * Sets in append mode if disposition equals a MOD, otherwise not.<br>
	 * If a mergable temporary file is passed, then is a mergable output stream.
	 * 
	 * @param dd data description instance
	 * @param ds dataset instance
	 * @param merging temporary file name of JEM node
	 * @return a RemoteOutputStream
	 * @throws NamingException 
	 * @throws Exception occurs if there is any error
	 */
	private OutputStream getOutputStream(DataDescriptionImpl dd, DataSetImpl ds, Context ctx, String temporaryFile) throws JPPFMessageException, NamingException {
		// INLINE dataset is not supported in output
		if (ds.getType() == DataSetType.INLINE) {
			throw new JPPFMessageException(NodeMessage.JEMC135E);
		} else if (ds.getType() == DataSetType.RESOURCE) {
			Object object = ctx.lookup(ds.getDataSource());
			if (object instanceof OutputStream){
				return (OutputStream)object;
			}
			throw new JPPFMessageException(NodeMessage.JEMC134E, ds.getDataSource());
		} else {
			// if has temporary, return a ouput stream to write temporary file remotely
			if (temporaryFile != null){
				return new RemoteOutputStream(task,  new File(temporaryFile), false);
			} else {
				// use real file, created to manage GDG
				return new RemoteOutputStream(task, ds.getRealFile(), dd.getDisposition().equalsIgnoreCase(Disposition.MOD));
			}
		}
	}

	/**
	 * Utility to read reference address on JNDI
	 * @param reference JNDI reference
	 * @param key key to search
	 * @return String value
	 */
	private String getValue(Reference reference, String key){
		RefAddr a = reference.get(key);
		if (a != null){
			return (String)a.getContent();
		}
		return null;
	}
}