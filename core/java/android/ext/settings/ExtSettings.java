package android.ext.settings;

import java.lang.reflect.Field;
import java.util.Set;

import static android.ext.settings.GnssConstants.SUPL_DISABLED;
import static android.ext.settings.GnssConstants.SUPL_SERVER_GRAPHENEOS_PROXY;
import static android.ext.settings.GnssConstants.SUPL_SERVER_STANDARD;

/** @hide */
public class ExtSettings {

    public static final BoolSetting AUTO_GRANT_OTHER_SENSORS_PERMISSION = new BoolSetting(
            Setting.Scope.PER_USER, "auto_grant_OTHER_SENSORS_perm", true);

    public static final BoolSetting SCREENSHOT_TIMESTAMP_EXIF = new BoolSetting(
            Setting.Scope.PER_USER, "screenshot_timestamp_exif", false);

    public static final IntSetting GNSS_SUPL = new IntSetting(
            Setting.Scope.GLOBAL, "force_disable_supl", // historical name
            SUPL_SERVER_GRAPHENEOS_PROXY, // default
            SUPL_SERVER_STANDARD, SUPL_DISABLED, SUPL_SERVER_GRAPHENEOS_PROXY // valid values
    );

    // also read in packages/modules/DnsResolver (DnsTlsTransport.cpp and doh/network/driver.rs)
    public static final IntSysProperty CONNECTIVITY_CHECKS = new IntSysProperty(
            "persist.sys.connectivity_checks",
            ConnChecksSetting.VAL_DEFAULT,
            ConnChecksSetting.VAL_GRAPHENEOS, ConnChecksSetting.VAL_STANDARD, ConnChecksSetting.VAL_DISABLED
    );

    private ExtSettings() {}

    // used for making settings defined in this class unreadable by third-party apps
    public static void getKeys(Setting.Scope scope, Set<String> dest) {
        for (Field field : ExtSettings.class.getDeclaredFields()) {
            if (!Setting.class.isAssignableFrom(field.getType())) {
                continue;
            }
            Setting s;
            try {
                s = (Setting) field.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }

            if (s.getScope() == scope) {
                if (!dest.add(s.getKey())) {
                    throw new IllegalStateException("duplicate definition of setting " + s.getKey());
                }
            }
        }
    }
}
