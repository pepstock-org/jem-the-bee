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
package org.pepstock.jem.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.pepstock.jem.util.CharSet;

/**
 * Contains an array of bytes. Uses in collection or in serialization process.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class BytesArray implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int BUFFER_SIZE = 2048;

	private byte[] buf;
	
	private int count = 0;

	/**
	 * Constructs a new array of bytes, with default dimension of 32.
	 */
	public BytesArray() {
		this(BUFFER_SIZE);
	}

	
	
	/**
	 * Constructs a new array of bytes and the size is a parameter.
	 * 
	 * @param size size of array
	 */
	public BytesArray(int size) {
		if (size < 0) {
			throw new IllegalArgumentException(IoMessage.JEMI001E.toMessage().getFormattedMessage(size));
		}
		buf = new byte[size];
	}

	/**
	 * Writes len bytes from the specified byte array starting at offset off to
	 * internal array of bytes.
	 * 
	 * @param b the data
	 * @param off the start offset in the data.
	 * @param len the number of bytes to write.
	 */
	public void write(byte[] b, int off, int len) {
		if (areInvalidWriteParms(off, len) || (off > b.length) || ((off + len) > b.length)) {
			throw new IndexOutOfBoundsException(IoMessage.JEMI002E.toMessage().getFormattedMessage(off, len, b.length));
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > buf.length) {
			byte[] newbuf = new byte[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}
	
	/**
	 * Checks parameters used by write method
	 * @param off offset to start writing
	 * @param len amount of bytes
	 * @return <code>true</code> if parameter are wrong
	 */
	private boolean areInvalidWriteParms(int off, int len){
		return (off < 0) || (len < 0) || ((off + len) < 0);
	}

	/**
	 * Reads the data from a InputStream, loading the array of bytes.
	 * 
	 * @param in input stream to read
	 * @throws IOException on errors during reading
	 */
	public void readFrom(InputStream in) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			int n;
			while ((n = in.read(buffer)) > 0) {
				write(buffer, 0, n);
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Writes the array of bytes to a OutputStream
	 * 
	 * @param out output stream to write
	 * @throws IOException on errors during writing
	 */
	public void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	/**
	 * Resets the count field of this byte array to zero.
	 */
	public void reset() {
		count = 0;
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current size of
	 * array of bytes and the valid contents of the buffer have been copied into
	 * it.
	 * 
	 * @return the current contents of array of bytes.
	 */
	public byte[] toByteArray() {
		byte[] newbuf = new byte[count];
		System.arraycopy(buf, 0, newbuf, 0, count);
		return newbuf;
	}

	/**
	 * Gets a specific byte. Helpful to implement a custom inputstream
	 * @param index  index of buffer to get
	 * @return byte of buffer
	 * 
	 */
	public byte read(int index) {
		if ((index < 0) || (index >= count)) {
			throw new IndexOutOfBoundsException(IoMessage.JEMI002E.toMessage().getFormattedMessage(index, 1, count));
		}
		return buf[index];
	}
	
	/**
	 * Returns the current size of the array.
	 * 
	 * @return the value of the count field, which is the number of valid bytes.
	 */
	public int size() {
		return count;
	}

	/**
	 * Converts the array's contents into a string decoding bytes using the
	 * platform's default character set. The length of the new String is a
	 * function of the character set, and hence may not be equal to the size of
	 * the array.
	 * 
	 * @return String decoded from the array's contents.
	 */
	@Override
	public String toString() {
		return new String(buf, 0, count, CharSet.DEFAULT);
	}

	/**
	 * Converts the array's contents into a string by decoding the bytes using
	 * the specified charsetName. The length of the new String is a function of
	 * the charset, and hence may not be equal to the length of the byte array.
	 * 
	 * @param enc the name of a supported charset
	 * @return String decoded from the array's contents.
	 * @throws UnsupportedEncodingException if the named charset is not
	 *             supported
	 */
	public String toString(String enc) throws UnsupportedEncodingException {
		return new String(buf, 0, count, enc);
	}
}