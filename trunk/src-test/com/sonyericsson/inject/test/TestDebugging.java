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
 * This class contains tests that make sure that the debug output is correct.
 */
public class TestDebugging extends TestCase {

    interface A {
    }

    interface B {
    }

    interface C {
    }

    interface D {
    }

    static class C1 implements Gluey, Singleton, A {

        @Glue(OPTIONAL)
        B m1;

        @Glue
        D m2;

        @Glue(OPTIONAL)
        char m3;

        @Glue
        A[] m4;

    }

    static class C2 implements Gluey, Singleton, B {

        @Glue(ORDERED)
        A[] m1;

        @Glue
        String m2;

        @Glue(1)
        int m3;

        @Glue
        double m4;

        @Glue({
                2, OPTIONAL
        })
        float m5;

    }

    static class C3 implements Gluey, Singleton, C {

        @Glue(1)
        float m1;

        @Glue({
                1, 4
        })
        int[] m2;

    }

    static class C4 implements Gluey, Singleton, D {

        @Glue(OPTIONAL)
        C m1;

        @Glue
        boolean m2;

        @Glue(22)
        int[] m3;

    }

    static class C5 implements Singleton, A {
    }

    static class C6 implements Singleton, A {
    }

    static class C7 implements Singleton {
    }

    static class C8 extends C1 implements Singleton {

        @Glue
        C m1;

    }

    static final void assertRowsInAnyOrder(String expected, String actual) {
        String[] actualRows = actual.split("\n");
        String[] expectedRows = expected.split("\n");

        for (int i = 0; i < actualRows.length; i++) {
            for (int j = 0; j < expectedRows.length; j++) {
                if (actualRows[i] != null && actualRows[i].equals(expectedRows[j])) {
                    expectedRows[j] = null;
                    actualRows[i] = null;
                    break;
                }
            }
        }
        for (int i = 0; i < actualRows.length; i++) {
            if (actualRows[i] != null) {
                fail("Did not expect row " + (i + 1) + ": \"" + actualRows[i] + "\"");
            }
        }
        for (int i = 0; i < expectedRows.length; i++) {
            if (expectedRows[i] != null) {
                fail("Missing the row \"" + expectedRows[i] + "\"");
            }
        }
    }

    /**
     * Make sure the debug print is generated correctly for a complex dependency
     * situation.
     */
    public void testToString() {
        DuctTape dt = new DuctTape();
        C1 c1 = new C1();
        dt.add(c1);
        dt.add(new C1());
        dt.add(new C2());
        dt.add(C3.class);
        dt.add(C4.class);
        dt.add(C5.class);
        dt.add(C6.class);
        dt.add(new C7());
        dt.add(new C8());
        dt.add("hello");
        dt.add(1, 4);
        dt.add(2, 1);
        dt.add(3, 4);
        dt.add(true);
        dt.add(12.34f, 1);
        dt.add(7.10f, 2);
        dt.add('x');
        dt.add(56.7);
        dt.add(new int[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        }, 22);
        dt.apply();
        String out = dt.toString();

        assertTrue(out.startsWith("DuctTape@" + Integer.toHexString(dt.hashCode())
                + " [\n\trobust = true\n\tpackage = "
                + "com.sonyericsson.inject.test.TestDebugging\n"));
        assertTrue(out.endsWith("\n]"));
        assertRowsInAnyOrder("DuctTape@" + Integer.toHexString(dt.hashCode()) + " [\n"
                + "\trobust = true\n" + "\tpackage = com.sonyericsson.inject.test.TestDebugging\n"
                + "\t[1, 2, 3, 4, 5, 6, 7, 8, 9, 10] (untouched)\n" + "\t56.7 (untouched)\n"
                + "\t'x' (untouched)\n" + "\t7.1 (untouched)\n" + "\t12.34 (untouched)\n"
                + "\ttrue (untouched)\n" + "\t3 (untouched)\n" + "\t2 (untouched)\n"
                + "\t1 (untouched)\n" + "\t\"hello\" (untouched)\n" + "\tC8 [\n" + "\t\tm1 = C3\n"
                + "\t\tm1 = C2\n" + "\t\tm2 = C4\n" + "\t\tm3 = 'x'\n"
                + "\t\tm4 = [C8, C1#1, C1#2, C6, C5]\n" + "\t]\n" + "\tC7 (untouched)\n"
                + "\tC2 [\n" + "\t\tm1 = [C6, C5, C1#2, C1#1, C8]\n" + "\t\tm2 = \"hello\"\n"
                + "\t\tm3 = 2\n" + "\t\tm4 = 56.7\n" + "\t\tm5 = 7.1\n" + "\t]\n" + "\tC1#1 [\n"
                + "\t\tm1 = C2\n" + "\t\tm2 = C4\n" + "\t\tm3 = 'x'\n"
                + "\t\tm4 = [C8, C1#1, C1#2, C6, C5]\n" + "\t]\n" + "\tC1#2 [\n" + "\t\tm1 = C2\n"
                + "\t\tm2 = C4\n" + "\t\tm3 = 'x'\n" + "\t\tm4 = [C8, C1#1, C1#2, C6, C5]\n"
                + "\t]\n" + "\tC3 (product) [\n" + "\t\tm1 = 12.34\n" + "\t\tm2 = [3, 2, 1]\n"
                + "\t]\n" + "\tC4 (product) [\n" + "\t\tm1 = C3\n" + "\t\tm2 = true\n"
                + "\t\tm3 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]\n" + "\t]\n"
                + "\tC6 (untouched product)\n" + "\tC5 (untouched product)\n"
                + "\t[3, 2, 1] (untouched product)\n" + "]\n"

        , out);
    }

    /**
     * Make sure the DOT output is generated correctly for a complex dependency
     * situation.
     */
    public void testToDot() {
        DuctTape dt = new DuctTape();
        C1 c1 = new C1();
        dt.add(c1);
        dt.add(new C1());
        dt.add(new C2());
        dt.add(C3.class);
        dt.add(C4.class);
        dt.add(C5.class);
        dt.add(C6.class);
        dt.add(new C7());
        dt.add(new C8());
        dt.add("hello");
        dt.add(1, 4);
        dt.add(2, 1);
        dt.add(3, 4);
        dt.add(true);
        dt.add(12.34f, 1);
        dt.add(7.10f, 2);
        dt.add('x');
        dt.add(56.7);
        dt.add(new int[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        }, 22);
        dt.apply();
        dt.add(Object.class);
        String out = dt.toDotString();

        assertTrue(out.startsWith("digraph {\n"));
        assertTrue(out.endsWith("\n}"));
        assertRowsInAnyOrder("digraph {\n"
                + "\tnode [shape=component, fontsize=15, fillcolor=\"#fff080\", "
                + "style=filled, fontname=\"sans\"];\n"
                + "\tedge [fontsize=10, fontname=\"sans italic\", color=\"#008000\", "
                + "arrowhead=vee, arrowsize=0.5];\n"
                + "\tgraph [fontsize=10, fontname=\"sans bold\", fontcolor=red, "
                + "label=\"Warning! The object mesh is not robust!\"];\n"
                + "\t\"C8\" -> \"C3\" [label=\"C\"];\n"
                + "\t\"C8\" -> \"C2\" [label=\"B\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C8\" -> \"C4\" [label=\"D\"];\n"
                + "\t\"C8\" -> \"'x'\" [label=\"m3\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C8\" -> \"C8\" [label=\"A\"];\n" + "\t\"C8\" -> \"C1#1\" [label=\"A\"];\n"
                + "\t\"C8\" -> \"C1#2\" [label=\"A\"];\n" + "\t\"C8\" -> \"C6\" [label=\"A\"];\n"
                + "\t\"C8\" -> \"C5\" [label=\"A\"];\n" + "\t\"C2\" -> \"C6\" [label=\"A (1)\"];\n"
                + "\t\"C2\" -> \"C5\" [label=\"A (2)\"];\n"
                + "\t\"C2\" -> \"C1#2\" [label=\"A (3)\"];\n"
                + "\t\"C2\" -> \"C1#1\" [label=\"A (4)\"];\n"
                + "\t\"C2\" -> \"C8\" [label=\"A (5)\"];\n"
                + "\t\"C2\" -> \"\\\"hello\\\"\" [label=\"m2\"];\n"
                + "\t\"C2\" -> \"2\" [label=\"m3\"];\n" + "\t\"C2\" -> \"56.7\" [label=\"m4\"];\n"
                + "\t\"C2\" -> \"7.1\" [label=\"m5\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C1#1\" -> \"C2\" [label=\"B\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C1#1\" -> \"C4\" [label=\"D\"];\n"
                + "\t\"C1#1\" -> \"'x'\" [label=\"m3\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C1#1\" -> \"C8\" [label=\"A\"];\n"
                + "\t\"C1#1\" -> \"C1#1\" [label=\"A\"];\n"
                + "\t\"C1#1\" -> \"C1#2\" [label=\"A\"];\n"
                + "\t\"C1#1\" -> \"C6\" [label=\"A\"];\n" + "\t\"C1#1\" -> \"C5\" [label=\"A\"];\n"
                + "\t\"C1#2\" -> \"C2\" [label=\"B\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C1#2\" -> \"C4\" [label=\"D\"];\n"
                + "\t\"C1#2\" -> \"'x'\" [label=\"m3\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C1#2\" -> \"C8\" [label=\"A\"];\n"
                + "\t\"C1#2\" -> \"C1#1\" [label=\"A\"];\n"
                + "\t\"C1#2\" -> \"C1#2\" [label=\"A\"];\n"
                + "\t\"C1#2\" -> \"C6\" [label=\"A\"];\n" + "\t\"C1#2\" -> \"C5\" [label=\"A\"];\n"
                + "\t\"C3\" [fillcolor=\"#e6f0c2\"];\n" + "\t\"C3\" -> \"12.34\" [label=\"m1\"];\n"
                + "\t\"C3\" -> \"3\" [label=\"m2\"];\n" + "\t\"C3\" -> \"2\" [label=\"m2\"];\n"
                + "\t\"C3\" -> \"1\" [label=\"m2\"];\n" + "\t\"C4\" [fillcolor=\"#e6f0c2\"];\n"
                + "\t\"C4\" -> \"C3\" [label=\"C\", style=dashed, color=\"#000080\"];\n"
                + "\t\"C4\" -> \"true\" [label=\"m2\"];\n"
                + "\t\"C4\" -> \"[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]\" [label=\"m3\"];\n"
                + "\t\"[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]\" " + "[fillcolor=white, shape=ellipse];\n"
                + "\t\"56.7\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"'x'\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"7.1\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"12.34\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"true\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"3\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"2\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"1\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"\\\"hello\\\"\" [fillcolor=white, shape=ellipse];\n"
                + "\t\"C7\" [fillcolor=\"#c2e6f0\"];\n" + "\t\"C6\" [fillcolor=\"#c2e6f0\"];\n"
                + "\t\"C5\" [fillcolor=\"#c2e6f0\"];\n" + "}\n", out);
    }

    /**
     * Make sure that the short debug representation contains valid information
     * and is formatted as expected.
     */
    public void testToShortString() {
        DuctTape dt = new DuctTape();
        C1 c1 = new C1();
        dt.add(c1);
        dt.add(new C1());
        dt.add(new C2());
        dt.add(C3.class);
        dt.add(C4.class);
        dt.add(C5.class);
        dt.add(C6.class);
        dt.add(new C7());
        dt.add(new C8());
        dt.add("hello");
        dt.add(1, 4);
        dt.add(2, 1);
        dt.add(3, 4);
        dt.add(true);
        dt.add(12.34f, 1);
        dt.add(7.10f, 2);
        dt.add('x');
        dt.add(56.7);
        dt.add(new int[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        }, 22);
        dt.apply();

        assertEquals("DuctTape[robust=true, objects=15 + 4 products, factories=4]",
                dt.toShortString());

        dt.remove(c1);
        dt.remove(C3.class);

        assertEquals("DuctTape[robust=false, objects=14 + 3 products, factories=3]",
                dt.toShortString());
    }

}
