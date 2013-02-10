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

/**
 * This class contains tests that make sure that duplicates of different kinds
 * are handled in the correct way.
 */
public class TestDuplicates extends TestCase {

    interface A {
    }

    interface B {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C2 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C3 implements Gluey, Singleton, B {

        @Glue
        A[] m1;

    }

    /**
     * Verify that only a single instance of an object is included, no matter
     * how many times it was added.
     */
    public void testExistingObjects() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c1);
        dt.add(c1);
        dt.add(new Object[] {
                c1, c1, c2, c1
        });
        dt.add(c3);
        dt.add(c2);
        dt.add(c2);
        dt.add(c3);
        dt.add(c1);
        dt.add(c2);
        dt.add(c3);
        dt.add(c2);
        dt.add(c3);
        dt.add(c3);
        dt.add(new Object[] {
                c3, c3, c2, c1
        });
        dt.apply();

        assertEquals(c3, c1.m1);
        assertEquals(c3, c2.m1);
        assertEquals(2, c3.m1.length);
        assertNotNull(c3.m1[0]);
        assertNotNull(c3.m1[1]);
        assertTrue(c3.m1[0] == c1 ^ c3.m1[1] == c1);
        assertTrue(c3.m1[0] == c2 ^ c3.m1[1] == c2);
    }

    /**
     * Verify that even though a factory is added several times, it is only used
     * once to produce an object.
     */
    public void testExistingFactories() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        dt.add(C3.class);
        dt.add(C3.class);
        dt.add(new Class<?>[] {
            C2.class
        });
        dt.add(C3.class);
        dt.add(new Class<?>[] {
                C3.class, C2.class, C3.class, C3.class
        });
        dt.apply();

        assertEquals(C3.class, c1.m1.getClass());
        C3 c3 = (C3)c1.m1;
        assertEquals(2, c3.m1.length);
        assertTrue(c3.m1[0] == c1 ^ c3.m1[1] == c1);
    }

    /**
     * Verify that when an object is added several times the robustness of the
     * tape is not affected.
     */
    public void testUnaffectedRobustness() {
        DuctTape dt = new DuctTape();

        Object o = new Object();
        dt.add(o);
        dt.apply();
        dt.add(o);

        assertTrue(dt.isRobust());

        dt.add(new Object[] {
                o, o, o, o, o
        });

        assertTrue(dt.isRobust());
    }

}
