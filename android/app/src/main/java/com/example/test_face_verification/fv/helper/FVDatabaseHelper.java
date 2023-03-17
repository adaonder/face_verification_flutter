package com.example.test_face_verification.fv.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.test_face_verification.common.util.LogUtil;
import com.example.test_face_verification.fv.data.FVData;

import java.util.HashSet;
import java.util.Set;

public class FVDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "FVDatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FaceVerification.db";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
            FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
            FeedReaderContract.FeedEntry.EMPLOYEE_ID + " TEXT," +
            FeedReaderContract.FeedEntry.IMAGE_URL + " LONGTEXT," +
            FeedReaderContract.FeedEntry.SUBJECT_TEMPLATE + " BLOB)";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public FVDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    public long delete(String ID) {
        return this.getWritableDatabase().delete(FeedReaderContract.FeedEntry.TABLE_NAME, FeedReaderContract.FeedEntry._ID + "=" + ID, null);
    }

    public boolean update(String id, String imageUrl, byte[] template) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        //contentValues.put(FeedReaderContract.FeedEntry.EMPLOYEE_ID, employeeId);
        contentValues.put(FeedReaderContract.FeedEntry.IMAGE_URL, imageUrl);
        contentValues.put(FeedReaderContract.FeedEntry.SUBJECT_TEMPLATE, template);
        long rowID = db.update(FeedReaderContract.FeedEntry.TABLE_NAME, contentValues, FeedReaderContract.FeedEntry._ID + "=" + id, null);
        return rowID != -1;
    }

    public boolean insert(String employeeID, String imageUrl, byte[] template) {
        LogUtil.e("FV insert");
        boolean success = false;
        boolean isNewData = true;
        for (FVData data : listData()) {
            if (employeeID.equals(data.getEmployeeId())) {
                LogUtil.e("DB already contains this subjectID : " + data.getId());
                isNewData = false;
                success = update(data.getId(), imageUrl, template);
                break;
            }
        }

        if (isNewData) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.EMPLOYEE_ID, employeeID);
            values.put(FeedReaderContract.FeedEntry.IMAGE_URL, imageUrl);
            values.put(FeedReaderContract.FeedEntry.SUBJECT_TEMPLATE, template);
            long rowID = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
            success = rowID != -1;
        }

        return success;
    }

    /*public List<String> listEmployeeIDs() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {FeedReaderContract.FeedEntry.EMPLOYEE_ID, FeedReaderContract.FeedEntry.IMAGE_URL};
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List subjectIDs = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String subjectID = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.EMPLOYEE_ID));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.IMAGE_URL));
            LogUtil.e("subjectID: " + subjectID + ", imageUrl: " + imageUrl);
            subjectIDs.add(subjectID);
        }
        cursor.close();
        return subjectIDs;
    }*/

    public Set<FVData> listData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {FeedReaderContract.FeedEntry._ID, FeedReaderContract.FeedEntry.EMPLOYEE_ID, FeedReaderContract.FeedEntry.IMAGE_URL};
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        Set<FVData> FVDataList = new HashSet<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String employeeId = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.EMPLOYEE_ID));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.IMAGE_URL));

            /*boolean isAvailable = false;
            for (FVData fvData : FVDataList) {
                if (fvData.getEmployeeId().equals(employeeId) && fvData.getImage_url().equals(imageUrl)) {
                    isAvailable = true;
                    break;
                }
            }*/

            LogUtil.e("++ employeeId: " + employeeId + ", imageUrl: " + imageUrl);
            FVData fvData = new FVData();
            fvData.setId(id);
            fvData.setEmployeeId(employeeId);
            fvData.setImageUrl(imageUrl);
            FVDataList.add(fvData);

            /*if (!isAvailable) {

            }*/
        }
        cursor.close();
        return FVDataList;
    }

    public byte[] getTemplate(FVData FVData) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {FeedReaderContract.FeedEntry.SUBJECT_TEMPLATE};
        String selection = FeedReaderContract.FeedEntry.EMPLOYEE_ID + " = ?";
        String[] selectionArgs = {FVData.getEmployeeId()};
        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.getColumnCount() > 1) {
            throw new IllegalStateException("DB returned few elements: " + cursor.getColumnCount());
        } else {
            cursor.moveToFirst();
            return cursor.getBlob(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.SUBJECT_TEMPLATE));
        }
    }

}

