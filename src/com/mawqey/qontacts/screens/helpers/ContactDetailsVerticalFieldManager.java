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
 * ContactDetailsVerticalFieldManager
 * 
 * This class helps drawing the rounded rectangle area.
 * 
 * Adopted the method by Michael Micheletti: http://www.deepgraysea.com/bbroundedrect.htm
 */
package com.mawqey.qontacts.screens.helpers;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ContactDetailsVerticalFieldManager extends VerticalFieldManager {
	// Layout values
	private static final int CURVE_X = 6; // X-axis inset of curve
	private static final int CURVE_Y = 6; // Y-axis inset of curve
	private static final int X_MARGIN = 2;
	private static final int Y_MARGIN = 5;
	private static final int BORDER_WIDTH = 2;
	private static final int X_FIELD_MARGIN = 25;
	private static final int Y_FIELD_MARGIN = 5;
	
	// Static colors
	private static final int BORDER_COLOR = 0xD6D3D6;
	private static final int BACKGROUND_COLOR = 0xFFFFFF; // White
	
	public ContactDetailsVerticalFieldManager() {
		
	}

	public ContactDetailsVerticalFieldManager(long style) {
		super(style);
	}
	
	public int getPreferredHeight() {
	    int height = 0;
	    int iNumFields = getFieldCount();
	    for (int i = 0; i < iNumFields; i++) {
	        height += getHeight(getField(i));
	    }
	    return height;
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	private int getHeight(Field f) {
		return Math.max(f.getContentHeight(), f.getHeight());
	}
	
	private int getWidth(Field f) {
		return Math.max(f.getContentWidth(), f.getWidth());
	}
	
    protected void sublayout(int maxWidth, int maxHeight) {
		/*
		 * call sublayout to set the initial extent we will be working
		 * with, we will redefine this later but it gives us our canvas
		 * Height can be specified as anything as it does not matter
		*/
		super.sublayout(getPreferredWidth(), Integer.MAX_VALUE);
		
		int iNumFields = getFieldCount();
		int iYPos = Y_FIELD_MARGIN;
		Field fField = null;
		
		for (int i = 0; i < iNumFields; i++) {
			fField = this.getField(i);
			int tmpHeight = getHeight(fField);
			layoutChild(fField, getWidth(fField) - (X_MARGIN * 2) - X_FIELD_MARGIN, tmpHeight);
			setPositionChild(fField, (maxWidth - getWidth(fField))/2, iYPos + Y_FIELD_MARGIN);  
			iYPos = iYPos + tmpHeight;
		}
		this.setExtent(getPreferredWidth(), iYPos + BORDER_WIDTH + (Y_FIELD_MARGIN * 2));
		//setVirtualExtent(maxWidth, 100);
    }
    
	protected void paint(Graphics g) {
		g.clear();
		g.setGlobalAlpha(255);
		g.setBackgroundColor(BACKGROUND_COLOR);
		
		int width = getPreferredWidth();
		int height = getPreferredHeight() + BORDER_WIDTH + (Y_FIELD_MARGIN * 2);
		
		g.setColor(BORDER_COLOR);
		g.fillRoundRect(X_MARGIN, Y_MARGIN, (width - (X_MARGIN * 2)), height, (CURVE_X * 2), (CURVE_Y * 2));
		
		g.setColor(BACKGROUND_COLOR);
		g.fillRoundRect((X_MARGIN + BORDER_WIDTH), (Y_MARGIN + BORDER_WIDTH), (width - ((X_MARGIN * 2) + (BORDER_WIDTH * 2))), (height - (BORDER_WIDTH * 2)), (CURVE_X * 2), (CURVE_Y * 2));
		
		g.setColor(Color.BLACK);
		super.paint(g);
	}
}
