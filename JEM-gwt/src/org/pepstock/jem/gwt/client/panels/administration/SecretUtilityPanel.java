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
package org.pepstock.jem.gwt.client.panels.administration;



import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.Tooltip;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.resources.CryptedValueAndHash;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Nodes table container for nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class SecretUtilityPanel extends VerticalPanel implements ResizeCapable {
	
	private PasswordTextBox secretBox = new PasswordTextBox();
	
	private PasswordTextBox secretBox2 = new PasswordTextBox();
	
	private Label encrypt = new Label("N/A");
	
	private Label hash = new Label("N/A");
	
	private Button execButton = null;
	
	private ScrollPanel scrollPanel = null;
		
	/**
	 * Creates the UI by the argument (the table)
	 *  
	 * @param nodes table of nodes 
	 */
	public SecretUtilityPanel() {
		VerticalPanel container = new VerticalPanel();
		setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		secretBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((secretBox.getText().length() > 0) && (secretBox2.getText().length() > 0) && secretBox.getText().equals(secretBox2.getText())){
					
					execButton.setEnabled(true);
				} else {
					execButton.setEnabled(false);
				}
				
			}
		});

		secretBox2.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((secretBox.getText().length() > 0) && (secretBox2.getText().length() > 0) && secretBox.getText().equals(secretBox2.getText())){
					execButton.setEnabled(true);
				} else {
					execButton.setEnabled(false);
				}
				
			}
		});

		execButton = new Button("Crypt", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getEncryptedSecret(secretBox.getText());
			}
		});
		execButton.setEnabled(false);
		execButton.addStyleName(Styles.INSTANCE.common().bigButtonPadding());
		new Tooltip(execButton, "Encrypt your secret text");

		// this size!
		container.setSpacing(10);

		// title
		container.add(new HTML("<h2>Secret utility</h2>"));
		
		// image and description
		HorizontalPanel imageAndDescription = new HorizontalPanel();
		imageAndDescription.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image image = new Image(Images.INSTANCE.keys());
		HTML description = new HTML("This utility allows you to have got an encrypted secret text (password and others) that you could use in your JCL.<br>"+
				"This function must be use when you define a Common Resource and you want to hide the password value.<br>"+
				"Calulate the value and copy & paste in your JCL.</p>");
		imageAndDescription.add(image);
		imageAndDescription.add(description);
		imageAndDescription.setCellWidth(description, Sizes.HUNDRED_PERCENT);
		
		container.add(imageAndDescription);
		
	    FlexTable form = new FlexTable();
	    form.setCellPadding(10);
    
	    // Add some standard form options
	    form.setHTML(RowIndex.ROW_1,ColumnIndex.COLUMN_1, "Secret");
	    form.setWidget(RowIndex.ROW_1,ColumnIndex.COLUMN_2, secretBox);
	    form.setHTML(RowIndex.ROW_2,ColumnIndex.COLUMN_1, "Secret (twice to check)");
	    form.setWidget(RowIndex.ROW_2,ColumnIndex.COLUMN_2, secretBox2);
	    
	    form.setHTML(RowIndex.ROW_3,ColumnIndex.COLUMN_1, "");
	    form.setWidget(RowIndex.ROW_3,ColumnIndex.COLUMN_2, execButton);
	    
	    form.setHTML(RowIndex.ROW_4,ColumnIndex.COLUMN_1, "Encrypted secret");
	    form.setWidget(RowIndex.ROW_4,ColumnIndex.COLUMN_2, encrypt);

	    form.setHTML(RowIndex.ROW_5,ColumnIndex.COLUMN_1, "Hash secret");
	    form.setWidget(RowIndex.ROW_5,ColumnIndex.COLUMN_2, hash);

	    container.add(form);
	    
	    scrollPanel = new ScrollPanel(container);
	    add(scrollPanel);
	    
	}


	/**
	 * 
	 * @param secret to encrypted
	 */
    public void getEncryptedSecret(final String secret) {
    	execButton.setEnabled(false);
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.COMMON_RESOURCES_MANAGER.getEncryptedSecret(secret, new GetEncryptedSecretAsyncCallback());
			
			}
	    });
    }

    private class GetEncryptedSecretAsyncCallback extends ServiceAsyncCallback<CryptedValueAndHash> {
		@Override
		public void onJemSuccess(CryptedValueAndHash result) {
			encrypt.setText(result.getCryptedValue());
			hash.setText(result.getHash());
		}
		
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Encrypt secret error!").show();
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			execButton.setEnabled(true);
        }
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	scrollPanel.setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    }
    
}
