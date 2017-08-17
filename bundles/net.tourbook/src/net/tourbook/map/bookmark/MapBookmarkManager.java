/*******************************************************************************
 * Copyright (C) 2005, 2017 Wolfgang Schramm and Contributors
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
package net.tourbook.map.bookmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import net.tourbook.application.TourbookPlugin;
import net.tourbook.common.UI;
import net.tourbook.common.time.TimeTools;
import net.tourbook.common.util.StatusUtil;
import net.tourbook.common.util.Util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.oscim.core.MapPosition;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class MapBookmarkManager {

// SET_FORMATTING_OFF

	private static final String					CONFIG_FILE_NAME				= "map-bookmarks.xml";			//$NON-NLS-1$
	private static final Bundle					_bundle							= TourbookPlugin.getDefault().getBundle();
	private static final IPath					_stateLocation					= Platform.getStateLocation(_bundle);
	//
	/**
	 * Version number is not yet used.
	 */
	private static final int					CONFIG_VERSION					= 1;
	//

// SET_FORMATTING_ON

	//
	// !!! this is a code formatting separator !!!
	static {}
	//
	// common attributes
	private static final String				ATTR_ID							= "id";						//$NON-NLS-1$

	//
	/*
	 * Root
	 */
	private static final String				TAG_ROOT						= "MapBookmarks";			//$NON-NLS-1$
	private static final String				ATTR_CONFIG_VERSION				= "configVersion";			//$NON-NLS-1$
	//
	/*
	 * Bookmarks
	 */
	private static final String				TAG_ALL_BOOKMARKS				= "AllBookmarks";			//$NON-NLS-1$
	private static final String				TAG_BOOKMARK					= "Bookmark";				//$NON-NLS-1$
	private static final String				TAG_ALL_RECENT_BOOKMARKS		= "AllRecentBookmarks";		//$NON-NLS-1$
	private static final String				TAG_RECENT_BOOKMARK				= "RecentBookmark";			//$NON-NLS-1$
	private static final String				ATTR_NAME						= "name";					//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_X				= "mapPositionX";			//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_Y				= "mapPositionY";			//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_SCALE			= "mapPositionScale";		//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_BEARING		= "mapPositionBearing";		//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_TILT			= "mapPositionTilt";		//$NON-NLS-1$
	private static final String				ATTR_MAP_POSITION_ZOOM_LEVEL	= "mapPositionZoomLevel";	//$NON-NLS-1$
	//
	private static final int				MAX_LRU_BOOKMARKS				= 3;
	//
	/**
	 * Contains all configurations which are loaded from a xml file.
	 */
	private static ArrayList<MapBookmark>	_allBookmarks					= new ArrayList<>();
	//
	/**
	 * Keep last recent used bookmarks
	 */
	private static LinkedList<MapBookmark>	_allRecentBookmarks				= new LinkedList<>();

	static {

		// load all bookmarks
		readBookmarksFromXml();
	}

	public static void addBookmark(final MapBookmark bookmark) {

		_allBookmarks.add(bookmark);

		setLastSelectedBookmark(bookmark);
	}

	private static XMLMemento create_Root() {

		final XMLMemento xmlRoot = XMLMemento.createWriteRoot(TAG_ROOT);

		// date/time
		xmlRoot.putString(Util.ATTR_ROOT_DATETIME, TimeTools.now().toString());

		// plugin version
		final Version version = _bundle.getVersion();
		xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MAJOR, version.getMajor());
		xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MINOR, version.getMinor());
		xmlRoot.putInteger(Util.ATTR_ROOT_VERSION_MICRO, version.getMicro());
		xmlRoot.putString(Util.ATTR_ROOT_VERSION_QUALIFIER, version.getQualifier());

		// config version
		xmlRoot.putInteger(ATTR_CONFIG_VERSION, CONFIG_VERSION);

		return xmlRoot;
	}

	public static ArrayList<MapBookmark> getAllMapBookmarks() {

		return _allBookmarks;
	}

	public static LinkedList<MapBookmark> getAllRecentBookmarks() {
		return _allRecentBookmarks;
	}

	private static File getXmlFile() {

		final File xmlFile = _stateLocation.append(CONFIG_FILE_NAME).toFile();

		return xmlFile;
	}

	/**
	 * @param xmlRoot
	 *            Can be <code>null</code> when not available
	 * @param allBookmarks
	 */
	private static void parseBookmarks(	final XMLMemento xmlRoot,
										final ArrayList<MapBookmark> allBookmarks) {

		if (xmlRoot == null) {
			return;
		}

		final XMLMemento xmlAllBookmarks = (XMLMemento) xmlRoot.getChild(TAG_ALL_BOOKMARKS);

		if (xmlAllBookmarks == null) {
			return;
		}

		for (final IMemento mementoBookmark : xmlAllBookmarks.getChildren()) {

			final XMLMemento xmlBookmark = (XMLMemento) mementoBookmark;

			try {

				final String xmlConfigType = xmlBookmark.getType();

				if (xmlConfigType.equals(TAG_BOOKMARK)) {

					// <Bookmark>

					final MapBookmark bookmark = new MapBookmark();

					parseBookmarks_One(xmlBookmark, bookmark);

					allBookmarks.add(bookmark);
				}

			} catch (final Exception e) {
				StatusUtil.log(Util.dumpMemento(xmlBookmark), e);
			}
		}
	}

	private static void parseBookmarks_One(final XMLMemento xmlBookmark, final MapBookmark bookmark) {

// SET_FORMATTING_OFF
// SET_FORMATTING_ON

		bookmark.id = Util.getXmlString(xmlBookmark, ATTR_ID, Long.toString(System.nanoTime()));
		bookmark.name = Util.getXmlString(xmlBookmark, ATTR_NAME, UI.EMPTY_STRING);

		/*
		 * Map position
		 */
		final MapPosition mapPosition = new MapPosition();

		mapPosition.x = Util.getXmlDouble(xmlBookmark, ATTR_MAP_POSITION_X, 0.5);
		mapPosition.y = Util.getXmlDouble(xmlBookmark, ATTR_MAP_POSITION_Y, 0.5);
		mapPosition.scale = Util.getXmlDouble(xmlBookmark, ATTR_MAP_POSITION_SCALE, 1);

		mapPosition.bearing = Util.getXmlFloat(xmlBookmark, ATTR_MAP_POSITION_BEARING, 0f);
		mapPosition.tilt = Util.getXmlFloat(xmlBookmark, ATTR_MAP_POSITION_TILT, 0f);
		mapPosition.zoomLevel = Util.getXmlInteger(xmlBookmark, ATTR_MAP_POSITION_ZOOM_LEVEL, 1);

		bookmark.setMapPosition(mapPosition);
	}

	private static void parseRecentBookmarks(	final XMLMemento xmlRoot,
												final LinkedList<MapBookmark> allRecentBookmarks) {

		if (xmlRoot == null) {
			return;
		}

		// <AllLastRecentUsedBookmarks>
		final XMLMemento xmlAllRecentBookmarks = (XMLMemento) xmlRoot.getChild(TAG_ALL_RECENT_BOOKMARKS);

		if (xmlAllRecentBookmarks == null) {
			return;
		}

		for (final IMemento mementoRecentBookmark : xmlAllRecentBookmarks.getChildren()) {

			final XMLMemento xmlRecentBookmark = (XMLMemento) mementoRecentBookmark;

			try {

				final String xmlType = xmlRecentBookmark.getType();

				if (xmlType.equals(TAG_RECENT_BOOKMARK)) {

					// <LastRecentUsedBookmark>

					final String recentId = xmlRecentBookmark.getString(ATTR_ID);

					if (recentId == null) {
						continue;
					}

					for (final MapBookmark mapBookmark : _allBookmarks) {

						if (recentId.equals(mapBookmark.id)) {

							// found lru bookmark

							allRecentBookmarks.add(mapBookmark);

							break;
						}
					}
				}

			} catch (final Exception e) {
				StatusUtil.log(Util.dumpMemento(xmlRecentBookmark), e);
			}
		}

	}

	/**
	 * Read or create configuration a xml file
	 * 
	 * @return
	 */
	private static synchronized void readBookmarksFromXml() {

		InputStreamReader reader = null;

		try {

			XMLMemento xmlRoot = null;

			// try to get bookmarks from saved xml file
			final File xmlFile = getXmlFile();
			final String absoluteFilePath = xmlFile.getAbsolutePath();
			final File inputFile = new File(absoluteFilePath);

			if (inputFile.exists()) {

				try {

					reader = new InputStreamReader(new FileInputStream(inputFile), UI.UTF_8);
					xmlRoot = XMLMemento.createReadRoot(reader);

				} catch (final Exception e) {
					// ignore
				}
			}

			// parse xml
			parseBookmarks(xmlRoot, _allBookmarks);

			// must be done AFTER the bookmarks are created
			parseRecentBookmarks(xmlRoot, _allRecentBookmarks);

			// fill up recent bookmarks with the remaining bookmarks
			for (final MapBookmark mapBookmark : _allBookmarks) {

				final boolean isAvailable = _allRecentBookmarks.contains(mapBookmark);

				if (isAvailable == false) {

					_allRecentBookmarks.addLast(mapBookmark);
				}
			}

		} catch (final Exception e) {
			StatusUtil.log(e);
		} finally {
			Util.close(reader);
		}
	}

	public static void saveState() {

		final XMLMemento xmlRoot = create_Root();

		saveState_LRUBookmarks(xmlRoot);
		saveState_Bookmarks(xmlRoot);

		Util.writeXml(xmlRoot, getXmlFile());
	}

	private static void saveState_Bookmarks(final XMLMemento xmlRoot) {

		// <AllBookmarks>
		final IMemento xmlAllBookmarks = xmlRoot.createChild(TAG_ALL_BOOKMARKS);

		for (final MapBookmark bookmark : _allBookmarks) {

			// <Bookmark>
			final IMemento xmlBookmark = xmlAllBookmarks.createChild(TAG_BOOKMARK);
			{
				xmlBookmark.putString(ATTR_ID, bookmark.id);
				xmlBookmark.putString(ATTR_NAME, bookmark.name);

				/*
				 * Map position
				 */
				final MapPosition mapPosition = bookmark.getMapPosition();

				Util.setXmlDouble(xmlBookmark, ATTR_MAP_POSITION_X, mapPosition.x);
				Util.setXmlDouble(xmlBookmark, ATTR_MAP_POSITION_Y, mapPosition.y);
				Util.setXmlDouble(xmlBookmark, ATTR_MAP_POSITION_SCALE, mapPosition.scale);

				xmlBookmark.putFloat(ATTR_MAP_POSITION_BEARING, mapPosition.bearing);
				xmlBookmark.putFloat(ATTR_MAP_POSITION_TILT, mapPosition.tilt);
				xmlBookmark.putInteger(ATTR_MAP_POSITION_ZOOM_LEVEL, mapPosition.zoomLevel);
			}
		}
	}

	private static void saveState_LRUBookmarks(final XMLMemento xmlRoot) {

		// <AllRecentBookmarks>
		final IMemento xmlAllLRU = xmlRoot.createChild(TAG_ALL_RECENT_BOOKMARKS);

		for (final MapBookmark mapBookmark : _allRecentBookmarks) {

			// <RecentBookmark>
			final IMemento xmlLRU = xmlAllLRU.createChild(TAG_RECENT_BOOKMARK);

			xmlLRU.putString(ATTR_ID, mapBookmark.id);
		}

	}

	public static void setLastSelectedBookmark(final MapBookmark selectedBookmark) {

		_allRecentBookmarks.remove(selectedBookmark);
		_allRecentBookmarks.addFirst(selectedBookmark);
	}

}
