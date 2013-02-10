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

package com.sonyericsson.inject.test;

import static com.sonyericsson.inject.DuctTape.OPTIONAL;

import com.sonyericsson.inject.DuctTape;
import com.sonyericsson.inject.Glue;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

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
import android.content.ContentResolver;
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
import android.test.AndroidTestCase;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

/**
 * This class contains tests that verifies that all services etc. can be
 * extracted properly from an added context of any kind.
 */
public class TestContext extends AndroidTestCase {

    interface A {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue
        ApplicationInfo m1;

        @Glue
        AssetManager m2;

        @Glue
        ClassLoader m3;

        @Glue
        ContentResolver m4;

        @Glue
        Looper m5;

        @Glue
        PackageManager m6;

        @Glue
        Resources m7;

        @Glue
        AccessibilityManager m8;

        @Glue
        AccountManager m9;

        @Glue
        ActivityManager m10;

        @Glue
        AlarmManager m11;

        @Glue
        AudioManager m12;

        @Glue
        ClipboardManager m13;

        @Glue
        ConnectivityManager m14;

        @Glue
        DevicePolicyManager m15;

        @Glue
        DropBoxManager m16;

        @Glue
        InputMethodManager m17;

        @Glue
        NotificationManager m18;

        @Glue
        LayoutInflater m19;

        @Glue
        LocationManager m20;

        @Glue
        WallpaperManager m21;

        @Glue
        PowerManager m22;

        @Glue
        SearchManager m23;

        @Glue
        SensorManager m24;

        @Glue
        TelephonyManager m25;

        @Glue
        UiModeManager m26;

        @Glue
        Vibrator m27;

        @Glue
        WifiManager m28;

        @Glue
        WindowManager m29;

        @Glue
        KeyguardManager m30;

    }

    static class C2 implements Gluey, Singleton {

        @Glue
        A[] m1;

    }

    static class C3 implements Gluey, Singleton, A {

        @Glue
        ApplicationInfo[] m1;

        @Glue
        AssetManager[] m2;

        @Glue
        ClassLoader[] m3;

        @Glue
        ContentResolver[] m4;

        @Glue
        Looper[] m5;

        @Glue
        PackageManager[] m6;

        @Glue
        Resources[] m7;

        @Glue
        AccessibilityManager[] m8;

        @Glue
        AccountManager[] m9;

        @Glue
        ActivityManager[] m10;

        @Glue
        AlarmManager[] m11;

        @Glue
        AudioManager[] m12;

        @Glue
        ClipboardManager[] m13;

        @Glue
        ConnectivityManager[] m14;

        @Glue
        DevicePolicyManager[] m15;

        @Glue
        DropBoxManager[] m16;

        @Glue
        InputMethodManager[] m17;

        @Glue
        NotificationManager[] m18;

        @Glue
        LayoutInflater[] m19;

        @Glue
        LocationManager[] m20;

        @Glue
        WallpaperManager[] m21;

        @Glue
        PowerManager[] m22;

        @Glue
        SearchManager[] m23;

        @Glue
        SensorManager[] m24;

        @Glue
        TelephonyManager[] m25;

        @Glue
        UiModeManager[] m26;

        @Glue
        Vibrator[] m27;

        @Glue
        WifiManager[] m28;

        @Glue
        WindowManager[] m29;

        @Glue
        KeyguardManager[] m30;

    }

    static class C4 implements Gluey, Singleton, A {

        @Glue({
                OPTIONAL, 1
        })
        ApplicationInfo m1;

        @Glue({
                OPTIONAL, 1
        })
        AssetManager m2;

        @Glue({
                OPTIONAL, 1
        })
        ClassLoader m3;

        @Glue({
                OPTIONAL, 1
        })
        ContentResolver m4;

        @Glue({
                OPTIONAL, 1
        })
        Looper m5;

        @Glue({
                OPTIONAL, 1
        })
        PackageManager m6;

        @Glue({
                OPTIONAL, 1
        })
        Resources m7;

        @Glue({
                OPTIONAL, 1
        })
        AccessibilityManager m8;

        @Glue({
                OPTIONAL, 1
        })
        AccountManager m9;

        @Glue({
                OPTIONAL, 1
        })
        ActivityManager m10;

        @Glue({
                OPTIONAL, 1
        })
        AlarmManager m11;

        @Glue({
                OPTIONAL, 1
        })
        AudioManager m12;

        @Glue({
                OPTIONAL, 1
        })
        ClipboardManager m13;

        @Glue({
                OPTIONAL, 1
        })
        ConnectivityManager m14;

        @Glue({
                OPTIONAL, 1
        })
        DevicePolicyManager m15;

        @Glue({
                OPTIONAL, 1
        })
        DropBoxManager m16;

        @Glue({
                OPTIONAL, 1
        })
        InputMethodManager m17;

        @Glue({
                OPTIONAL, 1
        })
        NotificationManager m18;

        @Glue({
                OPTIONAL, 1
        })
        LayoutInflater m19;

        @Glue({
                OPTIONAL, 1
        })
        LocationManager m20;

        @Glue({
                OPTIONAL, 1
        })
        WallpaperManager m21;

        @Glue({
                OPTIONAL, 1
        })
        PowerManager m22;

        @Glue({
                OPTIONAL, 1
        })
        SearchManager m23;

        @Glue({
                OPTIONAL, 1
        })
        SensorManager m24;

        @Glue({
                OPTIONAL, 1
        })
        TelephonyManager m25;

        @Glue({
                OPTIONAL, 1
        })
        UiModeManager m26;

        @Glue({
                OPTIONAL, 1
        })
        Vibrator m27;

        @Glue({
                OPTIONAL, 1
        })
        WifiManager m28;

        @Glue({
                OPTIONAL, 1
        })
        WindowManager m29;

        @Glue({
                OPTIONAL, 1
        })
        KeyguardManager m30;

    }

    static class C5 implements Gluey, Singleton, A {

        @Glue(1)
        ApplicationInfo m1;

        @Glue(1)
        AssetManager m2;

        @Glue(1)
        ClassLoader m3;

    }

    /**
     * Make sure that system services and other context related objects are
     * properly injected when a context is available.
     */
    public void testContextExtraction() {
        DuctTape dt = new DuctTape(getContext());

        C1 c1 = new C1();

        dt.add(c1);

        dt.apply();

        assertNotNull(c1.m1);
        assertNotNull(c1.m2);
        assertNotNull(c1.m3);
        assertNotNull(c1.m4);
        assertNotNull(c1.m5);
        assertNotNull(c1.m6);
        assertNotNull(c1.m7);
        assertNotNull(c1.m8);
        assertNotNull(c1.m9);
        assertNotNull(c1.m10);
        assertNotNull(c1.m11);
        assertNotNull(c1.m12);
        assertNotNull(c1.m13);
        assertNotNull(c1.m14);
        assertNotNull(c1.m15);
        assertNotNull(c1.m16);
        assertNotNull(c1.m17);
        assertNotNull(c1.m18);
        assertNotNull(c1.m19);
        assertNotNull(c1.m20);
        assertNotNull(c1.m21);
        assertNotNull(c1.m22);
        assertNotNull(c1.m23);
        assertNotNull(c1.m24);
        assertNotNull(c1.m25);
        assertNotNull(c1.m26);
        assertNotNull(c1.m27);
        assertNotNull(c1.m28);
        assertNotNull(c1.m29);
        assertNotNull(c1.m30);

        assertEquals(getContext().getApplicationInfo(), c1.m1);
        assertEquals(getContext().getAssets(), c1.m2);
        assertEquals(getContext().getClassLoader(), c1.m3);
        assertEquals(getContext().getContentResolver(), c1.m4);
        assertEquals(getContext().getMainLooper(), c1.m5);
        assertEquals(getContext().getPackageManager(), c1.m6);
        assertEquals(getContext().getResources(), c1.m7);
        assertEquals(getContext().getSystemService(Service.ACCESSIBILITY_SERVICE), c1.m8);
        assertEquals(getContext().getSystemService(Service.ACCOUNT_SERVICE), c1.m9);
        assertEquals(getContext().getSystemService(Service.ACTIVITY_SERVICE), c1.m10);
        assertEquals(getContext().getSystemService(Service.ALARM_SERVICE), c1.m11);
        assertEquals(getContext().getSystemService(Service.AUDIO_SERVICE), c1.m12);
        assertEquals(getContext().getSystemService(Service.CLIPBOARD_SERVICE), c1.m13);
        assertEquals(getContext().getSystemService(Service.CONNECTIVITY_SERVICE), c1.m14);
        assertEquals(getContext().getSystemService(Service.DEVICE_POLICY_SERVICE), c1.m15);
        assertEquals(getContext().getSystemService(Service.DROPBOX_SERVICE), c1.m16);
        assertEquals(getContext().getSystemService(Service.INPUT_METHOD_SERVICE), c1.m17);
        assertEquals(getContext().getSystemService(Service.NOTIFICATION_SERVICE), c1.m18);
        assertEquals(getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE), c1.m19);
        assertEquals(getContext().getSystemService(Service.LOCATION_SERVICE), c1.m20);
        assertEquals(getContext().getSystemService(Service.WALLPAPER_SERVICE), c1.m21);
        assertEquals(getContext().getSystemService(Service.POWER_SERVICE), c1.m22);
        assertEquals(getContext().getSystemService(Service.SEARCH_SERVICE), c1.m23);
        assertEquals(getContext().getSystemService(Service.SENSOR_SERVICE), c1.m24);
        assertEquals(getContext().getSystemService(Service.TELEPHONY_SERVICE), c1.m25);
        assertEquals(getContext().getSystemService(Service.UI_MODE_SERVICE), c1.m26);
        assertEquals(getContext().getSystemService(Service.VIBRATOR_SERVICE), c1.m27);
        assertEquals(getContext().getSystemService(Service.WIFI_SERVICE), c1.m28);
        assertEquals(getContext().getSystemService(Service.WINDOW_SERVICE), c1.m29);

        // Each call for a KEYGUARD_SERVICE generates a new instance
        assertEquals(KeyguardManager.class, c1.m30.getClass());
    }

    /**
     * Make sure that a factory class that requires system services is
     * instantiated when a context has been given.
     */
    public void testExtractionInFactory() {
        DuctTape dt = new DuctTape(getContext());

        C2 c2 = new C2();

        dt.add(c2);
        dt.add(C1.class);
        dt.add(C3.class);
        dt.add(C5.class);

        dt.apply();

        assertNotNull(c2.m1);
        assertEquals(2, c2.m1.length);
        assertTrue(c2.m1[0].getClass() == C1.class ^ c2.m1[1].getClass() == C1.class);
        assertTrue(c2.m1[0].getClass() == C3.class ^ c2.m1[1].getClass() == C3.class);
    }

    /**
     * Make sure that system services and other context related objects are
     * properly injected into array fields when a context is available.
     */
    public void testContextArray() {
        DuctTape dt = new DuctTape(getContext());

        C3 c3 = new C3();

        dt.add(c3);

        dt.apply();

        assertNotNull(c3.m1);
        assertNotNull(c3.m2);
        assertNotNull(c3.m3);
        assertNotNull(c3.m4);
        assertNotNull(c3.m5);
        assertNotNull(c3.m6);
        assertNotNull(c3.m7);
        assertNotNull(c3.m8);
        assertNotNull(c3.m9);
        assertNotNull(c3.m10);
        assertNotNull(c3.m11);
        assertNotNull(c3.m12);
        assertNotNull(c3.m13);
        assertNotNull(c3.m14);
        assertNotNull(c3.m15);
        assertNotNull(c3.m16);
        assertNotNull(c3.m17);
        assertNotNull(c3.m18);
        assertNotNull(c3.m19);
        assertNotNull(c3.m20);
        assertNotNull(c3.m21);
        assertNotNull(c3.m22);
        assertNotNull(c3.m23);
        assertNotNull(c3.m24);
        assertNotNull(c3.m25);
        assertNotNull(c3.m26);
        assertNotNull(c3.m27);
        assertNotNull(c3.m28);
        assertNotNull(c3.m29);
        assertNotNull(c3.m30);

        assertEquals(1, c3.m1.length);
        assertEquals(1, c3.m2.length);
        assertEquals(1, c3.m3.length);
        assertEquals(1, c3.m4.length);
        assertEquals(1, c3.m5.length);
        assertEquals(1, c3.m6.length);
        assertEquals(1, c3.m7.length);
        assertEquals(1, c3.m8.length);
        assertEquals(1, c3.m9.length);
        assertEquals(1, c3.m10.length);
        assertEquals(1, c3.m11.length);
        assertEquals(1, c3.m12.length);
        assertEquals(1, c3.m13.length);
        assertEquals(1, c3.m14.length);
        assertEquals(1, c3.m15.length);
        assertEquals(1, c3.m16.length);
        assertEquals(1, c3.m17.length);
        assertEquals(1, c3.m18.length);
        assertEquals(1, c3.m19.length);
        assertEquals(1, c3.m20.length);
        assertEquals(1, c3.m21.length);
        assertEquals(1, c3.m22.length);
        assertEquals(1, c3.m23.length);
        assertEquals(1, c3.m24.length);
        assertEquals(1, c3.m25.length);
        assertEquals(1, c3.m26.length);
        assertEquals(1, c3.m27.length);
        assertEquals(1, c3.m28.length);
        assertEquals(1, c3.m29.length);
        assertEquals(1, c3.m30.length);

        assertEquals(getContext().getApplicationInfo(), c3.m1[0]);
        assertEquals(getContext().getAssets(), c3.m2[0]);
        assertEquals(getContext().getClassLoader(), c3.m3[0]);
        assertEquals(getContext().getContentResolver(), c3.m4[0]);
        assertEquals(getContext().getMainLooper(), c3.m5[0]);
        assertEquals(getContext().getPackageManager(), c3.m6[0]);
        assertEquals(getContext().getResources(), c3.m7[0]);
        assertEquals(getContext().getSystemService(Service.ACCESSIBILITY_SERVICE), c3.m8[0]);
        assertEquals(getContext().getSystemService(Service.ACCOUNT_SERVICE), c3.m9[0]);
        assertEquals(getContext().getSystemService(Service.ACTIVITY_SERVICE), c3.m10[0]);
        assertEquals(getContext().getSystemService(Service.ALARM_SERVICE), c3.m11[0]);
        assertEquals(getContext().getSystemService(Service.AUDIO_SERVICE), c3.m12[0]);
        assertEquals(getContext().getSystemService(Service.CLIPBOARD_SERVICE), c3.m13[0]);
        assertEquals(getContext().getSystemService(Service.CONNECTIVITY_SERVICE), c3.m14[0]);
        assertEquals(getContext().getSystemService(Service.DEVICE_POLICY_SERVICE), c3.m15[0]);
        assertEquals(getContext().getSystemService(Service.DROPBOX_SERVICE), c3.m16[0]);
        assertEquals(getContext().getSystemService(Service.INPUT_METHOD_SERVICE), c3.m17[0]);
        assertEquals(getContext().getSystemService(Service.NOTIFICATION_SERVICE), c3.m18[0]);
        assertEquals(getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE), c3.m19[0]);
        assertEquals(getContext().getSystemService(Service.LOCATION_SERVICE), c3.m20[0]);
        assertEquals(getContext().getSystemService(Service.WALLPAPER_SERVICE), c3.m21[0]);
        assertEquals(getContext().getSystemService(Service.POWER_SERVICE), c3.m22[0]);
        assertEquals(getContext().getSystemService(Service.SEARCH_SERVICE), c3.m23[0]);
        assertEquals(getContext().getSystemService(Service.SENSOR_SERVICE), c3.m24[0]);
        assertEquals(getContext().getSystemService(Service.TELEPHONY_SERVICE), c3.m25[0]);
        assertEquals(getContext().getSystemService(Service.UI_MODE_SERVICE), c3.m26[0]);
        assertEquals(getContext().getSystemService(Service.VIBRATOR_SERVICE), c3.m27[0]);
        assertEquals(getContext().getSystemService(Service.WIFI_SERVICE), c3.m28[0]);
        assertEquals(getContext().getSystemService(Service.WINDOW_SERVICE), c3.m29[0]);

        // Each call for a KEYGUARD_SERVICE generates a new instance
        assertEquals(KeyguardManager.class, c3.m30[0].getClass());
    }

    /**
     * Make sure fields that take system services but that requires tags do not
     * get injected with any services even though there is a context available.
     */
    public void testTaggedContextStuff() {
        DuctTape dt = new DuctTape(getContext());

        C4 c4 = new C4();

        dt.add(c4);

        dt.apply();

        assertNull(c4.m1);
        assertNull(c4.m2);
        assertNull(c4.m3);
        assertNull(c4.m4);
        assertNull(c4.m5);
        assertNull(c4.m6);
        assertNull(c4.m7);
        assertNull(c4.m8);
        assertNull(c4.m9);
        assertNull(c4.m10);
        assertNull(c4.m11);
        assertNull(c4.m12);
        assertNull(c4.m13);
        assertNull(c4.m14);
        assertNull(c4.m15);
        assertNull(c4.m16);
        assertNull(c4.m17);
        assertNull(c4.m18);
        assertNull(c4.m19);
        assertNull(c4.m20);
        assertNull(c4.m21);
        assertNull(c4.m22);
        assertNull(c4.m23);
        assertNull(c4.m24);
        assertNull(c4.m25);
        assertNull(c4.m26);
        assertNull(c4.m27);
        assertNull(c4.m28);
        assertNull(c4.m29);
    }

}
