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
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.mawqey.qontacts.main.QontactsApplication;

public class AboutScreen extends MainScreen {

	public AboutScreen() {
		super();
		LabelField title = new LabelField(QontactsApplication.res.getString(QontactsResource.ABOUT) + " " + QontactsApplication.res.getString(QontactsResource.APP_TITLE));
		setTitle(title);
		
		VerticalFieldManager textManager = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH);
		
		Font fonts[] = new Font[2];
        fonts[0] = Font.getDefault(); 
        fonts[1] = Font.getDefault().derive(Font.BOLD);
		
		String newLine = "\n";
		Character copyrightSign = new Character('\u00A9'); //©
		
		LabelField aboutTitle = new LabelField(QontactsApplication.res.getString(QontactsResource.APP_TITLE));
		aboutTitle.setFont(fonts[1]);
		
		BasicEditField versionField = new BasicEditField(QontactsApplication.res.getString(QontactsResource.VERSION) + ": ", ApplicationDescriptor.currentApplicationDescriptor().getVersion(), 50, (BasicEditField.READONLY | BasicEditField.CONSUME_INPUT));
		BasicEditField developedByField = new BasicEditField(QontactsApplication.res.getString(QontactsResource.DEVELOPED_BY) + ": ", QontactsApplication.res.getString(QontactsResource.DEVELOPER), 60, (BasicEditField.READONLY | BasicEditField.CONSUME_INPUT));
		
		String aboutTextCopyrightNotice = QontactsApplication.res.getString(QontactsResource.COPYRIGHT) + " " + copyrightSign + " 2010 " + QontactsApplication.res.getString(QontactsResource.DEVELOPER) + ". " + QontactsApplication.res.getString(QontactsResource.ALL_RIGHTS_RESERVED) + ".";
		String aboutTextURL = "http://www.qontactsapp.com/";
		
		String aboutText = newLine + aboutTextCopyrightNotice + newLine + aboutTextURL;
		
		ActiveRichTextField activeRichTextField = new ActiveRichTextField(aboutText, ActiveRichTextField.USE_TEXT_WIDTH);
		
		textManager.add(aboutTitle);
		textManager.add(versionField);
		textManager.add(developedByField);
		textManager.add(activeRichTextField);
		add(textManager);
	}
	
}
