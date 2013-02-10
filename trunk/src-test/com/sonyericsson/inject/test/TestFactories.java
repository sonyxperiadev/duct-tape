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
 * This class contains tests that make sure that factory classes are handled the
 * expected way.
 */
public class TestFactories extends TestCase {

    interface A {
    }

    interface B {
    }

    interface C {
    }

    interface Vital {
    }

    interface Optional {
    }

    static class V implements Vital {
    }

    static class O implements Optional {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C2 implements Gluey, Singleton, B {

        @Glue
        C m1;

        @Glue(OPTIONAL)
        Optional o;

    }

    static class C3 implements Gluey, Singleton, C {

        @Glue
        A m1;

        @Glue
        Vital v;

    }

    static class C4 implements Gluey, Singleton, B {

        @Glue
        A m1;

    }

    static class C5 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C6 implements Singleton, A {
    }

    static class C7 implements Gluey, Singleton {

        @Glue
        A m1;

    }

    static class C8 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C9 implements Gluey, Singleton, B {

        @Glue
        C m1;

    }

    static class C10 implements Gluey, Singleton, A {

        @Glue
        B m1;

    }

    static class C11 implements Singleton, A {
    }

    static class C12 implements Gluey, Singleton {

        @Glue
        B m1;

    }

    static class C13 implements Singleton, B {
    }

    static class C14 implements Gluey, Singleton, A {

        @Glue({
                OPTIONAL, ORDERED
        })
        B[] m1;

        @Glue({
                ORDERED, OPTIONAL
        })
        B[] m2;

    }

    static class C15 implements Gluey, Singleton, A {

        @Glue(OPTIONAL)
        B m1;

    }

    static class C16 implements Gluey, Singleton {

        @Glue
        A m1;

        @Glue(OPTIONAL)
        B m2;

    }

    static class C17 implements Gluey, Singleton, B {

        @Glue(OPTIONAL)
        A m1;

    }

    /**
     * Check that everything gets instantiated and bound together when
     * everything is available.
     */
    public void testAllValid() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();

        dt.add(c1);
        dt.add(new V());
        dt.add(new O());
        dt.add(C2.class);
        dt.add(C3.class);
        dt.apply();

        assertNotNull(c1.m1);
        C2 c2 = (C2)c1.m1;
        assertNotNull(c2.m1);
        C3 c3 = (C3)c2.m1;
        assertNotNull(c3.m1);
        assertEquals(c2, c1.m1);
        assertEquals(c3, c2.m1);
        assertEquals(c1, c3.m1);
    }

    /**
     * Check that it is still possible to tape when some factories do not get
     * all requirements fulfilled. This means that factories with injections
     * that cannot be resolved should not even be instantiated.
     */
    public void testMissingVital() {
        DuctTape dt = new DuctTape();

        C15 c15 = new C15();

        dt.add(c15);
        dt.add(C2.class);
        dt.add(C3.class);
        dt.apply();

        assertNull(c15.m1);
    }

    /**
     * Check that when there are too many available objects for the class that a
     * factory produces no object is produced.
     */
    public void testTooManyToBeValid() {
        DuctTape dt = new DuctTape();

        C4 c41 = new C4();
        C4 c42 = new C4();

        dt.add(C5.class);
        dt.add(C6.class);
        dt.add(c41);
        dt.add(c42);

        dt.apply();

        assertNotNull(c41.m1);
        assertNotNull(c42.m1);

        assertEquals(C6.class, c41.m1.getClass());
        assertEquals(C6.class, c42.m1.getClass());
        assertEquals(c41.m1, c42.m1);
    }

    /**
     * Check that when a factory producing objects that requires objects from
     * another available factory, but which objects can not have all injections
     * fulfilled, the first factory is not allowed to produce.
     */
    public void testManyInvalid() {
        DuctTape dt = new DuctTape();

        C7 c7 = new C7();

        dt.add(c7);
        dt.add(C8.class);
        dt.add(C9.class);
        dt.add(C10.class);
        dt.add(C11.class);
        dt.apply();

        assertEquals(C11.class, c7.m1.getClass());
    }

    /**
     * Check that a factory that requires a single instance of a type that is
     * available from many factories is not allowed to produce.
     */
    public void testManyOnSingle() {
        DuctTape dt = new DuctTape();

        C12 c12 = new C12();
        C13 c13 = new C13();

        dt.add(c12);
        dt.add(c13);
        dt.add(C17.class);
        dt.add(C6.class);
        dt.add(C11.class);
        dt.apply();

        assertEquals(c13, c12.m1);
    }

    /**
     * Check that when a factory requires many instances all relevant classes
     * produces objects which are then injected.
     */
    public void testManyInjected() {
        DuctTape dt = new DuctTape();

        C4 c4 = new C4();

        dt.add(c4);
        dt.add(C13.class);
        dt.add(C14.class);
        dt.apply();

        assertNotNull(c4.m1);
        assertEquals(C14.class, c4.m1.getClass());
        C14 c14 = (C14)c4.m1;
        assertNotNull(c14.m1);
    }

    /**
     * Check that unless an instance has a mandatory field, requiring the type
     * that a factory class can provide, the factory will not produce.
     */
    public void testNeedBasedCreation() {
        DuctTape dt = new DuctTape();

        C16 c16 = new C16();

        dt.add(c16);
        dt.add(C13.class);
        dt.add(C6.class);
        dt.apply();

        assertNotNull(c16.m1);
        assertEquals(C6.class, c16.m1.getClass());
        assertNull(c16.m2);
    }

}
