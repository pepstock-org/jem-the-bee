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
package org.pepstock.jem.protocol;

import java.nio.ByteBuffer;
import java.text.MessageFormat;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.protocol.message.EndedJobMessage;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.JobIdGenerator;
import org.pepstock.jem.util.Numbers;

import com.thoughtworks.xstream.XStream;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 * @param <T> Object type to serialize
 */
public abstract class Message<T> {
	
	/**
	 * Default id to be ignored when is set -1. 
	 * It uses for synch communication 
	 */
	private static final long NO_ID = -1L;
	
	private static final XStream XSTREAM = new XStream();
	
	private int length = 0;
	
	private String id = null;
	
	private T object = null;
	
	/**
	 * @return the code
	 */
	public abstract int getCode();

	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [code=" + getCode() + ", id=" + id + ", length=" + length + ", object=" + object + "]";
	}

	/**
	 * 
	 * @return
	 * @throws JemException
	 */
	public ByteBuffer serialize() throws JemException{
		try {
			if (object == null){
				throw new JemException("Object inside the message is null");
			}
			String xml = XSTREAM.toXML(object);
			length = xml.length();
			
			long prefixId = NO_ID;
			long suffixId = NO_ID;
			if (id != null){
				// parse the job id
				MessageFormat jobIdFormat = new MessageFormat(JobIdGenerator.JOBID_FORMAT);
				Object[] idsLong = jobIdFormat.parse(id);
				prefixId = (Long)idsLong[0];
				suffixId = (Long)idsLong[1];
			}
			
			if (this instanceof EndedJobMessage){
				System.err.println(getId());
				System.err.println(xml);
			}
			
			// length of buffer is: 4 bytes for code, 8 for prefix ID, 8 for suffix ID, 4 for length and rest of object
			ByteBuffer buffer = ByteBuffer.allocate(length * Numbers.N_2 + Numbers.N_24);
			buffer.clear();
			buffer.putInt(getCode());
			buffer.putLong(prefixId);
			buffer.putLong(suffixId);
			buffer.putInt(length);
			buffer.put(xml.getBytes(CharSet.DEFAULT));
			return buffer;
		} catch (Exception e) {
			if (e instanceof JemException){
				throw (JemException)e;
			}
			throw new JemException(e);
		}
	}
	
	/**
	 * 
	 * @param byteBuffer
	 * @return
	 * @throws JemException
	 */
	@SuppressWarnings("unchecked")
	public T deserialize(ByteBuffer byteBuffer) throws JemException{
		try {
			byteBuffer.position(0);
			int code = byteBuffer.getInt();
			if (code != getCode()){
				throw new JemException("Invalid protocol: code received is "+code+" but should be "+getCode());
			}
			long prefixId = byteBuffer.getLong();
			long suffixId = byteBuffer.getLong();

			if (prefixId != NO_ID && suffixId != NO_ID){
				id = JobIdGenerator.createJobId(prefixId, suffixId);
			}
			length = byteBuffer.getInt();
			if (length > 0){
				byte[] array = new byte[length];
				byteBuffer.get(array);
				String xml = new String(array, CharSet.DEFAULT);
				object = (T)XSTREAM.fromXML(xml);
			}
			return object;
		} catch (Exception e) {
			if (e instanceof JemException){
				throw (JemException)e;
			}
			throw new JemException(e);
		}
	}

}
