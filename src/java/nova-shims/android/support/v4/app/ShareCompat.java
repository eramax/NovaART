package android.support.v4.app;

public class ShareCompat {
    public android.support.v4.app.ShareCompat.ShareCompatImpl access$000() { return null; }
    public void configureMenuItem(android.view.Menu p0, int p1, android.support.v4.app.ShareCompat.IntentBuilder p2) {}
    public void configureMenuItem(android.view.MenuItem p0, android.support.v4.app.ShareCompat.IntentBuilder p1) {}
    public android.content.ComponentName getCallingActivity(android.app.Activity p0) { return null; }
    public java.lang.String getCallingPackage(android.app.Activity p0) { return null; }

    public static class IntentBuilder {
        public android.support.v4.app.ShareCompat.IntentBuilder addEmailBcc(java.lang.String p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder addEmailCc(java.lang.String p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder addEmailTo(java.lang.String p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder addStream(android.net.Uri p0) { return null; }
        public void combineArrayExtra(java.lang.String p0, java.util.ArrayList p1) {}
        public android.content.Intent createChooserIntent() { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder from(android.app.Activity p0) { return null; }
        public android.app.Activity getActivity() { return null; }
        public android.content.Intent getIntent() { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setChooserTitle(int p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setEmailBcc(java.lang.String[] p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setEmailCc(java.lang.String[] p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setEmailTo(java.lang.String[] p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setHtmlText(java.lang.String p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setStream(android.net.Uri p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setSubject(java.lang.String p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setText(java.lang.CharSequence p0) { return null; }
        public android.support.v4.app.ShareCompat.IntentBuilder setType(java.lang.String p0) { return null; }
        public void startChooser() {}
    }

    public static class IntentReader {
        public android.support.v4.app.ShareCompat.IntentReader from(android.app.Activity p0) { return null; }
        public android.content.ComponentName getCallingActivity() { return null; }
        public android.graphics.drawable.Drawable getCallingActivityIcon() { return null; }
        public android.graphics.drawable.Drawable getCallingApplicationIcon() { return null; }
        public java.lang.CharSequence getCallingApplicationLabel() { return null; }
        public java.lang.String getCallingPackage() { return null; }
        public java.lang.String[] getEmailBcc() { return null; }
        public java.lang.String[] getEmailCc() { return null; }
        public java.lang.String[] getEmailTo() { return null; }
        public java.lang.String getHtmlText() { return null; }
        public android.net.Uri getStream() { return null; }
        public android.net.Uri getStream(int p0) { return null; }
        public int getStreamCount() { return 0; }
        public java.lang.String getSubject() { return null; }
        public java.lang.CharSequence getText() { return null; }
        public java.lang.String getType() { return null; }
        public boolean isMultipleShare() { return false; }
        public boolean isShareIntent() { return false; }
        public boolean isSingleShare() { return false; }
    }

    public static class ShareCompatImpl {
        public void configureMenuItem(android.view.MenuItem p0, android.support.v4.app.ShareCompat.IntentBuilder p1) {}
        public java.lang.String escapeHtml(java.lang.CharSequence p0) { return null; }
    }

    public static class ShareCompatImplBase {
        public void configureMenuItem(android.view.MenuItem p0, android.support.v4.app.ShareCompat.IntentBuilder p1) {}
        public java.lang.String escapeHtml(java.lang.CharSequence p0) { return null; }
        public void withinStyle(java.lang.StringBuilder p0, java.lang.CharSequence p1, int p2, int p3) {}
    }

    public static class ShareCompatImplICS {
        public void configureMenuItem(android.view.MenuItem p0, android.support.v4.app.ShareCompat.IntentBuilder p1) {}
        public boolean shouldAddChooserIntent(android.view.MenuItem p0) { return false; }
    }

    public static class ShareCompatImplJB {
        public java.lang.String escapeHtml(java.lang.CharSequence p0) { return null; }
        public boolean shouldAddChooserIntent(android.view.MenuItem p0) { return false; }
    }
}
