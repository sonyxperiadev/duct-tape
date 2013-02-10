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
import static com.sonyericsson.inject.DuctTape.ORDERED;

import com.sonyericsson.inject.DuctTape;
import com.sonyericsson.inject.Glue;
import com.sonyericsson.inject.GluingException;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

import junit.framework.TestCase;

/**
 * This class contains tests that make sure that injections marked as optional
 * are handled correctly.
 */
public class TestOptional extends TestCase {

    interface A {
    }

    interface B {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        A m1;

        @Glue({
                ORDERED, OPTIONAL
        })
        B[] m2;

    }

    static class C2 implements Gluey, Singleton, A {

        @Glue
        A m1;

        @Glue({
                OPTIONAL, ORDERED
        })
        B m2;

    }

    static class C3 implements B {
    }

    /**
     * Make sure that it is possible to tape when there are no available
     * implementations for optional fields.
     */
    public void testMissing() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();

        dt.add(c1);
        dt.add(c2);
        dt.apply();

        assertEquals(c2, c1.m1);
        assertEquals(c2, c2.m1);
        assertNull(c1.m2);
        assertNull(c2.m2);
    }

    /**
     * Make sure that optional fields can handle being injected when there are
     * matching references.
     */
    public void testExisting() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c1);
        dt.add(c2);
        dt.add(c3);
        dt.apply();

        assertEquals(c2, c1.m1);
        assertEquals(c2, c2.m1);
        assertEquals(c3, c2.m2);
        assertEquals(1, c1.m2.length);
        assertEquals(c3, c1.m2[0]);
    }

    /**
     * Provoke an exception by trying to tape when some required interface
     * references cannot be resolved.
     */
    public void testNegative() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        try {
            dt.apply();
            fail();
        } catch (GluingException e) {
        }
    }

}
