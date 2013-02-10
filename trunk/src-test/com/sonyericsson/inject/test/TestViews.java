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

import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class contains test cases that makes sure that views identified by type
 * and ID are properly injected when available.
 */
public class TestViews extends AndroidTestCase {

    interface A {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue(R.layout.ducttape_layout)
        View m1;

    }

    static class C2 implements Gluey, Singleton {

        @Glue
        A m1;

    }

    static class C3 implements Gluey, Singleton, A {

        @Glue(R.id.view1)
        TextView m1;

    }

    /**
     * Make sure that when a non-inflated layout is referred in an injection it
     * is properly inflated and injected when there is a context available.
     */
    public void testViewInflation() {
        DuctTape dt = new DuctTape(getContext());

        C1 c1 = new C1();

        dt.add(c1);

        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(LinearLayout.class, c1.m1.getClass());
    }

    /**
     * Make sure that when a factory class requires a layout to be inflated it
     * produces its object when a context with the requested resource is
     * available.
     */
    public void testInflateInFactory() {
        DuctTape dt = new DuctTape(getContext());

        C2 c2 = new C2();

        dt.add(c2);
        dt.add(C1.class);

        dt.apply();

        assertNotNull(c2.m1);
        assertEquals(C1.class, c2.m1.getClass());
    }

    /**
     * Make sure that when a view is available and an injection refers to it
     * using its ID it is properly injected.
     */
    public void testViewFinding() {
        DuctTape dt = new DuctTape();

        C3 c3 = new C3();

        dt.add(LayoutInflater.from(getContext()).inflate(R.layout.ducttape_layout, null));
        dt.add(c3);

        dt.apply();

        assertNotNull(c3.m1);
        assertEquals(R.id.view1, c3.m1.getId());
        assertEquals("abcd123", c3.m1.getText());
    }

    /**
     * Make sure that a factory class requiring a specific view to be injected
     * produces its object when the required view is available.
     */
    public void testFindInFactory() {
        DuctTape dt = new DuctTape(getContext());

        C2 c2 = new C2();

        dt.add(c2);
        dt.add(LayoutInflater.from(getContext()).inflate(R.layout.ducttape_layout, null));
        dt.add(C3.class);

        dt.apply();

        assertNotNull(c2.m1);
        assertEquals(C3.class, c2.m1.getClass());
    }

}
