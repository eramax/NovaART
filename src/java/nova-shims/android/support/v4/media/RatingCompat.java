package android.support.v4.media;

public class RatingCompat {
    public int describeContents() { return 0; }
    public android.support.v4.media.RatingCompat fromRating(java.lang.Object p0) { return null; }
    public float getPercentRating() { return 0f; }
    public java.lang.Object getRating() { return null; }
    public int getRatingStyle() { return 0; }
    public float getStarRating() { return 0f; }
    public boolean hasHeart() { return false; }
    public boolean isRated() { return false; }
    public boolean isThumbUp() { return false; }
    public android.support.v4.media.RatingCompat newHeartRating(boolean p0) { return null; }
    public android.support.v4.media.RatingCompat newPercentageRating(float p0) { return null; }
    public android.support.v4.media.RatingCompat newStarRating(int p0, float p1) { return null; }
    public android.support.v4.media.RatingCompat newThumbRating(boolean p0) { return null; }
    public android.support.v4.media.RatingCompat newUnratedRating(int p0) { return null; }
    public java.lang.String toString() { return null; }
    public void writeToParcel(android.os.Parcel p0, int p1) {}

    public static class Api19Impl {
        public float getPercentRating(android.media.Rating p0) { return 0f; }
        public int getRatingStyle(android.media.Rating p0) { return 0; }
        public float getStarRating(android.media.Rating p0) { return 0f; }
        public boolean hasHeart(android.media.Rating p0) { return false; }
        public boolean isRated(android.media.Rating p0) { return false; }
        public boolean isThumbUp(android.media.Rating p0) { return false; }
        public android.media.Rating newHeartRating(boolean p0) { return null; }
        public android.media.Rating newPercentageRating(float p0) { return null; }
        public android.media.Rating newStarRating(int p0, float p1) { return null; }
        public android.media.Rating newThumbRating(boolean p0) { return null; }
        public android.media.Rating newUnratedRating(int p0) { return null; }
    }

    public static class ExternalSyntheticApiModelOutline0 {
        public float m(android.media.Rating p0) { return 0f; }
        public android.media.MediaDescription.Builder m(android.media.MediaDescription.Builder p0, android.graphics.Bitmap p1) { return null; }
        public android.os.Parcelable.Creator m() { return null; }
        public void m(android.media.MediaDescription p0, android.os.Parcel p1, int p2) {}
        public void m(android.media.RemoteControlClient p0, int p1, long p2, float p3) {}
        public float m$1(android.media.Rating p0) { return 0f; }
        public android.media.MediaDescription.Builder m$1(android.media.MediaDescription.Builder p0, android.net.Uri p1) { return null; }
        public android.os.Parcelable.Creator m$1() { return null; }
        public void m$1(android.media.browse.MediaBrowser p0, java.lang.String p1, android.media.browse.MediaBrowser.SubscriptionCallback p2) {}
        public int m$2(android.media.session.MediaController.PlaybackInfo p0) { return 0; }
        public android.media.MediaDescription.Builder m$2(android.media.MediaDescription.Builder p0, java.lang.CharSequence p1) { return null; }
        public void m$2(android.media.session.MediaController.TransportControls p0, java.lang.String p1, android.os.Bundle p2) {}
        public int m$3(android.media.session.MediaController.PlaybackInfo p0) { return 0; }
        public void m$3(android.media.session.MediaController.TransportControls p0, java.lang.String p1, android.os.Bundle p2) {}
        public void m$4(android.media.session.MediaController.TransportControls p0) {}
        public void m$4(android.media.session.MediaController.TransportControls p0, java.lang.String p1, android.os.Bundle p2) {}
        public void m$5(android.media.session.MediaController.TransportControls p0) {}
        public void m$6(android.media.session.MediaController.TransportControls p0) {}
        public void m$7(android.media.session.MediaController.TransportControls p0) {}
    }

    public static class a {
        public android.support.v4.media.RatingCompat a(android.os.Parcel p0) { return null; }
        public android.support.v4.media.RatingCompat[] b(int p0) { return null; }
        public android.support.v4.media.RatingCompat createFromParcel(android.os.Parcel p0) { return null; }
        public android.support.v4.media.RatingCompat[] newArray(int p0) { return null; }
    }
}
