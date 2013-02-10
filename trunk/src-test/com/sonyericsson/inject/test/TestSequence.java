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
 * This class contains tests that make sure that array injections that requires
 * the references to be ordered by dependency are made correctly.
 */
public class TestSequence extends TestCase {

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

    interface G {
    }

    interface All {
    }

    interface Some {
    }

    static class C1 implements Gluey, Singleton {

        @Glue(ORDERED)
        All[] m1;

    }

    static class C2 implements Gluey, Singleton, All {

        @Glue
        A m1;

        @Glue
        B m2;

    }

    static class C3 implements Gluey, Singleton, A, All {

        @Glue
        B m1;

    }

    static class C4 implements Gluey, Singleton, B, All {

        @Glue
        C m1;

    }

    static class C5 implements C, All {
    }

    static class C6 implements Gluey, Singleton, F, All, Some {

        @Glue
        E m1;

        @Glue
        G m2;

    }

    static class C7 implements Gluey, Singleton, F, G, All, Some {

        @Glue
        D m1;

    }

    static class C8 implements D, E, All, Some {
    }

    static class C9 implements Gluey, Singleton, All, Some {

        @Glue
        F[] m1;

        @Glue
        E m2;

    }

    static class C10 implements Gluey, Singleton {

        @Glue(ORDERED)
        Some[] m1;

    }

    static class C11 implements Gluey, Singleton, C, All {

        @Glue
        A m1;

    }

    static class C12 implements Gluey, Singleton, All {

        @Glue
        A m1;

    }

    static class C13 implements Gluey, Singleton, A, All {

        @Glue
        B m1;

    }

    static class C14 implements Gluey, Singleton, B, All {

        @Glue
        C m1;

    }

    static class C15 implements Gluey, Singleton, C, All {

        @Glue
        D m1;

    }

    static class C16 implements Gluey, Singleton, D, All {

        @Glue(OPTIONAL)
        A m1;

    }

    static class C17 implements Gluey, Singleton, A {

        @Glue(ORDERED)
        A[] m1;

    }

    static class C18 implements Gluey, Singleton, A {

        @Glue
        A[] m1;

    }

    /*
     * Check that the sequence of references injected into arrays reflects the
     * dependencies between the referenced objects.
     */
    public void testSingleSequence() {

        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();
        C4 c4 = new C4();
        C5 c5 = new C5();

        dt.add(c1);
        dt.add(c4);
        dt.add(c3);
        dt.add(c2);
        dt.add(c5);
        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(4, c1.m1.length);
        assertEquals(c5, c1.m1[0]);
        assertEquals(c4, c1.m1[1]);
        assertEquals(c3, c1.m1[2]);
        assertEquals(c2, c1.m1[3]);
    }

    /**
     * Check that the call sequence still gets correct even when there are
     * multiple sequence groups present in the dependency graph.
     */
    public void testMultipleSequences() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();
        C4 c4 = new C4();
        C5 c5 = new C5();
        C6 c6 = new C6();
        C7 c7 = new C7();
        C8 c8 = new C8();
        C9 c9 = new C9();
        C10 c10 = new C10();

        dt.add(c6);
        dt.add(c9);
        dt.add(c1);
        dt.add(c7);
        dt.add(c4);
        dt.add(c8);
        dt.add(c3);
        dt.add(c10);
        dt.add(c2);
        dt.add(c5);
        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(8, c1.m1.length);
        assertTrue(c5 == c1.m1[0] || c8 == c1.m1[0]);
        assertTrue(c4 == c1.m1[1] || c7 == c1.m1[1]);
        assertTrue(c3 == c1.m1[2] || c6 == c1.m1[2]);
        assertTrue(c2 == c1.m1[3] || c9 == c1.m1[3]);
        assertTrue(c5 == c1.m1[4] || c8 == c1.m1[4]);
        assertTrue(c4 == c1.m1[5] || c7 == c1.m1[5]);
        assertTrue(c3 == c1.m1[6] || c6 == c1.m1[6]);
        assertTrue(c2 == c1.m1[7] || c9 == c1.m1[7]);
        assertNotNull(c10.m1);
        assertEquals(4, c10.m1.length);
        assertEquals(c8, c10.m1[0]);
        assertEquals(c7, c10.m1[1]);
        assertEquals(c6, c10.m1[2]);
        assertEquals(c9, c10.m1[3]);
    }

    /**
     * Check that circular dependencies are treated as a single dependency node.
     */
    public void testCircularDependencies() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C2 c2 = new C2();
        C3 c3 = new C3();
        C4 c4 = new C4();
        C11 c11 = new C11();
        C12 c12 = new C12();

        dt.add(c1);
        dt.add(c3);
        dt.add(c11);
        dt.add(c2);
        dt.add(c12);
        dt.add(c4);
        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(5, c1.m1.length);
        assertTrue(c4 == c1.m1[0] || c3 == c1.m1[0] || c11 == c1.m1[0]);
        assertTrue(c4 == c1.m1[1] || c3 == c1.m1[1] || c11 == c1.m1[1]);
        assertTrue(c4 == c1.m1[2] || c3 == c1.m1[2] || c11 == c1.m1[2]);
        assertTrue(c2 == c1.m1[3] || c12 == c1.m1[3]);
        assertTrue(c2 == c1.m1[4] || c12 == c1.m1[4]);
    }

    /**
     * Check that optional injections are not treated as dependencies in the
     * call sequence calculation.
     */
    public void testCircularOptionals() {
        DuctTape dt = new DuctTape();

        C1 c1 = new C1();
        C13 c13 = new C13();
        C14 c14 = new C14();
        C15 c15 = new C15();
        C16 c16 = new C16();

        dt.add(c1);
        dt.add(c16);
        dt.add(c14);
        dt.add(c15);
        dt.add(c13);
        dt.apply();

        assertNotNull(c1.m1);
        assertEquals(4, c1.m1.length);
        assertEquals(c16, c1.m1[0]);
        assertEquals(c15, c1.m1[1]);
        assertEquals(c14, c1.m1[2]);
        assertEquals(c13, c1.m1[3]);
    }

    /**
     * Check that when an object has circular dependencies towards several other
     * objects so that there is no determinable dependency at all the injected
     * array is still correct.
     */
    public void testSuperCircularSequence() {
        DuctTape dt = new DuctTape();

        C17 c17 = new C17();
        C18 c181 = new C18();
        C18 c182 = new C18();

        dt.add(c17);
        dt.add(c181);
        dt.add(c182);
        dt.apply();

        assertNotNull(c17.m1);
        assertEquals(3, c17.m1.length);
        assertTrue(c17.m1[0] == c17 ^ c17.m1[1] == c17 ^ c17.m1[2] == c17);
        assertTrue(c17.m1[0] == c181 ^ c17.m1[1] == c181 ^ c17.m1[2] == c181);
        assertTrue(c17.m1[0] == c182 ^ c17.m1[1] == c182 ^ c17.m1[2] == c182);
        assertNotNull(c181.m1);
        assertEquals(3, c181.m1.length);
        assertTrue(c181.m1[0] == c17 ^ c181.m1[1] == c17 ^ c181.m1[2] == c17);
        assertTrue(c181.m1[0] == c181 ^ c181.m1[1] == c181 ^ c181.m1[2] == c181);
        assertTrue(c181.m1[0] == c182 ^ c181.m1[1] == c182 ^ c181.m1[2] == c182);
        assertNotNull(c182.m1);
        assertEquals(3, c182.m1.length);
        assertTrue(c182.m1[0] == c17 ^ c182.m1[1] == c17 ^ c182.m1[2] == c17);
        assertTrue(c182.m1[0] == c181 ^ c182.m1[1] == c181 ^ c182.m1[2] == c181);
        assertTrue(c182.m1[0] == c182 ^ c182.m1[1] == c182 ^ c182.m1[2] == c182);
    }

}
