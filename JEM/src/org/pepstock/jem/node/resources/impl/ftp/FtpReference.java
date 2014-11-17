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
package org.pepstock.jem.node.resources.impl.ftp;

import java.io.InputStream;
import java.util.List;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceLoaderReference;
import org.pepstock.jem.node.resources.ResourcePropertiesUtil;

/**
 * Sets constants for JNDI for FTPClient oject. It uses Apache common net classes.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class FtpReference extends ResourceLoaderReference {

	private static final long serialVersionUID = 1L;

	/**
	 * Custom FTP factory
	 */
	public static final String FTP_FACTORY = FtpFactory.class.getName();
	
	/**
	 * could return an InputStream or OutputStream. It sets only input
	 */
	public static final String FTP_OBJECT = InputStream.class.getName();
	
	/**
	 * Creates a JNDI reference for FTP purposes
	 */
	public FtpReference() {
		super(FTP_OBJECT, FTP_FACTORY, null);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.ResourceLoaderReference#loadResource(org.pepstock.jem.node.resources.Resource, java.util.List, java.lang.String)
	 */
	@Override
	public void loadResource(Resource res, List<DataDescriptionImpl> ddList, String sourceName) throws JemException {
		boolean asInputStream = false;
		// checks if I have a dataset linked to a datasource
		for (DataDescriptionImpl ddImpl : ddList) {
			for (DataSetImpl ds: ddImpl.getDatasets()){
				// if has resource linked
				// checks if the name is the same
				if (ds.getType() == DataSetType.RESOURCE && ds.getDataSource().equalsIgnoreCase(sourceName)){
					asInputStream = true;
					// sets file name (remote one)
					ResourcePropertiesUtil.addProperty(res, FtpResourceKeys.REMOTE_FILE,  ds.getName());
					// sets if wants to have a OutputStream or InputStream using
					// disposition of dataset
					if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.SHR)){
						ResourcePropertiesUtil.addProperty(res, FtpResourceKeys.ACTION_MODE, FtpResourceKeys.ACTION_WRITE);
					} else {
						ResourcePropertiesUtil.addProperty(res, FtpResourceKeys.ACTION_MODE, FtpResourceKeys.ACTION_READ);
					}
				}
			}
		}
		ResourcePropertiesUtil.addProperty(res, FtpResourceKeys.AS_INPUT_STREAM, Boolean.toString(asInputStream));
	}
}