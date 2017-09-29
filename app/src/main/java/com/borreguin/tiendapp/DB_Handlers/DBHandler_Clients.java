package com.borreguin.tiendapp.DB_Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.ParcelFormatException;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto on 8/29/2017.
 * com.borreguin.tiendapp
 */

public class DBHandler_Clients extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 8;

    // Database Name
    private static final String DATABASE_NAME = "DB_infoClients";

    // Table names
    private static final String TABLE_CLIENTS = "tbl_clients";
    private static final String TABLE_METADATA = "tbl_metadata";

    // Clients Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DSCRP = "description";
    private static final String KEY_ToPAY = "toPay";
    private static final String KEY_ToRELY = "toRely";
    private static final String KEY_ACCOUNT = "accountDetail";
    private static final String KEY_DATE_UPDATE = "dateUpdate";

    // Metadata values:
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    // Id for getting values in string constructor
    private static final int Idx_id_0 = 0, Idx_name_1 = 1, Idx_dscrp_2 = 2,
            Idx_toPay_3 = 3, Idx_toRely_4 =4, Idx_account_5 = 5, Idx_dateUpdate_6 =6;

    // Global values
    private Global global = new Global();

    // Constructor
    public DBHandler_Clients(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLE OF CLIENTS
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CLIENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_DSCRP + " TEXT,"
                + KEY_ToPAY + " FLOAT,"
                + KEY_ToRELY + " INTEGER,"
                + KEY_ACCOUNT + " INTEGER,"
                + KEY_DATE_UPDATE + " TEXT"
                + ")";

        /*                  TYPE        Primary     Description
            ID:             Integer     yes         ID del cliente
            name:           text                    Nombre del cliente
            description:    text                    descripción del cliente
            toPay      :    Float                   Deuda del cliente
            toRely     :    Integer                 Se puede fiar al cliente? ( 0 -> false)
            account    :    Integer                Numero de cuenta actual
            date_update:    Text                    Fecha de actualización

         */

        // For key and value table, KEY_ID is unique
        // TABLE METADATA_CLIENTS
        String CREATE_METADATA = "CREATE TABLE " + TABLE_METADATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_KEY + " STRING, "
                + KEY_VALUE + " STRING "
                + ")";
        /*                  TYPE        Primary     Description
            ID:             text     yes         ID del member
            detail:         text                    detail document
         */
        try {
            db.execSQL(CREATE_METADATA);
        }catch (SQLException e){
            e.printStackTrace();
        }

        try {
            db.execSQL(CREATE_CONTACTS_TABLE);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
        // Creating tables again
        onCreate(db);
    }

    // Adding new client
    public void addClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, client.getName()); // Client Name
        values.put(KEY_DSCRP, client.getDescription()); // Client Description
        values.put(KEY_ToPAY, client.getToPay()); // to Pay
        values.put(KEY_ToRELY, client.getToRely_int()); // to Give products
        values.put(KEY_ACCOUNT,  client.getAccount());  // Account number
        values.put(KEY_DATE_UPDATE, client.getDate_update_string());
        // Inserting Row
        db.insert(TABLE_CLIENTS, null, values);
        // Closing database connection
        db.close();
    }

    // Getting one client using the name of the client
    public Client getClient(String ClientName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLIENTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DSCRP, KEY_ToPAY, KEY_ToRELY,
                        KEY_ACCOUNT, KEY_DATE_UPDATE},
                KEY_NAME + "=?",
                new String[]{ClientName}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return new Client();
        }

        try {
            Client contact = new Client(
                    Integer.parseInt(cursor.getString(Idx_id_0)),
                    cursor.getString(Idx_name_1),
                    cursor.getString(Idx_dscrp_2),
                    Float.parseFloat(cursor.getString(Idx_toPay_3)),
                    Integer.parseInt(cursor.getString(Idx_toRely_4)),
                    Integer.parseInt(cursor.getString(Idx_account_5)),
                    cursor.getString(Idx_dateUpdate_6)
            );
            cursor.close();
            // return client
            return contact;

        }catch (ParseException e) {
            e.printStackTrace();
        }

        return new Client();
    }


    public Client getClient(int idClient) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CLIENTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DSCRP, KEY_ToPAY, KEY_ToRELY,
                        KEY_ACCOUNT, KEY_DATE_UPDATE},
                KEY_ID + "=?",
                new String[]{String.valueOf(idClient)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return new Client();
        }

        try {
            Client contact = new Client(
                    Integer.parseInt(cursor.getString(Idx_id_0)),
                    cursor.getString(Idx_name_1),
                    cursor.getString(Idx_dscrp_2),
                    Float.parseFloat(cursor.getString(Idx_toPay_3)),
                    Integer.parseInt(cursor.getString(Idx_toRely_4)),
                    Integer.parseInt(cursor.getString(Idx_account_5)),
                    cursor.getString(Idx_dateUpdate_6)
            );
            cursor.close();
            db.close();
            // return client
            return contact;

        }catch (ParseException e) {
            e.printStackTrace();
        }
        cursor.close();
        db.close();
        return new Client();
    }





    // Getting All Clients
    public List<Client> getAllClients() {
        List<Client> clientList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLIENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //try {

            if(cursor.getCount()<=0){
                Client temporal = new Client(global.id_temp, global.prefix + "", "", 0);
                clientList.add(temporal);
                cursor.close();
                db.close();
                return clientList;
            }
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    try {
                        Client client = new Client();
                        client.setId(Integer.parseInt(cursor.getString(Idx_id_0)));
                        client.setName(cursor.getString(Idx_name_1));
                        client.setDescription(cursor.getString(Idx_dscrp_2));
                        client.setToPay(Float.parseFloat(cursor.getString(Idx_toPay_3)));
                        client.setToRely(Integer.parseInt(cursor.getString(Idx_toRely_4)));
                        client.setAccount(Integer.parseInt(cursor.getString(Idx_account_5)));
                        client.setDate_update(cursor.getString(Idx_dateUpdate_6));
                        // Adding contact to list
                        clientList.add(client);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        //}catch (SQLException e){
        //    e.printStackTrace();
        //}catch (Exception e){
        //    e.printStackTrace();
        //}


        // return contact list
        return clientList;
    }

    // Getting clients Count
    public int getClientsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CLIENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        // return count
        return cursor.getCount();
    }

    // Updating a client
    public int updateClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        client.update_date();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, client.getName());
        values.put(KEY_DSCRP, client.getDescription());
        values.put(KEY_ToPAY, client.getToPay());
        values.put(KEY_ToRELY, client.getToRely_int());
        values.put(KEY_ACCOUNT, client.getAccount());
        values.put(KEY_DATE_UPDATE, client.getDate_update_string());

        // updating row
        try {
            return db.update(TABLE_CLIENTS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(client.getId())});
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    // Deleting a client
    public void deleteClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLIENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(client.getId()) });
        db.close();
    }
    
}
