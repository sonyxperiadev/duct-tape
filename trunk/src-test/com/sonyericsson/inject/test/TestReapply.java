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
 * This class contains tests that verifies that injections are properly updated
 * when the object mesh is changed.
 */
public class TestReapply extends TestCase {

    interface A {
    }

    interface B {
    }

    interface C {
    }

    interface D {
    }

    interface E {
    }

    interface F {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        B[] m1;

    }

    static class C2 implements Gluey, Singleton, B {

        @Glue(OPTIONAL)
        C m1;

    }

    static class C3 implements Singleton, C {
    }

    static class C4 implements Singleton, B {
    }

    static class C5 implements Gluey, Singleton {

        @Glue({
                OPTIONAL, 1, 3
        })
        B[] m1;

        @Glue({
                OPTIONAL, 2, 4
        })
        B[] m2;

        @Glue({
                OPTIONAL, 5
        })
        B m3;

    }

    static class C6 implements Gluey, Singleton, A, D {

        @Glue
        B m1;

        @Glue(OPTIONAL)
        E m2;

    }

    static class C7 implements Gluey, Singleton, B {

        @Glue(OPTIONAL)
        C[] m1;

    }

    static class C8 implements Gluey, Singleton, C, D {

        @Glue(OPTIONAL)
        A m1;

    }

    static class C9 implements Gluey, Singleton, C {

        @Glue(OPTIONAL)
        D[] m1;

    }

    static class C10 implements Gluey, Singleton, D {

        @Glue({
                OPTIONAL, 1
        })
        C[] m1;

    }

    static class C11 implements Gluey, Singleton, C {

        static int numCreations = 0;

        C11() {
            numCreations++;
        }

    }

    static class C12 implements Gluey, Singleton {

        @Glue(OPTIONAL)
        D m1;

        @Glue
        F m2;

    }

    static class C13 implements Gluey, Singleton, E {
    }

    static class C14 implements Singleton, F {
    }

    static class C15 implements Gluey, Singleton, A, D {

        @Glue
        B m1;

        @Glue
        E m2;

    }

    static class C16 implements Gluey, Singleton, B {

        @Glue
        C[] m1;

    }

    static class C17 implements Gluey, Singleton {

        @Glue
        B m1;

    }

    static class C18 implements Gluey, Singleton {

        @Glue
        C m1;

    }

    static class C19 implements Gluey, Singleton, C {
    }

    /**
     * Make sure that references are properly updated when one object has been
     * removed.
     */
    public void testRemoveOne() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c1);
        dt.add(c2);
        dt.add(c3);
        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertNotNull(c2.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c2, c1.m1[0]);
        assertEquals(c3, c2.m1);

        dt.remove(c3);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c2, c1.m1[0]);
        assertNull(c2.m1);
    }

    /**
     * Make sure that references are properly updated when one object has been
     * added.
     */
    public void testAddOne() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();

        dt.add(c1);
        dt.add(c2);
        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c2, c1.m1[0]);
        assertNull(c2.m1);

        dt.add(c3);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertNotNull(c2.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c2, c1.m1[0]);
        assertEquals(c3, c2.m1);
    }

    /**
     * Make sure that references are properly updated when several objects have
     * been removed.
     */
    public void testRemoveMany() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C4 c41 = new C4();
        C4 c42 = new C4();
        C4 c43 = new C4();

        dt.add(c1);
        dt.add(c41);
        dt.add(c42);
        dt.add(c43);
        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(3, c1.m1.length);
        assertTrue(c1.m1[0] == c41 || c1.m1[1] == c41 || c1.m1[2] == c41);
        assertTrue(c1.m1[0] == c42 || c1.m1[1] == c42 || c1.m1[2] == c42);
        assertTrue(c1.m1[0] == c43 || c1.m1[1] == c43 || c1.m1[2] == c43);

        dt.remove(c41);
        dt.remove(c43);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c42, c1.m1[0]);
    }

    /**
     * Make sure that references are properly updated when several objects have
     * been added.
     */
    public void testAddMany() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C4 c41 = new C4();
        C4 c42 = new C4();
        C4 c43 = new C4();

        dt.add(c1);
        dt.add(c42);
        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c42, c1.m1[0]);

        dt.add(c41);
        dt.add(c43);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertNotNull(c1.m1);
        assertEquals(3, c1.m1.length);
        assertTrue(c1.m1[0] == c41 || c1.m1[1] == c41 || c1.m1[2] == c41);
        assertTrue(c1.m1[0] == c42 || c1.m1[1] == c42 || c1.m1[2] == c42);
        assertTrue(c1.m1[0] == c43 || c1.m1[1] == c43 || c1.m1[2] == c43);
    }

    /**
     * Make sure that all references are properly updated when the tags on
     * several objects have been changed.
     */
    public void testChangeTags() {
        DuctTape dt = new DuctTape();

        C5 c5 = new C5();
        C4 c41 = new C4();
        C4 c42 = new C4();
        C4 c43 = new C4();
        C4 c44 = new C4();

        dt.add(c5);
        dt.add(c41);
        dt.add(c42, new int[] {
                1, 4
        });
        dt.add(c42, 2);
        dt.add(c43, 3);
        dt.add(C4.class, 5);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertEquals(2, c5.m1.length);
        assertTrue(c5.m1[0] == c42 ^ c5.m1[1] == c42);
        assertTrue(c5.m1[0] == c43 ^ c5.m1[1] == c43);
        assertEquals(1, c5.m2.length);
        assertEquals(c42, c5.m2[0]);
        assertNull(c5.m3);

        dt.remove(c42, 2);
        dt.remove(c42, 1);
        dt.remove(C4.class, 5);
        dt.add(c44, 1);
        dt.add(c43, 4);

        assertTrue(!dt.isRobust());

        dt.apply();

        assertTrue(dt.isRobust());
        assertEquals(2, c5.m1.length);
        assertTrue(c5.m1[0] == c44 ^ c5.m1[1] == c44);
        assertTrue(c5.m1[0] == c43 ^ c5.m1[1] == c43);
        assertEquals(2, c5.m2.length);
        assertTrue(c5.m2[0] == c42 ^ c5.m2[1] == c42);
        assertTrue(c5.m2[0] == c43 ^ c5.m2[1] == c43);
        assertNull(c5.m3);
    }

    /**
     * Make sure that all injected references in several objects with relations
     * to each other are updated properly.
     */
    public void testAlterSeveral() {
        DuctTape dt = new DuctTape();

        C6 c6 = new C6();
        C7 c7 = new C7();
        C8 c8 = new C8();
        C9 c9 = new C9();
        C10 c10 = new C10();

        dt.add(c6);
        dt.add(c7);
        dt.add(c8);
        dt.add(c9);
        dt.apply();

        assertNotNull(c7.m1);
        assertEquals(2, c7.m1.length);
        assertTrue(c7.m1[0] == c9 ^ c7.m1[1] == c9);
        assertTrue(c7.m1[0] == c8 ^ c7.m1[1] == c8);
        assertEquals(c6, c8.m1);
        assertNotNull(c9.m1);
        assertEquals(2, c9.m1.length);
        assertTrue(c9.m1[0] == c6 ^ c9.m1[1] == c6);
        assertTrue(c9.m1[0] == c8 ^ c9.m1[1] == c8);
        assertEquals(c7, c6.m1);

        dt.remove(c6);
        dt.apply();

        assertNotNull(c7.m1);
        assertEquals(2, c7.m1.length);
        assertTrue(c7.m1[0] == c9 ^ c7.m1[1] == c9);
        assertTrue(c7.m1[0] == c8 ^ c7.m1[1] == c8);
        assertNotNull(c9.m1);
        assertEquals(1, c9.m1.length);
        assertEquals(c8, c9.m1[0]);
        assertNull(c8.m1);

        dt.add(c10);
        dt.add(c9, 1);
        dt.apply();

        assertNotNull(c10.m1);
        assertEquals(1, c10.m1.length);
        assertEquals(c9, c10.m1[0]);
        assertNotNull(c9.m1);
        assertEquals(2, c9.m1.length);
        assertTrue(c9.m1[0] == c10 ^ c9.m1[1] == c10);
        assertTrue(c9.m1[0] == c8 ^ c9.m1[1] == c8);
        assertNotNull(c7.m1);
        assertEquals(2, c7.m1.length);
        assertTrue(c7.m1[0] == c9 ^ c7.m1[1] == c9);
        assertTrue(c7.m1[0] == c8 ^ c7.m1[1] == c8);
    }

    /**
     * Make sure that factory products are reused when re-applying.
     */
    public void testProductPersistence() {
        DuctTape dt = new DuctTape();

        C6 c6 = new C6();

        dt.add(c6);
        dt.add(C7.class);
        dt.apply();

        assertNotNull(c6.m1);
        assertEquals(C7.class, c6.m1.getClass());
        C7 c7 = (C7)c6.m1;

        C1 c1 = new C1();
        dt.remove(c6);
        dt.add(c1);
        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c7, c1.m1[0]);

        C8 c8 = new C8();
        dt.add(c8);
        dt.remove(c1);
        dt.apply();

        assertNull(c8.m1);

        dt.add(c1);
        dt.add(c6);
        dt.apply();

        assertEquals(c7, c6.m1);
        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c7, c1.m1[0]);
    }

    /**
     * Make sure that factory products are not removed until apply() is invoked.
     */
    public void testProductRemoval() {
        DuctTape dt = new DuctTape();

        C15 c15 = new C15();
        C18 c18 = new C18();

        dt.add(c15);
        dt.add(c18);
        dt.add(C7.class);
        dt.add(C13.class);
        dt.add(C19.class);
        dt.apply();

        assertNotNull(c15.m1);
        assertEquals(C7.class, c15.m1.getClass());
        C7 c7 = (C7)c15.m1;
        assertNotNull(c15.m2);
        assertEquals(C13.class, c15.m2.getClass());
        C13 c13 = (C13)c15.m2;
        assertEquals(C19.class, c18.m1.getClass());
        C19 c19 = (C19)c18.m1;

        dt.remove(C7.class);
        dt.remove(C19.class);
        dt.remove(C13.class);
        dt.add(C7.class);
        dt.add(C13.class);
        dt.add(C19.class);
        dt.apply();

        assertEquals(c7, c15.m1);
        assertEquals(c13, c15.m2);
        assertEquals(c19, c18.m1);
    }

    /**
     * Make sure that factory products that are no longer in use do not
     * interfere with resolving the current setup.
     */
    public void testProductNonInteference() {
        DuctTape dt = new DuctTape();

        C15 c15 = new C15();
        C12 c12 = new C12();

        dt.add(c15);
        dt.add(c12);
        dt.add(C16.class);
        dt.add(C13.class);
        dt.add(C11.class);
        dt.add(C14.class);
        dt.apply();

        assertNotNull(c15.m1);
        assertEquals(C16.class, c15.m1.getClass());
        C16 c16 = (C16)c15.m1;
        assertNotNull(c15.m2);
        assertEquals(C13.class, c15.m2.getClass());
        C13 c13 = (C13)c15.m2;
        assertNotNull(c16.m1);
        assertEquals(1, c16.m1.length);
        assertEquals(C11.class, c16.m1[0].getClass());
        assertEquals(1, C11.numCreations);
        assertEquals(c15, c12.m1);

        dt.remove(c15);
        dt.remove(C11.class);
        dt.apply();

        assertNull(c12.m1);
        assertEquals(1, C11.numCreations);

        dt.add(C11.class);
        dt.apply();

        // This is the important check - if the C7 factory was processed
        // despite not being in use it would cause a new instance of C11.
        assertEquals(1, C11.numCreations);

        dt.add(c15);
        dt.apply();

        assertNotNull(c15.m1);
        assertEquals(c16, c15.m1);
        assertNotNull(c15.m2);
        assertEquals(c13, c15.m2);
        assertNotNull(c16.m1);
        assertEquals(1, c16.m1.length);
        assertEquals(C11.class, c16.m1[0].getClass());
        assertEquals(2, C11.numCreations);
        assertEquals(c15, c12.m1);
    }

    /**
     * Make sure that when a factory has produced a product, that object is not
     * included when its injection criteria is no longer fulfilled.
     */
    public void testUnresolvableProduct() {
        DuctTape dt = new DuctTape();

        C3 c3 = new C3();
        C17 c17 = new C17();

        dt.add(c3);
        dt.add(c17);
        dt.add(C16.class);
        dt.apply();

        assertNotNull(c17.m1);
        assertEquals(C16.class, c17.m1.getClass());

        dt.remove(c3);
        try {
            dt.apply();
            fail();
        } catch (GluingException e) {
        }

        dt.add(c3);
        dt.apply();

        assertNotNull(c17.m1);
        assertEquals(C16.class, c17.m1.getClass());
    }

}
