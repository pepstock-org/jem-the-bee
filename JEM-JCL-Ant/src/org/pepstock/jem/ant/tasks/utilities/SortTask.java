/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   
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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.pepstock.jem.annotations.AssignDataDescription;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.utilities.sort.DefaultComparator;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.log.LogAppl;

/**
 * Is a utility (both a task ANT and a main program) that sort data.<br>
 */
public class SortTask extends AbstractIOTask {
	/**
	 * We multiply by two because later on someone insisted on counting the
	 * memory usage as 2 bytes per character. By this model, loading a file
	 * with 1 character will use 2 bytes.
	 */
	private static final int BYTES_X_CHAR = 2;
	
	private static final int DEFAULTMAXTEMPFILES = 1024;

	/**
	 * Key for the class to load to transform and load data  
	 */
	private static final String CLASS = "class";
	
	@AssignDataDescription(INPUT_DATA_DESCRIPTION_NAME)
	private static FileInputStream istream = null;
	
	@AssignDataDescription(OUTPUT_DATA_DESCRIPTION_NAME)
	private static FileOutputStream ostream = null;
	
	private static final int INITIAL_CAPACITY = 11;

	/**
	 * Empty constructor
	 */
	public SortTask() {
	}

	/**
	 * Divides the file into small blocks. If the blocks
	 * are too small, we shall create too many temporary files.
	 * If they are too big, we shall be using too much memory.
	 * 
	 * @param filetobesorted
	 * @param maxtmpfiles
	 * @return block size
	 * @throws IOException 
	 */
	public static long estimateBestSizeOfBlocks(FileInputStream filetobesorted, int maxtmpfiles) throws IOException {
		long sizeoffile = filetobesorted.getChannel().size() * BYTES_X_CHAR;
		/**
		 * We multiply by two because later on someone insisted on counting the
		 * memory usage as 2 bytes per character. By this model, loading a file
		 * with 1 character will use 2 bytes.
		 */
		// we don't want to open up much more than maxtmpfiles temporary files,
		// better run
		// out of memory first.
		long blocksize = sizeoffile / maxtmpfiles + (sizeoffile % maxtmpfiles == 0 ? 0 : 1);

		// on the other hand, we don't want to create many temporary files
		// for naught. If blocksize is smaller than half the free memory, grow
		// it.
		long freemem = Runtime.getRuntime().freeMemory();
		if (blocksize < freemem / BYTES_X_CHAR) {
			blocksize = freemem / BYTES_X_CHAR;
		}
		return blocksize;
	}

	/**
	 * This will simply load the file by blocks of x rows, then sort them
	 * in-memory, and write the result to temporary files that have to be merged
	 * later.
	 * 
	 * @param fileInput some flat file
	 * @param cmp string comparator
	 * @return a list of temporary flat files
	 * @throws IOException 
	 */
	public static List<File> sortInBatch(FileInputStream fileInput, Comparator<String> cmp) throws IOException {
		return sortInBatch(fileInput, cmp, DEFAULTMAXTEMPFILES, Charset.defaultCharset(), null);
	}

	/**
	 * This will simply load the file by blocks of x rows, then sort them
	 * in-memory, and write the result to temporary files that have to be merged
	 * later. You can specify a bound on the number of temporary files that will
	 * be created.
	 * 
	 * @param fileInput some flat file
	 * @param cmp string comparator
	 * @param maxtmpfiles maximal number of temporary files
	 * @param cs 
	 * @param Charset character set to use (can use Charset.defaultCharset())
	 * @param tmpdirectory location of the temporary files (set to null for
	 *            default location)
	 * @return a list of temporary flat files
	 * @throws IOException 
	 */
	public static List<File> sortInBatch(FileInputStream fileInput, Comparator<String> cmp, int maxtmpfiles, Charset cs, File tmpdirectory) throws IOException {
		List<File> files = new ArrayList<File>();
		BufferedReader fbr = new BufferedReader(new InputStreamReader(fileInput, cs));
		// in bytes
		long blocksize = estimateBestSizeOfBlocks(fileInput, maxtmpfiles);

		try {
			List<String> tmplist = new ArrayList<String>();
			String line = "";
			try {
				while (line != null) {
					// in bytes
					long currentblocksize = 0;
					// as long as you have enough memory
					while ((currentblocksize < blocksize) && ((line = fbr.readLine()) != null)) { 
						tmplist.add(line);
						// java uses 16 bits per character?
						currentblocksize += line.length() * BYTES_X_CHAR; 
					}
					files.add(sortAndSave(tmplist, cmp, cs, tmpdirectory));
					tmplist.clear();
				}
			} catch (EOFException oef) {
				// ignore
				LogAppl.getInstance().ignore(oef.getMessage(), oef);

				if (!tmplist.isEmpty()) {
					files.add(sortAndSave(tmplist, cmp, cs, tmpdirectory));
					tmplist.clear();
				}
			}
		} finally {
			fbr.close();
		}
		return files;
	}

	/**
	 * Sort a list and save it to a temporary file
	 * 
	 * @return the file containing the sorted data
	 * @param tmplist data to be sorted
	 * @param cmp string comparator
	 * @param cs charset to use for output (can use Charset.defaultCharset())
	 * @param tmpdirectory location of the temporary files (set to null for
	 *            default location)
	 * @throws IOException 
	 */
	public static File sortAndSave(List<String> tmplist, Comparator<String> cmp, Charset cs, File tmpdirectory) throws IOException {
		Collections.sort(tmplist, cmp);
		File newtmpfile = File.createTempFile("sortInBatch", "flatfile", tmpdirectory);
		newtmpfile.deleteOnExit();
		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newtmpfile), cs));
		try {
			for (String r : tmplist) {
				fbw.write(r);
				fbw.newLine();
			}
		} finally {
			fbw.flush();
			fbw.close();
		}
		return newtmpfile;
	}

	/**
	 * This merges a bunch of temporary flat files
	 * 
	 * @param files
	 * @param fileOutput 
	 * @param cmp 
	 * @param output file
	 * @return The number of lines sorted.
	 * @throws IOException 
	 */
	public static int mergeSortedFiles(List<File> files, FileOutputStream fileOutput, final Comparator<String> cmp) throws IOException {
		return mergeSortedFiles(files, fileOutput, cmp, Charset.defaultCharset());
	}

	/**
	 * This merges a bunch of temporary flat files
	 * 
	 * @param files
	 * @param fileOutput 
	 * @param cmp 
	 * @param cs 
	 * @param output file
	 * @param Charset character set to use to load the strings
	 * @return The number of lines sorted.
	 * @throws IOException 
	 */
	public static int mergeSortedFiles(List<File> files, FileOutputStream fileOutput, final Comparator<String> cmp, Charset cs) throws IOException {
		PriorityQueue<BinaryFileBuffer> pq = new PriorityQueue<BinaryFileBuffer>(INITIAL_CAPACITY, new Comparator<BinaryFileBuffer>() {
			public int compare(BinaryFileBuffer i, BinaryFileBuffer j) {
				return cmp.compare(i.peek(), j.peek());
			}
		});
		for (File f : files) {
			BinaryFileBuffer bfb = new BinaryFileBuffer(f, cs);
			pq.add(bfb);
		}
		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(fileOutput, cs));
		int rowcounter = 0;
		try {
			while (!pq.isEmpty()) {
				BinaryFileBuffer bfb = pq.poll();
				String r = bfb.pop();
				fbw.write(r);
				fbw.newLine();
				++rowcounter;
				if (bfb.empty()) {
					bfb.getBufferReader().close();
					// we don't need you anymore
					boolean isDeleted = bfb.getOriginalfile().delete();
	            	if (!isDeleted){
	            		// nop
	            	}
				} else {
					// add it back
					pq.add(bfb); 
				}
			}
		} finally {
			fbw.flush();
			fbw.close();
			for (BinaryFileBuffer bfb : pq){
				bfb.close();
			}
		}
		return rowcounter;
	}

	/**
	 * Main program, called by StepJava class. It reads from dd defined as INPUT and writes in OUTPUT one
	 * 
	 * @param args has a optional comaprator class to use to sort
	 * @throws ParseException if an exception occurs
	 * @throws ClassNotFoundException if an exception occurs
	 * @throws IllegalAccessException if an exception occurs
	 * @throws InstantiationException if an exception occurs
	 * @throws AntException if an exception occurs
	 * @throws NamingException if an exception occurs
	 * @throws IOException if an exception occurs
	 */
	@SuppressWarnings({"static-access", "unchecked" })
	public static void main(String[] args) throws ParseException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		// -class mandatory arg
		Option classArg = OptionBuilder.withArgName(CLASS).hasArg().withDescription("class of Comparator<String> to invoke reading the objects").create(CLASS);
		classArg.setRequired(false);

		// parses all arguments
		ArgumentsParser parser = new ArgumentsParser(SortTask.class.getName());
		parser.getOptions().add(classArg);
		
		// saves all arguments in common variables
		Properties properties = parser.parseArg(args);
		
		String classParam = properties.getProperty(CLASS);

		Comparator<String> comparator = null;
		if (classParam !=null) {
			Object objectTL = Class.forName(classParam).newInstance();
			if (objectTL instanceof Comparator<?>) {
				LogAppl.getInstance().emit(AntMessage.JEMA042I, objectTL.getClass().getName());
				comparator = (Comparator<String>) objectTL;
			} else {
				LogAppl.getInstance().emit(AntMessage.JEMA043I);
				comparator = new DefaultComparator();
				
			}
		} else {
			LogAppl.getInstance().emit(AntMessage.JEMA043I);
			comparator = new DefaultComparator();
		}
		
		int maxtmpfiles = DEFAULTMAXTEMPFILES;
		Charset cs = Charset.defaultCharset();

		if (istream.getChannel().size() > 0){
			List<File> l = sortInBatch(istream, comparator, maxtmpfiles, cs, null);
			mergeSortedFiles(l, ostream, comparator, cs);
		}

	}
}

/**
 * Buffer which contains data to sort
 */
class BinaryFileBuffer {
	public static final int BUFFERSIZE = 2048;
	private BufferedReader bufferReader;
	private File originalfile;
	private String cache;
	private boolean empty;

	/**
	 * 
	 * @param f
	 * @param cs
	 * @throws IOException
	 */
	public BinaryFileBuffer(File f, Charset cs) throws IOException {
		originalfile = f;
		bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(f), cs), BUFFERSIZE);
		reload();
	}
	
	/**
	 * @return the bufferReader
	 */
	public BufferedReader getBufferReader() {
		return bufferReader;
	}


	/**
	 * @return the originalfile
	 */
	public File getOriginalfile() {
		return originalfile;
	}

	/**
	 * @return true if empty, otherwise false
	 */
	public boolean empty() {
		return empty;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void reload() throws IOException {
		try {
			if ((this.cache = bufferReader.readLine()) == null) {
				empty = true;
				cache = null;
			} else {
				empty = false;
			}
		} catch (EOFException oef) {
			// ignore
			LogAppl.getInstance().ignore(oef.getMessage(), oef);
			
			empty = true;
			cache = null;
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		bufferReader.close();
	}

	/**
	 * @return null if is empty otherwise cache string
	 */
	public String peek() {
		if (empty()){
			return null;
		}
		return cache;
	}

	/**
	 * @return peek value string
	 * @throws IOException
	 */
	public String pop() throws IOException {
		String answer = peek();
		reload();
		return answer;
	}

}