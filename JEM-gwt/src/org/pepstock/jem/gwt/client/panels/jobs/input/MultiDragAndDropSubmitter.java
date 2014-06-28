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
package org.pepstock.jem.gwt.client.panels.jobs.input;

import java.util.LinkedHashMap;
import java.util.Map;

import org.moxieapps.gwt.uploader.client.File;
import org.moxieapps.gwt.uploader.client.Stats;
import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
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
import org.pepstock.jem.gwt.client.events.EventBus;
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
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * A file submitter in Web 2.0 style, with multi file and drag & drop support
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class MultiDragAndDropSubmitter extends AbstractInspector implements Submitter {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.progressBar().ensureInjected();
		Styles.INSTANCE.dragDrop().ensureInjected();
	}
	private static final String OVERALL_STYLE_SUFFIX = "overall";
	
	// header
	private JobHeader header = new JobHeader("Submit Jobs", this);
	// footer panel, containing the switch submitter button
	private HorizontalPanel footer = new HorizontalPanel();
	// drop & progress panel
	private HorizontalPanel mainHPanel = new HorizontalPanel();
	// drop label description
	private Label dropLabel = new Label("Drag & Drop"); 
	// overall progress label
	private Label overallProgressLabel = new Label("Overall Progress");
	// the overall progress bar
	private ProgressBar overallProgressBar = new ProgressBar();
	// progress label
	private Label fileProgressLabel = new Label("File Progress");
	// the uploader object
    private Uploader uploader = new Uploader();
    // the drop area label
    private Label dropFilesLabel = new Label();
	// holds the progress bars
	private VerticalPanel filesProgressBarPanel = new VerticalPanel();
    // progress bar panel scroll wrapper
	private ScrollPanel progressBarPanelScroller = new ScrollPanel();
    // left size of inspector, with file drop area
	private FlexTable uploaderArea = new FlexTable();
	// right size of inspector, with progress bar
	private VerticalPanel progressArea = new VerticalPanel();
	// switch submitter button
	private Button switchSubmitter = new Button("Legacy Submitter");
	// map file with progress bar
    private Map<String, UploadFileItem> progressBars = new LinkedHashMap<String, UploadFileItem>();
    // map file with cancel buttons
    private Map<String, Button> cancelButtons = new LinkedHashMap<String, Button>();
    
    private boolean allowCancel;
    
    private int uploadedCount = 0;
    private int errorCount = 0;
    private int cancelledCount = 0;
    
    /**
	 * Build the wigdet, with cancel upload not permitted
	 */
	public MultiDragAndDropSubmitter() {
		this(false);
	}
    
	/**
	 * Build the wigdet
	 * @param allowCancel <code>true</code> if you want the user be able to cancel uploads, <code>false</code> otherwhise
	 */
	public MultiDragAndDropSubmitter(boolean allowCancel) {
		super(true);
		// set the allow cancel flag
		this.allowCancel = allowCancel;
		
        // set uploader options
        uploader.setUploadURL(GWT.getModuleBaseURL()+SERVICE_NAME)  
                .setFileSizeLimit(FILE_SIZE_LIMIT)
                .setFileTypes(FILE_TYPES)
                .setFilePostName(LegacySubmitter.FILE_UPLOAD_FIELD)
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
        uploaderArea.setCellSpacing(5);
		uploaderArea.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		dropLabel.setWidth(Sizes.HUNDRED_PERCENT);
		dropLabel.setStyleName(Styles.INSTANCE.inspector().title());
		dropLabel.addStyleName(Styles.INSTANCE.common().bold());

		uploaderArea.setWidget(0, 0, dropLabel);
		uploaderArea.setWidget(0, 1, uploader);
		uploaderArea.setWidget(1, 0, dropFilesLabel);
		uploaderArea.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		uploaderArea.getFlexCellFormatter().setColSpan(1, 0, 2);
		uploaderArea.getFlexCellFormatter().setHeight(1, 0, Sizes.HUNDRED_PERCENT);
		uploaderArea.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// Progress area
		
		progressArea.setSpacing(5);
		progressArea.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		progressArea.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		progressArea.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		overallProgressLabel.setWidth(Sizes.HUNDRED_PERCENT);
		overallProgressLabel.setStyleName(Styles.INSTANCE.inspector().title());
		overallProgressLabel.addStyleName(Styles.INSTANCE.common().bold());
		progressArea.add(overallProgressLabel);
		overallProgressBar.addStyleDependentName(OVERALL_STYLE_SUFFIX);
        overallProgressBar.setTitle("Overall Progress");  
        overallProgressBar.setHeight(Sizes.toString(20));
        overallProgressBar.setWidth(Sizes.HUNDRED_PERCENT);  
		overallProgressBar.setTextFormatter(new OverallProgressBarTextFormatter());
		overallProgressBar.setMinProgress(0.0d);
		overallProgressBar.setProgress(-1.0d);
		progressArea.add(overallProgressBar);
		fileProgressLabel.setWidth(Sizes.HUNDRED_PERCENT);
		fileProgressLabel.setStyleName(Styles.INSTANCE.inspector().title());
		fileProgressLabel.addStyleName(Styles.INSTANCE.common().bold());
		progressArea.add(fileProgressLabel);
		filesProgressBarPanel.setSpacing(2);
		filesProgressBarPanel.setWidth(Sizes.HUNDRED_PERCENT);
		filesProgressBarPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		progressBarPanelScroller.setAlwaysShowScrollBars(false);
		progressBarPanelScroller.setWidget(filesProgressBarPanel);
		progressArea.add(progressBarPanelScroller);
		progressArea.setCellHeight(filesProgressBarPanel, Sizes.HUNDRED_PERCENT);
		
		// Footer
		switchSubmitter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventBus.INSTANCE.fireEventFromSource(new org.pepstock.jem.gwt.client.events.SubmitterClosedEvent(true), MultiDragAndDropSubmitter.this);
			}
		});
		footer.setSpacing(10);
		footer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		footer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		footer.setWidth(Sizes.HUNDRED_PERCENT);
		footer.add(switchSubmitter);

		// Main
		
		mainHPanel.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		mainHPanel.setSpacing(5);
		mainHPanel.add(uploaderArea);
		mainHPanel.add(progressArea);
		mainHPanel.setCellWidth(uploaderArea, "50%");
		mainHPanel.setCellWidth(progressArea, "50%");
	}

	@Override
	protected void onLoad() {
		int scrollerHeight;
		scrollerHeight = getAvailableHeight() - overallProgressLabel.getOffsetHeight() - 4*progressArea.getSpacing();
		progressBarPanelScroller.setHeight(Sizes.toString(scrollerHeight));
	}

	/*
	 * Upload handlers
	 */
	
	private class MyUploadErrorHandler implements UploadErrorHandler {
        public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {
        	if (allowCancel) {
        		cancelButtons.get(uploadErrorEvent.getFile().getId()).setEnabled(false);
        	}
            new Toast(MessageLevel.ERROR, 
            	"Upload of file " + uploadErrorEvent.getFile().getName() + " failed due to " + uploadErrorEvent.getErrorCode().toString() + ": " + uploadErrorEvent.getMessage(), 
            	"Upload Error").show();
            setUploaderAreaBeforeDrop();
            errorCount++;
            return true;  
        }  
	}
	
	private class MyFileQueueErrorHandler implements FileQueueErrorHandler {
        public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {  
            new Toast(MessageLevel.ERROR, 
           		"Upload of file " + fileQueueErrorEvent.getFile().getName() + " failed due to " + fileQueueErrorEvent.getErrorCode().toString() + ": " + fileQueueErrorEvent.getMessage(), 
               	"Upload Error").show();
            setUploaderAreaBeforeDrop();
            errorCount++;
            return true;  
        }  
	}
	
	private class MyFileDialogCompleteHandler implements FileDialogCompleteHandler {
        public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {
        	int totalFilesInQueue = fileDialogCompleteEvent.getTotalFilesInQueue();
        	// reset the overall progress bar values
        	overallProgressBar.setProgress(-1);
        	overallProgressBar.setMaxProgress(totalFilesInQueue);
        	// check and start uploads
        	if (totalFilesInQueue > 0 && uploader.getStats().getUploadsInProgress() <= 0) {
        		uploader.startUpload();
        		setUploaderAreaAfterDrop();
        	}
            return true;  
        }  
	}
	
	private class MyFileDialogStartHandler implements FileDialogStartHandler {
        public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {
            // Clear the uploads that have completed, if none are in process  
            if (uploader.getStats().getUploadsInProgress() <= 0) {  
                filesProgressBarPanel.clear();  
                progressBars.clear();
                if (allowCancel) {
                	cancelButtons.clear();
                }
            }  
            return true;  
        }  
	}
	
	private class MyUploadCompleteHanlder implements UploadCompleteHandler {
        public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {
        	if (allowCancel) {
        		cancelButtons.get(uploadCompleteEvent.getFile().getId()).setEnabled(false);
        	}
            // increment the overall progress bar progress
            overallProgressBar.setProgress(overallProgressBar.getProgress()+1);
            // increment counter
            uploadedCount++;
            // do next upload if any
            uploader.startUpload();
    		// test if no files left in queue, change style to be ready to another upload
        	Stats stats = uploader.getStats();
        	if (stats.getFilesQueued() == 0) {
                setUploaderAreaBeforeDrop();
                // display a Toast that summarize uploads
                MessageLevel level = (errorCount > 0) ? MessageLevel.WARNING : MessageLevel.INFO;
                String message = uploadedCount + " file(s) uploaded, " + errorCount + " error(s), " + cancelledCount + " cancelled";
                new Toast(level, message, "Upload completed").show();
            	// reset the counters
            	uploadedCount = 0;
            	errorCount = 0;
            	cancelledCount = 0;
        	}
            return true;  
        }  
	}
	
	private class MyUploadProgressHandler implements UploadProgressHandler {
		public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {
			File file = uploadProgressEvent.getFile();
			UploadFileItem uploadFileItem;
			if (!progressBars.containsKey(file.getId())) {
				uploadFileItem = new UploadFileItem(file);
				progressBars.put(file.getId(), uploadFileItem);
				filesProgressBarPanel.insert(uploadFileItem, 0);
			} else {
				uploadFileItem = progressBars.get(file.getId());
			}
            ProgressBar progressBar = uploadFileItem.getProgressBar();
            progressBar.setProgress((double) uploadProgressEvent.getBytesComplete() / uploadProgressEvent.getBytesTotal());  
            return true;  
        }	
	}
	
	private class UploadFileItem extends FlexTable {
		
		private final File file;
		private ProgressBar progressBar;
		
		public UploadFileItem(final File file) {
			this.file = file;
	        // Create a Progress Bar for this file  
	        progressBar = new ProgressBar(0.0, 1.0, 0.0, new FileProgressBarTextFormatter());
	        progressBar.setTitle(file.getName());  
	        progressBar.setHeight(Sizes.toString(20));
	        progressBar.setWidth(Sizes.HUNDRED_PERCENT);  

	        // Add Cancel Button Image
	        Button cancelButton = null;
	        if (allowCancel) {
		        final Button button = new Button("Cancel");  
		        button.addClickHandler(new ClickHandler() {  
		            public void onClick(ClickEvent event) {  
		                uploader.cancelUpload(file.getId(), false);  
		                progressBars.get(file.getId()).getProgressBar().setProgress(-1.0d);  
		                button.setEnabled(false);
		                cancelledCount++;
		            }  
		        });  
		        cancelButtons.put(file.getId(), button);
		        cancelButton = button;
	        }

	        // Add the Bar and Button to the progress bar panel
	        // +--------------------------+
	        // | File Name		 | Cancel |
	        // | ProgressBar              |      
	        // +--------------------------+
	        
			// first row
	        setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	        setCellSpacing(3);
	        Label fileName = new Label(file.getName()); 
	        setWidget(0, 0, fileName);
	        getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	        if (allowCancel) {
	        	setWidget(0, 1, cancelButton);
	            getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
	        }
	        
	        // second row
	        setWidget(1, 0, progressBar);
	        
	        getFlexCellFormatter().setColSpan(1, 0, 2);
	        getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		}

		@SuppressWarnings("unused")
		public File getFile() {
			return file;
		}
		
		public ProgressBar getProgressBar() {
			return progressBar;
		}

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
                filesProgressBarPanel.clear();  
                progressBars.clear();
                if (allowCancel) {
                	cancelButtons.clear();
                }
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
	 * The single file progress bar text formatter  
	 */
	private static class FileProgressBarTextFormatter extends ProgressBar.TextFormatter {

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
	 *	The overall progress bar text formatter 
	 */
	private static class OverallProgressBarTextFormatter extends ProgressBar.TextFormatter {

		@Override
		protected String getText(ProgressBar bar, double curProgress) {
			String text;
			if (curProgress < 0) {
				text = "None";
			} else {
				text = ((int)curProgress) + "/" + (int)bar.getMaxProgress();
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
		return mainHPanel;
	}

	/*
	 * (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getActions()
	 */
	@Override
	public Panel getActions() {
		return footer;
	}
	
}