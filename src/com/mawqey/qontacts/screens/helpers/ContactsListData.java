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
package com.mawqey.qontacts.screens.helpers;

import net.rim.blackberry.api.pdap.BlackBerryContact;

public class ContactsListData {

	private BlackBerryContact _contactItem;
	private boolean _checked;
	
	public ContactsListData()
    {
		_contactItem = null;
        _checked = false;
    }
	
	public ContactsListData(Object contactItem, boolean checked)
	{
		_contactItem = (BlackBerryContact)contactItem;
		_checked = checked;
	}
	
	public BlackBerryContact getContactItem()
	{
		return _contactItem;
	}
	
	public String getStringVal()
	{
		String _stringVal = getContactName(_contactItem);
		return _stringVal;
	}
	
	public boolean isChecked()
	{
		return _checked;
	}
	
	public void setChecked(boolean checked)
	{
		_checked = checked;
	}
	
	//Toggle the checked status.
	public void toggleChecked()
	{
		_checked = !_checked;
	}
	
    public String getContactName(BlackBerryContact contact) {
        if (contact == null) {
            return null;
        }

        String displayName = "(no name)";

        // First, see if there is a meaningful name set for the contact.
        if (contact.countValues(BlackBerryContact.NAME) > 0) {
            final String[] name = contact.getStringArray(BlackBerryContact.NAME, BlackBerryContact.ATTR_NONE);
            final String firstName = name[BlackBerryContact.NAME_GIVEN];
            final String lastName = name[BlackBerryContact.NAME_FAMILY];
            if (firstName != null && lastName != null) {
                displayName = firstName + " " + lastName;
            } else if (firstName != null) {
                displayName = firstName;
            } else if (lastName != null) {
                displayName = lastName;
            }

            if (displayName != null) {
                final String namePrefix = name[BlackBerryContact.NAME_PREFIX];
                if (namePrefix != null) {
                    displayName = namePrefix + " " + displayName;
                }
                return displayName;
            }
        }

        // If not, use the company name.
        if (contact.countValues(BlackBerryContact.ORG) > 0) {
            final String companyName = contact.getString(BlackBerryContact.ORG, 0);
            if (companyName != null) {
                return companyName;
            }
        }
        
        return displayName;
	}
}
