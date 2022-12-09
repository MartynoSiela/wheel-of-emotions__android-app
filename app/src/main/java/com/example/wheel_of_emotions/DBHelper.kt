package com.example.wheel_of_emotions

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                EMOTION_ID_COL + " INTEGER," +
                DATE_COL + " LONG" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun clearTable(table : String) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $table")
        // TODO this might be needed in the future...
        // db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = '$TABLE_NAME'");
    }

    fun addEmotion(emotion_id : Int, date : Long ){

        val values = ContentValues()
        values.put(EMOTION_ID_COL, emotion_id)
        values.put(DATE_COL, date)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    fun getEmotions(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    companion object{
        private const val DATABASE_NAME = "database_emotions_wheel"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "table_emotions"
        private const val ID_COL = "id"
        private const val EMOTION_ID_COL = "emotion"
        private const val DATE_COL = "date"
    }
}