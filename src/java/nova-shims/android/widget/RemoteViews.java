package android.widget;

public class RemoteViews {
    public void addStableView(int p0, android.widget.RemoteViews p1, int p2) {}
    public void addView(int p0, android.widget.RemoteViews p1) {}
    public android.view.View apply(android.content.Context p0, android.view.ViewGroup p1) { return null; }
    public android.widget.RemoteViews clone() { return null; }
    public int getLayoutId() { return 0; }
    public java.lang.String getPackage() { return null; }
    public void removeAllViews(int p0) {}
    public void setBlendMode(int p0, java.lang.String p1, android.graphics.BlendMode p2) {}
    public void setBoolean(int p0, java.lang.String p1, boolean p2) {}
    public void setCharSequence(int p0, java.lang.String p1, int p2) {}
    public void setCharSequenceAttr(int p0, java.lang.String p1, int p2) {}
    public void setChronometerCountDown(int p0, boolean p1) {}
    public void setColor(int p0, java.lang.String p1, int p2) {}
    public void setColorAttr(int p0, java.lang.String p1, int p2) {}
    public void setColorInt(int p0, java.lang.String p1, int p2, int p3) {}
    public void setColorStateList(int p0, java.lang.String p1, int p2) {}
    public void setColorStateList(int p0, java.lang.String p1, android.content.res.ColorStateList p2, android.content.res.ColorStateList p3) {}
    public void setColorStateListAttr(int p0, java.lang.String p1, int p2) {}
    public void setContentDescription(int p0, java.lang.CharSequence p1) {}
    public void setFloat(int p0, java.lang.String p1, float p2) {}
    public void setFloatDimen(int p0, java.lang.String p1, float p2, int p3) {}
    public void setFloatDimen(int p0, java.lang.String p1, int p2) {}
    public void setFloatDimenAttr(int p0, java.lang.String p1, int p2) {}
    public void setIcon(int p0, java.lang.String p1, android.graphics.drawable.Icon p2, android.graphics.drawable.Icon p3) {}
    public void setImageViewBitmap(int p0, android.graphics.Bitmap p1) {}
    public void setImageViewResource(int p0, int p1) {}
    public void setInt(int p0, java.lang.String p1, int p2) {}
    public void setIntDimen(int p0, java.lang.String p1, float p2, int p3) {}
    public void setIntDimen(int p0, java.lang.String p1, int p2) {}
    public void setIntDimenAttr(int p0, java.lang.String p1, int p2) {}
    public void setLong(int p0, java.lang.String p1, long p2) {}
    public void setOnClickFillInIntent(int p0, android.content.Intent p1) {}
    public void setOnClickPendingIntent(int p0, android.app.PendingIntent p1) {}
    public void setPendingIntentTemplate(int p0, android.app.PendingIntent p1) {}
    public void setRemoteAdapter(int p0, android.content.Intent p1) {}
    public void setRemoteAdapter(int p0, int p1, android.content.Intent p2) {}
    public void setTextColor(int p0, int p1) {}
    public void setTextViewText(int p0, java.lang.CharSequence p1) {}
    public void setTextViewTextSize(int p0, int p1, float p2) {}
    public void setViewLayoutHeight(int p0, float p1, int p2) {}
    public void setViewLayoutHeightDimen(int p0, int p1) {}
    public void setViewLayoutWidth(int p0, float p1, int p2) {}
    public void setViewLayoutWidthDimen(int p0, int p1) {}
    public void setViewOutlinePreferredRadius(int p0, float p1, int p2) {}
    public void setViewOutlinePreferredRadiusDimen(int p0, int p1) {}
    public void setViewPadding(int p0, int p1, int p2, int p3, int p4) {}
    public void setViewVisibility(int p0, int p1) {}

    public static class RemoteCollectionItems {
        public static class Builder {
            public Builder addItem(long id, RemoteViews view) { return this; }
            public RemoteCollectionItems build() { return new RemoteCollectionItems(); }
            public Builder setHasStableIds(boolean v) { return this; }
            public Builder setViewTypeCount(int v) { return this; }
        }
    }

    public static class Builder {
        public RemoteCollectionItems.Builder addItem(long p0, RemoteViews p1) { return null; }
        public RemoteCollectionItems build() { return null; }
        public RemoteCollectionItems.Builder setHasStableIds(boolean p0) { return null; }
        public RemoteCollectionItems.Builder setViewTypeCount(int p0) { return null; }
    }
}
