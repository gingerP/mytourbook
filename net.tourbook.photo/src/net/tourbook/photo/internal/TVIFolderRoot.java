/*******************************************************************************
 * Copyright (C) 2005, 2013  Wolfgang Schramm and Contributors
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
package net.tourbook.photo.internal;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;

public class TVIFolderRoot extends TVIFolder {

	private File[]	_rootFolder;

	public TVIFolderRoot(final PicDirFolder picDirFolder, final TreeViewer folderViewer, final File[] rootFolder) {

		super(picDirFolder, folderViewer);

		_rootFolder = rootFolder;
	}

	@Override
	protected void fetchChildren() {

		for (final File rootFolder : _rootFolder) {
			addChild(new TVIFolderFolder(_picDirFolder, _folderViewer, rootFolder, true));
		}
	}
}
