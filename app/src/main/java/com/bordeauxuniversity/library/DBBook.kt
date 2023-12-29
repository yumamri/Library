import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.bordeauxuniversity.library.Book
import com.bordeauxuniversity.library.MySQLiteDB

class DBBook(context: Context) {
    private val VERSION_BDD = 1
    private var db: SQLiteDatabase
    private var sqlLiteDB: MySQLiteDB = MySQLiteDB(context, "books", null, VERSION_BDD)

    init {
        // On crée la BDD et sa table
        db = sqlLiteDB.writableDatabase
    }

    fun open() {
        // On ouvre la BDD en écriture
        // already done in init
    }

    fun close() {
        // On ferme la BDD
        db.close()
    }

    fun getBdd(): SQLiteDatabase {
        return db
    }

    fun insertBook(book: Book): Long {
        // Création d'un ContentValues (fonctionne comme une HashMap)
        val values = ContentValues()
        values.put("isbn", book.isbn)
        values.put("title", book.title)
        // On insère l'objet dans la BDD via le ContentValues
        print("book inserted")
        return db.insert("books", null, values)
    }

    fun getBookByTitle(title: String): Book? {
        // Récupère dans un Cursor les valeurs correspondant à un book contenu dans la BDD
        val cursor = db.query("books", null, "title = ?", arrayOf(title), null, null, null)
        return cursorToBook(cursor)
    }

    fun cursorToBook(cursor: Cursor): Book? {
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val isbn = cursor.getString(cursor.getColumnIndexOrThrow("isbn"))

            return Book(id, title, isbn)
        } else {
            // Cursor is empty
            print("Cursor is empty")
            return null
        }
    }

}
