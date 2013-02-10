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

package com.sonyericsson.inject;

/**
 * Interface that must be implemented by all classes in which injections should
 * be made. In other words this means that all
 * {@link com.sonyericsson.inject.Glue} annotations will be completely ignored
 * unless your class implements this interface.
 * <p>
 * <h3>Correct</h3>
 * 
 * <pre>
 * class MyClass implements Gluey {
 * 
 *     &#064;Inject
 *     int mYeyIWillGetAnInjection;
 * 
 * }
 * </pre>
 * <p>
 * <h3>Incorrect</h3>
 * 
 * <pre>
 * class MyClass {
 * 
 *     &#064;Inject
 *     int mBummerIWillBeIgnored;
 * 
 * }
 * </pre>
 */
public interface Gluey {

    /* Intentionally empty */

}
