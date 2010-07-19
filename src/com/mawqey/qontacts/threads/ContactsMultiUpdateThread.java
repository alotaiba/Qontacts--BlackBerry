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

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;

import com.mawqey.qontacts.main.QontactsApplication;
import com.mawqey.qontacts.models.ContactsModel;
import com.mawqey.qontacts.screens.ContactsListScreen;
import com.mawqey.qontacts.screens.ProgressPopupScreen;
import com.mawqey.qontacts.screens.helpers.ContactsListData;

public class ContactsMultiUpdateThread extends Thread {
	
	private Vector selectedContactsIndices;
	private ProgressPopupScreen progressScreen;
	private ContactsModel contactsModel;
	private ContactsListScreen contactsListScreen;
	
	private int selectedContactsCount;
	private int selectedContactsConvertedCount;
	
	public ContactsMultiUpdateThread(ContactsModel contactsModel, ContactsListScreen contactsListScreen) {
		this.contactsModel = contactsModel;
		this.contactsListScreen = contactsListScreen;
		
		this.selectedContactsIndices = contactsListScreen.contactListGetSelectedItemsIndices();
		this.selectedContactsCount = this.selectedContactsIndices.size();
		this.selectedContactsConvertedCount = 0;
		this.progressScreen = new ProgressPopupScreen(QontactsApplication.res.getString(QontactsResource.UPDATING_CONTACTS) + "...", this.selectedContactsCount, GaugeField.LABEL_AS_PROGRESS);
	}

	public void run() {
		ContactsMultiUpdateProgressUiThread updateProgressUiThread = new ContactsMultiUpdateProgressUiThread();
		ContactsMultiUpdateDataThread updateDataThread = new ContactsMultiUpdateDataThread();
		
		updateProgressUiThread.start();
		updateDataThread.start();
	}
	
	public class ContactsMultiUpdateDataThread extends Thread {
		
		public ContactsMultiUpdateDataThread() {}
		
		public void run() {
			for (int i = ContactsMultiUpdateThread.this.selectedContactsCount - 1; i >= 0; i--) {
				final int index = ((Integer) ContactsMultiUpdateThread.this.selectedContactsIndices.elementAt(i)).intValue();
				ContactsListData data = (ContactsListData) ContactsMultiUpdateThread.this.contactsListScreen.blackBerryContacts.elementAt(index);
				final BlackBerryContact contact = (BlackBerryContact)data.getContactItem();
				UiApplication.getUiApplication().invokeAndWait(new Runnable() {
					public void run() {
						if (ContactsMultiUpdateThread.this.contactsModel.convertContactNumbers(contact)) {
							ContactsMultiUpdateThread.this.selectedContactsConvertedCount++;
							ContactsMultiUpdateThread.this.contactsListScreen.contactListDeleteItems(index);
						}
					}
				});
			}
		}
	}
	
	public class ContactsMultiUpdateProgressUiThread extends Thread {
		
		public ContactsMultiUpdateProgressUiThread() {}
		
		public void run() {
			ContactsMultiUpdateThread.this.progressScreen.show();
			
			while(ContactsMultiUpdateThread.this.selectedContactsConvertedCount < ContactsMultiUpdateThread.this.selectedContactsCount) {
				UiApplication.getUiApplication().invokeAndWait(new Runnable() {
					public void run() {
						ContactsMultiUpdateThread.this.progressScreen.updateGaugeFieldValue(ContactsMultiUpdateThread.this.selectedContactsConvertedCount);
						ContactsMultiUpdateThread.this.progressScreen.updateGaugeFieldLabel(ContactsMultiUpdateThread.this.selectedContactsConvertedCount + "/" + ContactsMultiUpdateThread.this.selectedContactsCount);
					}
				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			UiApplication.getUiApplication().invokeAndWait(new Runnable() {
				public void run() {
					try {
						ContactsMultiUpdateThread.this.progressScreen.updateGaugeFieldValue(ContactsMultiUpdateThread.this.selectedContactsConvertedCount);
						ContactsMultiUpdateThread.this.progressScreen.updateGaugeFieldLabel(ContactsMultiUpdateThread.this.selectedContactsConvertedCount + "/" + ContactsMultiUpdateThread.this.selectedContactsCount);
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ContactsMultiUpdateThread.this.progressScreen.remove();
				}
			});
		}
	}
	
}
