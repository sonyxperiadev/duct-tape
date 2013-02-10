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
import com.sonyericsson.inject.GluingException;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

import junit.framework.TestCase;

/**
 * This class contains tests that make sure that fields with array types are
 * injected correctly depending on the situation.
 */
public class TestArrays extends TestCase {

    interface A {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        A[] m1;

        @Glue(OPTIONAL)
        A m2;

    }

    static class C2 implements Gluey, Singleton, A {
    }

    static class C3 {

        @Glue(OPTIONAL)
        A[] m1;

    }

    static class C4 implements Gluey, Singleton {

        @Glue
        A[][] m1;

        @Glue
        A[][][] m2;

    }

    /**
     * Make sure that 1D-arrays that are added as objects (as opposed to each
     * element being added as a separate object) are properly injected where
     * appropriate.
     */
    public void testOneDimArray() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        Object o = new C2[] {
                new C2(), new C2()
        };
        dt.add(o);
        dt.apply();

        assertNotNull(c1.m1);
        assertNull(c1.m2);
        assertEquals(2, c1.m1.length);
        assertEquals(o, c1.m1);
    }

    /**
     * Make sure that a 2D-array that is added as an object is not injected in
     * any way into a field of the same element type but which has only one
     * dimension.
     */
    public void testTooManyDimsArray() {
        DuctTape dt = new DuctTape();

        C3 c3 = new C3();

        dt.add(c3);
        Object o = new C2[][] {
                {}, {
                        new C2(), new C2()
                }
        };
        dt.add(o);
        dt.apply();

        assertNull(c3.m1);
    }

    /**
     * Make sure that a 2D-array is properly injected into both a 2D-field and a
     * 3D-field, where the latter will have its first dimension set to the same
     * size as the number of 2D-arrays injected.
     */
    public void testMultiDimsArray() {
        DuctTape dt = new DuctTape();

        C4 c4 = new C4();

        dt.add(c4);
        Object o = new C2[][] {
                {}, {
                        new C2(), new C2()
                }
        };
        dt.add(o);
        dt.apply();

        assertEquals(o, c4.m1);
        assertNotNull(c4.m2);
        assertEquals(1, c4.m2.length);
        assertEquals(o, c4.m2[0]);
    }

    /**
     * Make sure that when there are both a 1D-array object and several object
     * of the same type that can be injected in a field of array type an
     * exception is thrown.
     */
    public void testAmbiguousArrays() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        Object o = new C2[] {
                new C2(), new C2()
        };
        dt.add(o);
        dt.add(new C2());

        try {
            dt.apply();
            fail();
        } catch (GluingException e) {
        }
    }

}
