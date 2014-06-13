/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.jobs.commons;

import java.util.LinkedHashMap;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.File;
import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.jobs.commons.inspector.JobHeader;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * A file submitter in Web 2.0 style, with multi file and drag & drop support
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class MultiDragAndDropSubmitter extends AbstractInspector {

	static {
		Styles.INSTANCE.progressBar().ensureInjected();
		Styles.INSTANCE.dragDrop().ensureInjected();
	}
	
	private static final String SERVICE_NAME = "submitter";
	
	/**
	 * 
	 */
	public static final String FILE_SIZE_LIMIT = "5 MB";
	
	// header
	private JobHeader header = new JobHeader("Submit Jobs", this);
	// inspector content panel (under the header)
	private HorizontalPanel mainPanel = new HorizontalPanel();
	// the uploader object
    final Uploader uploader = new Uploader();
    // the drop area label
    final Label dropFilesLabel = new Label();
	// holds the progress bars
	final VerticalPanel progressBarPanel = new VerticalPanel();
    // progress bar panel scroll wrapper
	final ScrollPanel progressBarPanelScroller = new ScrollPanel();
    // left size of inspector, with file drop area
	private VerticalPanel uploaderArea = new VerticalPanel();
	// right size of inspector, with progress bar
	private VerticalPanel progressArea = new VerticalPanel();
	// progress label
	private Label progressLabel = new Label("Progress");

	// map file with progress bar
    final Map<String, ProgressBar> progressBars = new LinkedHashMap<String, ProgressBar>();
    // map file with cancel buttons
    final Map<String, Button> cancelButtons = new LinkedHashMap<String, Button>();

	/**
	 * 
	 */
	public MultiDragAndDropSubmitter() {
		super();

        // set uploader options
        uploader.setUploadURL(GWT.getModuleBaseURL()+SERVICE_NAME)  
                /*.setButtonImageURL(GWT.getModuleBaseURL() + "resources/images/buttons/upload_new_version_button.png")*/  
                /*.setButtonWidth(133)*/  
                /*.setButtonHeight(22)*/  
                .setFileSizeLimit(FILE_SIZE_LIMIT)  
                .setButtonCursor(Uploader.Cursor.HAND)  
                .setButtonAction(Uploader.ButtonAction.SELECT_FILES)  
                .setFileQueuedHandler(new MyFileQueuedHandler())  
                .setUploadProgressHandler(new MyUploadProgressHandler())  
                .setUploadCompleteHandler(new MyUploadCompleteHanlder())  
                .setFileDialogStartHandler(new MyFileDialogStartHandler())  
                .setFileDialogCompleteHandler(new MyFileDialogCompleteHandler())  
                .setFileQueueErrorHandler(new MyFileQueueErrorHandler())  
                .setUploadErrorHandler(new MyUploadErrorHandler());  		
		
        dropFilesLabel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
        setUploaderAreaBeforeDrop();  
    	// add handlers
        dropFilesLabel.addDragOverHandler(new MyDragOverHandler());  
        dropFilesLabel.addDragLeaveHandler(new MyDragLeaveHandler());  
        dropFilesLabel.addDropHandler(new MyDropHandler());  
        
        // Uploader area
		uploaderArea.setSpacing(5);
		uploaderArea.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		uploaderArea.add(dropFilesLabel);
		uploaderArea.setCellVerticalAlignment(dropFilesLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		uploaderArea.setCellHeight(dropFilesLabel, Sizes.HUNDRED_PERCENT);
		
		uploaderArea.add(uploader);
		uploaderArea.setCellHeight(uploader, "0%");
		
		// Progress area
		
		progressArea.setSpacing(5);
		progressArea.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		progressArea.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		progressArea.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		progressLabel.setWidth(Sizes.HUNDRED_PERCENT);
		progressLabel.setStyleName(Styles.INSTANCE.inspector().title());
		progressLabel.addStyleName(Styles.INSTANCE.common().bold());
		progressArea.add(progressLabel);
		progressBarPanel.setSpacing(2);
		progressBarPanel.setWidth(Sizes.HUNDRED_PERCENT);
		progressBarPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		progressBarPanelScroller.setAlwaysShowScrollBars(false);
		progressBarPanelScroller.setWidget(progressBarPanel);
		progressArea.add(progressBarPanelScroller);
		progressArea.setCellHeight(progressBarPanel, Sizes.HUNDRED_PERCENT);
		
		// Main
		
		mainPanel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		mainPanel.setSpacing(5);
		mainPanel.add(uploaderArea);
		mainPanel.add(progressArea);
		mainPanel.setCellWidth(uploaderArea, "50%");
		mainPanel.setCellWidth(progressArea, "50%");
	}

	@Override
	protected void onLoad() {
		int scrollerHeight;
		scrollerHeight = getAvailableHeight() - progressLabel.getOffsetHeight() - 4*progressArea.getSpacing();
		progressBarPanelScroller.setHeight(Sizes.toString(scrollerHeight));
	}

	/*
	 * Upload handlers
	 */
	
	private class MyUploadErrorHandler implements UploadErrorHandler {
        public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {  
            cancelButtons.get(uploadErrorEvent.getFile().getId()).setEnabled(false);
            new Toast(MessageLevel.ERROR, 
            	"Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to " + uploadErrorEvent.getErrorCode().toString() + ": " + uploadErrorEvent.getMessage(), 
            	"Upload Error").show();
            setUploaderAreaBeforeDrop();
            return true;  
        }  
	}
	
	private class MyFileQueueErrorHandler implements FileQueueErrorHandler {
        public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {  
            new Toast(MessageLevel.ERROR, 
           		"Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to " + fileQueueErrorEvent.getErrorCode().toString() + ": " + fileQueueErrorEvent.getMessage(), 
               	"Upload Error").show();
            setUploaderAreaBeforeDrop();
            return true;  
        }  
	}
	
	private class MyFileDialogCompleteHandler implements FileDialogCompleteHandler {
        public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
            if (fileDialogCompleteEvent.getTotalFilesInQueue() > 0) {  
                if (uploader.getStats().getUploadsInProgress() <= 0) {  
                    uploader.startUpload();  
                }  
            }
        	int selected = fileDialogCompleteEvent.getNumberOfFilesSelected();
        	int queued = fileDialogCompleteEvent.getNumberOfFilesQueued();
        	new Toast(queued != selected ? MessageLevel.WARNING : MessageLevel.INFO, "Uploaded " + queued + " file(s) of " + selected + " selected", "Upload completed").show();
            setUploaderAreaBeforeDrop();
            return true;  
        }  
	}
	
	private class MyFileDialogStartHandler implements FileDialogStartHandler {
        public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {  
            if (uploader.getStats().getUploadsInProgress() <= 0) {  
                // Clear the uploads that have completed, if none are in process  
                progressBarPanel.clear();  
                progressBars.clear();  
                cancelButtons.clear();  
            }  
            return true;  
        }  
	}
	
	private class MyUploadCompleteHanlder implements UploadCompleteHandler {
        public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {  
            cancelButtons.get(uploadCompleteEvent.getFile().getId()).setEnabled(false);  
            uploader.startUpload();
            return true;  
        }  
	}
	
	private class MyUploadProgressHandler implements UploadProgressHandler {
		public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {  
            ProgressBar progressBar = progressBars.get(uploadProgressEvent.getFile().getId());  
            progressBar.setProgress((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal());  
            return true;  
        }	
	}
	
	private class MyFileQueuedHandler implements FileQueuedHandler {
		public boolean onFileQueued(final FileQueuedEvent fileQueuedEvent) {  
			Widget item = buildUploadFileItem(fileQueuedEvent.getFile());
            progressBarPanel.add(item);
            progressBarPanelScroller.setVerticalScrollPosition(progressBarPanelScroller.getMaximumVerticalScrollPosition());
            return true;  
        }  	
	}
	
	private Widget buildUploadFileItem(final File file) {
        // Create a Progress Bar for this file  
        final ProgressBar progressBar = new ProgressBar(0.0, 1.0, 0.0, new ProgressBarTextFormatter());
        progressBar.setTitle(file.getName());  
        progressBar.setHeight(Sizes.toString(20));
        progressBar.setWidth(Sizes.HUNDRED_PERCENT);  
        progressBars.put(file.getId(), progressBar);  

        // Add Cancel Button Image  
        final Button cancelButton = new Button("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                uploader.cancelUpload(file.getId(), false);  
                progressBars.get(file.getId()).setProgress(-1.0d);  
                cancelButton.setEnabled(false);  
            }  
        });  
        cancelButtons.put(file.getId(), cancelButton);  

        // Add the Bar and Button to the progress bar panel
        // +--------------------------+
        // | File Name				  |
        // | ProgressBar     | Cancel |
        // +--------------------------+
        
        // first row
        FlexTable itemTable = new FlexTable();
        itemTable.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
        itemTable.setCellSpacing(3);
        Label fileName = new Label(file.getName()); 
        itemTable.setWidget(0, 0, fileName);
        itemTable.setWidget(0, 1, cancelButton);
        
        itemTable.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        itemTable.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        
        // second row
        itemTable.setWidget(1, 0, progressBar);
        
        itemTable.getFlexCellFormatter().setColSpan(1, 0, 2);
        itemTable.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        return itemTable;
	}
	
	/*
	 * Drag & Drop handlers
	 */
	
	private class MyDragOverHandler implements DragOverHandler {
        public void onDragOver(DragOverEvent event) {  
            if (!uploader.getButtonDisabled()) {
            	setUploaderAreaDropping();
            }  
        }  
	}
	
	private class MyDragLeaveHandler implements DragLeaveHandler {
        public void onDragLeave(DragLeaveEvent event) {  
        	setUploaderAreaBeforeDrop();
        }  
	}
	
	private class MyDropHandler implements DropHandler {
		public void onDrop(DropEvent event) {
        	setUploaderAreaAfterDrop();
            if (uploader.getStats().getUploadsInProgress() <= 0) {  
                progressBarPanel.clear();  
                progressBars.clear();  
                cancelButtons.clear();  
            }  
            uploader.addFilesToQueue(Uploader.getDroppedFiles(event.getNativeEvent()));  
            event.preventDefault();
            event.getNativeEvent().preventDefault();
        }  	
	}

	/*
	 * Style methods
	 */
	
	private void setUploaderAreaBeforeDrop() {
		dropFilesLabel.setStyleName(Styles.INSTANCE.dragDrop().beforeDrop());
		dropFilesLabel.setText("Drag files here");
	}
	
	private void setUploaderAreaDropping() {
		dropFilesLabel.setStyleName(Styles.INSTANCE.dragDrop().dropping());
		dropFilesLabel.setText("Release files");
	}
	
	private void setUploaderAreaAfterDrop() {
		dropFilesLabel.setStyleName(Styles.INSTANCE.dragDrop().afterDrop());
    	dropFilesLabel.setText("Uploading files...");
	}

	/*
	 * The progress bar text formatter 
	 */
	
	private class ProgressBarTextFormatter extends ProgressBar.TextFormatter {

		@Override  
        protected String getText(ProgressBar bar, double curProgress) {
			String text;
            if (curProgress < 0) {  
                text = "Cancelled";  
            } else {
                text = ((int) (100 * bar.getPercent())) + "%";
            }
            return text;
        }  
    }

	/*
	 * (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getHeader()
	 */
	@Override
	public FlexTable getHeader() {
		return header;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getContent()
	 */
	@Override
	public Panel getContent() {
		return mainPanel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getActions()
	 */
	@Override
	public Panel getActions() {
		return null;
	}
	
}