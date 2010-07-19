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
package com.mawqey.qontacts.screens;

import i18n.QontactsResource;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.mawqey.qontacts.main.QontactsApplication;
import com.mawqey.qontacts.models.ContactsModel;
import com.mawqey.qontacts.screens.helpers.ContactDetailsVerticalFieldManager;

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ContactDetailsScreen extends MainScreen {
	private ContactsModel contactsModel;
	private BlackBerryContact thisContact;
	private Hashtable contactNumbers;
	private MenuItem _updateThisContact;
	private MenuItem _about;
	private static final int BACKGROUND_COLOR = 0xDEDFDE; // Gray-ish
	
	public ContactDetailsScreen(ContactsModel contactsModel, BlackBerryContact contact, final String contactName, final Integer contactToRemoveIndex, final ContactsListScreen parentClass) {
		super(NO_VERTICAL_SCROLL);
        VerticalFieldManager mainDetailsScreen = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR) {
			public void paint(Graphics g) {
				g.setBackgroundColor(BACKGROUND_COLOR);
				g.clear();
				super.paint(g);
			}            
			protected void sublayout( int maxWidth, int maxHeight ) {
				super.sublayout( maxWidth, maxHeight);
				setExtent( maxWidth, maxHeight);
			}
		};
		ContactDetailsVerticalFieldManager nameFieldManager = new ContactDetailsVerticalFieldManager(ContactDetailsVerticalFieldManager.USE_ALL_WIDTH);
		ContactDetailsVerticalFieldManager currentNumbersFieldManager = new ContactDetailsVerticalFieldManager(ContactDetailsVerticalFieldManager.USE_ALL_WIDTH);
		ContactDetailsVerticalFieldManager updatedNumbersFieldManager = new ContactDetailsVerticalFieldManager(ContactDetailsVerticalFieldManager.USE_ALL_WIDTH);
		
		this.contactsModel = new ContactsModel();
		this.contactsModel.openContactsDB();
		this.contactsModel.initAtrributesLabels();
		this.thisContact = contact;
		this.contactNumbers = this.contactsModel.updateableContactNumbers(this.thisContact);
		
	    LabelField title = new LabelField(QontactsApplication.res.getString(QontactsResource.PREVIEW_CONTACT));
	    setTitle(title);
	    
	    //Due to 4.5 bug in displaying RTL LabelField, I have to include it in a VerticalManager
	    VerticalFieldManager nameTitleManger = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH);
		LabelField nameTitle = new LabelField(QontactsApplication.res.getString(QontactsResource.NAME) + ": " + contactName);
		nameTitleManger.add(nameTitle);
		nameFieldManager.add(nameTitleManger);
		
		Vector originalNumbers = (Vector) this.contactNumbers.get("Original Numbers");
		
		VerticalFieldManager currentNumbersTitleManger = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH);
		LabelField currentNumbersTitle = new LabelField(QontactsApplication.res.getString(QontactsResource.CURRENT_NUMBERS) + " (" + originalNumbers.size() + ")");
		currentNumbersTitleManger.add(currentNumbersTitle);
		currentNumbersFieldManager.add(currentNumbersTitleManger);
		
		Enumeration originalNumbersItems = originalNumbers.elements();
		while (originalNumbersItems.hasMoreElements()) {
			Hashtable numberItem = (Hashtable) originalNumbersItems.nextElement();
			String number = (String) numberItem.get("Number");
			Integer attr = (Integer) numberItem.get("Attribute");
			String label = this.contactsModel.getAtrributeLabel(attr) + ": ";
			BasicEditField detailField = new BasicEditField("  " + label, number, 50, (BasicEditField.READONLY | BasicEditField.CONSUME_INPUT));
			currentNumbersFieldManager.add(detailField);
		}
		
		Vector updatedNumbers = (Vector) this.contactNumbers.get("Updated Numbers");
		
		VerticalFieldManager updatedNumbersTitleManger = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH);
		LabelField updatedNumbersTitle = new LabelField(QontactsApplication.res.getString(QontactsResource.UPDATED_NUMBERS) + " (" + updatedNumbers.size() + ")");
		updatedNumbersTitleManger.add(updatedNumbersTitle);
		updatedNumbersFieldManager.add(updatedNumbersTitleManger);
		
		Enumeration updatedNumbersItems = updatedNumbers.elements();
		while (updatedNumbersItems.hasMoreElements()) {
			Hashtable numberItem = (Hashtable) updatedNumbersItems.nextElement();
			String number = (String) numberItem.get("Number");
			Integer attr = (Integer) numberItem.get("Attribute");
			String label = this.contactsModel.getAtrributeLabel(attr) + ": ";
			BasicEditField detailField = new BasicEditField("  " + label, number, 50, (BasicEditField.READONLY | BasicEditField.CONSUME_INPUT));
			updatedNumbersFieldManager.add(detailField);
		}
		
		mainDetailsScreen.add(nameFieldManager);
		mainDetailsScreen.add(currentNumbersFieldManager);
		mainDetailsScreen.add(updatedNumbersFieldManager);
		
		add(mainDetailsScreen);
		
		this._updateThisContact = new MenuItem(QontactsApplication.res.getString(QontactsResource.UPDATE_CONTACT), 100, 10) {
	        public void run() {
	        	if (confirmContactUpdate(contactName)) {
		        	if (updateContact()) {
		        		parentClass.contactToRemoveIndex = contactToRemoveIndex;
		        		Dialog.inform(QontactsApplication.res.getString(QontactsResource.STRING_PREVIEWSCREEN_DETAILS_OF) + " " + contactName + " " + QontactsApplication.res.getString(QontactsResource.STRING_UPDATED_SUCCESSFULLY));
		        		UiApplication.getUiApplication().popScreen(getScreen());
		        	} else {
		        		Dialog.alert("There was an error updating " + contactName + "'s details, please try again!");
		        	}
	        	}
	        }
	    };
	    
		this._about = new MenuItem(QontactsApplication.res.getString(QontactsResource.ABOUT) + " " + QontactsApplication.res.getString(QontactsResource.APP_TITLE), 100000, 20) {
	        public void run() {
	        	AboutScreen aboutScreen = new AboutScreen();
	        	UiApplication.getUiApplication().pushScreen(aboutScreen);
	        }
	    };
	}
	
    public void makeMenu(Menu menu, int instance)
    {
        menu.add(this._updateThisContact);
        menu.add(this._about);
        super.makeMenu(menu, instance);
    }
    
    private boolean updateContact() {
    	Vector updatedNumbers = (Vector) this.contactNumbers.get("Updated Numbers");
    	if (this.contactsModel.convertContactNumbers(this.thisContact, updatedNumbers)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
	private boolean confirmContactUpdate(String contactName){
		int confirm = Dialog.ask(Dialog.D_OK_CANCEL, QontactsApplication.res.getString(QontactsResource.STRING_UPDATE_QUESTION) + " " + contactName + QontactsApplication.res.getString(QontactsResource.QUESTION_MARK));
		if (confirm == Dialog.CANCEL) {
			return false;
		} else {
			return true;
		}
	}

}
