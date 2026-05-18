package android.support.v4.graphics.drawable;

public class DrawableWrapperDonut {
    public void clearColorFilter() {}
    public void draw(android.graphics.Canvas p0) {}
    public android.graphics.Rect getBounds() { return null; }
    public int getChangingConfigurations() { return 0; }
    public android.graphics.drawable.Drawable.ConstantState getConstantState() { return null; }
    public android.graphics.drawable.Drawable getCurrent() { return null; }
    public int getIntrinsicHeight() { return 0; }
    public int getIntrinsicWidth() { return 0; }
    public int getLevel() { return 0; }
    public int getMinimumHeight() { return 0; }
    public int getMinimumWidth() { return 0; }
    public int getOpacity() { return 0; }
    public boolean getPadding(android.graphics.Rect p0) { return false; }
    public int[] getState() { return null; }
    public android.graphics.Region getTransparentRegion() { return null; }
    public android.graphics.drawable.Drawable getWrappedDrawable() { return null; }
    public void invalidateDrawable(android.graphics.drawable.Drawable p0) {}
    public void invalidateSelf() {}
    public boolean isCompatTintEnabled() { return false; }
    public boolean isStateful() { return false; }
    public boolean isVisible() { return false; }
    public android.graphics.drawable.Drawable mutate() { return null; }
    public android.support.v4.graphics.drawable.DrawableWrapperDonut.DrawableWrapperState mutateConstantState() { return null; }
    public android.graphics.drawable.Drawable newDrawableFromState(android.graphics.drawable.Drawable.ConstantState p0, android.content.res.Resources p1) { return null; }
    public void onBoundsChange(android.graphics.Rect p0) {}
    public boolean onLevelChange(int p0) { return false; }
    public void scheduleDrawable(android.graphics.drawable.Drawable p0, java.lang.Runnable p1, long p2) {}
    public void scheduleSelf(java.lang.Runnable p0, long p1) {}
    public void setAlpha(int p0) {}
    public void setChangingConfigurations(int p0) {}
    public void setColorFilter(int p0, android.graphics.PorterDuff.Mode p1) {}
    public void setColorFilter(android.graphics.ColorFilter p0) {}
    public void setCompatTint(int p0) {}
    public void setCompatTintList(android.content.res.ColorStateList p0) {}
    public void setCompatTintMode(android.graphics.PorterDuff.Mode p0) {}
    public void setDither(boolean p0) {}
    public void setFilterBitmap(boolean p0) {}
    public boolean setState(int[] p0) { return false; }
    public boolean setVisible(boolean p0, boolean p1) { return false; }
    public void setWrappedDrawable(android.graphics.drawable.Drawable p0) {}
    public void unscheduleDrawable(android.graphics.drawable.Drawable p0, java.lang.Runnable p1) {}
    public void unscheduleSelf(java.lang.Runnable p0) {}
    public void updateLocalState(android.content.res.Resources p0) {}
    public boolean updateTint(int[] p0) { return false; }

    public static class DrawableWrapperState {
        public boolean canConstantState() { return false; }
        public int getChangingConfigurations() { return 0; }
        public android.graphics.drawable.Drawable newDrawable() { return null; }
        public android.graphics.drawable.Drawable newDrawable(android.content.res.Resources p0) { return null; }
    }

    public static class DrawableWrapperStateDonut {
        public android.graphics.drawable.Drawable newDrawable(android.content.res.Resources p0) { return null; }
    }
}
