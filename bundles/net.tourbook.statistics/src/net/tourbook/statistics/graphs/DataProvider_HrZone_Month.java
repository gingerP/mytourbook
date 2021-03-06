/*******************************************************************************
 * Copyright (C) 2005, 2018 Wolfgang Schramm and Contributors
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

package net.tourbook.statistics.graphs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.tourbook.data.TourPerson;
import net.tourbook.database.TourDatabase;
import net.tourbook.ui.SQLFilter;
import net.tourbook.ui.TourTypeFilter;
import net.tourbook.ui.UI;

public class DataProvider_HrZone_Month extends DataProvider {

	private static DataProvider_HrZone_Month	_instance;

	private TourData_MonthHrZones				_monthData;

	private DataProvider_HrZone_Month() {}

	public static DataProvider_HrZone_Month getInstance() {
		if (_instance == null) {
			_instance = new DataProvider_HrZone_Month();
		}
		return _instance;
	}

	TourData_MonthHrZones getMonthData(	final TourPerson person,
										final TourTypeFilter tourTypeFilter,
										final int lastYear,
										final int numYears,
										final boolean refreshData) {

		/*
		 * check if the required data are already loaded
		 */
		if (_activePerson == person
				&& _activeTourTypeFilter == tourTypeFilter
				&& lastYear == _lastYear
				&& numYears == _numberOfYears
				&& refreshData == false) {
			return _monthData;
		}

		_activePerson = person;
		_activeTourTypeFilter = tourTypeFilter;
		_lastYear = lastYear;
		_numberOfYears = numYears;

		_monthData = new TourData_MonthHrZones();

		String fromTourData;

		final SQLFilter sqlFilter = new SQLFilter(SQLFilter.TAG_FILTER);
		if (sqlFilter.isTagFilterActive()) {

			// with tag filter

			fromTourData = NL

					+ "FROM (			" + NL //$NON-NLS-1$

					+ " SELECT			" + NL //$NON-NLS-1$

					+ "  StartYear,		" + NL //$NON-NLS-1$
					+ "  StartMonth,					" + NL //$NON-NLS-1$

					+ "  HrZone0,		" + NL //$NON-NLS-1$
					+ "  HrZone1,		" + NL //$NON-NLS-1$
					+ "  HrZone2,		" + NL //$NON-NLS-1$
					+ "  HrZone3,		" + NL //$NON-NLS-1$
					+ "  HrZone4,		" + NL //$NON-NLS-1$
					+ "  HrZone5,		" + NL //$NON-NLS-1$
					+ "  HrZone6,		" + NL //$NON-NLS-1$
					+ "  HrZone7,		" + NL //$NON-NLS-1$
					+ "  HrZone8,		" + NL //$NON-NLS-1$
					+ "  HrZone9		" + NL //$NON-NLS-1$

					+ (" FROM " + TourDatabase.TABLE_TOUR_DATA) + NL//$NON-NLS-1$

					// get tag id's
					+ (" LEFT OUTER JOIN " + TourDatabase.JOINTABLE__TOURDATA__TOURTAG + " jTdataTtag") + NL //$NON-NLS-1$ //$NON-NLS-2$
					+ (" ON tourID = jTdataTtag.TourData_tourId") + NL //$NON-NLS-1$

					+ (" WHERE StartYear IN (" + getYearList(lastYear, numYears) + ")") + NL //$NON-NLS-1$ //$NON-NLS-2$
					+ (" AND NumberOfHrZones > 0") + NL //$NON-NLS-1$
					+ sqlFilter.getWhereClause() + NL

					+ ") td" //$NON-NLS-1$
			;

		} else {

			// without tag filter

			fromTourData = NL

					+ (" FROM " + TourDatabase.TABLE_TOUR_DATA) + NL //$NON-NLS-1$

					+ (" WHERE StartYear IN (" + getYearList(lastYear, numYears) + ")") + NL //$NON-NLS-1$ //$NON-NLS-2$
					+ (" AND NumberOfHrZones > 0") + NL //$NON-NLS-1$
					+ sqlFilter.getWhereClause() + NL

			;
		}

		final String sqlString = NL +

				"SELECT" + NL //$NON-NLS-1$

				+ " StartYear,		" + NL // 										1 //$NON-NLS-1$
				+ " StartMonth,		" + NL // 										2 //$NON-NLS-1$

				+ " SUM(CASE WHEN hrZone0 > 0 THEN hrZone0 ELSE 0 END)," + NL //	3 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone1 > 0 THEN hrZone1 ELSE 0 END)," + NL //	4 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone2 > 0 THEN hrZone2 ELSE 0 END)," + NL //	5 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone3 > 0 THEN hrZone3 ELSE 0 END)," + NL //	6 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone4 > 0 THEN hrZone4 ELSE 0 END)," + NL //	7 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone5 > 0 THEN hrZone5 ELSE 0 END)," + NL //	8 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone6 > 0 THEN hrZone6 ELSE 0 END)," + NL //	9 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone7 > 0 THEN hrZone7 ELSE 0 END)," + NL //	10 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone8 > 0 THEN hrZone8 ELSE 0 END)," + NL //	11 //$NON-NLS-1$
				+ " SUM(CASE WHEN hrZone9 > 0 THEN hrZone9 ELSE 0 END)" + NL //		12 //$NON-NLS-1$

				+ fromTourData

				+ (" GROUP BY StartYear, StartMonth") + NL //							//$NON-NLS-1$
				+ (" ORDER BY StartYear, StartMonth") + NL //							//$NON-NLS-1$
		;

		try {

			final int maxZones = 10; // hr zones: 0...9
			final int serieLength = maxZones;
			final int valueLength = 12 * numYears;

			final int[][] dbHrZones = new int[serieLength][valueLength];

			final Connection conn = TourDatabase.getInstance().getConnection();
			{
				final PreparedStatement statement = conn.prepareStatement(sqlString);
				sqlFilter.setParameters(statement, 1);

				final ResultSet result = statement.executeQuery();
				while (result.next()) {

					final int dbYear = result.getInt(1);
					final int dbMonth = result.getInt(2);

					final int yearIndex = numYears - (lastYear - dbYear + 1);
					final int monthIndex = (dbMonth - 1) + yearIndex * 12;

					dbHrZones[0][monthIndex] = result.getInt(3);
					dbHrZones[1][monthIndex] = result.getInt(4);
					dbHrZones[2][monthIndex] = result.getInt(5);
					dbHrZones[3][monthIndex] = result.getInt(6);
					dbHrZones[4][monthIndex] = result.getInt(7);
					dbHrZones[5][monthIndex] = result.getInt(8);
					dbHrZones[6][monthIndex] = result.getInt(9);
					dbHrZones[7][monthIndex] = result.getInt(10);
					dbHrZones[8][monthIndex] = result.getInt(11);
					dbHrZones[9][monthIndex] = result.getInt(12);
				}
			}
			conn.close();

			_monthData.hrZoneValues = dbHrZones;

//			System.out.println("HR Zones\t");
//			// TODO remove SYSTEM.OUT.PRINTLN
//
//			for (int zoneIndex = 0; zoneIndex < 10; zoneIndex++) {
//				for (int monthIndex = 0; monthIndex < 12; monthIndex++) {
//					final int hrZone = dbHrZones[zoneIndex][monthIndex];
//					if (hrZone > 0) {
//						System.out.println("\tz:" + zoneIndex + "\tm:" + monthIndex + "\thr:" + hrZone);
//						// TODO remove SYSTEM.OUT.PRINTLN
//					}
//				}
//				System.out.println("\t");
//				// TODO remove SYSTEM.OUT.PRINTLN
//			}
//
//			System.out.println("\t");
//			System.out.println("\t");
//// TODO remove SYSTEM.OUT.PRINTLN

		} catch (final SQLException e) {
			UI.showSQLException(e);
		}

		return _monthData;
	}

}
