package com.example.mystockassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class DataHandlr extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";
    //DB Table Name
    private static final String TABLE_NAME = "StockTable";
    //DB column
    private static final String SYMBOL = "Symbol";
    private static final String NAME = "Name";
    private static final String PRICE = "Price";
    private static final String CHANGE = "Change";
    private static final String PERCENT = "Percent";
    private static final String VALUE = "Value";
    private final SQLiteDatabase database;
    private MainActivity mainActivity;

    //Create Table
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    NAME +  " TEXT not null," +
                    PRICE + " DOUBLE not null," +
                    CHANGE + " DOUBLE not null," +
                    PERCENT + " DOUBLE not null," +
                    VALUE + " TEXT not null)";


    public DataHandlr(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<Stock> loadStocks() {
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME, // The Table To Query
                new String[]{SYMBOL,NAME,PRICE,CHANGE,PERCENT,VALUE}, // The Columns To Return
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                Double price = cursor.getDouble(2);
                Double change = cursor.getDouble(3);
                Double percent = cursor.getDouble(4);
                String value = cursor.getString(5);
                Stock s = new Stock(symbol,name,price,change,percent,value);
                stocks.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    void addStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getStockSymbol());
        values.put(NAME, stock.getCName());
        values.put(PRICE, stock.getPrice());
        values.put(CHANGE, stock.getPChange());
        values.put(PERCENT, stock.getChangePercent());
        values.put(VALUE, stock.getValue());

        long key = database.insert(TABLE_NAME, null, values);
    }

    void updateStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getStockSymbol());
        values.put(NAME, stock.getCName());
        values.put(PRICE, stock.getPrice());
        values.put(CHANGE, stock.getPChange());
        values.put(PERCENT, stock.getChangePercent());
        values.put(VALUE, stock.getValue());

        long numRows = database.update(TABLE_NAME, values, SYMBOL + " = ?",
                new String[] {
                        String.format(Locale.getDefault(), "%s", stock.getStockSymbol())}
        );
    }

    void deleteStock(String stoID) {
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?",
                new String[]{
                        String.format(Locale.getDefault(), "%s", stoID)}
        );
    }

    void shutDown() {
        database.close();
    }
}
