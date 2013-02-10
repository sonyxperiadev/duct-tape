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
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

import junit.framework.TestCase;

/**
 * This class contains tests that verifies that injections that requires
 * whatever is injected to be tagged with specific tags works out as expected.
 */
public class TestTags extends TestCase {

    static final int Q = 0;

    static final int K = 1;

    static final int L = 2;

    static final int M = 3;

    static final int X = 4;

    static final int A = 5;

    static final int B = 6;

    interface A {
    }

    interface B {
    }

    static class C1 implements Gluey, Singleton {

        @Glue(Q)
        A m1;

        @Glue
        A[] m2;

    }

    static class C2 implements Gluey, Singleton, A {

        @Glue(Q)
        A m1;

        @Glue({
                K, L, ORDERED, M
        })
        B m2;

    }

    static class C3 implements A, B, Singleton {
    }

    static class C4 implements Gluey, Singleton, A {

        @Glue(X)
        B[] m1;

    }

    static class C5 implements Gluey, Singleton, B {

        @Glue
        A m1;

    }

    static class C6 implements Gluey, Singleton {

        @Glue(Q)
        A m1;

        @Glue({
                OPTIONAL, K
        })
        B m2;

    }

    /**
     * Test that named objects are distributed correctly when the annotated
     * field has one or more names requirements on what is injected.
     */
    public void testFilter() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c31 = new C3();
        C3 c32 = new C3();

        dt.add(c1);
        dt.add(c2, Q);
        dt.add(c31, L);
        dt.add(c32, X);
        dt.apply();

        assertEquals(c2, c1.m1);
        assertEquals(3, c1.m2.length);
        assertTrue(c1.m2[0] == c2 || c1.m2[1] == c2 || c1.m2[2] == c2);
        assertTrue(c1.m2[0] == c31 || c1.m2[1] == c31 || c1.m2[2] == c31);
        assertTrue(c1.m2[0] == c32 || c1.m2[1] == c32 || c1.m2[2] == c32);
    }

    /**
     * Tests that an object is injected when only some of its names matches the
     * requested ones.
     */
    public void testMultipleTags() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c2);
        dt.add(c3, A);
        dt.add(c3, L);
        dt.add(c3, B);
        dt.add(c3, Q);
        dt.apply();

        assertEquals(c3, c2.m1);
        assertEquals(c3, c2.m2);
    }

    /**
     * Tests that all names of an object is used when finding matching
     * injection, even names that have been added to an object after the object
     * itself was added.
     */
    public void testAddedTag() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c2);
        dt.add(c3);
        dt.add(c3, A);
        dt.add(c3, L);
        dt.add(c3, B);
        dt.add(c3, Q);
        dt.apply();

        assertEquals(c3, c2.m1);
        assertEquals(c3, c2.m2);
    }

    /**
     * Test that naming an object that does not exist in the setup is just
     * ignored.
     */
    public void testTaggingNonexisting() {
        DuctTape dt = new DuctTape();

        dt.add(new Object(), 123);
        dt.add(new Object(), 124);
        dt.add(new Object(), 125);
    }

    /**
     * Tests that setting an object name several times is still treated the same
     * way as just setting it once.
     */
    public void testDuplicateTag() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c2);
        dt.add(c3, L);
        dt.add(c3, L);
        dt.add(c3, L);
        dt.add(c3, L);
        dt.add(c3, Q);
        dt.add(c3, Q);
        dt.add(c3, Q);
        dt.add(c3, Q);
        dt.apply();

        assertEquals(c3, c2.m1);
        assertEquals(c3, c2.m2);
    }

    /**
     * Test that when a factory requires a name it produces an instance when
     * there is an object available with the correct name.
     */
    public void testFactoryRequiringTag() {
        DuctTape dt = new DuctTape();

        C5 c51 = new C5();
        C5 c52 = new C5();

        dt.add(C4.class);
        dt.add(c51, X);
        dt.add(c52, X);

        dt.apply();

        assertNotNull(c51.m1);
        assertNotNull(c52.m1);

        C4 c4 = (C4)c51.m1;
        assertNotNull(c4.m1);
        assertEquals(2, c4.m1.length);
    }

    /**
     * Test that when an injection requires a tag matching factories are used in
     * case they have been given that tag.
     */
    public void testTaggedFactories() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        dt.add(c2);
        dt.add(C3.class, new int[] {
                K, L
        });
        dt.add(C3.class, Q);
        dt.add(C1.class, Q);
        dt.add(C1.class, new int[] {
                K, L
        });

        dt.apply();

        assertNotNull(c2.m1);
        assertNotNull(c2.m2);
        assertEquals(C3.class, c2.m1.getClass());
        assertEquals(C3.class, c2.m2.getClass());
    }

    /**
     * Test that when tags are changed on a factory the injections are properly
     * updated on products already produced.
     */
    public void testChangeFactoryTags() {
        DuctTape dt = new DuctTape();

        C6 c6 = new C6();
        dt.add(c6);
        dt.add(C3.class, Q);
        dt.apply();

        assertNotNull(c6.m1);
        assertNull(c6.m2);
        C3 c3 = (C3)c6.m1;

        dt.add(C3.class, K);
        dt.apply();

        assertNotNull(c6.m1);
        assertNotNull(c6.m2);
        assertEquals(c3, c6.m1);
        assertEquals(c3, c6.m2);

        dt.remove(C3.class, K);
        dt.apply();

        assertNotNull(c6.m1);
        assertNull(c6.m2);
    }

}
