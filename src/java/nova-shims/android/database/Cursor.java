package android.database;

public interface Cursor {
    int getCount();
    int getPosition();
    boolean moveToFirst();
    boolean moveToNext();
    boolean moveToPosition(int position);
    boolean isAfterLast();
    boolean isBeforeFirst();
    boolean isFirst();
    boolean isLast();
    String getString(int columnIndex);
    int getInt(int columnIndex);
    long getLong(int columnIndex);
    float getFloat(int columnIndex);
    double getDouble(int columnIndex);
    byte[] getBlob(int columnIndex);
    int getColumnIndex(String columnName);
    int getColumnIndexOrThrow(String columnName) throws Exception;
    String getColumnName(int columnIndex);
    String[] getColumnNames();
    int getColumnCount();
    void close();
    boolean isClosed();
    void registerContentObserver(android.database.ContentObserver observer);
    void unregisterContentObserver(android.database.ContentObserver observer);
}
