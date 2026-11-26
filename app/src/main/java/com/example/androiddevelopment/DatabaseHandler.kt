package com.example.androiddevelopment

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Looper
import android.provider.BaseColumns
import android.util.Log

object DBContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_LOGIN = "login"
        const val COLUMN_PASS = "pass"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_BIRTH_DATE = "birth_date"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_AVATAR = "avatar_index"
        const val COLUMN_IS_ADMIN = "is_admin"
    }
}

class DatabaseHandler private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private val appContext = context.applicationContext

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "Users.db"

        @Volatile
        private var instance: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHandler(context).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE ${DBContract.UserEntry.TABLE_NAME} (
                ${DBContract.UserEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DBContract.UserEntry.COLUMN_LOGIN} TEXT UNIQUE,
                ${DBContract.UserEntry.COLUMN_PASS} TEXT,
                ${DBContract.UserEntry.COLUMN_EMAIL} TEXT,
                ${DBContract.UserEntry.COLUMN_FULL_NAME} TEXT,
                ${DBContract.UserEntry.COLUMN_BIRTH_DATE} TEXT,
                ${DBContract.UserEntry.COLUMN_GENDER} TEXT,
                ${DBContract.UserEntry.COLUMN_AVATAR} INTEGER,
                ${DBContract.UserEntry.COLUMN_IS_ADMIN} INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DBContract.UserEntry.TABLE_NAME}")
        onCreate(db)
    }

    // Имитация задержки при обращении к БД (только в фоновом потоке)
    private fun simulateDelay() {
        val prefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val delayEnabled = prefs.getBoolean("db_delay_enabled", true)
        if (delayEnabled && Looper.myLooper() != Looper.getMainLooper()) {
            Log.d("DatabaseHandler", "Имитация задержки БД: 2000 мс...")
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun getUsersCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DBContract.UserEntry.TABLE_NAME}", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun isLoginExists(login: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_LOGIN} = ?",
            arrayOf(login)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun addUser(user: User): Boolean {
        simulateDelay()
        if (isLoginExists(user.login)) return false

        val isFirstUser = getUsersCount() == 0
        val adminFlag = if (isFirstUser || user.isAdmin) 1 else 0

        val db = writableDatabase
        val values = ContentValues().apply {
            put(DBContract.UserEntry.COLUMN_LOGIN, user.login)
            put(DBContract.UserEntry.COLUMN_PASS, user.pass)
            put(DBContract.UserEntry.COLUMN_EMAIL, user.email)
            put(DBContract.UserEntry.COLUMN_FULL_NAME, user.fullName)
            put(DBContract.UserEntry.COLUMN_BIRTH_DATE, user.birthDate)
            put(DBContract.UserEntry.COLUMN_GENDER, user.gender)
            put(DBContract.UserEntry.COLUMN_AVATAR, user.avatarIndex)
            put(DBContract.UserEntry.COLUMN_IS_ADMIN, adminFlag)
        }
        val result = db.insert(DBContract.UserEntry.TABLE_NAME, null, values)
        return result != -1L
    }

    fun loginUser(login: String, pass: String): User? {
        simulateDelay()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_LOGIN} = ? AND ${DBContract.UserEntry.COLUMN_PASS} = ?",
            arrayOf(login, pass)
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = readUserFromCursor(cursor)
        }
        cursor.close()
        return user
    }

    fun getUserById(id: Int): User? {
        simulateDelay()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = readUserFromCursor(cursor)
        }
        cursor.close()
        return user
    }

    fun getAllUsers(): ArrayList<User> {
        simulateDelay()
        val list = ArrayList<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DBContract.UserEntry.TABLE_NAME}", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(readUserFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun makeAdmin(userId: Int) {
        simulateDelay()
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DBContract.UserEntry.COLUMN_IS_ADMIN, 1)
        }
        db.update(
            DBContract.UserEntry.TABLE_NAME,
            values,
            "${DBContract.UserEntry.COLUMN_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    fun deleteUser(userId: Int) {
        simulateDelay()
        val db = writableDatabase
        db.delete(
            DBContract.UserEntry.TABLE_NAME,
            "${DBContract.UserEntry.COLUMN_ID} = ?",
            arrayOf(userId.toString())
        )
    }

    private fun readUserFromCursor(cursor: Cursor): User {
        return User(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_ID)),
            login = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_LOGIN)),
            pass = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_PASS)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_EMAIL)) ?: "",
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_FULL_NAME)) ?: "",
            birthDate = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_BIRTH_DATE)) ?: "",
            gender = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_GENDER)) ?: "М",
            avatarIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AVATAR)),
            isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_IS_ADMIN)) == 1
        )
    }
}