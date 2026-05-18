package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

public class ApplicationInfo extends PackageItemInfo implements Parcelable {

    public String packageName;
    public String sourceDir;
    public String publicSourceDir;
    public String dataDir;
    public String nativeLibraryDir;
    public String processName;
    public String className;
    public int flags;
    public int targetSdkVersion = 35;
    public int minSdkVersion = 21;
    public int uid;
    public boolean enabled = true;
    public String taskAffinity;
    public int theme;
    public int icon;
    public int banner;
    public int logo;
    public int descriptionRes;
    public int uiOptions;
    public int networkSecurityConfigRes;
    public String splitSourceDirs[];
    public String splitPublicSourceDirs[];
    public String[] sharedLibraryFiles;
    public String scanSourceDir;
    public String scanPublicSourceDir;
    public int labelRes;
    public CharSequence nonLocalizedLabel;
    public String manageSpaceActivityName;
    public String backupAgentName;
    public int fullBackupContent;
    public int dataExtractionRulesRes;
    public String volumeUuid;
    public int compileSdkVersion;
    public String compileSdkVersionCodename;

    // Common flag constants
    public static final int FLAG_SYSTEM               = 1 << 0;
    public static final int FLAG_DEBUGGABLE           = 1 << 1;
    public static final int FLAG_HAS_CODE             = 1 << 2;
    public static final int FLAG_PERSISTENT           = 1 << 3;
    public static final int FLAG_FACTORY_TEST         = 1 << 4;
    public static final int FLAG_ALLOW_TASK_REPARENTING = 1 << 5;
    public static final int FLAG_ALLOW_CLEAR_USER_DATA = 1 << 6;
    public static final int FLAG_UPDATED_SYSTEM_APP   = 1 << 7;
    public static final int FLAG_TEST_ONLY            = 1 << 8;
    public static final int FLAG_SUPPORTS_SMALL_SCREENS = 1 << 9;
    public static final int FLAG_SUPPORTS_NORMAL_SCREENS = 1 << 10;
    public static final int FLAG_SUPPORTS_LARGE_SCREENS = 1 << 11;
    public static final int FLAG_RESIZEABLE_FOR_SCREENS = 1 << 12;
    public static final int FLAG_SUPPORTS_SCREEN_DENSITIES = 1 << 13;
    public static final int FLAG_VM_SAFE_MODE         = 1 << 14;
    public static final int FLAG_ALLOW_BACKUP         = 1 << 15;
    public static final int FLAG_KILL_AFTER_RESTORE   = 1 << 16;
    public static final int FLAG_RESTORE_ANY_VERSION  = 1 << 17;
    public static final int FLAG_EXTERNAL_STORAGE     = 1 << 18;
    public static final int FLAG_LARGE_HEAP           = 1 << 19;
    public static final int FLAG_STOPPED              = 1 << 20;
    public static final int FLAG_SUPPORTS_RTL         = 1 << 22;
    public static final int FLAG_INSTALLED            = 1 << 23;
    public static final int FLAG_IS_DATA_ONLY         = 1 << 24;
    public static final int FLAG_IS_GAME              = 1 << 25;
    public static final int FLAG_FULL_BACKUP_ONLY     = 1 << 26;
    public static final int FLAG_USES_CLEARTEXT_TRAFFIC = 1 << 27;
    public static final int FLAG_MULTIARCH            = 1 << 31;

    public ApplicationInfo() {}
    public ApplicationInfo(ApplicationInfo orig) { this.packageName = orig.packageName; this.sourceDir = orig.sourceDir; this.flags = orig.flags; }

    public boolean isSystemApp() { return (flags & FLAG_SYSTEM) != 0; }
    public boolean isPrivilegedApp() { return false; }
    public boolean isSignedWithPlatformKey() { return false; }
    public String getCodePath() { return sourceDir; }
    public boolean isResourceOverlay() { return false; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) { dest.writeString(packageName); }

    public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator<ApplicationInfo>() {
        @Override public ApplicationInfo createFromParcel(Parcel in) {
            ApplicationInfo ai = new ApplicationInfo();
            ai.packageName = in.readString();
            return ai;
        }
        @Override public ApplicationInfo[] newArray(int size) { return new ApplicationInfo[size]; }
    };
}
