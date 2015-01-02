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
package org.pepstock.catalog.gdg;

import java.io.File;
import java.io.IOException;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.catalog.DataSetType;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.util.Parser;

/**
 * Reads and writes GDG information, by root.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class GDGManager {

	/**
	 * To avoid any instantiation
	 */
	private GDGManager() {
		
	}

	/**
	 * Loads the generation of data description, after locking of root.<br>
	 * Works correctly if the root is locked.
	 * 
	 * @param ddImpl data description instance
	 * @throws IOException if I/O error occurs
	 */
	public static void load(DataDescriptionImpl ddImpl) throws IOException {
		// scans all dataset contained in dataset list
		for (DataSetImpl dataset : ddImpl.getDatasets()) {
			// checks if dataset is GDG, otherwise do nothing
			if (dataset.getType() == DataSetType.GDG) {
				// file in dataset is root of GDG
				// gets Root and loads it
				Root root = new Root(dataset.getFile());

				// if is a reference, it doesn't have to calculate anything
				if (ddImpl.getDataDescriptionReference() != null) {
					// creates the file instance using the directory of Root and
					// generation as file name
					File referFile = new File(root.getFile().getParentFile(), dataset.getRealName());
					if (!referFile.exists()){
						throw new IOException(GDGMessage.JEMD001E.toMessage().getFormattedMessage(ddImpl.getName(), dataset.getRealName()));
					}

				} else {
					// gets key by offset of dataset
					String key = GDGUtil.getGenerationIndex(root, dataset.getOffset());
					// gets generation file name
					String generation = GDGUtil.getGeneration(root, dataset.getOffset());

					// creates the file instance using the directory of Root and
					// generation as file name
					File newFile = new File(root.getFile().getParentFile(), generation);
					
					// if accesses to existing file
					if (!ddImpl.getDisposition().equalsIgnoreCase(Disposition.NEW)) {
						// if relative offset is greater than 0 is not possible
						// with relative index positive is mandatory disposition
						// NEW
						// being greater than last version of GDG
						if (dataset.getOffset() > 0){
							throw new IOException(GDGMessage.JEMD002E.toMessage().getFormattedMessage(ddImpl.getName(), generation, ddImpl.getDisposition()));
						}
						// the file must exist if wants to access to existing
						// file
						if (!newFile.exists()){
							throw new IOException(GDGMessage.JEMD001E.toMessage().getFormattedMessage(ddImpl.getName(), generation));
						}
					} else {
						// wants NEW generation so generation MUST be grater
						// than 0,
						// otherwise already exists and is not possible
						// to access in NEW mode
						if (dataset.getOffset() <= 0){
							throw new IOException(GDGMessage.JEMD003E.toMessage().getFormattedMessage(ddImpl.getName(), generation));
						}
						// if exists, exception because wants to create new
						// file!
						if (newFile.exists()){
							throw new IOException(GDGMessage.JEMD004E.toMessage().getFormattedMessage(ddImpl.getName(), generation));
						}
					}
					// set key, real name and file of generation
					dataset.setKey(key);
					dataset.setRealName(generation);
					dataset.setRealFile(newFile);
				}
			}
		}
	}

	/**
	 * Stores all information of used GDG inside of root file, during locking of
	 * root.<br>
	 * Works correctly if the root is locked.
	 * 
	 * @param ddImpl data description instance
	 * @throws IOException if I/O error occurs
	 */
	public static void store(DataDescriptionImpl ddImpl) throws IOException {
		// scans all dataset contained in dataset list
		for (DataSetImpl dataset : ddImpl.getDatasets()) {
			// checks if dataset is GDG, otherwise do nothing
			if (dataset.getType() == DataSetType.GDG) {
				// file in dataset is root of GDG
				// gets Root and loads it
				Root root = new Root(dataset.getFile());

				// checks if has real name. it must have
				if (dataset.hasRealName()) {
					/**
					 * IF NOT OPEN I don't change the index
					 */
					// checks if is a NEW generation and not a referback
					if ((dataset.getOffset() > 0) && (ddImpl.getDataDescriptionReference() == null)) {
						// checks if new real file exists.
						// if not, none open the file so DON'T change the list
						// of generations!!
						if (dataset.getRealFile().exists()) {
							// gets key
							String key = dataset.getKey();
							// gets the last version key
							String lastVersionKey = GDGUtil.getGenerationIndex(root, 0);
							// checks if key of file is greater than last
							// version
							// because could happen that has 2 new generation of
							// same GDG and set last version
							// methid could overrirde the right last version
							if (key.compareTo(lastVersionKey) > 0) {
								// if Key is last version, must compute the offset to 
								// last version because offset could be more than 1
								int newOffset = Parser.parseInt(key) - Parser.parseInt(lastVersionKey);
								// sets last version and version
								root.setLastVersion(newOffset);
							}
							root.setVersion(key, dataset.getRealName());
							// stores GDG data
							root.commit();
						}
					} else {
						
						// is not a new file so checks if the name is the same
						// otherwise changes it
						// is a key of root properties?
						if (root.hasVersion(dataset.getKey())) {
							// if yes, gets saved filename
							String savedVersion = root.getVersion(dataset.getKey());
							// checks if the filename is equals to saved
							// version. if no, save new filename
							if (!savedVersion.equalsIgnoreCase(dataset.getRealName())) {
								// sets new filename for key
								root.setVersion(dataset.getKey(), dataset.getRealName());
								// stores root data
								root.commit();
							}
						} else {
							// if is not inside the keys (strange!!! Should be)
							// adds the version to passed key
							root.setVersion(dataset.getKey(), dataset.getRealName());
							// stores root data
							root.commit();
						}
					}
				}
			}
		}
	}
}