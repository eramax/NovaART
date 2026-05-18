package android.content.pm;

public class PackageInfo {
    public String packageName;
    public String[] splitNames;
    public int versionCode;
    public long longVersionCode;
    public String versionName;
    public String sharedUserId;
    public int sharedUserLabel;
    public ApplicationInfo applicationInfo;
    public ActivityInfo[] activities;
    public String[] permissions;
    public int[] requestedPermissionsFlags;
    public String[] requestedPermissions;
    public int firstInstallTime;
    public long firstInstallTimeLong;
    public int lastUpdateTime;
    public long lastUpdateTimeLong;
    public int[] gids;

    public static final int REQUESTED_PERMISSION_GRANTED = 0x10;
}
