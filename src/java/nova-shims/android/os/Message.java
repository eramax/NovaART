package android.os;

public final class Message implements Parcelable {

    public int what;
    public int arg1;
    public int arg2;
    public Object obj;
    public long when;
    public int flags;
    public Handler target;
    public Runnable callback;
    public Bundle data;
    public android.os.Messenger replyTo;
    public int sendingUid = -1;
    public int workSourceUid = -1;

    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize;
    private static final int MAX_POOL_SIZE = 50;
    Message next;

    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0;
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Handler h) { Message m = obtain(); m.target = h; return m; }
    public static Message obtain(Handler h, Runnable callback) { Message m = obtain(h); m.callback = callback; return m; }
    public static Message obtain(Handler h, int what) { Message m = obtain(h); m.what = what; return m; }
    public static Message obtain(Handler h, int what, Object obj) { Message m = obtain(h, what); m.obj = obj; return m; }
    public static Message obtain(Handler h, int what, int arg1, int arg2) { Message m = obtain(h, what); m.arg1 = arg1; m.arg2 = arg2; return m; }
    public static Message obtain(Handler h, int what, int arg1, int arg2, Object obj) { Message m = obtain(h, what, arg1, arg2); m.obj = obj; return m; }
    public static Message obtain(Message orig) {
        Message m = obtain(); m.what = orig.what; m.arg1 = orig.arg1; m.arg2 = orig.arg2;
        m.obj = orig.obj; m.target = orig.target; m.callback = orig.callback;
        if (orig.data != null) m.data = new Bundle(orig.data); return m;
    }

    public void recycle() {
        if (isInUse()) return;
        recycleUnchecked();
    }

    void recycleUnchecked() {
        flags = FLAG_IN_USE;
        what = 0; arg1 = 0; arg2 = 0; obj = null; when = 0;
        target = null; callback = null; data = null;
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) { next = sPool; sPool = this; sPoolSize++; }
        }
    }

    public static final int FLAG_IN_USE  = 1 << 0;
    public static final int FLAG_ASYNCHRONOUS = 1 << 1;

    boolean isInUse() { return (flags & FLAG_IN_USE) != 0; }
    public boolean isAsynchronous() { return (flags & FLAG_ASYNCHRONOUS) != 0; }
    public void setAsynchronous(boolean async) {
        if (async) flags |= FLAG_ASYNCHRONOUS; else flags &= ~FLAG_ASYNCHRONOUS;
    }

    public Handler getTarget() { return target; }
    public Runnable getCallback() { return callback; }

    public Bundle getData() { if (data == null) data = new Bundle(); return data; }
    public Bundle peekData() { return data; }
    public void setData(Bundle data) { this.data = data; }
    public void setTarget(Handler target) { this.target = target; }

    public void sendToTarget() { target.sendMessage(this); }

    public void copyFrom(Message o) {
        this.flags = o.flags & ~FLAG_IN_USE; this.what = o.what;
        this.arg1 = o.arg1; this.arg2 = o.arg2; this.obj = o.obj;
        if (o.data != null) setData(new Bundle(o.data)); else data = null;
    }

    @Override public String toString() { return "Message{what=" + what + " target=" + target + "}"; }

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) { dest.writeInt(what); dest.writeInt(arg1); dest.writeInt(arg2); }
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override public Message createFromParcel(Parcel in) {
            Message m = Message.obtain(); m.what = in.readInt(); m.arg1 = in.readInt(); m.arg2 = in.readInt(); return m;
        }
        @Override public Message[] newArray(int size) { return new Message[size]; }
    };
}
