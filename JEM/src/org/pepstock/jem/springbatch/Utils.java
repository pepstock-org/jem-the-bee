/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A set of methods to encode and decode XML content element, to get attributes
 * from element (mandatory and optional)
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Utils {

	private static final String ENCODING_UTF8 = "UTF-8";

	private static final String ENCODING_DEFAULT = ENCODING_UTF8;
	
	/**
	 * To avoid any instantiation
	 */
	private Utils() {
		
	}

	/**
	 * Encode by URL encoder, by default encoding (UTF8)
	 * 
	 * @param s string to encode
	 * @return string encoded
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String encode(String s) throws UnsupportedEncodingException {
		return encode(s, ENCODING_DEFAULT);
	}

	/**
	 * Encode by URL encoder, using enconding charset parameter
	 * 
	 * @param s string to encode
	 * @param charEncoding charset encoding
	 * @return string encoded
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String encode(String s, String charEncoding) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, charEncoding);
	}

	/**
	 * Decode by URL decoder, by default decoding (UTF8)
	 * 
	 * @param s string to decode
	 * @return string decoded
	 * @throws UnsupportedEncodingException if decoding is not supported occurs
	 */
	public static String decode(String s) throws UnsupportedEncodingException {
		return decode(s, ENCODING_DEFAULT);
	}

	/**
	 * Decode by URL decoder, using deconding charset parameter
	 * 
	 * @param s string to decode
	 * @param charEncoding charset decoding
	 * @return string decoded
	 * @throws UnsupportedEncodingException if decoding is not supported occurs
	 */
	public static String decode(String s, String charEncoding) throws UnsupportedEncodingException {
		return (s == null) ? null : URLDecoder.decode(s, charEncoding);
	}

	/**
	 * Returns the value of mandatory attribute
	 * 
	 * @param attrs list of elements attributes
	 * @param attribute attribute to extract
	 * @return value of attribute
	 * @throws SAXException if attribute to search is not present, an exception
	 *             occurs
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String getMandatory(Attributes attrs, String attribute) throws SAXException, UnsupportedEncodingException {
		return getMandatory(attrs, attribute, ENCODING_DEFAULT);
	}

	/**
	 * Returns the value of mandatory attribute
	 * 
	 * @param attrs list of elements attributes
	 * @param attribute attribute to extract
	 * @param charEncoding charset encoding
	 * @return value of attribute
	 * @throws SAXException if attribute to search is not present, an exception
	 *             occurs
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String getMandatory(Attributes attrs, String attribute, String charEncoding) throws SAXException, UnsupportedEncodingException {
		// search attribute, in lower case and in upper case if not found
		String value = attrs.getValue(attribute);
		if (value == null){
			value = attrs.getValue(attribute.toUpperCase());
		}
		if (value == null){
			value = attrs.getValue(attribute.toLowerCase());
		}

		// no attribute so Exception
		if (value == null){
			throw new SAXException("Attribute '" + attribute + "\' is missing");
		}
		// decode and return
		return decode(value, charEncoding);
	}

	/**
	 * Returns the value of optional attribute
	 * 
	 * @param attrs list of elements attributes
	 * @param attribute attribute to extract
	 * @param defaultValue if attribute is not set in element, this value will
	 *            be returned
	 * @return value of attribute
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String getOptional(Attributes attrs, String attribute, String defaultValue) throws UnsupportedEncodingException {
		return getOptional(attrs, attribute, defaultValue, ENCODING_DEFAULT);
	}

	/**
	 * Returns the value of optional attribute
	 * 
	 * @param attrs list of elements attributes
	 * @param attribute attribute to extract
	 * @param defaultValue if attribute is not set in element, this value will
	 *            be returned
	 * @param charEncoding charset encoding
	 * @return value of attribute
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String getOptional(Attributes attrs, String attribute, String defaultValue, String charEncoding) throws UnsupportedEncodingException {
		// search attribute, in lower case and in upper case if not found
		String value = attrs.getValue(attribute);
		if (value == null){
			value = attrs.getValue(attribute.toUpperCase());
		}
		if (value == null){
			value = attrs.getValue(attribute.toLowerCase());
		}
		// if not found, return defaultValue
		return value == null ? defaultValue : decode(value, charEncoding);
	}

	/**
	 * 
	 * @param attrs list of elements attributes
	 * @param attribute attribute to extract
	 * @return value of attribute, if attribute is not set in element,
	 *         <code>null</code> will be returned
	 * @throws UnsupportedEncodingException if encoding is not supported occurs
	 */
	public static String getOptional(Attributes attrs, String attribute) throws UnsupportedEncodingException {
		return getOptional(attrs, attribute, null);
	}
}