/**
 * Qontacts Mobile Application
 * Qontacts is a mobile application that updates the address book contacts
 * to the new Qatari numbering scheme.
 * 
 * Copyright (C) 2010  Abdulrahman Saleh Alotaiba
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * ProgreePopupScreen
 * 
 * This class defines ProgressBar object, creates popup screen with title, and perpetually updating
 * progress gauge. Instantiate and run as thread to start progress update. Call
 * remove() method when finished to remove popup screen and shutdown thread.
 * 
 * Thanks to markat2k from blackberryforums.com : http://www.blackberryforums.com/developer-forum/66815-progress-bar-popup-using-gaugefield.html
 */

package com.mawqey.qontacts.screens;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

public class ProgressPopupScreen {

	private PopupScreen popup;
	private GaugeField gaugeField;
	
	public ProgressPopupScreen(String title) {
		DialogFieldManager manager = new DialogFieldManager();

		popup = new PopupScreen(manager);
		
		manager.addCustomField(new LabelField(title));
	}
	
	public ProgressPopupScreen(String title, int maximum, long style) {
		DialogFieldManager manager = new DialogFieldManager();

		popup = new PopupScreen(manager);
		gaugeField = new GaugeField(null, 0, maximum, 0, style);

		manager.addCustomField(new LabelField(title));
		manager.addCustomField(gaugeField);
	}
	
	public void show() {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				UiApplication.getUiApplication().pushScreen(popup);
				popup.doPaint();
			}
		});
	}
	
	public void remove() {
		if (popup.isDisplayed()) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					UiApplication.getUiApplication().popScreen(popup);
				}
			});
		}
	}
	
	public void updateGaugeFieldValue(int value) {
		gaugeField.setValue(value);
		popup.doPaint();
	}
	
	public void updateGaugeFieldLabel(String value) {
		gaugeField.setLabel(value);
		popup.doPaint();
	}

}
