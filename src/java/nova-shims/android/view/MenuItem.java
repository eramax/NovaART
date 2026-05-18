package android.view;

public interface MenuItem {
    int getItemId();
    CharSequence getTitle();
    MenuItem setTitle(CharSequence title);
    MenuItem setEnabled(boolean enabled);
    MenuItem setVisible(boolean visible);
    boolean isEnabled();
    boolean isVisible();
    MenuItem setIcon(android.graphics.drawable.Drawable icon);
    MenuItem setCheckable(boolean checkable);
    MenuItem setChecked(boolean checked);
    boolean isChecked();
}
