package com.example.sunshine.sunshine;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


        import android.content.ContentValues;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.test.AndroidTestCase;
        import android.util.Log;

        import data.WeatherContract;
        import data.WeatherDbHelper;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // Test data we're going to insert into the DB to see if it works.
        String testLocationSetting = "99705";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((WeatherContract.LocationEntry.COLUMN_COORD_LONG));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(testCityName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Fantastic.  Now that we have a location, add some weather!
            // Fantastic.  Now that we have a location, add some weather!
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            // A cursor is your primary interface to the query results.
            Cursor weatherCursor = db.query(
                    WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null  // sort order
            );

            if (!weatherCursor.moveToFirst()) {
                fail("No weather data returned!");
            }

            assertEquals(weatherCursor.getInt(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY)), locationRowId);
            assertEquals(weatherCursor.getString(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)), "20141205");
            assertEquals(weatherCursor.getDouble(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES)), 1.1);
            assertEquals(weatherCursor.getDouble(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)), 1.2);
            assertEquals(weatherCursor.getDouble(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)), 1.3);
            assertEquals(weatherCursor.getInt(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), 75);
            assertEquals(weatherCursor.getInt(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), 65);
            assertEquals(weatherCursor.getString(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)), "Asteroids");
            assertEquals(weatherCursor.getDouble(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)), 5.5);
            assertEquals(weatherCursor.getInt(
                    weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)), 321);

            weatherCursor.close();
            dbHelper.close();
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }
}