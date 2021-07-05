package com.nimexpress.nimexpressidcardtdaandsigninsurance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.nimexpress.nimexpressidcardtdaandsigninsurance.model.MobileKeyFixSqlModel;

public class DBHelperMobileKey extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase;

    private final String TAG = getClass().getSimpleName();

    public DBHelperMobileKey(@Nullable Context context) {
        super(context, "mobile_key_sql.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_bt_sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                "%s TEXT,%s TEXT)",
                MobileKeyFixSqlModel.TABLE,
                MobileKeyFixSqlModel.Column.ID,
                MobileKeyFixSqlModel.Column.KEY_CODE,
                MobileKeyFixSqlModel.Column.IS_FIX_MODE
                );
        db.execSQL(create_bt_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_mobile_key_sql = "DROP TABLE IF EXISTS mobile_key_sql";

        db.execSQL(DROP_mobile_key_sql);

        onCreate(db);

    }

    public MobileKeyFixSqlModel getMobileKeyFixSQLData(){
        MobileKeyFixSqlModel mobileKeyFixSqlModel = null;
        sqLiteDatabase = this.getWritableDatabase();

        String[] projection = {
                MobileKeyFixSqlModel.Column.ID,
                MobileKeyFixSqlModel.Column.KEY_CODE,
                MobileKeyFixSqlModel.Column.IS_FIX_MODE,
        };


        Cursor cursor = sqLiteDatabase.query(MobileKeyFixSqlModel.TABLE,
                projection,
            null,
                null,
                null,
                null,
                null);
        if (cursor!=null){
            cursor.moveToFirst();
            if (cursor.getCount()==0){
                return null;
            }
            mobileKeyFixSqlModel = new MobileKeyFixSqlModel();
            mobileKeyFixSqlModel.setId(cursor.getInt(0));
            mobileKeyFixSqlModel.setKEY_CODE(cursor.getString(1));
            mobileKeyFixSqlModel.setIS_FIX_MODE(cursor.getString(2));
        }
        sqLiteDatabase.close();
        return mobileKeyFixSqlModel;
    }


    public long add_data_to_sql(MobileKeyFixSqlModel mobileKeyFixSqlModel){

        sqLiteDatabase = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MobileKeyFixSqlModel.Column.KEY_CODE, mobileKeyFixSqlModel.getKEY_CODE());
        values.put(MobileKeyFixSqlModel.Column.IS_FIX_MODE, mobileKeyFixSqlModel.getIS_FIX_MODE());

// Insert the new row, returning the primary key value of the new row
        long newRowId = sqLiteDatabase.insert(MobileKeyFixSqlModel.TABLE, null, values);
        sqLiteDatabase.close();

        return newRowId;
    }

    public int update_data_to_sql(MobileKeyFixSqlModel mobileKeyFixSqlModel){
        sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MobileKeyFixSqlModel.Column.KEY_CODE, mobileKeyFixSqlModel.getKEY_CODE());
        values.put(MobileKeyFixSqlModel.Column.IS_FIX_MODE, mobileKeyFixSqlModel.getIS_FIX_MODE());

// Which row to update, based on the title
        String selection = MobileKeyFixSqlModel.Column.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(mobileKeyFixSqlModel.getId())};

        int count = sqLiteDatabase.update(
                MobileKeyFixSqlModel.TABLE,
                values,
                selection,
                selectionArgs);
        sqLiteDatabase.close();
        return count;
    }

    public int delete_data_to_sql(MobileKeyFixSqlModel mobileKeyFixSqlModel){
        sqLiteDatabase = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = MobileKeyFixSqlModel.Column.ID + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(mobileKeyFixSqlModel.getId())};
// Issue SQL statement.
        int deletedRows = sqLiteDatabase.delete(MobileKeyFixSqlModel.TABLE, selection, selectionArgs);
        sqLiteDatabase.close();

        return deletedRows;
    }
}
