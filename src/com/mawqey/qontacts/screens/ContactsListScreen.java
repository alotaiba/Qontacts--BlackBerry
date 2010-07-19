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

import java.util.Vector;

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

import com.mawqey.qontacts.main.QontactsApplication;
import com.mawqey.qontacts.models.ContactsModel;
import com.mawqey.qontacts.screens.helpers.ContactsListData;
import com.mawqey.qontacts.threads.ContactsMultiUpdateThread;

public final class ContactsListScreen extends MainScreen implements ListFieldCallback {
	private ListField contactListField;
	public Vector blackBerryContacts;

	private ContactsModel contactsModel;

	private MenuItem _toggleMenuItem;
	private MenuItem _previewMenuItem;
	private MenuItem _markAllMenuItem;
	private MenuItem _unmarkAllMenuItem;
	private MenuItem _updateSelectedMenuItem;
	private MenuItem _about;
	
	public Integer contactToRemoveIndex = null;
	
    
    public void makeMenu(Menu menu, int instance)
    {
        Field focus = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
        
        if(focus == contactListField && !contactListField.isEmpty()) 
        {
            menu.add(this._toggleMenuItem);
            menu.add(this._previewMenuItem);
            menu.add(this._markAllMenuItem);
            menu.add(this._unmarkAllMenuItem);
            menu.add(this._updateSelectedMenuItem);
        }
        menu.add(this._about);
        
        super.makeMenu(menu, instance);
    }
    
    public boolean onMenu(int instance) {
    	if (!contactListField.isEmpty()) {
	    	final int index = contactListField.getSelectedIndex();
	    	final ContactsListData data = (ContactsListData) blackBerryContacts.elementAt(index);
	    	String markUnmarkString = (data.isChecked()) ? QontactsApplication.res.getString(QontactsResource.UNMARK) : QontactsApplication.res.getString(QontactsResource.MARK);
	    	
		    this._previewMenuItem = new MenuItem(QontactsApplication.res.getString(QontactsResource.PREVIEW_CONTACT), 1000, 100) {
		        public void run() {
		        	Screen thisClass = UiApplication.getUiApplication().getActiveScreen();
		        	BlackBerryContact contact = (BlackBerryContact)data.getContactItem();
		        	String name = (String) data.getContactName(contact);
		        	ContactDetailsScreen contactsDetailsScreen = new ContactDetailsScreen(ContactsListScreen.this.contactsModel, contact, name, new Integer(index), (ContactsListScreen) thisClass);
					UiApplication.getUiApplication().pushScreen(contactsDetailsScreen);
		        }
	        };
	    	
			this._toggleMenuItem = new MenuItem(markUnmarkString, 100000, 10) {
		        public void run() {
		        	contactlistDataSetItemToggle();
		        }
		    };
		    
		    this._markAllMenuItem = new MenuItem(QontactsApplication.res.getString(QontactsResource.MARK_ALL), 100001, 20) {
		        public void run() {
		        	contactlistDataSetItemsChecked(true);
		        }
		    };
		    
		    this._unmarkAllMenuItem = new MenuItem(QontactsApplication.res.getString(QontactsResource.UNMARK_ALL), 100002, 30) {
		        public void run() {
		        	contactlistDataSetItemsChecked(false);
		        }
		    };
		    this._updateSelectedMenuItem = null;
		    
		    final Vector selectedContactsIndices = contactListGetSelectedItemsIndices();
		    if (selectedContactsIndices.size() > 0) {
	    	    this._updateSelectedMenuItem = new MenuItem(QontactsApplication.res.getString(QontactsResource.UPDATE_SELECTED) + " (" + selectedContactsIndices.size() + ")", 10000000, 40) {
	    	        public void run() {
	    	        	if (confirmSelectionUpdate(selectedContactsIndices.size())) {
	    	        		contactlistDataUpdateSelected();
	    	        	}
	    	        }
	    	    };
		    }
    	}
    	
		this._about = new MenuItem(QontactsApplication.res.getString(QontactsResource.ABOUT) + " " + QontactsApplication.res.getString(QontactsResource.APP_TITLE), 100000000, 50) {
	        public void run() {
	        	AboutScreen aboutScreen = new AboutScreen();
	        	UiApplication.getUiApplication().pushScreen(aboutScreen);
	        }
	    };
    	
    	super.onMenu(instance);
		return true;
    }
	
	public ContactsListScreen(ContactsModel contactsModel, Vector contactItems) {
		super();
		LabelField title = new LabelField(QontactsApplication.res.getString(QontactsResource.TITLE_CONTACTS_LIST));
		setTitle(title);
		
		this.contactsModel = contactsModel;
		
		contactListField = new ListField(contactItems.size()) {
			//Allow the space bar to toggle the status of the selected row.
			protected boolean keyChar(char key, int status, int time) {
				boolean retVal = false;
				//If the spacebar was pressed...
				if (key == Characters.SPACE) {
					contactlistDataSetItemToggle();
					//Consume this keyChar (key pressed).
		            retVal = true;
		        }
		        return retVal;
		    }
		};
		contactListField.setCallback(this);
		contactListField.setEmptyString("* " + QontactsApplication.res.getString(QontactsResource.NO_CONTACTS) + " *", DrawStyle.HCENTER);
		add(contactListField);
		
		blackBerryContacts = contactItems;
		contactListField.setSize(blackBerryContacts.size());
	}
	
	private void contactlistDataSetItemToggle() {
        //Get the index of the selected row.
        int index = contactListField.getSelectedIndex();
        
        //Get the ChecklistData for this row.
        ContactsListData data = (ContactsListData)blackBerryContacts.elementAt(index);
        
        //Toggle its status.
        data.toggleChecked();
        
        //Update the Vector with the new ChecklistData.
        blackBerryContacts.setElementAt(data, index);
        
        //Invalidate the modified row of the ListField.
        contactListField.invalidate(index);
	}
	
	public void drawListRow(ListField list, Graphics graphics, int index, int y, int width) {
		if ( contactListField == list && index < blackBerryContacts.size()) {
			//Character LRE = new Character('\u202A');
			//Character RLE = new Character('\u202B');
			//Character PDF = new Character('\u202C');
			Character LRM = new Character('\u200E');
			Character RLM = new Character('\u200F');
			
			//Where to draw? Left to right or right to left?
			Character UTFBiDiChar = LRM;
			int drawStyle = DrawStyle.LEFT;
			
			if (QontactsApplication.language.equals("ar")) {
				UTFBiDiChar = RLM;
				drawStyle = DrawStyle.RIGHT;
			}
			
			ContactsListData currentRow = (ContactsListData)this.get(list, index);
			int updateableNumbersCount = ((Vector) contactsModel.updateableContactNumbers(currentRow.getContactItem()).get("Updated Numbers")).size();

			
			StringBuffer rowString = new StringBuffer();
			
			rowString.append(UTFBiDiChar);
			
			if (currentRow.isChecked())
			{
				rowString.append(Characters.BALLOT_BOX_WITH_CHECK);
			}
			else
			{
				rowString.append(Characters.BALLOT_BOX);
			}
			
			//Append a couple spaces and the row's text.
			rowString.append(Characters.SPACE);
			rowString.append(Characters.SPACE);
			
			rowString.append(currentRow.getStringVal());
			
			rowString.append(UTFBiDiChar);
			
			rowString.append(Characters.SPACE);
			
			rowString.append("(" + updateableNumbersCount + ")");
			
			graphics.drawText(rowString.toString(), 0, y, (DrawStyle.TOP | drawStyle), width);
		}
	}
	
	public Object get(ListField fieldVar, int index) {
		if (contactListField == fieldVar) {
			//If index is out of bounds an exception will be thrown,
			//but that's the behavior we want in that case.
			return blackBerryContacts.elementAt(index);
		}
		return null;
	}

	public int getPreferredWidth(ListField fieldVar) {
		//use all the width of the current LCD
		return Display.getWidth();
	}

	public int indexOfList(ListField fieldVar, String prefix, int start) {
		return blackBerryContacts.indexOf(prefix, start);
	}
	
	private void contactlistDataSetItemsChecked(boolean checked) {
		for (int i = 0; i < blackBerryContacts.size(); i++) {
			ContactsListData dataItem = (ContactsListData)blackBerryContacts.elementAt(i);
			dataItem.setChecked(checked);
	        blackBerryContacts.setElementAt(dataItem, i);
	        contactListField.invalidate(i);
			/*
			if (checked && !dataItem.isChecked()) {
				dataItem.setChecked(checked);
			} else if (!checked && dataItem.isChecked()) {
				dataItem.setChecked(checked);
			}
			*/
		}
	}
	
	public void contactListDeleteItems(int index) {
        //Update the Vector with the new ChecklistData.
        blackBerryContacts.removeElementAt(index);
        
        //Invalidate the modified row of the ListField.
        contactListField.delete(index);
	}
	
	public Vector contactListGetSelectedItemsIndices() {
		Vector indices = new Vector();
		for (int i = 0; i < blackBerryContacts.size(); i++) {
			ContactsListData dataItem = (ContactsListData)blackBerryContacts.elementAt(i);
			if (dataItem.isChecked()) {
				indices.addElement(new Integer(i));
			}
		}
		return indices;
	}
	
	private boolean confirmSelectionUpdate(int selectedCount){
		int confirm = Dialog.ask(Dialog.D_OK_CANCEL, QontactsApplication.res.getString(QontactsResource.STRING_UPDATE_QUESTION) + " " + selectedCount + " " + QontactsApplication.res.getString(QontactsResource.CONTACT) + QontactsApplication.res.getString(QontactsResource.QUESTION_MARK));
		if (confirm == Dialog.CANCEL) {
			return false;
		} else {
			return true;
		}
	}
	
	private void contactlistDataUpdateSelected() {
		ContactsMultiUpdateThread contactsUpdateThread = new ContactsMultiUpdateThread(this.contactsModel, this);
		contactsUpdateThread.start();
	}
	
	protected void onExposed() {
		//null != 0 :)
		if (this.contactToRemoveIndex != null) {
			contactListDeleteItems(this.contactToRemoveIndex.intValue());
			this.contactToRemoveIndex = null;
		}
	}
}
