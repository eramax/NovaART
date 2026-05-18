package android.view;

public class WindowInsets {
    public android.view.WindowInsets consumeDisplayCutout() { return null; }
    public android.view.WindowInsets consumeStableInsets() { return null; }
    public android.view.WindowInsets consumeSystemWindowInsets() { return null; }
    public boolean equals(java.lang.Object p0) { return false; }
    public java.util.List getBoundingRects(int p0) { return null; }
    public java.util.List getBoundingRectsIgnoringVisibility(int p0) { return null; }
    public android.view.DisplayCutout getDisplayCutout() { return null; }
    public android.graphics.Insets getInsets(int p0) { return null; }
    public android.graphics.Insets getInsetsIgnoringVisibility(int p0) { return null; }
    public android.graphics.Insets getMandatorySystemGestureInsets() { return null; }
    public android.graphics.Rect getPrivacyIndicatorBounds() { return null; }
    public android.view.RoundedCorner getRoundedCorner(int p0) { return null; }
    public int getStableInsetBottom() { return 0; }
    public int getStableInsetLeft() { return 0; }
    public int getStableInsetRight() { return 0; }
    public int getStableInsetTop() { return 0; }
    public android.graphics.Insets getStableInsets() { return null; }
    public android.graphics.Insets getSystemGestureInsets() { return null; }
    public int getSystemWindowInsetBottom() { return 0; }
    public int getSystemWindowInsetLeft() { return 0; }
    public int getSystemWindowInsetRight() { return 0; }
    public int getSystemWindowInsetTop() { return 0; }
    public android.graphics.Insets getSystemWindowInsets() { return null; }
    public android.graphics.Insets getTappableElementInsets() { return null; }
    public boolean hasInsets() { return false; }
    public boolean hasStableInsets() { return false; }
    public boolean hasSystemWindowInsets() { return false; }
    public int hashCode() { return 0; }
    public android.view.WindowInsets inset(int p0, int p1, int p2, int p3) { return null; }
    public boolean isConsumed() { return false; }
    public boolean isRound() { return false; }
    public boolean isVisible(int p0) { return false; }
    public android.view.WindowInsets replaceSystemWindowInsets(int p0, int p1, int p2, int p3) { return null; }
    public android.view.WindowInsets replaceSystemWindowInsets(android.graphics.Rect p0) { return null; }

    public static class Builder {
        public android.view.WindowInsets build() { return null; }
        public android.view.WindowInsets.Builder setDisplayCutout(android.view.DisplayCutout p0) { return null; }
        public android.view.WindowInsets.Builder setInsets(int p0, android.graphics.Insets p1) { return null; }
        public android.view.WindowInsets.Builder setInsetsIgnoringVisibility(int p0, android.graphics.Insets p1) { return null; }
        public android.view.WindowInsets.Builder setMandatorySystemGestureInsets(android.graphics.Insets p0) { return null; }
        public android.view.WindowInsets.Builder setPrivacyIndicatorBounds(android.graphics.Rect p0) { return null; }
        public android.view.WindowInsets.Builder setRoundedCorner(int p0, android.view.RoundedCorner p1) { return null; }
        public android.view.WindowInsets.Builder setStableInsets(android.graphics.Insets p0) { return null; }
        public android.view.WindowInsets.Builder setSystemGestureInsets(android.graphics.Insets p0) { return null; }
        public android.view.WindowInsets.Builder setSystemWindowInsets(android.graphics.Insets p0) { return null; }
        public android.view.WindowInsets.Builder setTappableElementInsets(android.graphics.Insets p0) { return null; }
        public android.view.WindowInsets.Builder setVisible(int p0, boolean p1) { return null; }
    }

    public static class Type {
        public int captionBar() { return 0; }
        public int displayCutout() { return 0; }
        public int ime() { return 0; }
        public int mandatorySystemGestures() { return 0; }
        public int navigationBars() { return 0; }
        public int statusBars() { return 0; }
        public int systemBars() { return 0; }
        public int systemGestures() { return 0; }
        public int systemOverlays() { return 0; }
        public int tappableElement() { return 0; }
    }
}
