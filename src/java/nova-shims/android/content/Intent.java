package android.content;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Intent implements Parcelable, Cloneable {

    // Common actions
    public static final String ACTION_MAIN          = "android.intent.action.MAIN";
    public static final String ACTION_VIEW          = "android.intent.action.VIEW";
    public static final String ACTION_SEND          = "android.intent.action.SEND";
    public static final String ACTION_EDIT          = "android.intent.action.EDIT";
    public static final String ACTION_PICK          = "android.intent.action.PICK";
    public static final String ACTION_CHOOSER       = "android.intent.action.CHOOSER";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_DEFAULT       = ACTION_VIEW;

    // Categories
    public static final String CATEGORY_DEFAULT     = "android.intent.category.DEFAULT";
    public static final String CATEGORY_LAUNCHER    = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER";

    // Flags
    public static final int FLAG_ACTIVITY_NEW_TASK              = 0x10000000;
    public static final int FLAG_ACTIVITY_CLEAR_TASK            = 0x00008000;
    public static final int FLAG_ACTIVITY_CLEAR_TOP             = 0x04000000;
    public static final int FLAG_ACTIVITY_SINGLE_TOP            = 0x20000000;
    public static final int FLAG_ACTIVITY_NO_ANIMATION          = 0x00010000;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS  = 0x00800000;
    public static final int FLAG_GRANT_READ_URI_PERMISSION       = 0x00000001;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION      = 0x00000002;

    // Fill-in flags
    public static final int FILL_IN_ACTION      = 0x1;
    public static final int FILL_IN_DATA        = 0x2;
    public static final int FILL_IN_CATEGORIES  = 0x4;
    public static final int FILL_IN_COMPONENT   = 0x8;
    public static final int FILL_IN_PACKAGE     = 0x10;
    public static final int FILL_IN_FLAGS       = 0x80;
    public static final int URI_INTENT_SCHEME   = 1;
    public static final int URI_ANDROID_APP_SCHEME = 2;

    private String mAction;
    private android.net.Uri mData;
    private String mType;
    private String mPackage;
    private ComponentName mComponent;
    private int mFlags;
    private HashSet<String> mCategories;
    private Bundle mExtras;

    public Intent() {}
    public Intent(String action) { mAction = action; }
    public Intent(String action, android.net.Uri uri) { mAction = action; mData = uri; }
    public Intent(Context context, Class<?> cls) {
        mComponent = new ComponentName(context, cls);
    }
    public Intent(String action, android.net.Uri uri, Context context, Class<?> cls) {
        mAction = action; mData = uri;
        mComponent = new ComponentName(context, cls);
    }
    public Intent(Intent o) {
        mAction = o.mAction; mData = o.mData; mType = o.mType;
        mPackage = o.mPackage; mComponent = o.mComponent; mFlags = o.mFlags;
        mCategories = o.mCategories != null ? new HashSet<>(o.mCategories) : null;
        mExtras = o.mExtras != null ? new Bundle(o.mExtras) : null;
    }

    public String getAction() { return mAction; }
    public Intent setAction(String action) { mAction = action; return this; }
    public android.net.Uri getData() { return mData; }
    public Intent setData(android.net.Uri data) { mData = data; mType = null; return this; }
    public String getDataString() { return mData != null ? mData.toString() : null; }
    public String getType() { return mType; }
    public Intent setType(String type) { mType = type; mData = null; return this; }
    public Intent setDataAndType(android.net.Uri data, String type) { mData = data; mType = type; return this; }
    public String getPackage() { return mPackage; }
    public Intent setPackage(String pkg) { mPackage = pkg; return this; }
    public ComponentName getComponent() { return mComponent; }
    public Intent setComponent(ComponentName component) { mComponent = component; return this; }
    public Intent setClass(Context context, Class<?> cls) { mComponent = new ComponentName(context, cls); return this; }
    public Intent setClassName(Context context, String className) { mComponent = new ComponentName(context.getPackageName(), className); return this; }
    public Intent setClassName(String pkg, String className) { mComponent = new ComponentName(pkg, className); return this; }
    public int getFlags() { return mFlags; }
    public Intent setFlags(int flags) { mFlags = flags; return this; }
    public Intent addFlags(int flags) { mFlags |= flags; return this; }
    public Set<String> getCategories() { return mCategories; }
    public Intent addCategory(String category) {
        if (mCategories == null) mCategories = new HashSet<>();
        mCategories.add(category); return this;
    }
    public boolean hasCategory(String category) { return mCategories != null && mCategories.contains(category); }
    public void removeCategory(String category) { if (mCategories != null) mCategories.remove(category); }
    public Bundle getExtras() { return mExtras; }
    public boolean hasExtra(String name) { return mExtras != null && mExtras.containsKey(name); }

    private Bundle extras() { if (mExtras == null) mExtras = new Bundle(); return mExtras; }
    public Intent putExtra(String name, boolean value)   { extras().putBoolean(name, value); return this; }
    public Intent putExtra(String name, byte value)      { extras().putByte(name, value); return this; }
    public Intent putExtra(String name, char value)      { extras().putChar(name, value); return this; }
    public Intent putExtra(String name, short value)     { extras().putShort(name, value); return this; }
    public Intent putExtra(String name, int value)       { extras().putInt(name, value); return this; }
    public Intent putExtra(String name, long value)      { extras().putLong(name, value); return this; }
    public Intent putExtra(String name, float value)     { extras().putFloat(name, value); return this; }
    public Intent putExtra(String name, double value)    { extras().putDouble(name, value); return this; }
    public Intent putExtra(String name, String value)    { extras().putString(name, value); return this; }
    public Intent putExtra(String name, CharSequence value) { extras().putCharSequence(name, value); return this; }
    public Intent putExtra(String name, Parcelable value){ extras().putParcelable(name, value); return this; }
    public Intent putExtra(String name, Bundle value)    { extras().putBundle(name, value); return this; }
    public Intent putExtra(String name, byte[] value)    { extras().putByteArray(name, value); return this; }
    public Intent putExtra(String name, int[] value)     { extras().putIntArray(name, value); return this; }
    public Intent putExtra(String name, long[] value)    { extras().putLongArray(name, value); return this; }
    public Intent putExtra(String name, float[] value)   { extras().putFloatArray(name, value); return this; }
    public Intent putExtra(String name, double[] value)  { extras().putDoubleArray(name, value); return this; }
    public Intent putExtra(String name, String[] value)  { extras().putStringArray(name, value); return this; }
    public Intent putExtras(Bundle extras) { extras().putAll(extras); return this; }
    public Intent putExtras(Intent src) { if (src.mExtras != null) extras().putAll(src.mExtras); return this; }
    public Intent replaceExtras(Bundle extras) { mExtras = extras != null ? new Bundle(extras) : null; return this; }
    public Intent removeExtra(String name) { if (mExtras != null) mExtras.remove(name); return this; }

    public boolean getBooleanExtra(String name, boolean def) { return mExtras != null ? mExtras.getBoolean(name, def) : def; }
    public byte getByteExtra(String name, byte def)          { return mExtras != null ? mExtras.getByte(name, def) : def; }
    public char getCharExtra(String name, char def)          { return mExtras != null ? mExtras.getChar(name, def) : def; }
    public short getShortExtra(String name, short def)       { return mExtras != null ? mExtras.getShort(name, def) : def; }
    public int getIntExtra(String name, int def)             { return mExtras != null ? mExtras.getInt(name, def) : def; }
    public long getLongExtra(String name, long def)          { return mExtras != null ? mExtras.getLong(name, def) : def; }
    public float getFloatExtra(String name, float def)       { return mExtras != null ? mExtras.getFloat(name, def) : def; }
    public double getDoubleExtra(String name, double def)    { return mExtras != null ? mExtras.getDouble(name, def) : def; }
    public String getStringExtra(String name)                { return mExtras != null ? mExtras.getString(name) : null; }
    public CharSequence getCharSequenceExtra(String name)    { return mExtras != null ? mExtras.getCharSequence(name) : null; }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T getParcelableExtra(String name) { return mExtras != null ? mExtras.<T>getParcelable(name) : null; }
    public Bundle getBundleExtra(String name)                { return mExtras != null ? mExtras.getBundle(name) : null; }
    public byte[] getByteArrayExtra(String name)             { return mExtras != null ? mExtras.getByteArray(name) : null; }
    public int[] getIntArrayExtra(String name)               { return mExtras != null ? mExtras.getIntArray(name) : null; }
    public long[] getLongArrayExtra(String name)             { return mExtras != null ? mExtras.getLongArray(name) : null; }
    public float[] getFloatArrayExtra(String name)           { return mExtras != null ? mExtras.getFloatArray(name) : null; }
    public double[] getDoubleArrayExtra(String name)         { return mExtras != null ? mExtras.getDoubleArray(name) : null; }
    public String[] getStringArrayExtra(String name)         { return mExtras != null ? mExtras.getStringArray(name) : null; }

    public static Intent parseUri(String uri, int flags) throws java.net.URISyntaxException { return new Intent(Intent.ACTION_VIEW); }
    public static Intent getIntent(String uri) throws java.net.URISyntaxException { return parseUri(uri, 0); }
    public String toUri(int flags) { return ""; }
    public static Intent createChooser(Intent target, CharSequence title) { return target; }
    public static Intent makeMainActivity(ComponentName mainActivity) {
        Intent i = new Intent(ACTION_MAIN);
        i.setComponent(mainActivity);
        i.addCategory(CATEGORY_LAUNCHER);
        return i;
    }
    public Intent cloneFilter() { Intent i = new Intent(); i.mAction = mAction; i.mData = mData; i.mType = mType; return i; }
    @Override public Intent clone() { return new Intent(this); }
    @Override public String toString() { return "Intent{action=" + mAction + "}"; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {}
    public static final Parcelable.Creator<Intent> CREATOR = new Parcelable.Creator<Intent>() {
        @Override public Intent createFromParcel(Parcel in) { return new Intent(); }
        @Override public Intent[] newArray(int size) { return new Intent[size]; }
    };

    public int fillIn(Intent other, int flags) { return 0; }
    public boolean filterEquals(Intent other) { return false; }
    public int filterHashCode() { return 0; }
    public String getScheme() { return mData != null ? mData.getScheme() : null; }
    public boolean isExplicit() { return mComponent != null; }
    public boolean hasFileDescriptors() { return false; }
    public String resolveType(Context context) { return mType; }
    public String resolveTypeIfNeeded(ContentResolver resolver) { return mType; }
}
