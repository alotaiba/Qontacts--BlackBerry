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
package com.mawqey.qontacts.threads;

import i18n.QontactsResource;

import java.util.Vector;

import net.rim.device.api.ui.UiApplication;

import com.mawqey.qontacts.main.QontactsApplication;
import com.mawqey.qontacts.models.ContactsModel;
import com.mawqey.qontacts.screens.ContactsListScreen;
import com.mawqey.qontacts.screens.ProgressPopupScreen;

public class ContactsListLoadThread extends Thread {
	
	private Vector contactItems;
	private ContactsModel contactsModel;
	private ProgressPopupScreen alertScreen;
	
	public ContactsListLoadThread() {
		this.alertScreen = new ProgressPopupScreen(QontactsApplication.res.getString(QontactsResource.STRING_MAIN_ANALYZING_CONTACTS) + "...");
		this.contactsModel = new ContactsModel();
		this.contactItems = new Vector();
		
		//Show the alert before the thread starts to avoid blocking the GUI
		this.alertScreen.show();
	}

	public void run() {
		try {
			Thread.sleep(25);
			this.contactsModel.openContactsDB();
			this.contactItems = this.contactsModel.getContactsItems();
			ContactsListScreen contactsScreen = new ContactsListScreen(this.contactsModel, this.contactItems);
			this.alertScreen.remove();
			UiApplication.getUiApplication().pushScreen(contactsScreen);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
