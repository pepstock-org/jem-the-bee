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
package org.pepstock.jem.node.stats;

import java.io.File;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class DefaultTransformAndLoader implements TransformAndLoader {
	
	private boolean toDelete = true;

	
	/**
	 * @return the toDelete
	 */
	public boolean isToDelete() {
		return toDelete;
	}

	/**
	 * @param toDelete the toDelete to set
	 */
	public void setToDelete(boolean toDelete) {
		this.toDelete = toDelete;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.stats.TransformAndLoader#fileStarted(java.io.File)
	 */
	@Override
	public void fileStarted(File file) throws TransformAndLoaderException{
		setToDelete(true);
		LogAppl.getInstance().emit(NodeMessage.JEMC148I, file.getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.stats.TransformAndLoader#fileEnded(java.io.File)
	 */
	@Override
	public void fileEnded(File file) throws TransformAndLoaderException {
		if (isToDelete()){
			boolean removed = file.delete();
			if (removed){
				LogAppl.getInstance().emit(NodeMessage.JEMC149I, file.getAbsolutePath());
			} else { 
				LogAppl.getInstance().emit(NodeMessage.JEMC150W, file.getAbsolutePath());
			}
		} else {
			LogAppl.getInstance().emit(NodeMessage.JEMC151W, file.getAbsolutePath());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.stats.TransformAndLoader#loadSuccess(org.pepstock.jem.node.stats.Sample)
	 */
	@Override
	public void loadSuccess(Sample sample) throws TransformAndLoaderException {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.stats.TransformAndLoader#loadFailed(org.pepstock.jem.node.stats.Sample)
	 */
	@Override
	public void loadFailed(String record, int line, Exception exception) throws TransformAndLoaderException {
		setToDelete(false);
		LogAppl.getInstance().emit(NodeMessage.JEMC152E, String.valueOf(line), exception.getMessage());
	}

}