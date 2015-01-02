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
package org.pepstock.jem.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;


/**
 * Utility to zip a complete folder to a output stream
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ZipUtil { 

	/**
	 * To avoid any instatiation
	 */
	private ZipUtil() {
		
	}

	/** 
     * Creates a zip output stream  at the specified path with the contents of the specified directory. 
     * 
     * @param folder folder to zip 
     * @param zipOutputStream output stream. Usually a bytearray 
     * @throws IOException if any error occurs 
     */
    public static void createZip(File folder, OutputStream zipOutputStream) throws IOException { 
        BufferedOutputStream bufferedOutputStream = null; 
        ZipArchiveOutputStream zipArchiveOutputStream = null; 
        try { 
            bufferedOutputStream = new BufferedOutputStream(zipOutputStream); 
            zipArchiveOutputStream = new ZipArchiveOutputStream(bufferedOutputStream); 
            addFileToZip(zipArchiveOutputStream, folder); 
        } finally {
        	if (zipArchiveOutputStream != null){
        		zipArchiveOutputStream.finish(); 
        		zipArchiveOutputStream.close(); 
        	}
        	if (bufferedOutputStream != null){
        		bufferedOutputStream.close();
        	}
        	if (zipOutputStream != null){
        		zipOutputStream.close();
        	}
        } 

    } 

    /**
     * It calls at first time for main folder. 
     * 
     * @param zipArchiveOutputStream zip output stream 
     * @param path is relative path from main folder 
     * 
     * @throws IOException if any error occurs 
     */
    private static void addFileToZip(ZipArchiveOutputStream zipArchiveOutputStream, File file) throws IOException {
    	addFileToZip(zipArchiveOutputStream, file, null);
    }
    
    /** 
     * Creates a zip entry for all files and/or directories of main folder 
     * 
     * @param zipArchiveOutputStream zip output stream 
     * @param file The file being added 
     * @param path is relative path from main folder 
     * 
     * @throws IOException if any error occurs 
     */
    private static void addFileToZip(ZipArchiveOutputStream zipArchiveOutputStream, File file, String path) throws IOException { 
    	// at first call it is the folder, otherwise is the relative path
        String entryName = (path != null) ? path + file.getName() : file.getName();
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(file, entryName); 
        zipArchiveOutputStream.putArchiveEntry(zipEntry); 

        // if is a file, add the content to zip file
        if (file.isFile()) { 
            FileInputStream fInputStream = null; 
            try { 
                fInputStream = new FileInputStream(file); 
                IOUtils.copy(fInputStream, zipArchiveOutputStream); 
                zipArchiveOutputStream.closeArchiveEntry(); 
            } finally { 
                IOUtils.closeQuietly(fInputStream);
            } 
        } else {
        	// is a directory so it calls recursively all files in folder
            zipArchiveOutputStream.closeArchiveEntry(); 
            File[] children = file.listFiles(); 
            if (children != null) { 
                for (File child : children) { 
                	addFileToZip(zipArchiveOutputStream, child, entryName + "/");
                } 
            } 
        } 
    } 
}