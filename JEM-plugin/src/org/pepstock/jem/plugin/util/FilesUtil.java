/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.Job;

/**
 * Utility to create and write files on temporary folder.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class FilesUtil {
	
	/**
	 * Temporary folder set by systems
	 */
	public static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

	/**
	 * Private constructor to avoid new instantiations 
	 */
	private FilesUtil() {
	}

	/**
	 * Writes the JCl content of passed job into temporary folder.
	 * 
	 * @param job job instance
	 * @return file created in temporary folder
	 * @throws IOException if any error occurs
	 */
	public static final File writeJcl(Job job) throws IOException{
		return writeToTempFile(job.getName()+".xml", job.getJcl().getContent());
	}

	/**
	 * Writes a content in a file in temporary folder.
	 * 
	 * @param fileName file name in temporary folder
	 * @param content content of file, in string format, to write 
	 * @return file created in temporary folder
	 * @throws IOException if any error occurs
	 */
	public static final File writeToTempFile(String fileName, String content) throws IOException{
		File file = new File(TEMP_DIRECTORY, fileName);
		FileUtils.write(file, content);
		return file;
	}
	
	/**
	 * Writes a byte stream in a file in temporary folder.
	 * 
	 * @param fileName file name in temporary folder
	 * @param content content of file, in string format, to write 
	 * @return file created in temporary folder
	 * @throws IOException if any error occurs
	 */
	public static final File writeToTempFile(String fileName, byte[] content) throws IOException{
		File file = new File(TEMP_DIRECTORY, fileName);
		FileUtils.writeByteArrayToFile(file, content);
		return file;
	}
}
