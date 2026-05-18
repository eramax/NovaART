package android.view;

public interface Menu {
    MenuItem add(int groupId, int itemId, int order, CharSequence title);
    MenuItem add(CharSequence title);
    MenuItem findItem(int id);
    int size();
    boolean hasVisibleItems();
    void clear();
}
