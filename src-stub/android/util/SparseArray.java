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

package android.util;

import java.util.HashMap;

public class SparseArray<E> {

    HashMap<Integer, E> m = new HashMap<Integer, E>();

    public SparseArray() {
    }

    public SparseArray(int initialCapacity) {
    }

    void append(int key, E value) {
        put(key, value);
    }

    void clear() {
        m.clear();
    }

    void delete(int key) {
        remove(key);
    }

    E get(int key) {
        return m.get(key);
    }

    E get(int key, E valueIfKeyNotFound) {
        E v = get(key);
        if (v == null) {
            return valueIfKeyNotFound;
        }
        return v;
    }

    int indexOfKey(int key) {
        return 0;
    }

    int indexOfValue(E value) {
        return 0;
    }

    int keyAt(int index) {
        return 0;
    }

    void put(int key, E value) {
        m.put(key, value);
    }

    void remove(int key) {
        m.remove(key);
    }

    void removeAt(int index) {
        m.remove(index);
    }

    void setValueAt(int index, E value) {
        m.put(index, value);
    }

    int size() {
        return m.size();
    }

    E valueAt(int index) {
        return get(index);
    }

}
