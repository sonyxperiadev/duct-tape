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

import java.util.Vector;

import com.sonyericsson.inject.DuctTape;
import com.sonyericsson.inject.Glue;
import com.sonyericsson.inject.Gluey;
import com.sonyericsson.inject.Singleton;

import static com.sonyericsson.inject.DuctTape.OPTIONAL;
import junit.framework.TestCase;

/**
 * This class contains tests that make sure that it is possible to remove
 * objects and tags without any problems.
 */
public class TestRemove extends TestCase {

    interface A {
    }

    static class C1 implements Gluey, Singleton {

        @Glue
        A[] m1;

    }

    static class C2 implements A {
    }

    static class C3 implements Gluey, Singleton {

        @Glue(1)
        A m1;

        @Glue(2)
        A m2;

    }

    static class C4 implements A {
    }

    static class C5 implements A {
    }

    static class C6 implements Gluey, Singleton {

        @Glue(OPTIONAL)
        A[] m1;

    }

    /**
     * Make sure it is possible to remove added objects.
     */
    public void testRemoving() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c21 = new C2();
        C2 c22 = new C2();
        C2 c23 = new C2();

        dt.add(c1);
        dt.add(c21);
        dt.add(c22);
        dt.add(c23);
        dt.remove(c21);
        dt.remove(c22);

        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c23, c1.m1[0]);
    }

    /**
     * Make sure it is possible to remove objects added with a specific tag.
     */
    public void testRemoveTagged() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c21 = new C2();
        C2 c22 = new C2();
        C2 c23 = new C2();
        C2 c24 = new C2();
        C2 c25 = new C2();
        C2 c26 = new C2();

        dt.add(c1);
        dt.add(C4.class, 6);
        dt.add(C5.class, 1);
        dt.add(c21, 1);
        dt.add(c22);
        dt.add(c23, new int[] {
                1, 2
        });
        dt.add(c24, 3);
        dt.add(c25, new int[] {
                4, 5, 6
        });
        dt.add(c26, 1);
        dt.remove(c21);
        dt.remove(1);
        dt.remove(new int[] {
                3, 6
        });

        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(c22, c1.m1[0]);
    }

    /**
     * Make sure it is possible to remove a batch of added objects at the same
     * time.
     */
    public void testRemoveBatch() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c21 = new C2();
        C2 c22 = new C2();
        C2 c23 = new C2();
        C2 c24 = new C2();
        C2 c25 = new C2();
        C2 c26 = new C2();
        C2 c27 = new C2();

        dt.add(c1);
        dt.add(c21);
        dt.add(c22);
        dt.add(c23);
        dt.add(c24);
        dt.add(c25);
        dt.remove(new Object[] {
                c22, c23, c25
        });
        Vector<C2> v = new Vector<C2>();
        v.add(c26);
        v.add(c27);
        dt.remove(v);

        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(2, c1.m1.length);
        assertTrue(c21 == c1.m1[0] ^ c21 == c1.m1[1]);
        assertTrue(c24 == c1.m1[0] ^ c24 == c1.m1[1]);
    }

    /**
     * Make sure that is possible to repeatedly modify the set of objects to be
     * processed.
     */
    public void testAddAndRemoveObjectRepeatedly() {
        DuctTape dt = new DuctTape();

        C6 oldC6 = null;
        for (int i = 0; i < 100; i++) {
            C6 c6 = new C6();
            if (i % 2 == 0) {
                dt.add(c6);
            } else {
                dt.add(new Object[] {
                    c6
                });
            }
            if (oldC6 != null) {
                dt.remove(oldC6);
            }
            oldC6 = c6;
        }

        dt.apply();
    }

    /**
     * Make sure that it is possible to remove previously added tag from an
     * object.
     */
    public void testRemoveObjectTag() {
        DuctTape dt = new DuctTape();

        C3 c3 = new C3();
        C2 c21 = new C2();
        C2 c22 = new C2();
        C2 c23 = new C2();

        dt.add(c3);
        dt.add(c21, 1);
        dt.add(c22);
        dt.add(c23, 1);
        dt.add(c23, 2);
        dt.add(c23, 3);
        dt.remove(c22);
        dt.remove(c23, 3);
        dt.remove(c23, 123);
        dt.remove(c23, 1);

        dt.apply();

        assertEquals(c21, c3.m1);
        assertEquals(c23, c3.m2);
    }

    /**
     * Make sure that it is possible to remove previously added tag from a
     * factory.
     */
    public void testRemoveFactoryTag() {
        DuctTape dt = new DuctTape();

        C2 c2 = new C2();
        C3 c3 = new C3();
        dt.add(c3);
        dt.add(C4.class, 1);
        dt.add(c2, new int[] {
                1, 3
        });
        dt.add(C2.class, new int[] {
                1, 2
        });
        dt.add(C2.class, 3);
        dt.add(C2.class, new int[] {
                7, 8
        });
        dt.remove(C2.class, 7);
        dt.remove(C2.class, 8);
        dt.remove(C2.class, 123);
        dt.remove(C2.class, 1);
        dt.remove(C4.class, 1);
        dt.remove(C1.class, 123);

        dt.apply();

        assertNotNull(c3.m1);
        assertNotNull(c3.m2);
        assertEquals(c2, c3.m1);
        assertTrue(c3.m2 != c2);
        assertEquals(C2.class, c3.m1.getClass());
    }

    /**
     * Make sure that it is possible to remove a previously added factory.
     */
    public void testRemoveFactory() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        dt.add(c1);
        dt.add(C2.class);
        dt.add(C4.class);
        dt.remove(C2.class);

        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(1, c1.m1.length);
        assertEquals(C4.class, c1.m1[0].getClass());
    }

    /**
     * Make sure that is possible to repeatedly modify the set of factories to
     * be used.
     */
    public void testAddAndRemoveFactoryRepeatedly() {
        DuctTape dt = new DuctTape();

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                dt.add(new Class<?>[] {
                    C4.class
                });
                dt.remove(C1.class);
            } else {
                dt.add(C1.class);
                dt.remove(new Class<?>[] {
                    C4.class
                });
            }
        }

        dt.apply();
    }

}
