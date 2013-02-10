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
import com.sonyericsson.inject.Singleton;

import junit.framework.TestCase;

public class TestSingleton extends TestCase {

    interface A {
    }

    interface B {
    }

    interface C {
    }

    interface D {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        A m1;

        @Glue
        B m2;

        @Glue
        C m3;

        @Glue
        D m4;

    }

    static class C2 implements A, B {
    }

    static class C3 implements Singleton, C, D {
    }

    static class C4 implements Gluey, Singleton {

        @Glue
        A[] m1;

        @Glue
        A[] m2;

    }

    static class C5 implements Singleton, A {
    }

    static class C6 implements Gluey, Singleton {

        @Glue
        A m1;

        @Glue
        B[] m2;

    }

    /**
     * 
     */
    public void testInstantiation() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        dt.add(C2.class);
        dt.add(C3.class);
        dt.apply();

        assertNotNull(c1.m1);
        assertNotNull(c1.m2);
        assertTrue(c1.m1 != c1.m2);
        assertEquals(C2.class, c1.m1.getClass());

        assertNotNull(c1.m3);
        assertNotNull(c1.m4);
        assertTrue(c1.m3 == c1.m4);
        assertEquals(C3.class, c1.m3.getClass());
    }

    /**
     * 
     */
    public void testUpdatingArray() {
        DuctTape dt = new DuctTape();

        C4 c4 = new C4();

        dt.add(c4);
        dt.add(C2.class);
        dt.add(C5.class);
        dt.apply();

        assertNotNull(c4.m1);
        assertNotNull(c4.m2);
        assertEquals(2, c4.m1.length);
        assertEquals(2, c4.m2.length);
        assertTrue(C5.class == c4.m1[0].getClass() ^ C5.class == c4.m1[1].getClass());
        assertTrue(C2.class == c4.m1[0].getClass() ^ C2.class == c4.m1[1].getClass());
        assertTrue((C2.class == c4.m1[0].getClass() && c4.m1[0] != c4.m2[0])
                || (C2.class == c4.m1[1].getClass() && c4.m1[1] != c4.m2[1]));
    }

    /**
     * 
     */
    public void testReuse() {
        DuctTape dt = new DuctTape();

        C6 c6 = new C6();

        dt.add(c6);
        dt.add(C2.class);
        dt.apply();

        assertNotNull(c6.m1);
        C2 c21 = (C2)c6.m1;
        assertNotNull(c6.m2);
        assertEquals(1, c6.m2.length);
        C2 c22 = (C2)c6.m2[0];
        assertTrue(c22 != c21);

        // Affect robustness to trigger re-processing
        dt.add(C1.class);
        dt.remove(C1.class);

        dt.apply();
        assertEquals(c21, c6.m1);
        assertNotNull(c6.m2);
        assertEquals(1, c6.m2.length);
        assertEquals(c22, c6.m2[0]);
    }

}
