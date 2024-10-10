package com.example.loginclass;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "LOGIN", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, fullname TEXT, email TEXT, password TEXT)");
        db.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, task TEXT, completed INTEGER DEFAULT 0, FOREIGN KEY(userId) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public void updateTaskStatus(int taskId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("completed", status); // Use the correct column name
        db.update("tasks", contentValues, "id = ?", new String[]{String.valueOf(taskId)});
    }


    public boolean insertUser(String username, String fullname, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("fullname", fullname);
        values.put("password", password);

        long results = db.insert("users", null, values);
        return results != -1;
    }

    public boolean updateUser(String username, String password, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long results = db.update("users", values, "id = ?", new String[]{String.valueOf(id)});
        return results != -1;
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "id = ?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            cursor.close();
            return username;
        } else {
            return null;
        }
    }

    @SuppressLint("Recycle")
    public boolean viewSingleUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        return data.getCount() > 0;
    }

    @SuppressLint("Recycle")
    public boolean checkUserDetails(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        return data.getCount() > 0;
    }

    @SuppressLint("Recycle")
    public Cursor viewAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users", null);
    }

    public boolean insertTask(int userId, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("task", task);

        long results = db.insert("tasks", null, values);
        return results != -1;
    }

    public void updateTask(int id, String newTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", newTask);
        db.update("tasks", contentValues, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
    }

    public Cursor searchTasks(int userId, String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "userId = ? AND task LIKE ?";
        String[] selectionArgs = { String.valueOf(userId), "%" + query + "%" };
        return db.query("tasks", null, selection, selectionArgs, null, null, null);
    }

    @SuppressLint("Recycle")
    public Cursor getUserTasks(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tasks WHERE userId = ?", new String[]{String.valueOf(userId)});
    }

    @SuppressLint("Range")
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
        }
        return userId;
    }
}
