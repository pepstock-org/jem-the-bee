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
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * CSS style sheets shared container
 * @author Marco "Fuzzo" Cuccato
 *
 */
@SuppressWarnings("javadoc")
public interface Styles extends ClientBundle {

	Styles INSTANCE = GWT.create(Styles.class);
	
	@Source("../resources/css/Administration.css")
	Administration administration();
	
	@Source("../resources/css/Common.css")
	Common common();

	@Source("../resources/css/Toast.css")
	Toast toast();
	
	@Source("../resources/css/Tooltip.css")
	Tooltip tooltip();

	@Source("../resources/css/MessageBox.css")
	MessageBox messageBox();
	
	@Source("../resources/css/Inspector.css")
	Inspector inspector();
	
	@Source("../resources/css/Header.css")
	Header header();

	@Source("../resources/css/LinkButtonBar.css")
	LinkButtonBarStyle linkButtonBarStyle();

	@Source("../resources/css/LoginBox.css")
	LoginBox loginBox();
	
	@Source("../resources/css/FileSystemBrowser.css")
	FileSystemBrowser fileSystemBrowser();
	
	@Source("../resources/css/TextBox.css")
	TextBox textBox();
	
	/* Widget override */
	@Source("../resources/css/MenuBar.css")
	CssResource menuBar();
	
	@Source("../resources/css/TabBar.css")
	CssResource tabBar();

	@Source("../resources/css/TabPanel.css")
	CssResource tabPanel();

	@Source("../resources/css/Loading.css")
	CssResource loading();
	
	@Source("../resources/css/StackPanelHeader.css")
	CssResource stackpanelHeader();
	
	/* Interfaces */

	interface LoginBox extends CssResource {
		String grid();
		String invisibleMessage();
		String logo();
		String license(); 
	}
	
	interface LinkButtonBarStyle extends CssResource {
		String bar();
		String button();
		String selectedButton();
	}

	interface Header extends CssResource {
		String infoBox();
		String infoBoxHeader();
	}
	
	interface Inspector extends CssResource {
		String title();
		String main();
		String rowDark();
		String rowLight();
		String gradientBackground();
		String inputMain();
		String headerDefaultPadding();
		String headerClosePadding();
		String adminTitle();
		String adminHeaderBack();		
	}
	
	interface Toast extends CssResource {
		String main();
		String title();
		String message();
		String blue();
		String green();
		String grey();
		String yellow();
		String red();
		String lightGreen();
		String lightBlue();
	}
	
	interface Tooltip extends CssResource {
		String tooltipBox();
		String tooltipArrow();
		String tooltipText();
	}
	
	interface MessageBox extends CssResource {
		String main();
		String title();
		String message();
	
	}
	
	interface Common extends CssResource {
		String footer();
		String notifyImage();
		String bold();
		String italic();
		String red();
		String marginLeft();
		String marginLeft20();
		String noWrap();
		String autoMargin();
		String verticalAlignMiddle();
		String defaultActionButton();
		String widthFull();
		String verticalAlignTop();
		String minHeight();
		String adminBorder();
		String highligtherCustomText();
		String highligtherCustomRow();
		String adminSelectedTreeItem();
		String adminUnselectedTreeItem();
		String padding4424();
		String headerWithData();
		String headerWithoutData();
		String textBoxError();
		String textRed();
		String permissionLabel();
		String permissionLabelSelected();
		String permissionLabelDisabled();
		String permissionDescription();
		String smallGreyDescription();
		String editMenuBar();
		String editMenuItemDisabled();
		String searcher();
		String searcherTextBox();
		String searcherFocus();
		String searcherUnabled();
		String searcherHistory();
		String pointer();
		String bigButtonPadding();
		String noMinMaxHeightWidth();
		String chartTickLabel();
	}
	
	interface Administration extends CssResource {
		String nodeList();
		String editorContainer();
	}

	interface FileSystemBrowser extends CssResource {
		String pathToken();
	}
	
	interface TextBox extends CssResource {
		String mandatoryError();		
	}
}
