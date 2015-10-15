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
package org.pepstock.jem.gwt.server.services;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.gfs.UploadedGfsChunkFile;
import org.pepstock.jem.gwt.server.UserInterfaceMessage;
import org.pepstock.jem.gwt.server.commons.DistributedTaskExecutor;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.executors.gfs.DeleteFile;
import org.pepstock.jem.node.executors.gfs.GetFile;
import org.pepstock.jem.node.executors.gfs.GetFilesList;
import org.pepstock.jem.node.executors.gfs.WriteChunk;
import org.pepstock.jem.node.security.LoggedUser;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.node.security.RegExpPermission;
import org.pepstock.jem.node.security.Roles;
import org.pepstock.jem.node.security.StringPermission;
import org.pepstock.jem.node.security.User;

/**
 * This service is able to read a folder and return the list of the files and
 * directories. Uses to view GFS.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class GfsManager extends DefaultService {

	/**
	 * Using the GFS type and starting path, reads on GFS all files and
	 * directories and return them. It checks also if the user has got the
	 * authorization to read or write that folders.
	 * 
	 * @param type
	 *            could a integer value
	 * @see GfsFile
	 * @param path
	 *            the folder (relative to type of GFS) to use to read files and
	 *            directories
	 * @param pathName
	 *            data payh name or null
	 * @return collections of files
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any error occurs
	 */
	public Collection<GfsFile> getFilesList(int type, String path,
			String pathName) throws ServiceMessageException {
		// checks user authentication
		// if not, this method throws an exception
		checkAuthentication();
		DistributedTaskExecutor<Collection<GfsFile>> task = new DistributedTaskExecutor<Collection<GfsFile>>(
				new GetFilesList(type, path, pathName), getMember());
		Collection<GfsFile> result = task.getResult();

		// checks authentication only if
		// the request is for data. All other file systems
		// are always available in READ
		if (type == GfsFileType.DATA) {
			// checks if the list is empty.
			// if not and after authorization check is empty, means that the use
			// doesn't have the right authorization
			boolean checkAuth = !result.isEmpty();
			for (Iterator<GfsFile> iter = result.iterator(); iter.hasNext();) {
				GfsFile file = iter.next();
				boolean match = match(file);
				// renoves the file because not authorized
				if (!match) {
					iter.remove();
				}
			}
			// if now is empty, it means that the user
			// doesn't have any authorization
			// and thorws an exception
			if (checkAuth && result.isEmpty()) {
				Subject currentUser = SecurityUtils.getSubject();
				Session shiroSession = currentUser.getSession();
				// gets user from session
				LoggedUser user = (LoggedUser) shiroSession
						.getAttribute(LoginManager.USER_KEY);
				String userid = (user != null) ? user.toString() : currentUser
						.toString();
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG008E,
						userid, path);
				throw new ServiceMessageException(
						UserInterfaceMessage.JEMG008E, userid, path);
			}
		}
		// return collections of GFS files
		return result;
	}

	/**
	 * Using the GFS type and file name, reads on GFS and return it. It checks
	 * also if the user has got the authorization to read that file.
	 * 
	 * @param type
	 *            could a integer value
	 * @see GfsFile
	 * @param file
	 *            the file name to retrieve
	 * @param pathName
	 *            data payh name or null
	 * @return content of file
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any error occurs
	 */
	public byte[] getFile(int type, String file, String pathName) throws ServiceMessageException {
		// checks authentication only if
		// the request is for data. 
		if (type == GfsFileType.DATA) {
			// creates the permission by file name
			String filesPermission = Permissions.FILES_READ + file;
			// checks user authentication
			// if not, this method throws an exception
			checkAuthorization(new StringPermission(filesPermission));
		} else {
			// checks if you can see the other filesystems
			checkGfsPermission(type);
		}
		DistributedTaskExecutor<byte[]> task = new DistributedTaskExecutor<byte[]>(new GetFile(type, file, pathName), getMember());
		return task.getResult();
	}
	
	/**
	 * Checks if the user has got the authorization to scan GFS
	 * 
	 * @param file
	 *            files to check
	 * @return true if authorized otherwise false
	 */
	private boolean match(GfsFile file) {
		// gets user
		Subject currentUser = SecurityUtils.getSubject();
		User userPrincipal = (User) currentUser.getPrincipal();

		// if administrator, always true
		if (currentUser.hasRole(Roles.ADMINISTRATOR)) {
			return true;
		}
		// checks if it has read. If yes OK!
		String filesPermission = Permissions.FILES_READ + file.getLongName();
		if (currentUser.isPermitted(new StringPermission(filesPermission))) {
			return true;
		}
		// check if it has write. If yes, OK!
		filesPermission = Permissions.FILES_WRITE + file.getLongName();
		if (currentUser.isPermitted(new StringPermission(filesPermission))) {
			return true;
		}
		// checks all file permissions
		// because it depends on path asked from user
		for (Permission permission : userPrincipal.getPermissions()) {
			String permString = permission.toString();
			if (permission instanceof RegExpPermission) {
				RegExpPermission regex = (RegExpPermission) permission;
				permString = regex.getPermissionPattern();
			}

			// for any general permissions to files, OK!
			if (permString.startsWith(Permissions.FILES)) {
				if (permString.equals(Permissions.FILES_STAR)
						|| permString.equals(Permissions.FILES_READ_ALL)
						|| permString.equals(Permissions.FILES_WRITE_ALL)) {
					return true;
				} else {
					// extract the permission pattern to check with path
					String filePattern = null;
					if (permString.startsWith(Permissions.FILES_READ)) {
						filePattern = StringUtils.removeStart(permString,
								Permissions.FILES_READ);
					} else if (permString.startsWith(Permissions.FILES_WRITE)) {
						filePattern = StringUtils.removeStart(permString,
								Permissions.FILES_WRITE);
					}
					// if permission pattern is longer than path, checks if
					// pattern start with path. if yes, means user can read
					if (filePattern != null
							&& filePattern.length() > file.getLongName()
									.length()
							&& filePattern.startsWith(file.getLongName())) {
						return true;
					}
				}
			}
		}
		// doesn't match! Not authorized
		return false;
	}

	/**
	 * 
	 * @param chunkFile
	 *            to upload
	 * @return true if the chunk write ended correctly, otherwise false 
	 * @throws ServiceMessageException
	 *             if any exception occurred during uploading
	 */
	public Boolean uploadChunk(UploadedGfsChunkFile chunkFile)
			throws ServiceMessageException {
		checkAuthentication();
		checkGfsPermission(chunkFile.getType());
		DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(
				new WriteChunk(chunkFile), getMember());
		return task.getResult();
	}

	/**
	 * Sued to delete a file from GFS (no data path)
	 * 
	 * @param type
	 *            could a integer value
	 * @see GfsFile
	 * @param file
	 *            the file name to retrieve
	 * @param pathName
	 *            data path name or null
	 * @return <code>true</code> if deleted
	 * @throws ServiceMessageException
	 * @throws Exception
	 *             if any error occurs
	 */
	public Boolean deleteFile(int type, String file, String pathName)
			throws ServiceMessageException {
		checkAuthentication();
		checkGfsPermission(type);

		DistributedTaskExecutor<Boolean> task = new DistributedTaskExecutor<Boolean>(
				new DeleteFile(type, file, pathName), getMember());
		return task.getResult();
	}
	/**
	 * 
	 * @param type
	 *            an integer that represent a specific folder on the GFS
	 * @see {@link GfsFileType}
	 * 
	 * @return
	 */
	private void checkGfsPermission(int type) throws ServiceMessageException {
		// gets user
		Subject currentUser = SecurityUtils.getSubject();
		// if administrator, always true
		if (currentUser.hasRole(Roles.ADMINISTRATOR)) {
			return;
		}
		String gfsPermission;
		boolean permitted = false;
		switch (type) {
		case GfsFileType.LIBRARY:
			gfsPermission = Permissions.GFS_LIBRARY;
			permitted = currentUser.isPermitted(new StringPermission(gfsPermission));
			break;
		case GfsFileType.SOURCE:
			gfsPermission = Permissions.GFS_SOURCES;
			permitted = currentUser.isPermitted(new StringPermission(gfsPermission));
			break;
		case GfsFileType.CLASS:
			gfsPermission = Permissions.GFS_CLASS;
			permitted = currentUser.isPermitted(new StringPermission(gfsPermission));
			break;
		case GfsFileType.BINARY:
			gfsPermission = Permissions.GFS_BINARY;
			permitted = currentUser.isPermitted(new StringPermission(gfsPermission));
			break;
		default:
			throw new ServiceMessageException(NodeMessage.JEMC264E);
		}
		// if not permitted throw exception
		if (!permitted) {
			Session shiroSession = currentUser.getSession();
			LoggedUser user = (LoggedUser) shiroSession
					.getAttribute(LoginManager.USER_KEY);
			// gets userid from session
			String userid = (user != null) ? user.toString() : currentUser
					.toString();
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG008E,
					userid, gfsPermission);
			throw new ServiceMessageException(UserInterfaceMessage.JEMG008E,
					userid, gfsPermission);
		}
	}

}
