package android.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SparseArray<E> implements Cloneable {
    private final LinkedHashMap<Integer, E> mMap;

    public SparseArray() {
        this(10);
    }

    public SparseArray(int initialCapacity) {
        mMap = new LinkedHashMap<>(Math.max(1, initialCapacity));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public SparseArray<E> clone() {
        SparseArray<E> clone = new SparseArray<>(mMap.size());
        clone.mMap.putAll(mMap);
        return clone;
    }

    public boolean contains(int key) {
        return mMap.containsKey(key);
    }

    public E get(int key) {
        return mMap.get(key);
    }

    public E get(int key, E valueIfKeyNotFound) {
        return mMap.containsKey(key) ? mMap.get(key) : valueIfKeyNotFound;
    }

    public void delete(int key) {
        mMap.remove(key);
    }

    public void remove(int key) {
        delete(key);
    }

    public void removeAt(int index) {
        int key = keyAt(index);
        if (index >= 0) {
            mMap.remove(key);
        }
    }

    public void removeAtRange(int index, int size) {
        for (int i = 0; i < size; i++) {
            removeAt(index);
        }
    }

    public void set(int key, E value) {
        put(key, value);
    }

    public void put(int key, E value) {
        mMap.put(key, value);
    }

    public int size() {
        return mMap.size();
    }

    public int keyAt(int index) {
        return new ArrayList<>(mMap.keySet()).get(index);
    }

    public E valueAt(int index) {
        return new ArrayList<>(mMap.values()).get(index);
    }

    public void setValueAt(int index, E value) {
        int key = keyAt(index);
        mMap.put(key, value);
    }

    public int indexOfKey(int key) {
        int i = 0;
        for (Integer current : mMap.keySet()) {
            if (current != null && current == key) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int indexOfValue(E value) {
        int i = 0;
        for (E current : mMap.values()) {
            if (current == value || (current != null && current.equals(value))) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void clear() {
        mMap.clear();
    }

    public void append(int key, E value) {
        put(key, value);
    }

    public boolean contentEquals(SparseArray<?> other) {
        return other != null && mMap.equals(other.mMap);
    }

    public int contentHashCode() {
        return mMap.hashCode();
    }

    @Override
    public String toString() {
        return mMap.toString();
    }
}
