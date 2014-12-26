/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Luca Cappello
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
package org.pepstock.jem.ant.validator.transformer;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Error listener activated when the XML check has been enabled for
 * ANT build files.
 * <br>
 * It always throw the received exception.
 * 
 * @author Luca Cappello
 * @version 1.0	
 *
 */
public class TransformerErrorListener implements ErrorListener {

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#error(javax.xml.transform.TransformerException)
	 */
	@Override
	public void error(TransformerException exception) throws TransformerException {
		throw exception;
	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#fatalError(javax.xml.transform.TransformerException)
	 */
	@Override
	public void fatalError(TransformerException exception) throws TransformerException {
		throw exception;
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.ErrorListener#warning(javax.xml.transform.TransformerException)
	 */
	@Override
	public void warning(TransformerException exception) throws TransformerException {
		throw exception;
	}
}