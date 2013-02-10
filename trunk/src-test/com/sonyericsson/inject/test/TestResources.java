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

import com.sonyericsson.inject.DuctTape;
import com.sonyericsson.inject.Glue;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.R;
import com.sonyericsson.inject.Singleton;

import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

/**
 * This class contains tests that make sure that application resources can be
 * extracted as expected when a context is given.
 */
public class TestResources extends AndroidTestCase {

    interface A {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue(R.color.c1)
        int m1;

        @Glue(R.string.s1)
        String m2;

        @Glue(R.dimen.d1)
        float m3;

        @Glue(R.array.ia1)
        int[] m4;

        @Glue(R.array.sa1)
        String[] m5;

        @Glue(R.bool.b1)
        boolean m6;

        @Glue(R.drawable.icon)
        Drawable m7;

        @Glue(R.color.ducttape_colors)
        ColorStateList m8;

        @Glue(R.xml.ducttape_xml)
        XmlResourceParser m9;

    }

    static class C2 implements Gluey, Singleton {

        @Glue
        A m1;

    }

    /**
     * Make sure application resources are properly injected into fields which
     * has resource ID tags.
     */
    public void testResourceInjection() {
        DuctTape dt = new DuctTape(getContext());

        C1 c1 = new C1();

        dt.add(c1);

        dt.apply();

        assertEquals(0xcafebabe, c1.m1);
        assertEquals("blurp", c1.m2);
        assertEquals(14f, c1.m3);
        assertEquals(2, c1.m4.length);
        assertEquals(78, c1.m4[0]);
        assertEquals(10, c1.m4[1]);
        assertEquals(3, c1.m5.length);
        assertEquals("hello", c1.m5[0]);
        assertEquals("funny", c1.m5[1]);
        assertEquals("man", c1.m5[2]);
        assertEquals(true, c1.m6);
        assertNotNull(c1.m7);
        assertNotNull(c1.m8);
        assertNotNull(c1.m9);
    }

    /**
     * Make sure that a factory class that requires application resources is
     * indeed used to instantiate an object when a context containing the needed
     * resources is available.
     */
    public void testResourcesInFactory() {
        DuctTape dt = new DuctTape(getContext());

        C2 c2 = new C2();

        dt.add(c2);
        dt.add(C1.class);

        dt.apply();

        assertNotNull(c2.m1);
        assertEquals(C1.class, c2.m1.getClass());
    }

}
