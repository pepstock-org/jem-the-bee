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
package org.pepstock.jem.node.tasks.jndi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.node.NodeMessage;

import com.thoughtworks.xstream.XStream;

/**
 * JNDI object factory. It looks inside the context all information save for data description
 * and returns a stream (input or output) using the definition found on JCL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataStreamFactory implements ObjectFactory {
	private XStream xstream = new XStream();

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
	@Override
	public Object getObjectInstance(Object object, Name name, Context ctx, Hashtable<?, ?> env) throws JNDIException {
		try {
			// checks if object passed is a Reference
			if (object instanceof Reference) {
				Reference reference = (Reference) object;

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
						return new SequenceInputStream(Collections.enumeration(inputStreams));
					} else {
						// if only 1 datasets, then prepares a InputStream
						DataSetImpl dstream = ddImpl.getDatasets().get(0);
						return getInputStream(dstream, ctx);
					}
				} else {
					// is not SHR then return OutputStream
					// multi datasets not implemented in output (means with
					// disposition OLD, MOD or NEW)
					DataSetImpl dstream = ddImpl.getDatasets().get(0);
					return getOutputStream(ddImpl, dstream, ctx);
				}
			}
			// if we arrive here, object is not a reference
			throw new JNDIException(NodeMessage.JEMC133E, object.getClass().getName());
		} catch (FileNotFoundException e) {
			throw new JNDIException(NodeMessage.JEMC232E, e);
		} catch (NamingException e) {
			throw new JNDIException(NodeMessage.JEMC133E, e, object.getClass().getName());
		}
	}

	/**
	 * Returns a FileInputStream, using the real name of DataSetImpl
	 * 
	 * @param ds Dataset implementation
	 * @return a FileInputstream
	 * @throws FileNotFoundException 
	 * @throws NamingException 
	 * @throws Exception occurs if there is any error
	 */
	private InputStream getInputStream(DataSetImpl ds, Context ctx) throws JNDIException, FileNotFoundException, NamingException {
		// use real file, created to manage GDG
		if (ds.getType() !=  DataSetType.RESOURCE){
			return new FileInputStream(ds.getRealFile());
		} else {
			Object object = ctx.lookup(ds.getDataSource());
			if (object instanceof InputStream){
				return (InputStream)object;
			}
			throw new JNDIException(NodeMessage.JEMC132E, ds.getDataSource());
		}
	}

	/**
	 * Returns a FileOutputStream, using the real name of DataSetImpl.<br>
	 * Sets in append mode if dispostion equals a MOD, otherwise not.
	 * 
	 * @param dd data decription instance
	 * @param ds dataset instance
	 * @return a FileOutputStream
	 * @throws NamingException 
	 * @throws FileNotFoundException 
	 * @throws Exception occurs if there is any error
	 */
	private OutputStream getOutputStream(DataDescriptionImpl dd, DataSetImpl ds, Context ctx) throws JNDIException, NamingException, FileNotFoundException {
		// INLINE dataset is not supported in output
		if (ds.getType() == DataSetType.INLINE) {
			throw new JNDIException(NodeMessage.JEMC135E);
		} else if (ds.getType() == DataSetType.RESOURCE) {
			Object object = ctx.lookup(ds.getDataSource());
			if (object instanceof OutputStream){
				return (OutputStream)object;
			}
			throw new JNDIException(NodeMessage.JEMC134E, ds.getDataSource());
		} else {
			// use real file, created to manage GDG
			return new FileOutputStream(ds.getRealFile(), dd.getDisposition().equalsIgnoreCase(Disposition.MOD));
		}
	}

}