package android.content.pm;

public class ComponentInfo extends PackageItemInfo {
    public ApplicationInfo applicationInfo;
    public String processName;
    public String descriptionRes;
    public boolean enabled = true;
    public boolean exported;
    public boolean directBootAware;

    public ComponentInfo() {}
    public ComponentInfo(ComponentInfo orig) {
        super(orig);
        applicationInfo = orig.applicationInfo;
        processName = orig.processName;
        enabled = orig.enabled;
        exported = orig.exported;
    }

    public boolean isEnabled() { return enabled; }
    public int getIconResource() { return icon != 0 ? icon : (applicationInfo != null ? applicationInfo.icon : 0); }
    public android.content.ComponentName getComponentName() {
        return new android.content.ComponentName(packageName, name);
    }
}
