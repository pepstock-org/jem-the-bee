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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.JobIdGenerator;
import org.pepstock.jem.util.Numbers;

import com.thoughtworks.xstream.XStream;

/**
 * Factory to create message with all parameters
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public final class ObjectFactory {
	
	// a kbytes
	private static final int KB = 1024;
	
	/**
	 * Maximum amount of bytes that you can transfer
	 */
	public static final int MAXIMUM_BUFFER_SIZE = KB * Numbers.N_8;
	
	private static final String DEFAULT_MESSAGE = "jem";
	
	private static final XStream XSTREAM = new XStream();

	/**
	 * to avoid any instantiation
	 */
	private ObjectFactory() {
	}

	/**
	 * Creates a message starting from a existing message
	 * @param message message source
	 * @param object object to set into message
	 * @param clazz type of object
	 * @return message to be sent
	 */
	public static <T> Message createMessage(Message message, T object, Class<T> clazz){
		return createMessage(message.getId(), message.getCode(), object, clazz);
	}
	
	/**
	 * Creates a message only with the code. ID is random, the object is a constant string
	 * @param code code of message
	 * @return message to be sent
	 */
	public static <T> Message createMessage(int code){
		return createMessage(JobIdGenerator.createRandomJobId(), code);
	}

	/**
	 * Creates a message with code and a specific ID. the object is a constant string
	 * @param id id of message
	 * @param code code of message 
	 * @return message to be sent
	 */
	public static <T> Message createMessage(String id, int code){
		return createMessage(id, code, DEFAULT_MESSAGE, String.class);
	}

	/**
	 * Creates a message with a random ID
	 * @param code code of message
	 * @param object object to set into message
	 * @param clazz type of object
	 * @return message to be sent
	 */
	public static <T> Message createMessage(int code, T object, Class<T> clazz){
		return createMessage(JobIdGenerator.createRandomJobId(), code, object, clazz);
	}

	/**
	 * Creates a message with its ID, code, object and type.
	 * @param id id of message
	 * @param code code of message
	 * @param object object to set into message
	 * @param clazz type of object
	 * @return message to be sent
	 */
	public static <T> Message createMessage(String id, int code, T object, Class<T> clazz){
		// creates the message
		Message message = new Message();
		message.setId(id);
		message.setCode(code);
		// serializes the object into message
		toMessage(message, object, clazz);
		return message;
	}
	
	/**
	 * Extracts the object from a message
	 * @param message message container of object
	 * @param clazz type of object
	 * @return object inside the message
	 * @throws JemException if any error occurs 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromMessage(Message message, Class<T> clazz) throws JemException{
		// checks if the value XML is not null
		if (message.getValue() == null){
			throw new JemException("value is null!");
		}
		// gets object
		Object obj = XSTREAM.fromXML(message.getValue());
		// if exception, throws it
		if (obj instanceof JemException){
			JemException exception = (JemException)obj;
			throw exception;
		}
		// returns object
		return (T) obj ;
	}

	/**
	 * Inserts teh object into message
	 * @param message message conatiner of object
	 * @param object object to set into message
	 * @param clazz type of object
	 */
	public static <T> void toMessage(Message message, T object, Class<T> clazz){
		String xml = XSTREAM.toXML(object);
		message.setValue(xml);
	}

	/**
	 * Serialize the object into XML and then into a byte array
	 *  * The format of protocol is:
	 * <br>
	 * 4 bytes (int) for the code of message 
	 * <br>
	 * 4 bytes (int) for the length of the content 
	 * <br>
	 * 8 bytes (long) for the prefix of ID 
	 * <br>
	 * 8 bytes (long) for the suffix of ID
	 * <br>
	 * n bytes (char) for the content of message 
	 *
	 * @param message message to serialize
	 * @return a byte buffer to write to client or server
	 * @throws JemException if any exception occurs
	 */
	public static ByteBuffer serialize(Message message) throws JemException{
		try {
			// if no object and no exception
			// the message is not valid
			if (message.getValue() == null){
				throw new JemException("Value inside the message is null ");
			}
			// if code is not set, error
			if (message.getCode() == Integer.MIN_VALUE){
				throw new JemException("Code inside the message is wrong");
			}
			String value = message.getValue();
			int code = message.getCode();
			// gets the length in bytes
			int length = value.length();
			
			// max length of bytes to sotre the content
			// is max buffer size minus 24 bytes:
			// 4 bytes for code of message
			// 4 bytes for length of message
			// 8 bytes for prefix of ID
			// 8 bytes for suffix of ID
			if (length > (MAXIMUM_BUFFER_SIZE - Numbers.N_24)){
				// if more then available
				// it cuts the message
				length = MAXIMUM_BUFFER_SIZE - Numbers.N_24;
				value = StringUtils.left(value, length);
			}
			
			// sets the ID
			long prefixId = Message.NO_ID;
			long suffixId = Message.NO_ID;
			if (message.getId() != null){
				// The ID of message has got the same format of JOB ID
				// therefore it uses the same message format to get the LONG values
				MessageFormat jobIdFormat = new MessageFormat(JobIdGenerator.JOBID_FORMAT);
				// parses
				Object[] idsLong = jobIdFormat.parse(message.getId());
				// stores the values
				prefixId = (Long)idsLong[0];
				suffixId = (Long)idsLong[1];
			}
			// length of buffer is: 4 bytes for code, 8 for prefix ID, 8 for suffix ID, 4 for length and rest of object
			ByteBuffer buffer = ByteBuffer.allocate(length + Numbers.N_24);
			// clears buffer
			buffer.clear();
			
			// puts all data
			buffer.putInt(code);
			buffer.putLong(prefixId);
			buffer.putLong(suffixId);
			buffer.putInt(length);
			// uses UTF-8
			buffer.put(value.getBytes(CharSet.DEFAULT));
			return buffer;
		} catch (Exception e) {
			if (e instanceof JemException){
				throw (JemException)e;
			}
			throw new JemException(e);
		}
	}
	
	public static List<Message> deserialize(ByteBuffer byteBuffer) throws JemException{
		List<Message> list = new LinkedList<Message>();
		// IMPORTANT: set position at the beginning of buffer
		byteBuffer.position(0);
		int code = byteBuffer.getInt();
		while(code > 0){
			Message msg = deserialize(byteBuffer, code);
			list.add(msg);
			code = byteBuffer.getInt();
		}
		return list;
	}
	
	/**
	 * Reads from a byte buffer and stores all information inside the object itself
	 * @param byteBuffer buffer of bytes reads from the socket
	 * @throws JemException if any error occurs
	 */
	private static Message deserialize(ByteBuffer byteBuffer, int code) throws JemException{
		try {
			Message message = new Message();
			// reads the code
			message.setCode(code);
			// reads the IDs long values
			long prefixId = byteBuffer.getLong();
			long suffixId = byteBuffer.getLong();

			// if are not -1, created the ID in string format
			if (prefixId != Message.NO_ID && suffixId != Message.NO_ID){
				message.setId(JobIdGenerator.createJobId(prefixId, suffixId));
			}
			// reads the length of content
			int length = byteBuffer.getInt();
			// if valid length
			if (length > 0){
				// creates the array using the length of result
				byte[] array = new byte[length];
				// reads the bytes array from buffer
				byteBuffer.get(array);
				// creates string with content
				String value = new String(array, CharSet.DEFAULT);
				message.setValue(value);
			}
			return message;
		} catch (Exception e) {
			if (e instanceof JemException){
				throw (JemException)e;
			}
			throw new JemException(e);
		}
	}

}
