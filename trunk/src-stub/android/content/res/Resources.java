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

package android.content.res;

import com.sonyericsson.inject.R;

import android.graphics.drawable.Drawable;

public class Resources {

    @SuppressWarnings("serial")
    public static class NotFoundException extends RuntimeException {

        /* Stub */

    }

    public Object getColor(int id) throws NotFoundException {
        switch (id) {
            case R.color.c1:
                return 0xcafebabe;
            default:
                throw new NotFoundException();
        }
    }

    public Object getBoolean(int id) throws NotFoundException {
        switch (id) {
            case R.bool.b1:
                return true;
            default:
                throw new NotFoundException();
        }
    }

    public Object getDimension(int id) throws NotFoundException {
        switch (id) {
            case R.dimen.d1:
                return 14f;
            default:
                throw new NotFoundException();
        }
    }

    public Object getString(int id) throws NotFoundException {
        switch (id) {
            case R.string.s1:
                return "blurp";
            default:
                throw new NotFoundException();
        }
    }

    public Object getDrawable(int id) throws NotFoundException {
        switch (id) {
            case R.drawable.icon:
                return new Drawable();
            default:
                throw new NotFoundException();
        }
    }

    public Object getColorStateList(int id) throws NotFoundException {
        switch (id) {
            case R.color.ducttape_colors:
                return new ColorStateList();
            default:
                throw new NotFoundException();
        }
    }

    public Object getStringArray(int id) throws NotFoundException {
        switch (id) {
            case R.array.sa1:
                return new String[] {
                        "hello", "funny", "man"
                };
            default:
                throw new NotFoundException();
        }
    }

    public Object getIntArray(int id) throws NotFoundException {
        switch (id) {
            case R.array.ia1:
                return new int[] {
                        78, 10
                };
            default:
                throw new NotFoundException();
        }
    }

    public XmlResourceParser getXml(int id) throws NotFoundException {
        switch (id) {
            case R.xml.ducttape_xml:
                return new XmlResourceParser();
            default:
                throw new NotFoundException();
        }
    }

    public int getIdentifier(String pkg, String type, String name) {
        return 0;
    }

}
