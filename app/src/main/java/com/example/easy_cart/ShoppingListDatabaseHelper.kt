package com.example.easy_cart

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull

class ShoppingListDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "shoppinglist.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "alllist"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT,deleted INTEGER DEFAULT 0, favorite INTEGER DEFAULT 0)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertList(shoppingList: ShoppingList){
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_TITLE, shoppingList.title)
            put(COLUMN_CONTENT, shoppingList.content)
            put("deleted", if (shoppingList.deleted) 1 else 0)
            put("favorite", if (shoppingList.favorite) 1 else 0)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllLists(): List<ShoppingList> {
        val shoppingList = mutableListOf<ShoppingList>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE deleted = 0"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val deleted = cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) == 1
            val favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
            val sList = ShoppingList(id, title, content, deleted, favorite)
            shoppingList.add(sList)
        }
        cursor.close()
        db.close()
        return shoppingList
    }

    fun getAllFavoriteList (): List<ShoppingList> {
        val shoppingList = mutableListOf<ShoppingList>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE favorite = 1 & deleted = 0"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val deleted = cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) == 1
            val favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
            val sList = ShoppingList(id, title, content, deleted, favorite)
            shoppingList.add(sList)
        }
        cursor.close()
        db.close()
        return shoppingList
    }

    fun getAllTrashLists(): List<ShoppingList> {
        val shoppingList = mutableListOf<ShoppingList>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE deleted = 1"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val deleted = cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) == 1
            val favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
            val sList = ShoppingList(id, title, content, deleted, favorite)
            shoppingList.add(sList)
        }
        cursor.close()
        db.close()
        return shoppingList
    }

    fun updateLists(shoppingList: ShoppingList){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, shoppingList.title)
            put(COLUMN_CONTENT, shoppingList.content)
            put("deleted", if (shoppingList.deleted) 1 else 0)
            put("favorite", if (shoppingList.favorite) 1 else 0)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(shoppingList.id.toString())
        db.update(TABLE_NAME,values, whereClause, whereArgs)
        db.close()
    }

    fun getListByID(listId: Int): ShoppingList{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $listId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val deleted = cursor.getInt(cursor.getColumnIndexOrThrow("deleted")) == 1
        val favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
        cursor.close()
        db.close()
        return ShoppingList(id, title, content, deleted, favorite);
    }

    fun deleteList (listId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(listId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun moveToTrash(listId: Int){
        val db = writableDatabase
        val values = ContentValues().apply {
            put("deleted", 1)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(listId.toString())
        db.update(TABLE_NAME,values, whereClause, whereArgs)
        db.close()
    }
    fun moveToFavorite (listId: Int){
        val db = writableDatabase
        val values = ContentValues().apply {
            put("favorite", 1)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(listId.toString())
        db.update(TABLE_NAME,values, whereClause, whereArgs)
        db.close()
    }

    fun restoreFromTrash(listId: Int){
        val db = writableDatabase
        val values = ContentValues().apply {
            put("deleted", 0)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(listId.toString())
        db.update(TABLE_NAME,values, whereClause, whereArgs)
        db.close()
    }

    fun deleteAllLists() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME") // Delete all rows
        db.close()
    }


}