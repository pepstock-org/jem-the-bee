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
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Client Bundle with all images used in the WEB APP.<br>
 * Is improving the performance.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public interface Images extends ClientBundle {

	/**
	 * Static reference to be used everywhere.
	 */
	Images INSTANCE = GWT.create(Images.class);

	/**
	 * @return JEM logo image
	 */
	@Source("../resources/images/logo.png")
	ImageResource logo();
	
	/**
	 * @return Pepstock logo image
	 */
	@Source("../resources/images/pepstock.png")
	ImageResource pepstock();
	
	/**
	 * @return JEM logo image
	 */
	@Source("../resources/images/logo_small.png")
	ImageResource logoSmall();

	/**
	 * @return notify small
	 */
	@Source("../resources/images/notify_small.png")
	ImageResource notifySmall();

	/**
	 * @return notify 
	 */
	@Source("../resources/images/notify.png")
	ImageResource notifyNormal();
	
	/**
	 * @return loading image
	 * @see Loading
	 */
	@Source("../resources/images/loading.gif")
	ImageResource loading();

	/**
	 * @return JEM logo image for login (and login error too)
	 */
	@Source("../resources/images/logoForLogin.png")
	ImageResource logoForLogin();

	/**
	 * @return JEM logo image for home 
	 */
	@Source("../resources/images/batch.png")
	ImageResource batch();

	/**
	 * @return JEM logo image for home 
	 */
	@Source("../resources/images/dos-batch.png")
	ImageResource dosbatch();

	/**
	 * @return JEM logo image for home 
	 */
	@Source("../resources/images/xml.png")
	ImageResource xml();

	/**
	 * @return JEM logo image for home 
	 */
	@Source("../resources/images/jem-home.png")
	ImageResource jemhome();

	/**
	 * @return lock icon for roles UI
	 */
	@Source("../resources/images/lock.png")
	ImageResource permission();

	/**
	 * @return user icon for roles UI
	 */
	@Source("../resources/images/user.png")
	ImageResource user();

	/**
	 * @return user icon for network address UI
	 */
	@Source("../resources/images/networkAddress.png")
	ImageResource networkAddress();
	
	/**
	 * @return close icon for UI (popup)
	 */
	@Source("../resources/images/close_24.png")
	ImageResource close24();
	
	/**
	 * @return job inspector icon
	 */
	@Source("../resources/images/gears_64.png")
	ImageResource gears();
	
	/**
	 * @return node inspector icon
	 */
	@Source("../resources/images/node_64.png")
	ImageResource node();

	/**
	 * @return swarm config inspector icon
	 */
	@Source("../resources/images/swarmconf_64.png")
	ImageResource swarmConfig();
	
	/**
	 * @return role inspector icon
	 */
	@Source("../resources/images/roles_64.png")
	ImageResource roles();
	
	/**
	 * @return info image for message box
	 */
	@Source("../resources/images/info.png")
	ImageResource info();
	
	/**
	 * @return question image for message box
	 */
	@Source("../resources/images/question.png")
	ImageResource question();

	/**
	 * @return error image for message box
	 */
	@Source("../resources/images/error.png")
	ImageResource error();

	/**
	 * @return poweroff icon
	 */
	@Source("../resources/images/powerOff_32.png")
	ImageResource powerOff32();

	/**
	 * @return key icon
	 */
	@Source("../resources/images/keys.png")
	ImageResource keys();

	/**
	 * @return key icon
	 */
	@Source("../resources/images/cube_64.png")
	ImageResource cube64();

	/**
	 * @return blue led (#3e6ddd)
	 */
	@Source("../resources/images/led/blue_18.png")
	ImageResource ledBlue18();
	
	/**
	 * @return green (#44d78e)
	 */
	@Source("../resources/images/led/green_18.png")
	ImageResource ledGreen18();

	/**
	 * @return grey led (#686868)
	 */
	@Source("../resources/images/led/grey_18.png")
	ImageResource ledGray18();

	/**
	 * @return light blue led (#ffce19)
	 */
	@Source("../resources/images/led/light_blue_18.png")
	ImageResource ledLightBlue18();

	/**
	 * @return light green led (#79e03b)
	 */
	@Source("../resources/images/led/light_green_18.png")
	ImageResource ledLightGreen18();

	/**
	 * @return red led (#ff1d1d)
	 */
	@Source("../resources/images/led/red_18.png")
	ImageResource ledRed18();

	/**
	 * @return yellow led (#ffce19)
	 */
	@Source("../resources/images/led/yellow_18.png")
	ImageResource ledYellow18();
	
	/**
	 * @return back arrow
	 */
	@Source("../resources/images/back.png")
	ImageResource back();
	
	/**
	 * @return file
	 */
	@Source("../resources/images/file.png")
	ImageResource file();
	
	/**
	 * @return folder
	 */
	@Source("../resources/images/folder.png")
	ImageResource folder();
	
	/**
	 * @return file 64x64
	 */
	@Source("../resources/images/file64.png")
	ImageResource file64();
	
	/**
	 * @return an home icon
	 */
	@Source("../resources/images/home1_24.png")
	ImageResource home124();

	/**
	 * @return a vertical red folder icon
	 */
	@Source("../resources/images/folderRed_24.png")
	ImageResource folderRed24();

	/**
	 * @return a disk drive
	 */
	@Source("../resources/images/driveOpen_24.png")
	ImageResource driveOpen24();

	/**
	 * @return a planet small icon
	 */
	@Source("../resources/images/planet_24.png")
	ImageResource planet24();

	/**
	 * @return a connected pc small icon
	 */
	@Source("../resources/images/network-pc_24.png")
	ImageResource networkPc24();

	/**
	 * @return a list small icon
	 */
	@Source("../resources/images/list_24.png")
	ImageResource list24();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/key_24.png")
	ImageResource key24();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_undo.png")
	ImageResource editUndo();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_indent.png")
	ImageResource editIndent();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_font_size.png")
	ImageResource editFontSize();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_check.png")
	ImageResource editCheck();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_select_all.png")
	ImageResource editSelectAll();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_submit.png")
	ImageResource editSubmit();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_save.png")
	ImageResource editSave();

	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/edit_save_disabled.png")
	ImageResource editSaveDisabled();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/history.png")
	ImageResource history();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/delete.png")
	ImageResource delete();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/tab_left.png")
	ImageResource tabLeft();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/tab_right.png")
	ImageResource tabRight();
	
	/**
	 * @return a key small icon
	 */
	@Source("../resources/images/up_arrow.png")
	ImageResource tooltipArrow();

	/**
	 * @return certificate icon
	 */
	@Source("../resources/images/certificate_64.png")
	ImageResource certificate();

}