package android.provider;

import android.content.ContentResolver;

public final class Settings {
    private Settings() {
    }

    public static class SettingNotFoundException extends Exception {
        public SettingNotFoundException(String name) {
            super(name);
        }
    }

    public static final class System {
        public static final String ACCELEROMETER_ROTATION = "accelerometer_rotation";

        private System() {
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            if (ACCELEROMETER_ROTATION.equals(name)) {
                return 0;
            }
            throw new SettingNotFoundException(name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            try {
                return getInt(cr, name);
            } catch (SettingNotFoundException e) {
                return def;
            }
        }
    }
}
