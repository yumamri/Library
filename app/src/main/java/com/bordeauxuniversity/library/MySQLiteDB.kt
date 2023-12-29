package com.bordeauxuniversity.library

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MySQLiteDB(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val TABLE_NAME = "books"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_ISBN = "isbn"

        private const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_ISBN TEXT);"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // On crée la base en exécutant la requête de création
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // On peut faire ce qu'on veut ici, mais on va juste supprimer la table et la recréer
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME;")
        onCreate(db)
    }
}