package android.content;

public class ContentResolver {
    public static final String SCHEME_CONTENT = "content";
    public ContentResolver() {}
    public android.database.Cursor query(android.net.Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    public int delete(android.net.Uri uri, String selection, String[] selectionArgs) { return 0; }
    public android.net.Uri insert(android.net.Uri uri, android.content.ContentValues values) { return null; }
    public int update(android.net.Uri uri, android.content.ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
