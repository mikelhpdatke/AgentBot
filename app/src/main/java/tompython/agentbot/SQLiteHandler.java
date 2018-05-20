package tompython.agentbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";


    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "IPManager";


    private static final String TABLE_IP = "BlackIP";

    private static final String COLUMN_IP_ID ="IP_Id";
    private static final String COLUMN_IP_CONTENT = "IP_Content";

    public SQLiteHandler(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tạo các bảng.
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script tạo bảng.
        String script = "CREATE TABLE " + TABLE_IP + "("
                + COLUMN_IP_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_IP_CONTENT + " TEXT" + ")";
        // Chạy lệnh tạo bảng.
        db.execSQL(script);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");

        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IP);
        // Và tạo lại.
        onCreate(db);
    }


    public void addIP(FoundIP ip) {
        Log.i(TAG, "MyDatabaseHelper.addIP ... " + ip.getIp_addr());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IP_CONTENT, ip.getIp_addr());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_IP, null, values);
        // Đóng kết nối database.
        db.close();
    }


    public FoundIP getIP(int id) {
        Log.i(TAG, "MyDatabaseHelper.getNote ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IP, new String[] { COLUMN_IP_ID,
                        COLUMN_IP_CONTENT }, COLUMN_IP_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FoundIP ip = new FoundIP(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
        // return note
        return ip;
    }

    /*
    public List<Note> getAllNotes() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setNoteId(Integer.parseInt(cursor.getString(0)));
                note.setNoteTitle(cursor.getString(1));
                note.setNoteContent(cursor.getString(2));

                // Thêm vào danh sách.
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }
    */
    public int getIPCount() {
        Log.i(TAG, "MyDatabaseHelper.getIPCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_IP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int updateIP(FoundIP ip) {
        Log.i(TAG, "MyDatabaseHelper.updateIP ... "  + ip.getIp_addr());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(COLUMN_IP_TITLE, note.getNoteTitle());
        values.put(COLUMN_IP_CONTENT, ip.getIp_addr());

        // updating row
        return db.update(TABLE_IP, values, COLUMN_IP_ID + " = ?",
                new String[]{String.valueOf(ip.getId())});
    }

    public void deleteIP(FoundIP ip) {
        //Log.i(TAG, "MyDatabaseHelper.updateNote ... " + note.getNoteTitle() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IP, COLUMN_IP_ID + " = ?",
                new String[] { String.valueOf(ip.getIp_addr()) });
        db.close();
    }
}
