package android.os;

import java.util.LinkedList;

public final class MessageQueue {

    private final LinkedList<Message> mMessages = new LinkedList<>();
    private boolean mQuitting;
    private java.util.List<IdleHandler> mIdleHandlers = new java.util.ArrayList<>();
    private java.util.List<OnFileDescriptorEventListener> mFileDescriptorListeners = new java.util.ArrayList<>();

    MessageQueue(boolean quitAllowed) {}

    public interface IdleHandler {
        boolean queueIdle();
    }

    public interface OnFileDescriptorEventListener {
        int EVENT_INPUT  = 1;
        int EVENT_OUTPUT = 2;
        int EVENT_ERROR  = 4;
        int onFileDescriptorEvents(java.io.FileDescriptor fd, int events);
    }

    public void addIdleHandler(IdleHandler handler) {
        if (handler != null) mIdleHandlers.add(handler);
    }

    public void removeIdleHandler(IdleHandler handler) {
        mIdleHandlers.remove(handler);
    }

    public void addOnFileDescriptorEventListener(java.io.FileDescriptor fd, int events, OnFileDescriptorEventListener listener) {}
    public void removeOnFileDescriptorEventListener(java.io.FileDescriptor fd) {}

    public boolean isIdle() { return mMessages.isEmpty(); }
    public boolean isPolling() { return !mQuitting; }

    boolean enqueueMessage(Message msg, long when) {
        if (mQuitting) return false;
        msg.when = when;
        synchronized (mMessages) { mMessages.add(msg); }
        return true;
    }

    Message next() {
        while (true) {
            synchronized (mMessages) {
                if (!mMessages.isEmpty()) {
                    Message msg = mMessages.peek();
                    long now = SystemClock.uptimeMillis();
                    if (msg.when <= now) { mMessages.poll(); return msg; }
                }
            }
            try { Thread.sleep(1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    void quit(boolean safe) { mQuitting = true; }
}
