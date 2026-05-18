package android.os;

public final class Message {
    public int what;
    public int arg1;
    public int arg2;
    public Object obj;
    public long when;

    Handler target;
    Runnable callback;
    Message next;

    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize;

    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Handler h) {
        Message m = obtain();
        m.target = h;
        return m;
    }

    public static Message obtain(Handler h, int what) {
        Message m = obtain(h);
        m.what = what;
        return m;
    }

    public static Message obtain(Handler h, int what, Object obj) {
        Message m = obtain(h, what);
        m.obj = obj;
        return m;
    }

    public void recycle() {
        what = 0; arg1 = 0; arg2 = 0; obj = null; when = 0;
        target = null; callback = null;
        synchronized (sPoolSync) {
            if (sPoolSize < 50) { next = sPool; sPool = this; sPoolSize++; }
        }
    }

    public void sendToTarget() {
        if (target != null) target.sendMessage(this);
    }
}
