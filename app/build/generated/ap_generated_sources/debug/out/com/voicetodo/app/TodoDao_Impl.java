package com.voicetodo.app;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class TodoDao_Impl implements TodoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Todo> __insertionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __deletionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __updateAdapterOfTodo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTodoById;

  public TodoDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodo = new EntityInsertionAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `todos` (`id`,`text`,`isDone`,`timestamp`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getId());
        if (value.getText() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getText());
        }
        final int _tmp = value.isDone() ? 1 : 0;
        stmt.bindLong(3, _tmp);
        stmt.bindLong(4, value.getTimestamp());
      }
    };
    this.__deletionAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `todos` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `todos` SET `id` = ?,`text` = ?,`isDone` = ?,`timestamp` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.getId());
        if (value.getText() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getText());
        }
        final int _tmp = value.isDone() ? 1 : 0;
        stmt.bindLong(3, _tmp);
        stmt.bindLong(4, value.getTimestamp());
        stmt.bindLong(5, value.getId());
      }
    };
    this.__preparedStmtOfDeleteTodoById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM todos WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTodo.insert(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteTodoById(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTodoById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteTodoById.release(_stmt);
    }
  }

  @Override
  public List<Todo> getAllTodos() {
    final String _sql = "SELECT * FROM todos ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfIsDone = CursorUtil.getColumnIndexOrThrow(_cursor, "isDone");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Todo _item;
        _item = new Todo();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpText;
        if (_cursor.isNull(_cursorIndexOfText)) {
          _tmpText = null;
        } else {
          _tmpText = _cursor.getString(_cursorIndexOfText);
        }
        _item.setText(_tmpText);
        final boolean _tmpIsDone;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsDone);
        _tmpIsDone = _tmp != 0;
        _item.setDone(_tmpIsDone);
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item.setTimestamp(_tmpTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
