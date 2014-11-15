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
package org.pepstock.jem.gwt.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.pepstock.jem.gwt.client.commons.XmlResultViewer;
import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageLevel;

/**
 * Is GWT server service which can provide methods to upload a file.<br>
 * This doesn't implement the usual method because MultiPart is not well supported by RPC of GWT.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public abstract class FileUploadManager extends DefaultManager {

	private static final long serialVersionUID = 1L;

	/**
	 * This is HTTP service method. It reads uploaded file
	 */
	@Override
	protected final void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// reference of error message
		String message = null;
		
		// checks if JEM is available
		if (isEnable()) {
	
			// using Apache fileupload, reads if is Mutlipart
			// if not, error
			ServletRequestContext ss = new ServletRequestContext(request);
			boolean isMultiPart = ServletFileUpload.isMultipartContent(ss);

			if (isMultiPart) {
				// reads HTTP multipart content
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);

				try {
					// reads HTTP request
					@SuppressWarnings("unchecked")
					List<FileItem> items = upload.parseRequest(request);
					try {
	                    String result = loaded(items);
	                    message = getMessage(MessageLevel.INFO.getIntLevel(), result);
                    } catch (Exception e) {
                    	LogAppl.getInstance().emit(UserInterfaceMessage.JEMG049E, e, e.getMessage());
                    	message = getMessage(MessageLevel.ERROR.getIntLevel(), e.getMessage());
                    }
				} catch (FileUploadException e) {
					LogAppl.getInstance().emit(UserInterfaceMessage.JEMG049E, e, e.getMessage());
					message = getMessage(MessageLevel.ERROR.getIntLevel(), UserInterfaceMessage.JEMG049E.toMessage().getFormattedMessage(e.getMessage()));
				}
			} else {
				// if is not multipart, call super http service
				super.service(request, response);
				return;
			}
		} else {
			message = getMessage(MessageLevel.ERROR.getIntLevel(), UserInterfaceMessage.JEMG003E.toMessage().getFormattedMessage(SharedObjects.getInstance().getHazelcastConfig().getGroupConfig().getName()));
		}
		// sets response to client
		// message is XML format that GWT can interpret
		response.setContentType("text/plain");
		response.getWriter().print(message);
		response.flushBuffer();
		response.getWriter().close();
	}
	
	/**
	 * THis method should scan file item and apply the right logic and actions using the data uploaded.
	 * @param items list of file item.
	 * @return message to report on UI
	 * @throws JemException if any exception occurs
	 */
	public abstract String loaded(List<FileItem> items) throws JemException; 

	/**
	 * Creates XML message to return to GWT client.<br>
	 * XML format:<br>
	 * <code>
	 * &lt;result&gt;
	 *   &lt;return-code&gt;
	 *   &lt;/return-code&gt;
	 *   &lt;message&gt;
	 *   &lt;/message&gt;
	 * &lt;/result&gt;
	 * </code>
	 * 
	 * 
	 * @param returnCode uses the message level (ERROR, WARNING, INFO)
	 * @param message message to return and show after submit
	 * @return XML string
	 */
	private String getMessage(int returnCode, String message){
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(XmlResultViewer.RESULT_TAG).append(">");
		sb.append("<").append(XmlResultViewer.RETURN_CODE_TAG).append(">");
		sb.append(returnCode);
		sb.append("</").append(XmlResultViewer.RETURN_CODE_TAG).append(">");
		sb.append("<").append(XmlResultViewer.MESSAGE_TAG).append(">");
		sb.append(message);
		sb.append("</").append(XmlResultViewer.MESSAGE_TAG).append(">");
		sb.append("</").append(XmlResultViewer.RESULT_TAG).append(">");
		return sb.toString();
	}
}