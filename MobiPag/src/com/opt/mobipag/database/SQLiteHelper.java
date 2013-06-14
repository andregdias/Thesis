package com.opt.mobipag.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class SQLiteHelper extends SQLiteOpenHelper {

    //tabela Precos
    public static final String TABLE_PRICES = "prices";
    public static final String COLUMN_TYPE = "typology";
    public static final String COLUMN_PRICE_OCCASIONAL = "occasional";
    public static final String COLUMN_PRICE_ANDANTE24 = "andante24";
    public static final String COLUMN_PRICE_SIGNATURE = "signature";
    public static final String COLUMN_NUM_ZONES = "numzones";
    public static final String COLUMN_TRAVEL_TIME = "traveltime";
    //tabela Utilizadores
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID_USER = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PIN = "pin";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_NIF = "nif";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_BALANCE = "balance";
    public static final String COLUMN_MAXAMOUNT = "maxamount";
    public static final String COLUMN_PUBLIC = "public";
    public static final String COLUMN_PRIVATE = "private";
    //tabela Bilhetes
    public static final String TABLE_TICKETS = "tickets";
    public static final String COLUMN_ID_TICKET = "_id";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_STATUS = "status"; //0 - Por usar; 1 - Em uso; 2 - Usado
    public static final String COLUMN_SECURITY = "securitycode";
    public static final String COLUMN_ID_USER2 = "iduser";
    public static final String COLUMN_FIRST_VALIDATION = "first_val";
    private static final String COLUMN_FIRST_STOP = "first_stop";
    //tabela Ocasionais
    public static final String TABLE_OCCASIONALS = "occasionals";
    public static final String COLUMN_NUMZONES = "numzones";
    public static final String COLUMN_OCCASIONAL_TRAVELTIME = "traveltime";
    public static final String COLUMN_ID_TICKET2 = "idticket";
    //tabela Assinaturas
    public static final String TABLE_SIGNATURES = "signatures";
    public static final String COLUMN_NUMVALID = "numvalidations";
    public static final String COLUMN_SIGNATURE_TRAVELTIME = "traveltime";
    //tabela Zonas
    public static final String TABLE_ZONES = "zones";
    public static final String COLUMN_ID_ZONE = "_id";
    public static final String COLUMN_DESCRIPTOR = "descriptor";
    //tabela Assinaturas_Zonas
    public static final String TABLE_SIGNATURES_ZONES = "signature_zones";
    public static final String COLUMN_IDSIGNATURE = "id_signature";
    public static final String COLUMN_IDZONE = "id_zone";
    //tabela Paragens
    public static final String TABLE_STOPS = "stops";
    public static final String COLUMN_ID_STOP = "_id";
    public static final String COLUMN_STOPNAME = "name";
    public static final String COLUMN_CODSMS = "smscode";
    public static final String COLUMN_OPERATOR = "operator";
    public static final String COLUMN_COORDX = "coordx";
    public static final String COLUMN_COORDY = "coordy";
    public static final String COLUMN_HISTORY = "history";
    //tabela Linhas
    public static final String TABLE_LINES = "lines";
    public static final String COLUMN_ID_LINE = "_id";
    public static final String COLUMN_LINECODE = "descriptor";
    public static final String COLUMN_LINENAME = "name";
    public static final String COLUMN_PATHCODE = "pathcode";
    //tabela Validacoes
    public static final String TABLE_VALIDATIONS = "validations";
    public static final String COLUMN_IDVALIDATION = "id_validation";
    public static final String COLUMN_IDTICKET = "id_ticket";
    public static final String COLUMN_IDSTOP2 = "id_stop";
    public static final String COLUMN_IDLINE = "id_line";
    public static final String COLUMN_SEQ_ID = "seq_id";
    public static final String COLUMN_VALIDATIONDATE = "validation_date";
    //tabela Historico
    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID_HIST = "_id";
    public static final String COLUMN_ID_USER3 = "id_user";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DETAILS2 = "details";
    private static final String DATABASE_NAME = "movepag.db";
    private static final int DATABASE_VERSION = 26;
    private static final String TABLE_PRICES_CREATE = "create table " + TABLE_PRICES +
            "(" + COLUMN_TYPE + " text primary key, " +
            COLUMN_PRICE_OCCASIONAL + " real not null, " +
            COLUMN_PRICE_ANDANTE24 + " real not null, " +
            COLUMN_PRICE_SIGNATURE + " real not null, " +
            COLUMN_NUM_ZONES + " integer not null, " +
            COLUMN_TRAVEL_TIME + " integer not null" +
            ");";
    private static final String TABLE_USERS_CREATE = "create table " + TABLE_USERS +
            "(" + COLUMN_ID_USER + " integer primary key autoincrement, " +
            COLUMN_EMAIL + " text not null, " +
            COLUMN_PASSWORD + " text not null, " +
            COLUMN_PIN + " text not null, " +
            COLUMN_NAME + " text, " +
            COLUMN_ADDRESS + " text, " +
            COLUMN_MOBILE + " text, " +
            COLUMN_NIF + " text, " +
            COLUMN_DOB + " text, " +
            COLUMN_BALANCE + " real not null, " +
            COLUMN_MAXAMOUNT + " integer, " +
            COLUMN_PRIVATE + " text not null, " +
            COLUMN_PUBLIC + " text not null, " +
            "UNIQUE (" + COLUMN_EMAIL + "));";
    private static final String TABLE_TICKETS_CREATE = "create table " + TABLE_TICKETS +
            "(" + COLUMN_ID_TICKET + " integer primary key autoincrement, " +
            COLUMN_DETAILS + " text not null, " +
            COLUMN_PRICE + " real, " +
            COLUMN_STATUS + " integer not null, " +
            COLUMN_SECURITY + " text, " +
            COLUMN_ID_USER2 + " integer not null, " +
            COLUMN_FIRST_VALIDATION + " text, " +
            COLUMN_FIRST_STOP + " text, " +
            "FOREIGN KEY (" + COLUMN_ID_USER2 + ") REFERENCES " + TABLE_USERS + " (" + COLUMN_ID_USER + "));";
    private static final String TABLE_OCCASIONALS_CREATE = "create table " + TABLE_OCCASIONALS +
            "(" + COLUMN_NUMZONES + " integer not null, " +
            COLUMN_OCCASIONAL_TRAVELTIME + " integer not null, " +
            COLUMN_ID_TICKET2 + " integer not null, " +
            "FOREIGN KEY (" + COLUMN_ID_TICKET2 + ") REFERENCES " + TABLE_TICKETS + " (" + COLUMN_ID_TICKET + "));";
    private static final String TABLE_SIGNATURE_CREATE = "create table " + TABLE_SIGNATURES +
            "(" + COLUMN_NUMVALID + " integer not null, " +
            COLUMN_OCCASIONAL_TRAVELTIME + " integer not null, " +
            COLUMN_ID_TICKET2 + " integer not null, " +
            "FOREIGN KEY (" + COLUMN_ID_TICKET2 + ") REFERENCES " + TABLE_TICKETS + " (" + COLUMN_ID_TICKET + "));";
    private static final String TABLE_ZONES_CREATE = "create table " + TABLE_ZONES +
            "(" + COLUMN_ID_ZONE + " integer primary key autoincrement, " +
            COLUMN_DESCRIPTOR + " text not null, " +
            "UNIQUE (" + COLUMN_DESCRIPTOR + "));";
    private static final String TABLE_SIGNATURES_ZONES_CREATE = "create table " + TABLE_SIGNATURES_ZONES +
            "(" + COLUMN_IDSIGNATURE + " integer not null, " +
            COLUMN_IDZONE + " integer not null, " +
            "PRIMARY KEY (" + COLUMN_IDSIGNATURE + ", " + COLUMN_IDZONE + "));";
    private static final String TABLE_STOPS_CREATE = "create table " + TABLE_STOPS +
            "(" + COLUMN_ID_STOP + " integer primary key autoincrement, " +
            COLUMN_STOPNAME + " text not null, " +
            COLUMN_CODSMS + " text not null, " +
            COLUMN_OPERATOR + " text not null, " +
            COLUMN_COORDX + " real not null, " +
            COLUMN_COORDY + " real not null, " +
            COLUMN_HISTORY + " integer, " +
            "UNIQUE (" + COLUMN_CODSMS + "));";
    private static final String TABLE_LINES_CREATE = "create table " + TABLE_LINES +
            "(" + COLUMN_ID_LINE + " integer primary key autoincrement, " +
            COLUMN_LINECODE + " text not null, " +
            COLUMN_LINENAME + " text not null, " +
            COLUMN_PATHCODE + " text not null, " +
            "UNIQUE (" + COLUMN_LINECODE + "));";
    private static final String TABLE_VALIDATIONS_CREATE = "create table " + TABLE_VALIDATIONS +
            "(" + COLUMN_IDVALIDATION + " integer primary key autoincrement, " +
            COLUMN_IDTICKET + " integer not null, " +
            COLUMN_IDSTOP2 + " integer not null, " +
            COLUMN_IDLINE + " integer not null, " +
            COLUMN_SEQ_ID + " integer not null, " +
            COLUMN_VALIDATIONDATE + " text);";
    private static final String TABLE_HISTORY_CREATE = "create table " + TABLE_HISTORY +
            "(" + COLUMN_ID_HIST + " integer primary key autoincrement, " +
            COLUMN_ID_USER3 + " integer not null, " +
            COLUMN_DATE + " text not null, " +
            COLUMN_AMOUNT + " real not null, " +
            COLUMN_DETAILS2 + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys=ON";
        database.execSQL(ENABLE_FOREIGN_KEYS);
        database.execSQL(TABLE_PRICES_CREATE);
        database.execSQL(TABLE_USERS_CREATE);
        database.execSQL(TABLE_TICKETS_CREATE);
        database.execSQL(TABLE_OCCASIONALS_CREATE);
        database.execSQL(TABLE_SIGNATURE_CREATE);
        database.execSQL(TABLE_ZONES_CREATE);
        database.execSQL(TABLE_SIGNATURES_ZONES_CREATE);
        database.execSQL(TABLE_STOPS_CREATE);
        database.execSQL(TABLE_LINES_CREATE);
        database.execSQL(TABLE_VALIDATIONS_CREATE);
        database.execSQL(TABLE_HISTORY_CREATE);
        insertData(database);
    }

    private void insertData(SQLiteDatabase database) {
        for (int i = 1; i <= 17; i++)
            database.execSQL("INSERT INTO " + TABLE_ZONES + " VALUES (null, 'N" + i + "')");
        for (int i = 1; i <= 16; i++)
            database.execSQL("INSERT INTO " + TABLE_ZONES + " VALUES (null, 'C" + i + "')");
        for (int i = 1; i <= 13; i++)
            database.execSQL("INSERT INTO " + TABLE_ZONES + " VALUES (null, 'S" + i + "')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNATURES_ZONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VALIDATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OCCASIONALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNATURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICES);
        onCreate(db);
    }
}