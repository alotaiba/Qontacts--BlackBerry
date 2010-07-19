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
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.mawqey.qontacts.main.QontactsApplication;
import com.mawqey.qontacts.threads.ContactsListLoadThread;

public class QontactsMainScreen extends MainScreen implements FieldChangeListener {
	private HorizontalFieldManager buttonManager;
	private VerticalFieldManager textManager;
	private MenuItem _about;
	ButtonField analyzeContacts;
	
	public QontactsMainScreen() {
		super();

		this.textManager = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH);
		this.buttonManager = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER);
		this._about = new MenuItem(QontactsApplication.res.getString(QontactsResource.ABOUT) + " " + QontactsApplication.res.getString(QontactsResource.APP_TITLE), 110, 10) {
	        public void run() {
	        	AboutScreen aboutScreen = new AboutScreen();
	        	UiApplication.getUiApplication().pushScreen(aboutScreen);
	        }
	    };
		
		LabelField title = new LabelField(QontactsApplication.res.getString(QontactsResource.APP_TITLE));
		setTitle(title);
		
        String newLine = "\n";
		
		String mainTextTitle = QontactsApplication.res.getString(QontactsResource.STRING_MAIN_HOW_TO_TITLE);
		String mainTextParagraph1 = newLine + newLine + QontactsApplication.res.getString(QontactsResource.STRING_MAIN_PARAGRAPH_1) + newLine + "http://qontactsapp.com/";
		String mainTextParagraph21 = newLine + newLine + QontactsApplication.res.getString(QontactsResource.STRING_MAIN_PARAGRAPH_21) + " \"";
		String mainTextButtonText = QontactsApplication.res.getString(QontactsResource.STRING_MAIN_ANALYZE_CONTACTS);
		String mainTextParagraph22 = "\" " + QontactsApplication.res.getString(QontactsResource.STRING_MAIN_PARAGRAPH_22) + newLine + newLine;
		String mainTextParagraph3 = QontactsApplication.res.getString(QontactsResource.STRING_MAIN_PARAGRAPH_3) + newLine;
		
		Font fonts[] = new Font[2];
        fonts[0] = Font.getDefault(); 
        fonts[1] = Font.getDefault().derive(Font.BOLD);
        
		int offsets[] = new int[] {
									//mainTextDevelopmentNote Format
									0, mainTextTitle.length(),
									//mainTextParagraph1 + mainTextParagraph21 Format
									mainTextTitle.length() + mainTextParagraph1.length() + mainTextParagraph21.length(),
									//mainTextButtonText
									mainTextTitle.length() + mainTextParagraph1.length() + mainTextParagraph21.length() + mainTextButtonText.length(),
									//Format for the rest after mainTextButtonText
									mainTextTitle.length() + mainTextParagraph1.length() + mainTextParagraph21.length() + mainTextButtonText.length() + mainTextParagraph22.length() + mainTextParagraph3.length()
								  };
        
        byte attributes[] = new byte[] {1, 0, 1, 0};
        
        String mainTextString = mainTextTitle + mainTextParagraph1 + mainTextParagraph21 + mainTextButtonText + mainTextParagraph22 + mainTextParagraph3;
		
		ActiveRichTextField activeRichTextField = new ActiveRichTextField(mainTextString, offsets, attributes, fonts, null, null, ActiveRichTextField.USE_TEXT_WIDTH);
		this.textManager.add(activeRichTextField);
		
		this.analyzeContacts = new ButtonField(QontactsApplication.res.getString(QontactsResource.STRING_MAIN_ANALYZE_CONTACTS), ButtonField.CONSUME_CLICK);
		this.analyzeContacts.setChangeListener(this);
		this.buttonManager.add(this.analyzeContacts);
		
		add(this.textManager);
		add(this.buttonManager);
	}

	public void fieldChanged(Field field, int context) {
		if (field == this.analyzeContacts) {
			ContactsListLoadThread contactsListLoadThread = new ContactsListLoadThread();
			UiApplication.getUiApplication().invokeLater(contactsListLoadThread);
		}
	}
	
	
    public void makeMenu(Menu menu, int instance)
    {
        menu.add(this._about);
        super.makeMenu(menu, instance);
    }
	
	public boolean onClose() {
		/*
		if (this.contactsModel != null) {
			System.out.println("Closing DB");
			this.contactsModel.closeContactsDB();
		}
		*/
		System.exit(0);
		return true;
	}

}
