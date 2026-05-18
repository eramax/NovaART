package android.os;

public final class Build {
    public static final String MANUFACTURER = "NovaART";
    public static final String MODEL = "NovaART";
    public static final String BRAND = "NovaART";
    public static final String DEVICE = "novaart";
    public static final String PRODUCT = "novaart";
    public static final String[] SUPPORTED_ABIS = { "x86_64", "x86" };

    private Build() {
    }

    public static final class VERSION {
        public static final int SDK_INT = 30;
        public static final String RELEASE = "11";
        public static final String CODENAME = "REL";

        private VERSION() {
        }
    }

    public static final class VERSION_CODES {
        public static final int CUPCAKE = 3;
        public static final int R = 30;

        private VERSION_CODES() {
        }
    }
}
