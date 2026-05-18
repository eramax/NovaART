package android.support.v4.app;

public class ActionBarDrawerToggle {
    public android.app.Activity access$400(android.support.v4.app.ActionBarDrawerToggle p0) { return null; }
    public boolean assumeMaterial(android.content.Context p0) { return false; }
    public android.graphics.drawable.Drawable getThemeUpIndicator() { return null; }
    public boolean isDrawerIndicatorEnabled() { return false; }
    public void onConfigurationChanged(android.content.res.Configuration p0) {}
    public void onDrawerClosed(android.view.View p0) {}
    public void onDrawerOpened(android.view.View p0) {}
    public void onDrawerSlide(android.view.View p0, float p1) {}
    public void onDrawerStateChanged(int p0) {}
    public boolean onOptionsItemSelected(android.view.MenuItem p0) { return false; }
    public void setActionBarDescription(int p0) {}
    public void setActionBarUpIndicator(android.graphics.drawable.Drawable p0, int p1) {}
    public void setDrawerIndicatorEnabled(boolean p0) {}
    public void setHomeAsUpIndicator(int p0) {}
    public void syncState() {}

    public static class ActionBarDrawerToggleImpl {
        public android.graphics.drawable.Drawable getThemeUpIndicator(android.app.Activity p0) { return null; }
        public java.lang.Object setActionBarDescription(java.lang.Object p0, android.app.Activity p1, int p2) { return null; }
        public java.lang.Object setActionBarUpIndicator(java.lang.Object p0, android.app.Activity p1, android.graphics.drawable.Drawable p2, int p3) { return null; }
    }

    public static class ActionBarDrawerToggleImplBase {
        public android.graphics.drawable.Drawable getThemeUpIndicator(android.app.Activity p0) { return null; }
        public java.lang.Object setActionBarDescription(java.lang.Object p0, android.app.Activity p1, int p2) { return null; }
        public java.lang.Object setActionBarUpIndicator(java.lang.Object p0, android.app.Activity p1, android.graphics.drawable.Drawable p2, int p3) { return null; }
    }

    public static class ActionBarDrawerToggleImplHC {
        public android.graphics.drawable.Drawable getThemeUpIndicator(android.app.Activity p0) { return null; }
        public java.lang.Object setActionBarDescription(java.lang.Object p0, android.app.Activity p1, int p2) { return null; }
        public java.lang.Object setActionBarUpIndicator(java.lang.Object p0, android.app.Activity p1, android.graphics.drawable.Drawable p2, int p3) { return null; }
    }

    public static class ActionBarDrawerToggleImplJellybeanMR2 {
        public android.graphics.drawable.Drawable getThemeUpIndicator(android.app.Activity p0) { return null; }
        public java.lang.Object setActionBarDescription(java.lang.Object p0, android.app.Activity p1, int p2) { return null; }
        public java.lang.Object setActionBarUpIndicator(java.lang.Object p0, android.app.Activity p1, android.graphics.drawable.Drawable p2, int p3) { return null; }
    }

    public static class Delegate {
        public android.graphics.drawable.Drawable getThemeUpIndicator() { return null; }
        public void setActionBarDescription(int p0) {}
        public void setActionBarUpIndicator(android.graphics.drawable.Drawable p0, int p1) {}
    }

    public interface DelegateProvider {
        android.support.v4.app.ActionBarDrawerToggle.Delegate getDrawerToggleDelegate();
    }

    public static class SlideDrawable {
        public void copyBounds(android.graphics.Rect p0) {}
        public void draw(android.graphics.Canvas p0) {}
        public float getPosition() { return 0f; }
        public void invalidateSelf() {}
        public void setOffset(float p0) {}
        public void setPosition(float p0) {}
    }
}
