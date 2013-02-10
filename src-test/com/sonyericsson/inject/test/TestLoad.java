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

import java.util.Vector;

import com.sonyericsson.inject.DuctTape;
import com.sonyericsson.inject.Glue;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

import junit.framework.TestCase;

/**
 * This class contains tests that make sure that nothing falls apart when a lot
 * of operations are made on large structures.
 */
public class TestLoad extends TestCase {

    static final int R = 0;

    static final int S = 1;

    static final int T = 2;

    static final int Q = 3;

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

    static class C0 {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        C2 c;

    }

    static class C2 implements Gluey, Singleton {

        @Glue
        C3 c;

    }

    static class C3 implements Gluey, Singleton {

        @Glue
        C4 c;

    }

    static class C4 implements Gluey, Singleton {

        @Glue
        C5 c;

    }

    static class C5 implements Gluey, Singleton {

        @Glue
        C6 c;

    }

    static class C6 implements Gluey, Singleton {

        @Glue
        C7 c;

    }

    static class C7 implements Gluey, Singleton {

        @Glue
        C8 c;

    }

    static class C8 implements Gluey, Singleton {

        @Glue
        C9 c;

    }

    static class C9 implements Gluey, Singleton {

        @Glue
        C10 c;

    }

    static class C10 implements Gluey, Singleton {

        @Glue
        C11 c;

    }

    static class C11 implements Gluey, Singleton {

        @Glue
        C12 c;

    }

    static class C12 implements Gluey, Singleton {

        @Glue
        C13 c;

    }

    static class C13 implements Gluey, Singleton {

        @Glue
        C14 c;

    }

    static class C14 implements Gluey, Singleton {

        @Glue
        C15 c;

    }

    static class C15 implements Gluey, Singleton {

        @Glue
        C16 c;

    }

    static class C16 implements Gluey, Singleton {

        @Glue
        C17 c;

    }

    static class C17 implements Gluey, Singleton {

        @Glue
        C1 c;

    }

    static class C18 implements Gluey, Singleton {

        @Glue
        C1 m1;

        @Glue
        C2 m2;

        @Glue
        C3 m3;

        @Glue
        C4 m4;

        @Glue
        C5 m5;

        @Glue
        C6 m6;

        @Glue
        C7 m7;

        @Glue
        C8 m8;

        @Glue
        C9 m9;

        @Glue
        C10 m10;

        @Glue
        C11 m11;

        @Glue
        C12 m12;

        @Glue
        C13 m13;

        @Glue
        C14 m14;

        @Glue
        C15 m15;

        @Glue
        C16 m16;

        @Glue
        C17 m17;

    }

    static class C19 implements Gluey, Singleton {

        @Glue({
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                23, 24, 25, 26, 27, 28, 29
        })
        Object m1;

    }

    static class C20 implements Gluey, Singleton, A {

        @Glue(OPTIONAL)
        B[] m1;

        @Glue
        D[] m2;

        @Glue({
                R, S, T
        })
        C[] m3;

    }

    static class C21 implements Gluey, Singleton, A, C, D {

        @Glue(Q)
        B[] m1;

        @Glue
        A[] m2;

    }

    static class C22 implements Gluey, Singleton, E {

        @Glue
        A[] m1;

        @Glue(ORDERED)
        B[] m2;

        @Glue(OPTIONAL)
        C[] m3;

        @Glue({
                S, OPTIONAL
        })
        D[] m4;

    }

    static class C23 implements Gluey, Singleton, A, B {

        @Glue
        C[] m1;

        @Glue
        D[] m2;

    }

    static class C24 extends C23 {
    }

    static class C25 extends C23 {
    }

    static class C26 extends C23 {
    }

    static class C27 extends C23 {
    }

    static class C28 extends C23 {
    }

    static class C29 extends C23 {
    }

    static class C30 extends C23 {
    }

    static class C31 extends C23 {
    }

    static class C32 extends C23 {
    }

    static class C33 extends C23 {
    }

    static class C34 extends C23 {
    }

    static class C35 extends C23 {
    }

    static class C36 extends C23 {
    }

    static class C37 extends C23 {
    }

    static class C38 extends C23 {
    }

    static class C39 extends C23 {
    }

    static class C40 extends C23 {
    }

    static class C41 extends C23 {
    }

    static class C42 extends C23 {
    }

    static class C43 extends C23 {
    }

    static class C44 extends C23 {
    }

    static class C45 extends C23 {
    }

    static class C46 extends C23 {
    }

    static class C47 extends C23 {
    }

    static class C48 extends C23 {
    }

    static class C49 extends C23 {
    }

    static class C50 extends C23 {
    }

    static class C51 extends C23 {
    }

    static class C52 extends C23 {
    }

    static class C53 extends C23 {
    }

    static class C54 extends C23 {
    }

    static class C55 extends C23 {
    }

    static class C56 extends C23 {
    }

    static class C57 extends C23 {
    }

    static class C58 extends C20 {
    }

    static class C59 extends C20 {
    }

    static class C60 extends C20 {
    }

    static class C61 extends C20 {
    }

    static class C62 extends C20 {
    }

    static class C63 extends C20 {
    }

    static class C64 extends C20 {
    }

    static class C65 extends C20 {
    }

    static class C66 extends C20 {
    }

    static class C67 extends C20 {
    }

    static class C68 extends C20 {
    }

    static class C69 extends C20 {
    }

    static class C70 extends C20 {
    }

    static class C71 extends C21 {
    }

    static class C72 extends C21 {
    }

    static class C73 extends C21 {
    }

    static class C74 extends C21 {
    }

    static class C75 extends C21 {
    }

    static class C76 extends C21 {
    }

    static class C77 extends C21 {
    }

    static class C78 extends C21 {
    }

    static class C79 extends C21 {
    }

    static class C80 extends C21 {
    }

    static class C81 extends C21 {
    }

    static class C82 extends C21 {
    }

    static class C83 extends C21 {
    }

    static class C84 extends C21 {
    }

    /**
     * Make sure that there is no problem with adding lots of factories to the
     * setup.
     */
    public void testManyFactories() {
        DuctTape dt = new DuctTape();

        dt.add(C1.class);
        dt.add(C2.class);
        dt.add(C3.class);
        dt.add(C4.class);
        dt.add(C5.class);
        dt.add(C6.class);
        dt.add(C7.class);
        dt.add(C8.class);
        dt.add(C9.class);
        dt.add(C10.class);
        dt.add(C11.class);
        dt.add(C12.class);
        dt.add(C13.class);
        dt.add(C14.class);
        dt.add(C15.class);
        dt.add(C16.class);
        dt.add(C17.class);

        dt.apply();
    }

    /**
     * Make sure that there is no problem with adding lots of objects to the
     * setup.
     */
    public void testManyObjects() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 50; i++) {
            dt.add(new C0());
        }

        dt.apply();
    }

    /**
     * Make sure that there is no problem with adding lots of named objects to
     * the setup.
     */
    public void testManyNamedObjects() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 50; i++) {
            dt.add(new C0(), i);
        }

        dt.apply();
    }

    /**
     * Make sure that there is no problem with adding lots of tags to objects.
     */
    public void testManyNames() {
        DuctTape dt = new DuctTape();

        C0 c = new C0();

        dt.add(c);
        for (int i = 0; i < 50; i++) {
            dt.add(c, i);
        }
        for (int i = 0; i < 50; i++) {
            dt.add(c, i + 100);
        }

        dt.apply();
    }

    /**
     * Make sure it is possible to add several objects in one batch.
     */
    public void testObjectBatch() {
        DuctTape dt = new DuctTape();

        dt.add(new Object[] {});
        dt.add(new Object[] {
                new C1(), new C2(), new C3()
        });
        dt.add(new Object[] {
            new C4()
        });
        dt.add(new Object[] {
                new C5(), new C6(), new C7(), new C8(), new C9(), new C10(), new C11()
        });
        dt.add(new C16());
        dt.add(new Object[] {
            new C17()
        });
        Vector<Object> v = new Vector<Object>();
        v.add(new C12());
        v.add(new C13());
        v.add(new C14());
        v.add(new C15());
        dt.add(v);

        dt.apply();
    }

    /**
     * Make sure it is possible to have a large number of dependencies to other
     * objects with different interfaces.
     */
    public void testManyDependencies() {
        DuctTape dt = new DuctTape();

        dt.add(new C1());
        dt.add(new C2());
        dt.add(new C3());
        dt.add(new C4());
        dt.add(new C5());
        dt.add(new C6());
        dt.add(new C7());
        dt.add(new C8());
        dt.add(new C9());
        dt.add(new C10());
        dt.add(new C11());
        dt.add(new C12());
        dt.add(new C13());
        dt.add(new C14());
        dt.add(new C15());
        dt.add(new C16());
        dt.add(new C17());
        dt.add(new C18());

        dt.apply();
    }

    /**
     * Make sure it is possible to add several factories in one batch.
     */
    public void testFactoryBatch() {
        DuctTape dt = new DuctTape();

        dt.add(new C1());
        dt.add(new Class<?>[] {
            C2.class
        });
        dt.add(new Class<?>[] {
                C3.class, C4.class, C5.class
        });
        dt.add(new Class<?>[] {
            C6.class
        });
        dt.add(C18.class);
        dt.add(new Class<?>[] {
                C7.class, C8.class, C9.class, C10.class
        });
        dt.add(C11.class);
        dt.add(new Class<?>[] {
                C12.class, C13.class
        });
        dt.add(new Class<?>[] {
            C14.class
        });
        dt.add(new Class<?>[] {
            C15.class
        });
        dt.add(new Class<?>[] {
                C16.class, C17.class
        });

        dt.apply();
    }

    /**
     * Make sure it is possible to add lots of tags to an object.
     */
    public void testManyObjectTags() {
        DuctTape dt = new DuctTape();

        Object o = new Object();
        for (int i = 0; i < 16; i++) {
            dt.add(o, i);
        }
        dt.add(o, new int[] {
                17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29
        });
        C19 c19 = new C19();
        dt.add(c19);

        dt.apply();

        assertNotNull(c19.m1);
        assertEquals(o, c19.m1);
    }

    /**
     * Make sure it is possible to add lots of tags to a factory.
     */
    public void testManyFactoryTags() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 16; i++) {
            dt.add(Object.class, i);
        }
        dt.add(Object.class, new int[] {
                17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29
        });
        C19 c19 = new C19();
        dt.add(c19);

        dt.apply();

        assertNotNull(c19.m1);
        assertEquals(Object.class, c19.m1.getClass());
    }

    /**
     * Make sure a large set of objects with a lot of cross dependencies is
     * processed correctly.
     */
    public void testObjectSoup() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 100; i++) {
            dt.add(new C20());
            dt.add(new C21(), T);
            dt.add(new C22());
            dt.add(new C23(), Q);
        }

        dt.apply();
    }

    /**
     * Make sure a large set of factories with a lot of cross dependencies to
     * other factories and objects is processed correctly.
     */
    public void testFactorySoup() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 100; i++) {
            dt.add(new C20());
        }
        dt.add(C24.class, Q);
        dt.add(C25.class, Q);
        dt.add(C26.class, Q);
        dt.add(C27.class, Q);
        dt.add(C28.class, Q);
        dt.add(C29.class, Q);
        dt.add(C30.class, Q);
        dt.add(C31.class, Q);
        dt.add(C32.class, T);
        dt.add(C33.class, Q);
        dt.add(C34.class, Q);
        dt.add(C35.class, Q);
        dt.add(C36.class, Q);
        dt.add(C37.class, Q);
        dt.add(C38.class, Q);
        dt.add(C39.class, Q);
        dt.add(C40.class, Q);
        dt.add(C41.class, Q);
        dt.add(C42.class, Q);
        dt.add(C43.class, Q);
        dt.add(C44.class, Q);
        dt.add(C45.class, Q);
        dt.add(C46.class);
        dt.add(C47.class);
        dt.add(C48.class, Q);
        dt.add(C49.class, Q);
        dt.add(C50.class, Q);
        dt.add(C51.class, Q);
        dt.add(C52.class, Q);
        dt.add(C53.class, Q);
        dt.add(C54.class);
        dt.add(C55.class, Q);
        dt.add(C56.class, Q);
        dt.add(C57.class, Q);
        dt.add(C58.class);
        dt.add(C59.class);
        dt.add(C60.class);
        dt.add(C61.class);
        dt.add(C62.class, T);
        dt.add(C63.class);
        dt.add(C64.class);
        dt.add(C65.class);
        dt.add(C66.class, Q);
        dt.add(C67.class);
        dt.add(C68.class);
        dt.add(C69.class);
        dt.add(C70.class);
        dt.add(C71.class, T);
        dt.add(C72.class, T);
        dt.add(C73.class, T);
        dt.add(C74.class);
        dt.add(C75.class);
        dt.add(C76.class, T);
        dt.add(C77.class, T);
        dt.add(C78.class, Q);
        dt.add(C79.class, T);
        dt.add(C80.class);
        dt.add(C81.class, T);
        dt.add(C82.class, T);
        dt.add(C83.class, T);
        dt.add(C84.class, T);

        dt.apply();
    }

}
