package com.example.luxevistaresort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "luxevista.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_GUESTS_TABLE =
            "CREATE TABLE Guests (" +
                    "Guest_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Name TEXT, " +
                    "Username TEXT, " +
                    "Password TEXT, " +
                    "Email TEXT, " +
                    "Phone_Number TEXT," +
                    "Travel_Dates TEXT," +
                    "Preferences TEXT);";

    private static final String CREATE_TABLE_BOOKINGS =
            "CREATE TABLE Bookings (" +
                    "Booking_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Guest_ID INTEGER, " +
                    "Room_ID INTEGER, " +
                    "Check_In_Date TEXT, " +
                    "Check_Out_Date TEXT, " +
                    "Total_Price INTEGER, " +
                    "Status TEXT, " +
                    "Room_Description TEXT, " +
                    "FOREIGN KEY(Guest_ID) REFERENCES Guests(Guest_ID), " +
                    "FOREIGN KEY(Room_ID) REFERENCES Rooms(Room_ID));";

    private static final String CREATE_TABLE_ROOMS =
            "CREATE TABLE Rooms (" +
                    "Room_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Room_Type TEXT, " +
                    "Description TEXT, " +
                    "Price_Per_Night INTEGER, " +
                    "Availability TEXT);";

    private static final String CREATE_TABLE_SERVICES =
            "CREATE TABLE Services (" +
                    "Service_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Service_Name TEXT, " +
                    "Description TEXT, " +
                    "Price INTEGER, " +
                    "Availability TEXT);";

    private static final String CREATE_TABLE_RESERVATIONS =
            "CREATE TABLE Reservations (" +
                    "Reservation_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Service_ID INTEGER, " +
                    "Guest_ID INTEGER, " +
                    "Reservation_Date TEXT, " +
                    "Reservation_Time TEXT, " +
                    "Status TEXT, " +
                    "FOREIGN KEY(Service_ID) REFERENCES Services(Service_ID), " +
                    "FOREIGN KEY(Guest_ID) REFERENCES Guests(Guest_ID));";

    private static final String CREATE_TABLE_ATTRACTIONS =
            "CREATE TABLE Attractions (" +
                    "Attraction_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Attraction_Name TEXT, " +
                    "Description TEXT, " +
                    "Location TEXT, " +
                    "Availability TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GUESTS_TABLE);
        db.execSQL(CREATE_TABLE_ROOMS);
        insertDefaultRooms(db);
        db.execSQL(CREATE_TABLE_SERVICES);
        insertDefaultServices(db);
        db.execSQL(CREATE_TABLE_RESERVATIONS);
        db.execSQL(CREATE_TABLE_ATTRACTIONS);
        insertDefaultAttractions(db);
        db.execSQL(CREATE_TABLE_BOOKINGS);
    }

    public ArrayList<Room> getFilteredRooms(String roomType, int maxPrice, boolean availability) {
        ArrayList<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "1=1";
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (roomType != null && !roomType.isEmpty()) {
            selection += " AND Room_Type LIKE ?";
            selectionArgs.add("%" + roomType + "%");
        }
        if (maxPrice > 0) {
            selection += " AND Price_Per_Night <= ?";
            selectionArgs.add(String.valueOf(maxPrice));
        }
        if (availability) {
            selection += " AND Availability = 'Available'";
        }

        Cursor cursor = db.query("Rooms", null, selection, selectionArgs.toArray(new String[0]), null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Room room = new Room(
                        cursor.getInt(cursor.getColumnIndex("Room_ID")),
                        cursor.getString(cursor.getColumnIndex("Room_Type")),
                        cursor.getString(cursor.getColumnIndex("Description")),
                        cursor.getInt(cursor.getColumnIndex("Price_Per_Night")),
                        cursor.getString(cursor.getColumnIndex("Availability"))
                );
                roomList.add(room);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return roomList;
    }

    public boolean addOrUpdateGuest(int guestId, String name, String username, String email, String phoneNumber, String travelDates, String preferences) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", name);
        contentValues.put("Username", username);
        contentValues.put("Email", email);
        contentValues.put("Phone_Number", phoneNumber);
        contentValues.put("Travel_Dates", travelDates);
        contentValues.put("Preferences", preferences);

        int rowsUpdated = db.update("Guests", contentValues, "Guest_ID = ?", new String[]{String.valueOf(guestId)});

        if (rowsUpdated > 0) {
            return true;
        } else {
            contentValues.put("Guest_ID", guestId);
            long result = db.insert("Guests", null, contentValues);
            return result != -1;
        }
    }

    public Cursor getGuestDetails(int guestId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Name, Username, Password, Email, Phone_Number, Travel_Dates, Preferences FROM Guests WHERE Guest_ID = ?";
        return db.rawQuery(query, new String[]{String.valueOf(guestId)});
    }


    public Cursor getServicesByPreference(String preferences) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Services WHERE Service_Name LIKE ?";
        return db.rawQuery(query, new String[]{"%" + preferences + "%"});
    }

    public Cursor getAttractionsByPreference(String preferences) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Attractions WHERE Attraction_Name LIKE ?";
        return db.rawQuery(query, new String[]{"%" + preferences + "%"});
    }

    public Cursor getRoomsByPreference(String preferences) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Rooms WHERE Room_Type LIKE ?";
        return db.rawQuery(query, new String[]{"%" + preferences + "%"});
    }

    public boolean addBooking(int guestId, int roomId, String checkInDate, String checkOutDate, int totalPrice, String roomDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Guest_ID", guestId);
        values.put("Room_ID", roomId);
        values.put("Check_In_Date", checkInDate);
        values.put("Check_Out_Date", checkOutDate);
        values.put("Total_Price", totalPrice);
        values.put("Status", "Confirmed");
        values.put("Room_Description", roomDescription);

        long result = db.insert("Bookings", null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Booking> getGuestBookings(int guestId) {
        ArrayList<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.Booking_ID, b.Guest_ID, b.Room_ID, b.Check_In_Date, " +
                "b.Check_Out_Date, b.Total_Price, b.Status, r.Room_Type, " +
                "r.Description AS Room_Description " +
                "FROM Bookings b " +
                "JOIN Rooms r ON b.Room_ID = r.Room_ID " +
                "WHERE b.Guest_ID = ? AND b.Status = 'Confirmed'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(guestId)});

        if (cursor.moveToFirst()) {
            do {
                bookings.add(new Booking(
                        cursor.getInt(cursor.getColumnIndex("Booking_ID")),
                        cursor.getInt(cursor.getColumnIndex("Guest_ID")),
                        cursor.getInt(cursor.getColumnIndex("Room_ID")),
                        cursor.getString(cursor.getColumnIndex("Room_Type")),
                        cursor.getString(cursor.getColumnIndex("Room_Description")),
                        cursor.getString(cursor.getColumnIndex("Check_In_Date")),
                        cursor.getString(cursor.getColumnIndex("Check_Out_Date")),
                        cursor.getInt(cursor.getColumnIndex("Total_Price")),
                        cursor.getString(cursor.getColumnIndex("Status"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookings;
    }

    public boolean updateBooking(int bookingId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update("Bookings", values, "Booking_ID = ?", new String[]{String.valueOf(bookingId)});
        return rowsAffected > 0;
    }

    public boolean deleteBooking(int bookingId) {
        SQLiteDatabase database = this.getWritableDatabase();
        int rowsDeleted = database.delete("Bookings", "Booking_ID = ?", new String[]{String.valueOf(bookingId)});
        return rowsDeleted > 0;
    }

    private void insertDefaultAttractions(SQLiteDatabase db) {
        String insertAttraction1 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Beach Tour', 'Guided tour of the beautiful beachfront and local highlights', 'Nearby Beaches', 'Available')";
        String insertAttraction2 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Water Sports Adventure', 'Enjoy activities like jet skiing, snorkeling, and paddle boarding', 'LuxeVista Resort - Water Sports Center', 'Available')";
        String insertAttraction3 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Sunset Rooftop Dining', 'An exquisite dining experience with stunning sunset views', 'LuxeVista Resort - Rooftop', 'Available')";
        String insertAttraction4 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Stargazing Night', 'Guided stargazing session with telescopes and expert astronomers', 'LuxeVista Resort - Rooftop Observatory', 'Available')";
        String insertAttraction5 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Snorkeling Adventure', 'Explore underwater life with guided snorkeling trips', 'Nearby Coral Reef', 'Available')";
        String insertAttraction6 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Jet Ski Experience', 'Exciting jet ski rides along the shoreline', 'LuxeVista Resort - Watersports Center', 'Available')";
        String insertAttraction7 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Gourmet Tasting', 'Sample dishes from our gourmet menu paired with fine wines', 'LuxeVista Resort - Gourmet Restaurant', 'Available')";
        String insertAttraction8 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Yoga by the Beach', 'Morning yoga sessions with ocean views', 'LuxeVista Resort - Seaside Pavilion', 'Available')";
        String insertAttraction9 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Fishing Expedition', 'Guided fishing trips in the nearby bay', 'LuxeVista Resort - Fishing Dock', 'Available')";
        String insertAttraction10 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Wine and Cheese Evening', 'Enjoy a selection of wines and cheeses at sunset', 'LuxeVista Resort - Wine Cellar', 'Available')";
        String insertAttraction11 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Scenic Beach Walk', 'Enjoy a leisurely walk along the pristine coastline', 'Nearby Beaches', 'Available')";
        String insertAttraction12 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Luxury Catamaran Cruise', 'Experience a luxurious sunset cruise on a private catamaran', 'Nearby Marina', 'Available')";
        String insertAttraction13 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Seaside Barbecue', 'A beachfront barbecue with freshly grilled seafood and live music', 'LuxeVista Resort - Beachfront', 'Available')";
        String insertAttraction14 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Cultural Dance Show', 'Witness traditional dances and local cultural performances', 'LuxeVista Resort - Amphitheater', 'Available')";
        String insertAttraction15 = "INSERT INTO Attractions (Attraction_Name, Description, Location, Availability) " +
                "VALUES ('Sea Kayaking Adventure', 'Explore hidden coves and coastal landmarks by kayak', 'Nearby Cove', 'Available')";

        db.execSQL(insertAttraction1);
        db.execSQL(insertAttraction2);
        db.execSQL(insertAttraction3);
        db.execSQL(insertAttraction4);
        db.execSQL(insertAttraction5);
        db.execSQL(insertAttraction6);
        db.execSQL(insertAttraction7);
        db.execSQL(insertAttraction8);
        db.execSQL(insertAttraction9);
        db.execSQL(insertAttraction10);
        db.execSQL(insertAttraction11);
        db.execSQL(insertAttraction12);
        db.execSQL(insertAttraction13);
        db.execSQL(insertAttraction14);
        db.execSQL(insertAttraction15);
    }

    private void insertDefaultServices(SQLiteDatabase db) {
        String insertService1 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Ocean View Spa', 'Relaxing spa treatments with ocean views', 12000, 'Available')";
        String insertService2 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Gourmet Dinner', 'Exclusive fine dining experience with a five-course meal', 20000, 'Available')";
        String insertService3 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Poolside Cabana', 'Private poolside cabana with personalized service', 15000, 'Available')";
        String insertService4 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Guided Beach Tour', 'A guided tour of the beach with insights into local marine life', 8000, 'Available')";
        String insertService5 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Sunset Yoga', 'Evening yoga sessions overlooking the ocean sunset', 5000, 'Available')";
        String insertService6 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Wine Tasting', 'Sample a curated selection of fine wines from local vineyards', 10000, 'Available')";
        String insertService7 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Personal Fitness Training', 'Private fitness training sessions tailored to your needs', 9000, 'Available')";
        String insertService8 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Kayak Adventure', 'A guided kayak adventure along the coastline', 6000, 'Available')";
        String insertService9 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Evening Jazz Lounge', 'Exclusive access to the jazz lounge with live performances', 4000, 'Available')";
        String insertService10 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Cooking Workshop', 'Learn to cook with our chefs in a hands-on workshop', 7500, 'Available')";
        String insertService11 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Private Beach Dinner', 'A romantic private dining experience by the beach', 25000, 'Available')";
        String insertService12 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Luxury Spa Retreat', 'Full-day spa retreat including massages, facials, and more', 30000, 'Available')";
        String insertService13 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('VIP Pool Access', 'Exclusive VIP access to private pools with luxury amenities', 18000, 'Available')";
        String insertService14 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Sunrise Yoga', 'Morning yoga sessions to rejuvenate your body and mind', 5000, 'Available')";
        String insertService15 = "INSERT INTO Services (Service_Name, Description, Price, Availability) " +
                "VALUES ('Candlelight Spa Evening', 'Evening spa treatments in a serene candlelit environment', 22000, 'Available')";

        db.execSQL(insertService1);
        db.execSQL(insertService2);
        db.execSQL(insertService3);
        db.execSQL(insertService4);
        db.execSQL(insertService5);
        db.execSQL(insertService6);
        db.execSQL(insertService7);
        db.execSQL(insertService8);
        db.execSQL(insertService9);
        db.execSQL(insertService10);
        db.execSQL(insertService11);
        db.execSQL(insertService12);
        db.execSQL(insertService13);
        db.execSQL(insertService14);
        db.execSQL(insertService15);
    }

    private void insertDefaultRooms(SQLiteDatabase db) {
        String insertRoom1 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Ocean View Suite', 'A luxurious room with a stunning view of the ocean', 25000, 'Available')";
        String insertRoom2 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Garden View Suite', 'A peaceful room overlooking our lush gardens', 18000, 'Available')";
        String insertRoom3 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Poolside Suite', 'A spacious suite with direct access to the pool area', 30000, 'Available')";
        String insertRoom4 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Luxury Suite', 'A spacious suite with a king-sized bed and premium amenities', 50000, 'Booked')";
        String insertRoom5 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Beachfront Double Room', 'An elegant room by the beach with double beds', 75000, 'Available')";
        String insertRoom6 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Single Room', 'A comfortable room for solo travelers with a queen-sized bed', 8000, 'Available')";
        String insertRoom7 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Double Room', 'A cozy room with two double beds, perfect for couples or friends', 15000, 'Booked')";
        String insertRoom8 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Suite', 'An elegant suite with a separate living area and bedroom', 35000, 'Available')";
        String insertRoom9 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Penthouse', 'A luxurious penthouse suite with panoramic city views and a private jacuzzi', 100000, 'Available')";
        String insertRoom10 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Single Room', 'A compact room ideal for solo stays, featuring modern amenities', 7000, 'Available')";
        String insertRoom11 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Double Room', 'Spacious and comfortable, with two beds and a modern bathroom', 16000, 'Available')";
        String insertRoom12 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Ocean View Suite', 'A suite with a breathtaking view of the ocean and high-end furnishings', 28000, 'Booked')";
        String insertRoom13 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Garden View Suite', 'Relax in a suite overlooking lush gardens with a king-sized bed', 22000, 'Available')";
        String insertRoom14 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Penthouse', 'A top-tier penthouse with floor-to-ceiling windows and a luxury kitchen', 120000, 'Booked')";
        String insertRoom15 = "INSERT INTO Rooms (Room_Type, Description, Price_Per_Night, Availability) " +
                "VALUES ('Luxury Suite', 'An exquisite suite featuring a jacuzzi, balcony, and premium services', 55000, 'Available')";

        db.execSQL(insertRoom1);
        db.execSQL(insertRoom2);
        db.execSQL(insertRoom3);
        db.execSQL(insertRoom4);
        db.execSQL(insertRoom5);
        db.execSQL(insertRoom6);
        db.execSQL(insertRoom7);
        db.execSQL(insertRoom8);
        db.execSQL(insertRoom9);
        db.execSQL(insertRoom10);
        db.execSQL(insertRoom11);
        db.execSQL(insertRoom12);
        db.execSQL(insertRoom13);
        db.execSQL(insertRoom14);
        db.execSQL(insertRoom15);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Guests");
        db.execSQL("DROP TABLE IF EXISTS Profile");
        db.execSQL("DROP TABLE IF EXISTS Booking_History");
        db.execSQL("DROP TABLE IF EXISTS Rooms");
        db.execSQL("DROP TABLE IF EXISTS Services");
        db.execSQL("DROP TABLE IF EXISTS Reservations");
        db.execSQL("DROP TABLE IF EXISTS Attractions");
        db.execSQL("DROP TABLE IF EXISTS Bookings");
        onCreate(db);
    }
}
