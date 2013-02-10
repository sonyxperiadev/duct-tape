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

import junit.framework.TestCase;

/**
 * This class contains tests that verifies that classes that inherit injections
 * in one way or another are handled as expected.
 */
public class TestInheritance extends TestCase {

    interface A {
    }

    interface B {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        A m1;

    }

    static class C2 implements A {
    }

    static class C3 extends C1 {
    }

    static class C4 extends C3 implements Gluey, Singleton {

        @Glue
        A m2;

    }

    static class C5 extends C1 implements Gluey, Singleton {

        @Glue
        A m2;

    }

    static class C6 extends C5 implements Gluey, Singleton {

        @Glue
        A m3;

    }

    static class C7 implements Gluey, Singleton {

        @Glue
        C4 m1;

    }

    static class C8 extends C3 implements Gluey, Singleton {

        @Glue
        B m2;

    }

    static class C9 implements Gluey, Singleton, B {
    }

    static class C10 implements Gluey, Singleton {

        @Glue(OPTIONAL)
        C8 m1;

    }

    /**
     * Make sure that all fields are injected even though they reside in
     * inherited classes.
     */
    public void testInheritedInjections() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C6 c6 = new C6();

        dt.add(c6);
        dt.add(c2);
        dt.apply();

        assertNotNull(c6.m1);
        assertNotNull(c6.m2);
        assertNotNull(c6.m3);
        assertEquals(c2, c6.m1);
        assertEquals(c2, c6.m2);
        assertEquals(c2, c6.m3);
    }

    /**
     * Make sure that even if a class in the the middle of the class hierarchy
     * does implement Injectable when the others do, all fields from ancestors
     * still get injected.
     */
    public void testInheritanceGap() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C4 c4 = new C4();

        dt.add(c4);
        dt.add(c2);
        dt.apply();

        assertNotNull(c4.m1);
        assertNotNull(c4.m2);
        assertEquals(c2, c4.m1);
        assertEquals(c2, c4.m2);
    }

    /**
     * Make sure that if a factory that has a complex inheritance chain where
     * inherited classes requires some injections are resolved correctly.
     */
    public void testFactoryInheritance() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C7 c7 = new C7();

        dt.add(C4.class);
        dt.add(c2);
        dt.add(c7);
        dt.apply();

        assertNotNull(c7.m1);
        C4 c4 = c7.m1;
        assertNotNull(c4.m1);
        assertNotNull(c4.m2);
        assertEquals(c2, c4.m1);
        assertEquals(c2, c4.m2);
    }

    /**
     * Make sure that a factory that has a complex inheritance chain, where some
     * of the requirements of the inherited classes cannot be met, does not
     * produce.
     */
    public void testInvalidFactoryInheritance() {
        DuctTape dt = new DuctTape();

        C10 c10 = new C10();

        dt.add(C8.class);
        dt.add(new C9());
        dt.add(c10);
        dt.apply();

        assertNull(c10.m1);
    }

}
