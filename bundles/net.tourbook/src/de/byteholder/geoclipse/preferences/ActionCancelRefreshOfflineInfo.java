/*******************************************************************************
 * Copyright (C) 2005, 2009  Wolfgang Schramm and Contributors
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package de.byteholder.geoclipse.preferences;

import net.tourbook.application.TourbookPlugin;

import org.eclipse.jface.action.Action;

public class ActionCancelRefreshOfflineInfo extends Action {

	private PrefPageMapProviders	fPrefPageMapFactories;

	public ActionCancelRefreshOfflineInfo(final PrefPageMapProviders prefPageMapFactories) {

		setToolTipText(Messages.Pref_Map_Button_CancelTileInfo_Tooltip);

		setImageDescriptor(TourbookPlugin.getImageDescriptor(Messages.App_Image_Cancel));
		setDisabledImageDescriptor(TourbookPlugin.getImageDescriptor(Messages.App_Image_Cancel_Disabled));

		fPrefPageMapFactories = prefPageMapFactories;
	}

	@Override
	public void run() {
		fPrefPageMapFactories.actionCancelRefreshOfflineInfo();
	}

}
