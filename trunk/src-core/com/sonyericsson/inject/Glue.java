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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for injection. This means that
 * {@link com.sonyericsson.inject.DuctTape#apply()} will try to find one or more
 * (depending on whether the field is an array or not) type compatible object
 * references and automatically set the field to that value.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Glue {

    /**
     * Specifies a filter which excludes all objects that do not have <i>at
     * least one</i> of the given tags. However, if no tags are specified all
     * objects passes the filter.
     * <p>
     * Example:
     * 
     * <pre>
     * &#064;Glue
     * EventListener[] mListeners;
     * </pre>
     * 
     * will inject all objects of type <code>EventListener</code>, while
     * 
     * <pre>
     * &#064;Glue({IMPORTANT, SOMEWHAT_IMPORTANT})
     * EventListener[] mListeners;
     * </pre>
     * 
     * injects only those objects tagged either <code>IMPORTANT</code> or
     * <code>SOMEWHAT_IMPORTANT</code> (which in this example are integer
     * constants assumed to be defined somewhere else in the code).
     * <p>
     * Note that Java allows omitting the curly braces when there is only one
     * value in the array, so injecting all objects with one specific tag looks
     * something like this:
     * <p>
     * 
     * <pre>
     * &#064;Glue(OPTIONAL)
     * NetworkClient mClient;
     * </pre>
     * 
     * @return Tags on the objects to be injected.
     */
    int[] value() default {};

}
