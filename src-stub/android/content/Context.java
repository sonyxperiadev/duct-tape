/* 
 * Duct Tape – Dependency Injection Framework
 * Copyright (c) 2011 Sony Ericsson Mobile Communications AB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Author: 
 *   Pär Spjuth (par.spjuth@sonyericsson.com)
 */

package android.content;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.Service;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.DropBoxManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

public class Context {

    private static Resources mRes = new Resources();

    private static ApplicationInfo mInfo = new ApplicationInfo();

    private static AssetManager mAssets = new AssetManager();

    private static ContentResolver mResolver = new ContentResolver();

    private static Looper mLooper = new Looper();

    private static PackageManager mPkgManager = new PackageManager();

    private static Service[] mServices = new Service[] {
            new AccessibilityManager(), new AccountManager(), new ActivityManager(),
            new AlarmManager(), new AudioManager(), new ClipboardManager(),
            new ConnectivityManager(), new DevicePolicyManager(), new DropBoxManager(),
            new InputMethodManager(), new NotificationManager(), new LayoutInflater(),
            new LocationManager(), new WallpaperManager(), new PowerManager(), new SearchManager(),
            new SensorManager(), new TelephonyManager(), new UiModeManager(), new Vibrator(),
            new WifiManager(), new WindowManager(), new KeyguardManager()
    };

    public static final int ACCESSIBILITY_SERVICE = 0;

    public static final int ACCOUNT_SERVICE = 1;

    public static final int ACTIVITY_SERVICE = 2;

    public static final int ALARM_SERVICE = 3;

    public static final int AUDIO_SERVICE = 4;

    public static final int CLIPBOARD_SERVICE = 5;

    public static final int CONNECTIVITY_SERVICE = 6;

    public static final int DEVICE_POLICY_SERVICE = 7;

    public static final int DROPBOX_SERVICE = 8;

    public static final int INPUT_METHOD_SERVICE = 9;

    public static final int NOTIFICATION_SERVICE = 10;

    public static final int LAYOUT_INFLATER_SERVICE = 11;

    public static final int LOCATION_SERVICE = 12;

    public static final int WALLPAPER_SERVICE = 13;

    public static final int POWER_SERVICE = 14;

    public static final int SEARCH_SERVICE = 15;

    public static final int SENSOR_SERVICE = 16;

    public static final int TELEPHONY_SERVICE = 17;

    public static final int UI_MODE_SERVICE = 18;

    public static final int VIBRATOR_SERVICE = 19;

    public static final int WIFI_SERVICE = 20;

    public static final int WINDOW_SERVICE = 21;

    public static final int KEYGUARD_SERVICE = 22;

    public Resources getResources() {
        return mRes;
    }

    public Object getApplicationInfo() {
        return mInfo;
    }

    public AssetManager getAssets() {
        return mAssets;
    }

    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    public ContentResolver getContentResolver() {
        return mResolver;
    }

    public Looper getMainLooper() {
        return mLooper;
    }

    public PackageManager getPackageManager() {
        return mPkgManager;
    }

    public Service getSystemService(int service) {
        if (service < mServices.length) {
            return mServices[service];
        } else {
            return null;
        }
    }

    public Context createPackageContext(String name, int flags) {
        return null;
    }

    public String getPackageName() {
        return null;
    }

}
