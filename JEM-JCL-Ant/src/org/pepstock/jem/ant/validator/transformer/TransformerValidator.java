/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   LucaC
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.ValidationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.CharSet;

/**
 * The Class TransformerValidator.
 * 
 * @author LucaC
 * @version 1.0
 */
public class TransformerValidator extends FileAlterationListenerAdaptor {

	private String xsltFile = null;
	private Transformer transformer = null;
	private FileAlterationMonitor monitor = null;
	private FileAlterationObserver observer = null;

	/**
	 * Load and validate xslt file
	 * 
	 * @param xsltValidatorFile the xslt file
	 * 
	 * @throws ValidationException
	 */
	public void load(String xsltValidatorFile) throws ValidationException {

		// activate file listner only at startup
		verifyXsltFile(xsltValidatorFile);
		createFileListner(xsltValidatorFile);

		// load transformer
		renewTransformer(xsltValidatorFile);
	}

	/**
	 * Renew transformer.
	 * 
	 * @param xsltvalidatorFile the xsltvalidator file
	 * @throws ValidationException the validation exception
	 */
	private synchronized void renewTransformer(String xsltvalidatorFile) {
		try {
			transformer = createTransformer(xsltvalidatorFile);
			LogAppl.getInstance().emit(AntMessage.JEMA051I, xsltvalidatorFile);
		} catch (ValidationException e) {
			LogAppl.getInstance().emit(AntMessage.JEMA057E, e, xsltvalidatorFile);
		}
	}

	/**
	 * Inits the transformer, load the xslt and validate it, load jem properties
	 * During the transformation, the transformer onbject is locked, so the file
	 * listner is inhibited to do a refresh.
	 * 
	 * @param xsltvalidatorFile the xslt file
	 * @return the transformer
	 * @throws ValidationException the validation exception
	 */
	private Transformer createTransformer(String xsltvalidatorFile) throws ValidationException {

		Transformer t = null;

		// Instantiate a TransformerFactory
		TransformerFactory tFactory = TransformerFactory.newInstance();

		// add error listner to capture validation error.
		ErrorListener tfel = new TransformerFactoryErrorListener();
		tFactory.setErrorListener(tfel);

		// check the transformer compliant
		if (!tFactory.getFeature(SAXTransformerFactory.FEATURE)) {
			throw new ValidationException(AntMessage.JEMA050E.toMessage().getFormattedMessage());
		}

		// activate xalan extension NodeInfo to map source xml code position
		tFactory.setAttribute(TransformerFactoryImpl.FEATURE_SOURCE_LOCATION, Boolean.TRUE);

		StreamSource ss = new StreamSource(xsltvalidatorFile);
		try {
			// A Transformer may be used multiple times.
			// Parameters and output properties are preserved across
			// transformations.
			t = tFactory.newTransformer(ss);
		} catch (TransformerConfigurationException e) {
			throw new ValidationException(AntMessage.JEMA047E.toMessage().getFormattedMessage(e.getMessage()), e);
		}

		// add custom error listener, necessary to avoid internal catch
		// of exception throwed by xslt
		ErrorListener el = new TransformerErrorListener();
		t.setErrorListener(el);

		// pass the parameter list to the xslt
		for (Object key : System.getProperties().keySet()) {
			String keyString = key.toString();
			String value = System.getProperty(keyString);
			t.setParameter(keyString, value);
		}
		for (String key : System.getenv().keySet()) {
			String value = System.getenv().get(key);
			t.setParameter(key, value);
		}

		return t;
	}

	/**
	 * Create the file listner. if already exists, recreate a new one
	 * 
	 * @param xsltvalidatorFile the xsltvalidator file
	 * @throws ValidationException the validation exception
	 */
	private void createFileListner(String xsltvalidatorFile) throws ValidationException {

		try {
			if (monitor == null) {
				// create a new monitor
				// interval default: 10 sec
				monitor = new FileAlterationMonitor(); 
			} else {
				// stop monitor
				monitor.stop();
			}

			// remove old observer
			if (observer != null) {
				monitor.removeObserver(observer);
				observer.removeListener(this);
				observer.destroy();
				observer = null;
			}

			// create new observer, the file is already validated
			File f = new File(xsltvalidatorFile);
			String fDir = f.getParent();
			String fName = f.getName();

			// Create a FileFilter
			IOFileFilter filter = FileFilterUtils.nameFileFilter(fName);

			observer = new FileAlterationObserver(fDir, filter);
			observer.addListener(this);
			monitor.addObserver(observer);

			// start monitor
			monitor.start();

		} catch (Exception e) {
			throw new ValidationException(e.getMessage(), e);
		}
	}

	/**
	 * Validate the AntJcl using a custom xslt.
	 * 
	 * @param jcl the jcl
	 * @throws ValidationException the validation exception
	 */
	public void validate(Jcl jcl) throws ValidationException {

		if (jcl == null) {
			throw new ValidationException("the jcl to validate is null");
		}

		transform(jcl.getContent());

	}

	/**
	 * Do the xsl transformation. During the transformation, the transformer
	 * object is locked, so the file listner is inhibited to do a refresh of
	 * transformer object
	 * 
	 * @param inXmlContent the in xml content
	 * @throws ValidationException the validation exception
	 */
	private synchronized void transform(String inXmlContent) throws ValidationException {

		// sync is necessary to avoid concurrent access to transformer by file
		// listner
		synchronized (this) {
			StreamSource source = null;
			StreamResult result = null;

			try {
				// traverse the transformation, if some errors are found will be
				// generated some exceptions, otherwise do nothing
				InputStream is = new ByteArrayInputStream(inXmlContent.getBytes(CharSet.DEFAULT));
				source = new StreamSource(is);
				// Use the Transformer to transform an XML Source and send the
				// output to a Result object.
				StringWriter sw = new StringWriter();
				result = new StreamResult(sw);
				transformer.transform(source, result);
				sw.close();

			} catch (TransformerConfigurationException e) {
				throw new ValidationException(e);
			} catch (TransformerException e) {
				throw new ValidationException(e.getMessageAndLocation(), e);
			} catch (Exception e) {
				throw new ValidationException(e);
			}
		}
	}

	/**
	 * Verify xslt file.
	 * 
	 * @param file the file
	 * @throws ValidationException the validation exception
	 */
	public void verifyXsltFile(String file) throws ValidationException {
		if (file == null || file.isEmpty()) {
			throw new ValidationException(AntMessage.JEMA049E.toMessage().getFormattedMessage("null or empty"));
		}

		File f = new File(file);
		if (!f.exists()) {
			throw new ValidationException(AntMessage.JEMA048E.toMessage().getFormattedMessage(file));
		}

		if (!f.isFile()) {
			throw new ValidationException(AntMessage.JEMA049E.toMessage().getFormattedMessage(file));
		}

		try {
			xsltFile = f.getCanonicalPath();
		} catch (IOException e) {
			throw new ValidationException(e);
		}
	}

	// Is triggered when a file is deleted from the monitored folder
	@Override
	public void onFileChange(File file) {
		try {
			if (file.getCanonicalPath().equals(this.xsltFile)) {
				renewTransformer(file.getCanonicalPath());
			}
		} catch (IOException e) {
			LogAppl.getInstance().emit(AntMessage.JEMA057E, e, file);
		}
	}
}