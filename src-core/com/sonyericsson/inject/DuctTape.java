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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.Service;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.DropBoxManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

/**
 * <p>
 * A framework for dependency injection and automatic instantiation of required
 * implementations.
 * </p>
 * <p>
 * Topics covered here:
 * <ol>
 * <li><a href="#Introduction">Introduction</a></li>
 * <li><a href="#InjectedFields">Injected Fields</a></li>
 * <li><a href="#ArrayInjection">Array Injection</a></li>
 * <li><a href="#FactoryObjects">Factory Objects</a></li>
 * <li><a href="#ContextAwareness">Context Awareness</a></li>
 * <li><a href="#ViewSupport">View Support</a></li>
 * <li><a href="#DebugOutput">Debug Output</a></li>
 * </ol>
 * </p>
 * <a name="Introduction"></a> <h3>Introduction</h3>
 * <p>
 * Automatic dependency injection in Java is the concept of letting objects know
 * the presence of each other through a registration framework, and never hard
 * code which object references another anywhere. The benefits in an object
 * oriented environment like Java is complete flexibility of the system
 * configuration and the promotion of modularity. Simply put, using dependency
 * injection each logical functionality in your system <i>should</i> be an
 * interface, and the implementation is then smaller or larger modules that
 * implements one or more interfaces. The injection framework ties it all
 * together and makes the implementation completely independent on how you have
 * implemented the interfaces.
 * </p>
 * <p>
 * The aim with this class is to provide a very easy and efficient way to use
 * dependency injection, and the name tries to reflect that. In essence you just
 * take all your objects and wrap them in duct tape:
 * </p>
 * 
 * <pre>
 * DuctTape dt = new DuctTape();
 * 
 * dt.add(anObject);
 * dt.add(anotherObject);
 * dt.add(yetAnotherObject);
 * 
 * // Inject references to requested types
 * dt.apply();
 * </pre>
 * <p>
 * In case your setup changes, e.g. a new window was created and you want to
 * stick it to the existing object mesh, you simply add the object and apply
 * some more tape:
 * </p>
 * 
 * <pre>
 * dt.add(oneMoreObject);
 * dt.apply();
 * </pre>
 * <p>
 * The same procedure can be applied regardless weather objects were removed,
 * added or both.
 * </p>
 * <a name="InjectedFields"></a> <h3>Injected Fields</h3>
 * <p>
 * To make values become injected into an object the class must fist be marked
 * as and injection target by implementing the
 * {@link com.sonyericsson.inject.Gluey} interface. Once that is in place all
 * fields of the class can be marked as injection targets by adding the
 * {@link com.sonyericsson.inject.Glue} annotation.
 * </p>
 * 
 * <pre>
 * class MyClass implements Gluey {
 * 
 *     &#064;Glue
 *     private SuperService mTheService;
 * 
 *     &#064;Glue
 *     private CheeseManager mManager;
 * 
 * }
 * </pre>
 * <p>
 * By default an object requires something to be available to inject into all of
 * its fields marked with {@link com.sonyericsson.inject.Glue}. However, by
 * adding the {@link #OPTIONAL} tag to the {@link com.sonyericsson.inject.Glue}
 * annotation the framework is told that it is OK not to inject anything into
 * that field.
 * </p>
 * 
 * <pre>
 * import static com.sonyericsson.inject.DuctTape.OPTIONAL;
 * ...
 * 
 * &#064;Glue(OPTIONAL)
 * private SomeClass mObject;
 * </pre>
 * <p>
 * This is an example of using a built-in tag on with the
 * {@link com.sonyericsson.inject.Glue} annotation, but you can also use custom
 * tags. These tags are used to select only those objects which have previously
 * been tagged with at least on of the given tags, e.g.
 * </p>
 * 
 * <pre>
 * // Define tag constant instead of using raw integers
 * public static final MY_TAG = DuctTape.MIN_TAG;
 * ...
 * 
 * // Require the injected object to be tagged
 * &#064;Glue(MY_TAG)
 * private SomeClass mObject;
 * ...
 * 
 * // Add and tag the object
 * dt = new DuctTape();
 * dt.add(myObject, MY_TAG);
 * </pre>
 * <p>
 * It is also possible to supply several tags with the
 * {@link com.sonyericsson.inject.Glue} annotation. These can be a mix of
 * built-in tags and custom tags.
 * </p>
 * 
 * <pre>
 * &#064;Glue({
 *         OPTIONAL, MY_FIRST_TAG, MY_SECOND_TAG
 * })
 * private SomeClass mObject;
 * </pre>
 * <p>
 * In this case the injected object is required to be tagged with <i>either</i>
 * <code>MY_FIRST_TAG</code> or <code>MY_SECOND_TAG</code>, and the field is
 * allowed to be injected with <code>null</code> in case there is no matching
 * object.
 * </p>
 * <a name="#ArraysInjection"></a> <h3>Array Injection</h3>
 * <p>
 * Normally an injected field represents a single reference or value. When
 * several objects that could be injected has been found this would cause an
 * {@link com.sonyericsson.inject.GluingException}. However, by declaring the
 * field as an array it will instead receive all of the matching objects.
 * </p>
 * 
 * <pre>
 * // This causes an exception when there are several
 * // object of SomeClass type.
 * private SomeClass mObject;
 * 
 * // This works well with both one and many objects of
 * // the SomeClass type.
 * private SomeClass[] mObjects;
 * </pre>
 * <p>
 * It is also possible to inject into primitive arrays this way. Object of the
 * corresponding type will then be collected and converted into a primitive
 * array before injection.
 * </p>
 * 
 * <pre>
 * private int[] mValues;
 * ...
 * 
 * dt.add(new Integer(123));
 * dt.add(new Integer(45));
 * dt.add(67);
 * 
 * // This will inject int[]{123, 45, 67} into mValues
 * dt.apply();
 * 
 * </pre>
 * <p>
 * However, since arrays are also objects this can sometimes lead to ambiguous
 * situations where it is not possible to know if the intention was to inject
 * one of the added arrays or to inject an array of matching objects. In these
 * cases {@link #apply()} will throw an
 * {@link com.sonyericsson.inject.GluingException} informing about the
 * situation.
 * </p>
 * 
 * <pre>
 * private String[] mMessages;
 * ...
 * 
 * String[] msgs = new String[] {
 *         "first", "second"
 * };
 * ...
 * 
 * dt.add((Object)msgs);
 * dt.add("third");
 * dt.add("fourth");
 * 
 * dt.apply(); // will throw an exception
 * </pre>
 * <p>
 * Normally this case is easily avoided by tagging the array and array
 * injections, since adding and injecting untagged arrays are usually a sign of
 * bad design anyhow.
 * </p>
 * <a name="#FactoryObjects"></a> <h3>Factory Objects</h3>
 * <p>
 * When adding a {@link java.lang.Class} object it will be treated in a special
 * manner. It will not be directly available for injection, instead it is used
 * to instantiate an object when there is a field compatible with its type,
 * which is useful when resources should only be allocated when the object is in
 * use. The creation will however only be done when the following criteria hold:
 * </p>
 * <p>
 * <ul>
 * <li>The injection is <i>not</i> tagged with {@link #OPTIONAL}.</li>
 * <li>All injections in the object that is about to be constructed can be
 * performed successfully.</li>
 * </ul>
 * </p>
 * <p>
 * The latter bullet might require other {@link java.lang.Class} objects to be
 * investigated. Circular references in these cases are allowed and resolved by
 * analyzing the big picture.
 * </p>
 * <p>
 * It is possible to tag {@link java.lang.Class} objects just as ordinary added
 * objects. When that is done the constructed object will have the same tag-set
 * as its class, and only be injected where matched by the injection criteria.
 * </p>
 * <p>
 * Normally a factory creates a new instance of the object for each injection.
 * This behavior can however be changed by making the class implementing the
 * {@link com.sonyericsson.inject.Singleton} interface. That tells the framework
 * that the same object instance should be used for all injections.
 * </p>
 * <a name="ContextAwareness"></a> <h3>Context Awareness</h3>
 * <p>
 * This class was designed with Android as primary target and has support for
 * automatically extracting resources and services from an application context.
 * This means that if you add an {@link android.content.Context} all fields that
 * have any of the types {@link android.accounts.AccountManager},
 * {@link android.app.ActivityManager}, {@link android.app.AlarmManager},
 * {@link android.app.KeyguardManager}, {@link android.app.NotificationManager},
 * {@link android.app.SearchManager}, {@link android.app.UiModeManager},
 * {@link android.app.WallpaperManager},
 * {@link android.app.admin.DevicePolicyManager},
 * {@link android.content.ContentResolver},
 * {@link android.content.pm.ApplicationInfo},
 * {@link android.content.pm.PackageManager},
 * {@link android.content.res.AssetManager},
 * {@link android.content.res.Resources},
 * {@link android.content.res.XmlResourceParser},
 * {@link android.hardware.SensorManager},
 * {@link android.location.LocationManager}, {@link android.media.AudioManager},
 * {@link android.net.ConnectivityManager}, {@link android.net.wifi.WifiManager}, {@link android.os.DropBoxManager}, {@link android.os.Looper},
 * {@link android.os.PowerManager}, {@link android.os.Vibrator},
 * {@link android.telephony.TelephonyManager},
 * {@link android.text.ClipboardManager}, {@link android.view.LayoutInflater},
 * {@link android.view.WindowManager} or
 * {@link android.view.accessibility.AccessibilityManager} will be injected with
 * whatever the context gives us.
 * </p>
 * <p>
 * The resources can be injected by simply stating the resource ID as tag on the
 * injection:
 * </p>
 * 
 * <pre>
 * &#064;Glue(R.integer.timeout)
 * int mTimeout;
 * </pre>
 * <p>
 * This also works on array resources.
 * </p>
 * <a name="ViewSupport"></a> <h3>View Support</h3>
 * <p>
 * In addition to built in support for application contexts there is also
 * support for extracting and inflating views. This is done by specifying the
 * view or layout ID as injection tag and giving a sub-class of
 * {@link android.view.View} as field type. The following steps will then be
 * taken in the attempt to find a suitable object to inject:
 * </p>
 * <p>
 * <ol>
 * <li>All added views are scanned for the given ID.</li>
 * <li>If an {@link android.app.Activity} were added its content view is scanned
 * for the given ID.</li>
 * <li>If an {@link android.content.Context} of any kind were added it is used
 * to try to inflate the layout with the given ID.</li>
 * </ol>
 * </p>
 * <p>
 * Note that unless the resulting view is of a type that can be cast to the
 * field where it is injected the search for an injectable object is considered
 * to have failed.
 * </p>
 * <a name="DebugOutput"></a> <h3>Debug Output</h3>
 * <p>
 * Apart from the standard {@link #toString()} and {@link #toShortString()} that
 * supplies the developer with textual debug information, there is also the
 * {@link #toDotString()} method which returns a graph description of the
 * current object mesh. The graph is returned in the <a
 * href="http://en.wikipedia.org/wiki/DOT_language">DOT language</a> and is
 * primarily intended to be run through a graph generator such as the <a
 * href="http://www.graphviz.org/Download..php">Graphviz Gvedit</a>. A typical
 * result might look like this:
 * </p>
 * <p>
 * <img src="DuctTape-1.png"
 * alt="Sample graph depicting an object mesh created by DuctTape." border="0"
 * />
 * </p>
 */
public class DuctTape {

    /**
     * The number of entries with which to grow an array when it runs out of
     * space.
     */
    private static final int ARRAY_INCREMENT_SIZE = 16;

    /**
     * Optimization so we only have to allocate this array once.
     */
    /*
     * NOTE: This field cannot be static since that would cause trouble when
     * several instances of this class is running in different threads.
     */
    private final int[] oneTag = new int[1];

    /**
     * The context which the object mesh lives in, or null when there is no
     * association to any Android context.
     */
    private Context mContext;

    /**
     * The current reference status between injected objects. Only true when
     * nothing has been modified after a call to apply().
     */
    private boolean mRobust;

    /**
     * Indicates whether this instance has experienced more than one call to
     * apply(). This can typically be used to skip some steps during the first
     * injection.
     */
    private boolean mProcessed;

    /**
     * The first node in the list of all nodes that should be involved in the
     * injection process.
     */
    private Node mFirstNode;

    /**
     * The last node in the list of all nodes that should be involved in the
     * injection process.
     */
    private Node mLastNode;

    /**
     * The number of nodes in the list of all nodes that should be involved in
     * the injection process.
     */
    private int mNumNodes;

    /**
     * First node in the list of factories, i.e. classes that can be used to
     * produce objects to satisfy object injection requirements.
     */
    private Factory mFactories;

    /**
     * Number of factories in the list.
     */
    private int mNumFactories;

    /**
     * Produced objects which should be removed next time apply() is called, but
     * might get their factory back and must therefore be remembered for now.
     */
    private Node mPendingRemoval;

    /**
     * The minimum value of a tag set on an object. An object may be tagged with
     * any integer equal to or between this value and {@link #MAX_TAG}.
     * <p>
     * It is recommended to use this constant as base for an enumeration of
     * constants representing tags in an application.
     * 
     * @see #MAX_TAG
     */
    public static final int MIN_TAG = 0x00000000;

    /**
     * The maximum value of a tag set on an object. An object may be tagged with
     * any integer equal to or between {@link #MIN_TAG} and this value.
     * 
     * @see #MIN_TAG
     */
    public static final int MAX_TAG = 0x00fffffd;

    /**
     * Tag that allows a field to be injected with <code>null</code> when there
     * is nothing to inject. It can only be used together with
     * {@link com.sonyericsson.inject.Glue}. Note that when this tag is present
     * on an injection there will be no attempts made to use added classes to
     * instantiate objects for this field.
     */
    /*
     * NOTE: The values of the constants OPTIONAL and ORDERED have been chosen
     * to not collide with any Android resource IDs or the MIN_TAG to MAX_TAG
     * range.
     */
    public static final int OPTIONAL = 0x00ffffff;

    /**
     * Tag that makes injected references become sorted by dependency. It can
     * only be used together with {@link com.sonyericsson.inject.Glue} and makes
     * sure that objects that other objects depend on are always present before
     * the objects that are dependent on them. Note that it does only affect
     * fields of with an array type.
     */
    public static final int ORDERED = 0x00fffffe;

    private static final class TaggedClass {

        public int[] mTags;

        public Class<?> mClazz;

        public TaggedClass(int[] tags, Class<?> clazz) {
            mTags = tags;
            mClazz = clazz;
        }

        @Override
        public final boolean equals(Object obj) {
            /*
             * Since we hash on the class all we need here is to find the
             * matching tag set.
             */
            TaggedClass tc = (TaggedClass)obj;
            final int[] currTags = mTags;
            final int[] tags = tc.mTags;
            final int currTagLen = currTags.length;
            final int tagLen = tags.length;
            if (currTagLen != tagLen) {
                return false;
            }
            for (int i = 0; i < tagLen; i++) {
                if (tags[i] != currTags[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public final int hashCode() {
            return mClazz.hashCode();
        }

    }

    private static final class Implementors {

        public Object[] mObjects;

        public Node[] mNodes;

        public int mNumNodes;

        public boolean mDirectArray;

        public boolean mGeneric;

        public Implementors(Object[] fieldData, Node[] children, int numChildren,
                boolean hasArrays, boolean generic) {
            mObjects = fieldData;
            mNodes = children;
            mNumNodes = numChildren;
            mGeneric = generic;
            mDirectArray = hasArrays;
        }

    }

    private static class Tagged {

        public int[] mTags;

        public int mTagsLen;

        public final void addTags(int[] tags, int len) {

            /* Make room */
            int[] currTags = mTags;
            int currTagsLen = mTagsLen;
            if (currTags == null) {
                currTags = new int[len];
                mTags = currTags;
            } else {
                final int totalLen = currTagsLen + len;
                if (totalLen >= currTags.length) {
                    int[] temp = new int[totalLen];
                    System.arraycopy(currTags, 0, temp, 0, currTagsLen);
                    mTags = temp;
                    currTags = temp;
                }
            }

            /* Add all non-duplicates */
            /*
             * NOTE: Possible duplicates in the array argument are ignored since
             * they get incrementally added to the array scanned for duplicates.
             */
            pick: for (int i = 0; i < len; i++) {

                /* Validate tag */
                final int tag = tags[i];
                if (tag < MIN_TAG || tag > MAX_TAG) {
                    throw new IllegalArgumentException("Tag value must be in range " + MIN_TAG
                            + " to " + MAX_TAG);
                }

                /* Skip duplicates */
                for (int j = 0; j < currTagsLen; j++) {
                    if (currTags[j] == tags[i]) {
                        continue pick;
                    }
                }
                currTags[currTagsLen++] = tag;
            }
            mTagsLen = currTagsLen;
        }

        public final boolean hasTag(int tags[]) {
            final int tagsLen = tags.length;
            final int currTagsLen = mTagsLen;
            final int[] currTags = mTags;
            for (int i = 0; i < tagsLen; i++) {
                final int tag = tags[i];
                for (int j = 0; j < currTagsLen; j++) {
                    final int currTag = currTags[j];
                    if (tag != OPTIONAL && tag != OPTIONAL && currTag == tag) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    private static final class Factory extends Tagged {

        /**
         * The factory has not yet been processed.
         */
        public static final int NOT_PROCESSED = 0;

        /**
         * The factory produces an object that will not be able to resolve all
         * mandatory references.
         */
        public static final int UNRESOLVABLE = 1;

        /**
         * The factory produces an object for which all mandatory references can
         * be resolved. Optional references are still not guaranteed to be
         * resolved.
         */
        public static final int RESOLVABLE = 2;

        /**
         * The factory is currently being validated.
         */
        public static final int PROCESSING = 3;

        /**
         * The factory has produced is object.
         */
        public static final int EXECUTED = 4;

        /**
         * The validation status of the factory.
         */
        public int mStatus;

        /**
         * The class which this factory use to produce objects.
         */
        public Class<?> mClazz;

        /**
         * Factories that depends on this factory, in case of a circular
         * dependency scenario when resolving factories.
         */
        public Factory[] mDependants;

        /**
         * The object produced by this factory, or null when nothing has been
         * produced yet (or only non-singleton products have been produced).
         */
        public Node mProduct;

        /**
         * Indicates whether the factory produces a singleton or not.
         */
        public boolean mNonSingleton;

        /**
         * Next factory in the list of factories.
         */
        public Factory mNext;

        /**
         * Previous factory in the list of factories.
         */
        public Factory mPrev;

        /**
         * The context from where this factory originated.
         */
        public Context mContext;

        /**
         * Information to be able to continue resolving the factory, or null in
         * case this factory in not currently being processed.
         */
        public ResolveContinuation mCont;

        public Factory(Class<?> clazz) {
            mClazz = clazz;
            mStatus = NOT_PROCESSED;
            mNonSingleton = !Singleton.class.isAssignableFrom(clazz);
        }

    }

    private static final class Node extends Tagged {

        /**
         * The node is part of an injected array that must be sorted by
         * dependency.
         */
        public static final int WILL_BE_SORTED = 0x00000001;

        /**
         * The node has has been merged into another node in the dependency
         * chain.
         */
        public static final int MERGED = 0x00000002;

        /**
         * The node is the head of a list of merged nodes.
         */
        public static final int MERGED_HEAD = 0x00000004;

        /**
         * The node has already been visited in the dependency calculation.
         */
        public static final int VISITED = 0x00000008;

        /**
         * The node should not be reused. Create a new instance instead.
         */
        public static final int DO_NOT_REUSE = 0x00000010;

        public int mFlags;

        public Node[] mChildren;

        public int mChildrenLen;

        public int mLastVisit;

        public Object mObject;

        /*
         * NOTE: Preparation for when objects can originate from different
         * contexts, and will need their resources to be fetched from the right
         * place.
         */
        public Context mContext;

        public Node mNextMerged;

        public Node mNext;

        public Node mPrev;

        public Node(Object obj) {
            mObject = obj;
        }

        public final void addChild(Node child) {

            /* Extend the array of children with a new chunk when necessary. */
            Node[] thisChildren = mChildren;
            int childrenLen = mChildrenLen;
            if (thisChildren == null || childrenLen >= thisChildren.length) {
                Node[] temp = new Node[childrenLen + ARRAY_INCREMENT_SIZE];
                if (thisChildren != null) {
                    System.arraycopy(thisChildren, 0, temp, 0, childrenLen);
                }
                mChildren = temp;
                thisChildren = temp;
            }
            thisChildren[childrenLen] = child;
            mChildrenLen++;
        }

        public final void addChildren(Node[] children, int length) {
            /*
             * Quick bail out when addition would make no difference This
             * optimization is here since this method is a performance
             * bottleneck in very large graphs.
             */
            Node[] thisChildren = mChildren;
            int childrenLen = mChildrenLen;
            boolean match = true;
            if (mChildren != null && length <= childrenLen) {
                final int stopIdx = childrenLen - length;
                int srcIdx = length - 1;
                for (int i = childrenLen - 1; i >= stopIdx; i--) {
                    if (children[srcIdx--] != thisChildren[i]) {
                        match = false;
                        break;
                    }
                }
            } else {
                match = false;
            }
            if (!match) {
                /* Extend the array of children with a new chunk when necessary */
                if (mChildren == null || childrenLen + length > thisChildren.length) {
                    Node[] temp = new Node[childrenLen + length + ARRAY_INCREMENT_SIZE];
                    if (mChildren != null) {
                        System.arraycopy(thisChildren, 0, temp, 0, childrenLen);
                    }
                    mChildren = temp;
                    thisChildren = temp;
                }
                System.arraycopy(children, 0, thisChildren, 0, length);
                mChildrenLen = length;
            }
        }

    }

    private static final class ResolveContinuation {

        public Class<?> mCurrent;

        public Field[] mFields;

        public int mFieldIdx;

        public Class<?> mClazz;

        public int[] mTags;

        public boolean mVital;

        public int mNumTags;

        public Class<?> mArray;

        public Factory mCandidate;

        public boolean mFound;

    }

    private static boolean isResolvable(Factory f, final Context ctxt, final View[] views,
            final int viewsLen, final Node nodes, final int numNodes, final Factory factories,
            final int numFactories, Factory[] path, HashMap<Integer, Object> idCache) {

        int pathIdx = 0;
        boolean valid = false;

        /* Provide fast access */
        Resources res = null;
        if (ctxt != null) {
            res = ctxt.getResources();
        }
        resolve: while (true) {
            switch (f.mStatus) {
                case Factory.EXECUTED:
                    /* Fall through */
                case Factory.RESOLVABLE:
                    return true;
                case Factory.UNRESOLVABLE:
                    return false;
                default:
                    /*
                     * Set factory into pending state during validation This is
                     * done to be able to resolve circular dependencies when
                     * calculating the validity.
                     */
                    f.mStatus = Factory.PROCESSING;

                    /* See if there is already something in progress */
                    ResolveContinuation cont = f.mCont;

                    /* Keep track of where we have been */
                    path[pathIdx] = f;

                    /* Scan all injection points */
                    Class<?> current;
                    if (cont == null) {
                        current = f.mClazz;
                    } else {
                        current = cont.mCurrent;
                    }
                    validation: while (Gluey.class.isAssignableFrom(current)) {
                        Field[] fields;
                        int fieldIdx;
                        if (cont == null) {
                            fields = current.getDeclaredFields();
                            fieldIdx = 0;
                        } else {
                            fields = cont.mFields;
                            fieldIdx = cont.mFieldIdx;
                        }
                        final int fieldsLen = fields.length;
                        fieldscan: while (fieldIdx < fieldsLen) {
                            /*
                             * NOTE: Only mandatory injections needs to be
                             * considered since the object can exists even
                             * though the optional ones cannot be carried out.
                             */
                            Field field = fields[fieldIdx];
                            final Glue injection;
                            if (cont == null) {
                                injection = getInjection(field);
                            } else {
                                injection = null;
                            }
                            if (injection != null || cont != null) {
                                Class<?> clazz = null;
                                final int[] tags;
                                boolean vital;
                                boolean sort;
                                int numTags;
                                int id;
                                Class<?> array = null;
                                boolean resolvable = false;

                                if (cont == null) {
                                    tags = injection.value();
                                    final int tagsLen = tags.length;
                                    vital = true;
                                    sort = false;
                                    numTags = tagsLen;
                                    id = 0;
                                    tagScan: for (int k = 0; k < tagsLen; k++) {
                                        final int tag = tags[k];
                                        switch (tag) {
                                            case OPTIONAL:
                                                vital = false;
                                                numTags--;
                                                if (sort) {
                                                    break tagScan;
                                                }
                                                break;
                                            case ORDERED:
                                                sort = true;
                                                numTags--;
                                                if (!vital) {
                                                    break tagScan;
                                                }
                                                break;
                                            default:
                                                id = tag;
                                                break;
                                        }
                                    }

                                    /*
                                     * NOTE: We need to know how many references
                                     * we can feed it, since if there are too
                                     * many this object should not be created.
                                     */
                                    clazz = field.getType();
                                    if (clazz.isArray()) {
                                        array = clazz;
                                        clazz = clazz.getComponentType();
                                    }
                                    clazz = wrapPrimitive(clazz);

                                    /*
                                     * See if there is any existing object, with
                                     * an accepted tag, that implements the type
                                     * of this field.
                                     */
                                    boolean found = false;
                                    boolean directArray = false;
                                    Node node = nodes;
                                    while (node != null) {
                                        Object candidate = node.mObject;
                                        if (clazz.isInstance(candidate)) {

                                            /*
                                             * Make sure we have the right
                                             * amount.
                                             */
                                            if (numTags == 0 || node.hasTag(tags)) {

                                                /* Ambiguous case */
                                                if (directArray) {
                                                    f.mStatus = Factory.UNRESOLVABLE;
                                                    break validation;
                                                }

                                                if (found) {
                                                    if (array == null) {
                                                        /*
                                                         * NOTE: Too many
                                                         * possible injections
                                                         * means that the
                                                         * instance that this
                                                         * factory produces
                                                         * would cause a taping
                                                         * exception if used,
                                                         * i.e. we can conclude
                                                         * that this factory is
                                                         * invalid already here.
                                                         */
                                                        f.mStatus = Factory.UNRESOLVABLE;
                                                        break validation;
                                                    } else {
                                                        break;
                                                    }
                                                } else {
                                                    found = true;
                                                    resolvable = true;

                                                    /*
                                                     * Break early when we know
                                                     * that we can handle more
                                                     * than one instance, and
                                                     * already found one.
                                                     */
                                                    /*
                                                     * NOTE: This optimization
                                                     * is not safe for for
                                                     * non-singletons since they
                                                     * might create other
                                                     * non-singletons that in
                                                     * the end want to create an
                                                     * instance of this type
                                                     * again, thus causing an
                                                     * infinite instantiation
                                                     * loop. Because of this the
                                                     * optimization is turned
                                                     * off for non-singletons.
                                                     */
                                                    if (array != null && !f.mNonSingleton) {
                                                        fieldIdx++;
                                                        continue fieldscan;
                                                    }
                                                }
                                            }
                                        } else if (!directArray && array != null
                                                && array.isInstance(candidate)
                                                && (numTags == 0 || node.hasTag(tags))) {
                                            found = true;
                                            resolvable = true;
                                            directArray = true;
                                        }
                                        node = node.mNext;
                                    }

                                    /* See if it is a service that is available */
                                    if (ctxt != null
                                            && numTags == 0
                                            && (clazz == ApplicationInfo.class
                                                    || clazz == AssetManager.class
                                                    || clazz == ClassLoader.class
                                                    || clazz == ContentResolver.class
                                                    || clazz == Looper.class
                                                    || clazz == PackageManager.class
                                                    || clazz == Resources.class
                                                    || clazz == AccessibilityManager.class
                                                    || clazz == AccountManager.class
                                                    || clazz == ActivityManager.class
                                                    || clazz == AlarmManager.class
                                                    || clazz == AudioManager.class
                                                    || clazz == ConnectivityManager.class
                                                    || clazz == DevicePolicyManager.class
                                                    || clazz == DropBoxManager.class
                                                    || clazz == InputMethodManager.class
                                                    || clazz == KeyguardManager.class
                                                    || clazz == LayoutInflater.class
                                                    || clazz == LocationManager.class
                                                    || clazz == NotificationManager.class
                                                    || clazz == PowerManager.class
                                                    || clazz == ClipboardManager.class
                                                    || clazz == SearchManager.class
                                                    || clazz == SensorManager.class
                                                    || clazz == TelephonyManager.class
                                                    || clazz == UiModeManager.class
                                                    || clazz == Vibrator.class
                                                    || clazz == WifiManager.class
                                                    || clazz == WindowManager.class || clazz == WallpaperManager.class)) {
                                        resolvable = true;
                                    }

                                    /* See if is an available resource */
                                    if ((id & 0xff000000) != 0) {

                                        /* First check for it in cache */
                                        Object obj = idCache.get(id);
                                        if (obj != null) {
                                            if (View.class.isAssignableFrom(clazz)) {
                                                resolvable = clazz.isInstance(obj);
                                            } else {
                                                resolvable = true;
                                            }
                                        } else {
                                            if (res != null) {
                                                try {
                                                    if (clazz == Integer.class) {
                                                        obj = res.getColor(id);
                                                        resolvable = true;
                                                    } else if (clazz == Boolean.class) {
                                                        obj = res.getBoolean(id);
                                                        resolvable = true;
                                                    } else if (clazz == Float.class) {
                                                        obj = res.getDimension(id);
                                                        resolvable = true;
                                                    } else if (clazz == String.class) {
                                                        obj = res.getString(id);
                                                        resolvable = true;
                                                    } else if (clazz == Drawable.class) {
                                                        obj = res.getDrawable(id);
                                                        resolvable = true;
                                                    } else if (clazz == ColorStateList.class) {
                                                        obj = res.getColorStateList(id);
                                                        resolvable = true;
                                                    } else if (clazz == XmlResourceParser.class
                                                            || clazz == XmlPullParser.class) {
                                                        obj = res.getXml(id);
                                                        resolvable = true;
                                                    }
                                                } catch (NotFoundException e1) {
                                                    try {
                                                        if (array != null) {
                                                            if (clazz == String.class) {
                                                                obj = res.getStringArray(id);
                                                                resolvable = true;
                                                                directArray = true;
                                                            } else if (clazz == Integer.class) {
                                                                obj = res.getIntArray(id);
                                                                resolvable = true;
                                                                directArray = true;
                                                            }
                                                        }
                                                    } catch (NotFoundException e2) {
                                                        /* Ignore */
                                                    }
                                                }

                                                /* Add to cache */
                                                if (obj != null) {
                                                    idCache.put(id, obj);
                                                }
                                            }

                                            /*
                                             * See if there is any matching
                                             * views
                                             */
                                            if (View.class.isAssignableFrom(clazz)) {

                                                /* Check all views for ID */
                                                View v = null;
                                                for (int l = 0; l < viewsLen; l++) {
                                                    View cv = views[l].findViewById(id);
                                                    if (cv != null) {
                                                        v = cv;
                                                    }
                                                }

                                                /*
                                                 * Check activity for view when
                                                 * possible
                                                 */
                                                if (v == null && ctxt instanceof Activity) {
                                                    v = ((Activity)ctxt).findViewById(id);
                                                }

                                                /* Attempt to inflate */
                                                if (v == null && ctxt != null) {
                                                    try {
                                                        v = ((LayoutInflater)ctxt
                                                                .getSystemService(Service.LAYOUT_INFLATER_SERVICE))
                                                                .inflate(id, null);
                                                    } catch (InflateException e) {
                                                        /* Ignore */
                                                    } catch (NotFoundException e) {
                                                        /* Ignore */
                                                    }
                                                }
                                                if (clazz.isInstance(v)) {
                                                    resolvable = true;

                                                    /* Add to cache */
                                                    idCache.put(id, v);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    tags = cont.mTags;
                                    vital = cont.mVital;
                                    numTags = cont.mNumTags;
                                    clazz = cont.mClazz;
                                    array = cont.mArray;
                                }

                                /*
                                 * See if there are any valid factories
                                 * producing objects of the wanted type.
                                 */
                                /*
                                 * NOTE: This is run as a last step to make it
                                 * easier to provide continuation information.
                                 */
                                boolean found;
                                Factory candidate;
                                if (cont == null) {
                                    found = false;
                                    candidate = factories;
                                } else {
                                    candidate = cont.mCandidate;
                                    found = cont.mFound;
                                }
                                while (true) {
                                    /* Check consequence of valid factory */
                                    /*
                                     * NOTE: This is done first in the loop to
                                     * support continuation.
                                     */
                                    if (valid) {
                                        if (found) {
                                            if (array == null) {
                                                f.mStatus = Factory.UNRESOLVABLE;
                                                break validation;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            found = true;
                                            resolvable = true;
                                            if (array != null) {
                                                break;
                                            }
                                        }
                                        valid = false;
                                    }

                                    /* End of loop */
                                    if (candidate == null) {
                                        break;
                                    }

                                    /* Check factory */
                                    if ((numTags == 0 || candidate.hasTag(tags))
                                            && clazz.isAssignableFrom(candidate.mClazz)) {
                                        /*
                                         * See if it matches what the current
                                         * factory wants and make sure we are
                                         * not walking in a circle.
                                         */
                                        /*
                                         * NOTE: When we encounter a circular
                                         * dependency we add ourselves as
                                         * dependable on that factory and assume
                                         * it is valid. If it turns out not to
                                         * be (because not all of its injected
                                         * field has been validated yet) it will
                                         * set this factory to invalid later.
                                         */
                                        switch (candidate.mStatus) {
                                            case Factory.EXECUTED:
                                                /* Fall through */
                                            case Factory.RESOLVABLE:
                                                valid = true;
                                                break;
                                            case Factory.UNRESOLVABLE:
                                                valid = false;
                                                break;
                                            case Factory.PROCESSING: {
                                                /*
                                                 * NOTE: We can not allow
                                                 * non-singletons to be part of
                                                 * circular dependency loops
                                                 * since it would cause an
                                                 * infinite instantiation loop.
                                                 */
                                                if (candidate.mNonSingleton) {
                                                    valid = false;
                                                    break;
                                                }

                                                /* Assume valid for now */
                                                valid = true;

                                                /* Find start of circle */
                                                int start = pathIdx;
                                                while (path[start] != candidate) {
                                                    start--;
                                                }

                                                /*
                                                 * Set circle as dependants to
                                                 * first factory.
                                                 */
                                                final int cirleLen = pathIdx - start + 1;
                                                Factory[] dependants = new Factory[cirleLen];
                                                System.arraycopy(path, start, dependants, 0,
                                                        cirleLen);
                                                candidate.mDependants = dependants;
                                                break;
                                            }
                                            default: {
                                                /* Prepare continuation */
                                                /*
                                                 * NOTE: This is used to put no
                                                 * load what so ever on the call
                                                 * stack when resolving
                                                 * factories, since the depth
                                                 * would in the worst case be
                                                 * equal to the total number of
                                                 * factories.
                                                 */
                                                ResolveContinuation c = new ResolveContinuation();
                                                c.mCurrent = current;
                                                c.mFields = fields;
                                                c.mFieldIdx = fieldIdx;
                                                c.mClazz = clazz;
                                                c.mTags = tags;
                                                c.mVital = vital;
                                                c.mNumTags = numTags;
                                                c.mArray = array;
                                                c.mCandidate = candidate.mNext;
                                                c.mFound = found;

                                                /*
                                                 * Restart processing with
                                                 * candidate factory
                                                 */
                                                f.mCont = c;
                                                pathIdx++;
                                                f = candidate;
                                                continue resolve;
                                            }
                                        }
                                    }
                                    candidate = candidate.mNext;
                                }

                                /*
                                 * Abort in case we could not find anything to
                                 * inject.
                                 */
                                if (!resolvable && vital) {
                                    f.mStatus = Factory.UNRESOLVABLE;
                                    break validation;
                                }

                                /* Release the current continuation */
                                cont = null;
                                f.mCont = null;
                            }

                            /* Visit next field */
                            fieldIdx++;
                        }

                        /* Walk up the hierarchy */
                        current = current.getSuperclass();
                    }

                    /* Resolve status */
                    final Factory[] dependants = f.mDependants;
                    if (f.mStatus == Factory.PROCESSING) {
                        f.mStatus = Factory.RESOLVABLE;
                    } else if (dependants != null) {

                        /* Invalidate all dependants */
                        final int dependantsLen = dependants.length;
                        for (int i = 0; i < dependantsLen; i++) {
                            dependants[i].mStatus = Factory.UNRESOLVABLE;
                        }
                        f.mDependants = null;
                    }
                    valid = (f.mStatus == Factory.RESOLVABLE || f.mStatus == Factory.EXECUTED);
                    break;
            }

            /* Handle previous continuation */
            if (pathIdx == 0) {
                return (f.mStatus == Factory.RESOLVABLE || f.mStatus == Factory.EXECUTED);
            } else {
                pathIdx--;
                f = path[pathIdx];
            }
        }
    }

    private static void internalError(Throwable t) {
        throw new IllegalStateException("Internal error", t);
    }

    private static Class<?> wrapPrimitive(Class<?> clazz) {
        if (clazz == int.class) {
            clazz = Integer.class;
        } else if (clazz == boolean.class) {
            clazz = Boolean.class;
        } else if (clazz == float.class) {
            clazz = Float.class;
        } else if (clazz == double.class) {
            clazz = Double.class;
        } else if (clazz == long.class) {
            clazz = Long.class;
        } else if (clazz == char.class) {
            clazz = Character.class;
        } else if (clazz == short.class) {
            clazz = Short.class;
        } else if (clazz == byte.class) {
            clazz = Byte.class;
        }
        return clazz;
    }

    private static Glue getInjection(final Field f) {
        Annotation[] anns = f.getDeclaredAnnotations();
        final int annsLen = anns.length;
        switch (annsLen) {
            case 0:
                return null;
            case 1: {
                final Annotation ann = anns[0];
                if (ann instanceof Glue) {
                    return (Glue)ann;
                }
                return null;
            }
            default:
                for (int i = 0; i < annsLen; i++) {
                    final Annotation ann = anns[i];
                    if (ann instanceof Glue) {
                        return (Glue)ann;
                    }
                }
                return null;
        }
    }

    private static boolean isPrimitive(Class<?> clazz) {
        return (clazz.isPrimitive() || clazz == int[].class || clazz == short[].class
                || clazz == boolean[].class || clazz == long[].class || clazz == byte[].class
                || clazz == float[].class || clazz == double[].class || clazz == char[].class);
    }

    private static String simpleName(int prefixLen, Class<?> clazz) {
        String name = clazz.getCanonicalName();
        if (name.startsWith("java.lang.")) {
            return name.substring("java.lang.".length());
        } else if (clazz.isPrimitive()) {
            return clazz.getCanonicalName();
        } else {
            return name.substring(prefixLen);
        }
    }

    private static String simpleName(int prefixLen, Object obj, ArrayList<Object> all) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            StringBuilder result = new StringBuilder();
            if (clazz == int[].class) {
                result.append(Arrays.toString((int[])obj));
            } else if (clazz == float[].class) {
                result.append(Arrays.toString((float[])obj));
            } else if (clazz == double[].class) {
                result.append(Arrays.toString((double[])obj));
            } else if (clazz == long[].class) {
                result.append(Arrays.toString((long[])obj));
            } else if (clazz == boolean[].class) {
                result.append(Arrays.toString((boolean[])obj));
            } else if (clazz == char[].class) {
                result.append(Arrays.toString((char[])obj));
            } else if (clazz == byte[].class) {
                result.append(Arrays.toString((byte[])obj));
            } else if (clazz == short[].class) {
                result.append(Arrays.toString((short[])obj));
            } else {
                result.append("[");
                Object[] arr = (Object[])obj;
                final int arrLen = arr.length;
                for (int i = 0; i < arrLen; i++) {
                    if (i != 0) {
                        result.append(", ");
                    }
                    result.append(simpleName(prefixLen, arr[i], all));
                }
                result.append("]");
            }
            return result.toString();
        } else if (obj instanceof Integer || obj instanceof Boolean || obj instanceof Double
                || obj instanceof Long || obj instanceof Float || obj instanceof Byte
                || obj instanceof Short) {
            return obj.toString();
        } else if (obj instanceof String) {
            return "\"" + obj.toString() + "\"";
        } else if (obj instanceof Character) {
            return "'" + obj.toString() + "'";
        } else {
            String name = clazz.getCanonicalName().substring(prefixLen);
            if (all != null) {
                int id = 0;
                int idx = 0;
                for (Object ref : all) {
                    if (ref.getClass() == clazz) {
                        idx++;
                    }
                    if (ref == obj) {
                        id = idx;
                    }
                }
                if (idx > 1) {
                    name += "#" + id;
                }
            }
            return name;
        }
    }

    private static String commonPrefix(String prefix, String text) {
        if (text.startsWith("java.lang.") || text.startsWith("int") || text.startsWith("boolean")
                || text.startsWith("float") || text.startsWith("double") || text.startsWith("long")
                || text.startsWith("char") || text.startsWith("short") || text.startsWith("byte")) {
            return prefix;
        } else if (prefix == null) {
            return text;
        } else {
            final int len = Math.min(prefix.length(), text.length());
            int idx = 0;
            while (idx < len && prefix.charAt(idx) == text.charAt(idx)) {
                idx++;
            }
            return prefix.substring(0, idx);
        }
    }

    private static String niceClassName(Class<?> clazz) {
        if (clazz.isArray()) {
            Class<?> comp = clazz.getComponentType();
            StringBuilder name = new StringBuilder(comp.getCanonicalName());
            while (true) {
                name.append("[]");
                if (comp.isArray()) {
                    comp = comp.getComponentType();
                } else {
                    break;
                }
            }
            return name.toString();
        } else {
            return clazz.getCanonicalName();
        }
    }

    private void removeNode(Node node) {
        final Node next = node.mNext;
        final Node prev = node.mPrev;
        if (prev != null) {
            prev.mNext = next;
        }
        if (next != null) {
            next.mPrev = prev;
        }
        if (node == mFirstNode) {
            mFirstNode = next;
        }
        if (node == mLastNode) {
            mLastNode = prev;
        }
    }

    private void removeFactory(Factory factory) {

        /* Unlink */
        final Factory next = factory.mNext;
        final Factory prev = factory.mPrev;
        if (prev != null) {
            prev.mNext = next;
        }
        if (next != null) {
            next.mPrev = prev;
        }
        if (factory == mFactories) {
            mFactories = next;
        }

        /* Queue for removal */
        /*
         * NOTE: This is done so that in case the factory is re-added before
         * apply() is run the product is not recreated.
         */
        final Node product = factory.mProduct;
        if (product != null) {
            Node pending = mPendingRemoval;
            product.mPrev = null;
            product.mNext = pending;
            mPendingRemoval = product;
            if (pending != null) {
                pending.mPrev = product;
            }
        }
    }

    private void add(Object obj, int[] tags, int len, Context ctxt, int res) {

        /* Filter out factories */
        if (obj instanceof Class<?>) {
            final Class<?> clazz = (Class<?>)obj;
            Factory factories = mFactories;
            if (factories == null) {
                factories = new Factory(clazz);
                factories.mContext = ctxt;
                if (tags != null) {
                    factories.addTags(tags, len);
                }
                mFactories = factories;
            } else {
                /* Ignore duplicates */
                Factory factory = factories;
                do {
                    if (factory.mClazz == clazz) {
                        Node product = factory.mProduct;
                        if (tags != null) {
                            factory.addTags(tags, len);
                            mRobust = false;

                            /* Also update tags on produced object */
                            if (product != null) {
                                product.mTags = factory.mTags;
                                product.mTagsLen = factory.mTagsLen;
                            }
                        }
                        return;
                    }
                    factory = factory.mNext;
                } while (factory != null);
                factory = new Factory(clazz);
                factory.mContext = ctxt;
                factory.mNext = factories;
                factories.mPrev = factory;
                if (tags != null) {
                    factory.addTags(tags, len);
                }
                mFactories = factory;
            }

            /* Stop pending removal on factory product */
            Node firstPending = mPendingRemoval;
            Node pending = firstPending;
            while (pending != null) {
                if (clazz == pending.mObject.getClass()) {

                    /* Unlink from queue */
                    Node prev = pending.mPrev;
                    Node next = pending.mNext;
                    if (prev != null) {
                        prev.mNext = next;
                    }
                    if (next != null) {
                        next.mPrev = prev;
                    }
                    if (firstPending == pending) {
                        mPendingRemoval = next;
                    }

                    /* Assign to factory again */
                    mFactories.mProduct = pending;
                    pending.mContext = ctxt;
                    break;
                }
                pending = pending.mNext;
            }

            /* Keep track of number of factories */
            mNumFactories++;
        } else {
            Node nodes = mFirstNode;
            if (nodes == null) {
                nodes = new Node(obj);
                nodes.mContext = ctxt;
                if (tags != null) {
                    nodes.addTags(tags, len);
                }
                mFirstNode = nodes;
                mLastNode = nodes;
            } else {
                /* Ignore duplicates */
                Node node = nodes;
                do {
                    if (node.mObject == obj) {
                        if (tags != null) {
                            node.addTags(tags, len);
                            mRobust = false;
                        }
                        return;
                    }
                    node = node.mNext;
                } while (node != null);
                node = new Node(obj);
                node.mContext = ctxt;
                node.mNext = nodes;
                nodes.mPrev = node;
                if (tags != null) {
                    node.addTags(tags, len);
                }
                mFirstNode = node;
            }
            mNumNodes++;
        }
        mRobust = false;
    }

    /**
     * Creates an empty and robust instance without any connection to the
     * Android system. This is useful for non-Android specific implementations.
     */
    public DuctTape() {
        this(null);
    }

    /**
     * Creates an empty and robust instance, living in the given Android
     * context.
     * 
     * @param ctxt The context to be used by the instance.
     * @see #isRobust()
     */
    public DuctTape(Context ctxt) {
        mRobust = true;
        mContext = ctxt;
    }

    /**
     * Convenience method to add an object without any tags.
     * 
     * @param obj The object to add to the list of objects to be glued.
     * @see #remove(Object)
     * @see #add(Object, int[])
     */
    public final void add(Object obj) {
        add(obj, null);
    }

    /**
     * Convenience method to add an array of objects without any tags.
     * 
     * @param objs The objects to add to the list of objects to be glued.
     * @see #remove(Object[])
     * @see #add(Object, int[])
     */
    public final void add(Object[] objs) {
        final int objsLen = objs.length;
        for (int i = 0; i < objsLen; i++) {
            add(objs[i]);
        }
    }

    /**
     * Convenience method to add an object with only a single tag.
     * 
     * @param obj The object to add to the list of objects to be glued.
     * @param tag Tag to be set on the object.
     * @see #remove(Object, int)
     * @see #add(Object, int[])
     */
    public final void add(Object obj, int tag) {
        oneTag[0] = tag;
        add(obj, oneTag);
    }

    /**
     * Convenience method to add a sequence of objects. Use this when adding
     * objects to e.g. a collection, set or other structure that supports the
     * {@link java.lang.Iterable} interface.
     * 
     * @param objs An object enumerating the objects to be added.
     * @see #remove(Iterable objs)
     * @see #add(Object, int[])
     */
    public final void add(Iterable<?> objs) {
        for (Object obj : objs) {
            add(obj);
        }
    }

    /**
     * <p>
     * Adds an object to the object mesh and tags it with the given tags. In
     * case the object has already been added the given tags are appended to the
     * tags already present on the object.
     * </p>
     * <p>
     * {@link java.lang.Class} objects are treated specially in the sense that
     * they are not directly added, but instead the object they represent is
     * created on-demand when another object requires its type. These on-demand
     * objects are persistent as long as the class object is not removed from
     * the mesh (the removal is however not permanent until a call to
     * {@link #apply()} is made).
     * </p>
     * 
     * @param obj The object to add to the list of objects to be glued.
     * @param tags Tags to be set on the object, or <code>null</code> in case
     *            the object should be added without any tags.
     * @see #remove(Object)
     * @see #add(Object)
     * @see #add(Object[])
     * @see #add(Object, int)
     */
    public final void add(Object obj, int[] tags) {
        if (tags != null) {
            add(obj, tags, tags.length, mContext, 0);
        } else {
            add(obj, null, 0, mContext, 0);
        }
    }

    /**
     * Removes an object from the mesh. In case the object is a
     * {@link java.lang.Class} which has been used to create an object, that
     * object is also removed (that operation is however postponed until the
     * next call to {@link #apply()} in case the removal is reverted).
     * <p>
     * This method does nothing when the given object is not present in the
     * mesh.
     * 
     * @param obj The object to remove.
     * @see #add(Object, int[])
     * @see #remove(int)
     * @see #remove(Object)
     * @see #remove(Object[])
     * @see #remove(Iterable)
     */
    public final void remove(Object obj) {
        if (obj instanceof Class<?>) {
            final Class<?> clazz = (Class<?>)obj;
            Factory factory = mFactories;
            while (factory != null) {
                if (factory.mClazz == clazz) {
                    removeFactory(factory);
                    mNumFactories--;
                    mRobust = false;
                    break;
                }
                factory = factory.mNext;
            }
        } else {
            Node node = mFirstNode;
            while (node != null) {
                if (node.mObject == obj) {
                    removeNode(node);
                    mNumNodes--;
                    mRobust = false;
                    return;
                }
                node = node.mNext;
            }
        }
    }

    /**
     * Convenience method to remove an array of objects.
     * 
     * @param objs The objects to remove.
     */
    public final void remove(Object[] objs) {
        final int objsLen = objs.length;
        for (int i = 0; i < objsLen; i++) {
            remove(objs[i]);
        }
    }

    /**
     * Convenience method to remove a sequence of objects. Use this when
     * removing objects in a e.g. a collection, set or other structure that
     * supports the {@link java.lang.Iterable} interface.
     * 
     * @param objs An object enumerating the objects to be removed. Objects not
     *            in the mesh are ignored.
     */
    public final void remove(Iterable<?> objs) {
        for (Object obj : objs) {
            remove(obj);
        }
    }

    /**
     * Removes objects with any of the given tags. Note that no distinction is
     * made between {@link java.lang.Class} objects and regular objects here
     * (objects automatically created from Class objects are however removed
     * together with the class).
     * 
     * @param tags Tags on objects to remove. An object matches when any of
     *            these tags are set on the object.
     * @see #remove(int)
     * @see #remove(Object)
     */
    public final void remove(int[] tags) {
        boolean robust = mRobust;
        Node node = mFirstNode;
        while (node != null) {
            if (node.hasTag(tags)) {
                removeNode(node);
                robust = false;
            }
            node = node.mNext;
        }
        Factory factory = mFactories;
        while (factory != null) {
            if (factory.hasTag(tags)) {
                removeFactory(factory);
                mNumFactories--;
                robust = false;
            }
            factory = factory.mNext;
        }
        mRobust = robust;
    }

    /**
     * Convenience method to remove all objects with the given tag.
     * 
     * @param tag Tag on objects to remove.
     * @see #remove(int[])
     * @see #remove(Object)
     */
    public final void remove(int tag) {
        oneTag[0] = tag;
        remove(oneTag);
    }

    /**
     * Removes a tag from the given object. In case the object is a
     * <code>Class</code> which has already constructed an object in a previous
     * call to {@link #apply()} that object's tags will be updated to match
     * those of the constructing <code>Class</code>.
     * <p>
     * This method does nothing when the given object is not a part of the list
     * of objects to be to be glued together.
     * 
     * @param obj Object on which to remove the tag.
     * @param tag The tag to remove.
     */
    public final void remove(Object obj, int tag) {
        if (obj instanceof Class<?>) {
            final Class<?> clazz = (Class<?>)obj;
            Factory factory = mFactories;
            search: while (factory != null) {
                if (factory.mClazz == clazz) {
                    int tagsLen = factory.mTagsLen;
                    final int[] tags = factory.mTags;
                    for (int j = 0; j < tagsLen; j++) {
                        if (tags[j] == tag) {
                            if (j + 1 != tagsLen) {
                                System.arraycopy(tags, j + 1, tags, j, tagsLen - j - 1);
                            }
                            tagsLen--;
                            factory.mTagsLen = tagsLen;

                            /* Also update tags on produced object */
                            Node product = factory.mProduct;
                            if (product != null) {
                                product.mTags = tags;
                                product.mTagsLen = tagsLen;
                            }
                            mRobust = false;
                            break search;
                        }
                    }
                    break search;
                }
                factory = factory.mNext;
            }
        } else {
            Node node = mFirstNode;
            search: while (node != null) {
                if (node.mObject == obj) {
                    int tagsLen = node.mTagsLen;
                    final int[] tags = node.mTags;
                    for (int j = 0; j < tagsLen; j++) {
                        if (tags[j] == tag) {
                            if (j + 1 != tagsLen) {
                                System.arraycopy(tags, j + 1, tags, j, tagsLen - j - 1);
                            }
                            tagsLen--;
                            node.mTagsLen = tagsLen;
                            mRobust = false;
                            break search;
                        }
                    }
                    break search;
                }
                node = node.mNext;
            }
        }
    }

    /**
     * Resets this instance to the same state as it had when it was first
     * constructed. This method is mainly intended as a way to recycle
     * instances.
     */
    public final void clear() {
        mFactories = null;
        mNumFactories = 0;
        mFirstNode = null;
        mLastNode = null;
        mPendingRemoval = null;
        mNumNodes = 0;
        mRobust = true;
    }

    /**
     * Gives the current status of the glued object mesh. Just after
     * {@link #apply()} has been executed the mesh is always robust, but adding
     * and removing objects usually changes that.
     * <p>
     * Note that this method does not scan any objects, why changes made
     * directly to injected fields will not be detected (and are not recommended
     * anyhow).
     * 
     * @return <code>true</code> when all objects has been properly injected,
     *         <code>false</code> when some changes has been made and
     *         {@link #apply()} has not yet been called.
     */
    public final boolean isRobust() {
        return mRobust;
    }

    /**
     * Calculates the robust state of the object mesh and injects values in all
     * tagged fields. This method does nothing when there are no objects to glue
     * together or the object mesh is already robust.
     * 
     * @throws GluingException when an injection fails either because of
     *             problems outside of this method, e.g. in object constructors,
     *             or because it was not possible to come to a conclusion on
     *             what to inject into a specific field.
     * @see #isRobust()
     */
    public final void apply() throws GluingException {

        /* Do nothing unless tape is needed */
        int nodesLen = mNumNodes;
        if (!mRobust && nodesLen > 0) {

            /*
             * Take height for objects that might be constructed by the
             * factories
             */
            final int factoriesLen = mNumFactories;

            /* Prepare fields for quick access */
            final Factory factories = mFactories;

            /* Execute all pending removals */
            /*
             * NOTE: Clearing the head of this releases the rest of it in one
             * sweep.
             */
            mPendingRemoval = null;

            /* Reset factories */
            Factory f = factories;
            while (f != null) {
                f.mStatus = Factory.NOT_PROCESSED;
                f = f.mNext;
            }

            /* Pre-process all nodes */
            View[] views = null;
            int viewsLen = 0;
            Node lastNode = mLastNode;
            Node firstNode = mFirstNode;
            Node node = firstNode;
            while (node != null) {

                /* Sort out special Android objects */
                Object obj = node.mObject;
                if (obj instanceof View) {
                    if (views == null) {
                        views = new View[nodesLen];
                    }
                    views[viewsLen++] = (View)obj;
                }

                /* Reset node status */
                node.mFlags = 0;
                node.mChildren = null;
                node.mNextMerged = null;

                /* Traverse all objects */
                node = node.mNext;
            }

            /* Prepare storage */
            final Factory[] factoryPath = new Factory[factoriesLen];
            HashMap<TaggedClass, Implementors> implCache = new HashMap<TaggedClass, Implementors>();
            ArrayList<Object[]> toBeSorted = new ArrayList<Object[]>();
            HashMap<Integer, Object> idCache = new HashMap<Integer, Object>();
            TaggedClass key = null;

            /* Visit all nodes up till first unused product */
            node = firstNode;
            while (node != null) {
                final Object instance = node.mObject;

                /* Only consider objects with injections */
                if (instance instanceof Gluey) {

                    /* Visit all classes that are injection targets */
                    Class<?> current = instance.getClass();
                    while (Gluey.class.isAssignableFrom(current)) {

                        /* Provide fast access to object's context */
                        Context ctxt = node.mContext;
                        final Resources res;
                        if (ctxt != null) {
                            res = ctxt.getResources();
                        } else {
                            res = null;
                        }

                        /* Get injection points */
                        Field[] fields = current.getDeclaredFields();
                        final int fieldsLen = fields.length;
                        for (int j = 0; j < fieldsLen; j++) {
                            Field field = fields[j];
                            Glue injection = getInjection(field);
                            if (injection != null) {
                                final int[] tags = injection.value();
                                final int tagsLen = tags.length;

                                /* See if this injection is vital */
                                boolean vital = true;
                                boolean sort = false;
                                int numTags = tagsLen;
                                int id = 0;
                                tagScan: for (int k = 0; k < tagsLen; k++) {
                                    final int tag = tags[k];
                                    switch (tag) {
                                        case OPTIONAL:
                                            vital = false;
                                            numTags--;
                                            if (sort) {
                                                break tagScan;
                                            }
                                            break;
                                        case ORDERED:
                                            sort = true;
                                            numTags--;
                                            if (!vital) {
                                                break tagScan;
                                            }
                                            break;
                                        default:
                                            id = tag;
                                            break;
                                    }
                                }

                                /* Ensure field is accessible */
                                field.setAccessible(true);

                                /* Prepare caching of exact content type */
                                boolean fieldChecked = false;
                                Object fieldObject = null;
                                Class<?> fieldObjectClazz = null;

                                /* Extract injection type information */
                                Class<?> clazz = field.getType();

                                /* Unwrap array */
                                final Class<?> array;
                                if (clazz.isArray()) {
                                    array = clazz;
                                    clazz = clazz.getComponentType();
                                } else {
                                    array = null;
                                }
                                clazz = wrapPrimitive(clazz);

                                /* Look up matching objects in cache */
                                if (key == null) {
                                    key = new TaggedClass(tags, clazz);
                                } else {
                                    key.mTags = tags;
                                    key.mClazz = clazz;
                                }
                                Implementors cached = implCache.get(key);
                                if (cached != null && array == null && !cached.mGeneric) {
                                    cached = null;
                                }

                                /* Find implementors on cache miss */
                                boolean justCached = false;
                                if (cached == null) {
                                    boolean directArray = false;

                                    /* Scan for existing implementing objects */
                                    int childrenLen = 1;
                                    Node[] children = new Node[1];
                                    int cacheLen = 0;
                                    Node n = firstNode;
                                    while (n != null) {

                                        /* Add implementing object to cache */
                                        if ((n.mFlags & Node.DO_NOT_REUSE) == 0) {
                                            Object candidate = n.mObject;
                                            boolean include = false;
                                            if (clazz.isInstance(candidate)
                                                    && (numTags == 0 || n.hasTag(tags))) {
                                                if (directArray) {
                                                    throw new GluingException(
                                                            "Conflicting injections "
                                                                    + niceClassName(children[cacheLen - 1].mObject
                                                                            .getClass())
                                                                    + " and "
                                                                    + niceClassName(candidate
                                                                            .getClass())
                                                                    + " for "
                                                                    + niceClassName(instance
                                                                            .getClass()) + "."
                                                                    + field.getName());
                                                }
                                                include = true;
                                            } else if (!directArray && array != null
                                                    && array.isInstance(candidate)
                                                    && (numTags == 0 || n.hasTag(tags))) {
                                                if (cacheLen != 0) {
                                                    throw new GluingException(
                                                            "Conflicting injections "
                                                                    + niceClassName(children[cacheLen - 1].mObject
                                                                            .getClass())
                                                                    + " and "
                                                                    + niceClassName(candidate
                                                                            .getClass())
                                                                    + " for "
                                                                    + niceClassName(instance
                                                                            .getClass()) + "."
                                                                    + field.getName());
                                                }
                                                directArray = true;
                                                include = true;
                                            }
                                            if (include) {
                                                if (cacheLen == childrenLen) {
                                                    childrenLen += ARRAY_INCREMENT_SIZE;
                                                    Node[] temp = new Node[childrenLen];
                                                    System.arraycopy(children, 0, temp, 0, cacheLen);
                                                    children = temp;
                                                }
                                                children[cacheLen++] = n;
                                            }
                                        }
                                        n = n.mNext;
                                    }

                                    /*
                                     * Create implementing objects using all
                                     * factories.
                                     */
                                    boolean generic = true;
                                    if (vital) {
                                        Factory factory = factories;
                                        while (factory != null) {
                                            if (factory.mStatus != Factory.UNRESOLVABLE
                                                    && (factory.mNonSingleton || factory.mStatus != Factory.EXECUTED)) {
                                                /*
                                                 * Check that the product is
                                                 * applicable.
                                                 */
                                                Class<?> factoryClazz = factory.mClazz;
                                                if (clazz.isAssignableFrom(factoryClazz)
                                                        && (numTags == 0 || factory.hasTag(tags))) {

                                                    /*
                                                     * Find the context of this
                                                     * factory.
                                                     */
                                                    Context prodCtxt = factory.mContext;

                                                    /*
                                                     * Make sure the class has
                                                     * access to all the types
                                                     * it needs.
                                                     */
                                                    if (factory.mStatus == Factory.EXECUTED
                                                            || isResolvable(factory, prodCtxt,
                                                                    views, viewsLen, firstNode,
                                                                    nodesLen, factories,
                                                                    factoriesLen, factoryPath,
                                                                    idCache)) {

                                                        /* Produce object */
                                                        try {
                                                            Node product = factory.mProduct;

                                                            /*
                                                             * Skip creation in
                                                             * case this is not
                                                             * a singleton and
                                                             * there is already
                                                             * an instance of
                                                             * the product in
                                                             * the field.
                                                             */
                                                            /*
                                                             * NOTE: Since we
                                                             * are comparing
                                                             * class-types and
                                                             * different context
                                                             * sensitive classes
                                                             * are created using
                                                             * different
                                                             * class-loaders, it
                                                             * is safe to assume
                                                             * that on a match
                                                             * we have an
                                                             * instance having
                                                             * the expected
                                                             * origin.
                                                             */
                                                            if (mProcessed && factory.mNonSingleton) {
                                                                try {
                                                                    Object reuse = null;
                                                                    if (!fieldChecked) {
                                                                        fieldObject = field
                                                                                .get(instance);
                                                                        if (fieldObject != null) {
                                                                            fieldObjectClazz = fieldObject
                                                                                    .getClass();
                                                                        }
                                                                        fieldChecked = true;
                                                                    }
                                                                    if (fieldObject != null) {
                                                                        if (array == null) {
                                                                            if (fieldObjectClazz == factoryClazz) {
                                                                                reuse = fieldObject;
                                                                            }
                                                                        } else {
                                                                            /*
                                                                             * Scan
                                                                             * the
                                                                             * array
                                                                             */
                                                                            Object[] arr = (Object[])fieldObject;
                                                                            final int arrLen = arr.length;
                                                                            for (int i = 0; i < arrLen; i++) {
                                                                                Object entry = arr[i];
                                                                                if (factoryClazz == entry
                                                                                        .getClass()) {
                                                                                    reuse = entry;
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    /*
                                                                     * Create
                                                                     * node for
                                                                     * reused
                                                                     * object
                                                                     */
                                                                    if (reuse != null) {
                                                                        product = new Node(reuse);
                                                                        product.mContext = prodCtxt;
                                                                        product.mFlags |= Node.DO_NOT_REUSE;
                                                                        final int[] factoryTags = factory.mTags;
                                                                        if (factoryTags != null) {
                                                                            product.mTags = factoryTags;
                                                                            product.mTagsLen = factory.mTagsLen;
                                                                        }
                                                                    }
                                                                } catch (IllegalArgumentException e) {
                                                                    /* Ignore */
                                                                } catch (IllegalAccessException e) {
                                                                    /* Ignore */
                                                                }
                                                            }

                                                            /*
                                                             * Only create
                                                             * product when one
                                                             * is not already
                                                             * available.
                                                             */
                                                            if (product == null) {

                                                                /*
                                                                 * Run the
                                                                 * factory.
                                                                 */
                                                                Constructor<?> constructor = factoryClazz
                                                                        .getDeclaredConstructor();
                                                                constructor.setAccessible(true);
                                                                final Object obj = constructor
                                                                        .newInstance();

                                                                /*
                                                                 * Include
                                                                 * object.
                                                                 */
                                                                product = new Node(obj);

                                                                /*
                                                                 * Put in
                                                                 * correct
                                                                 * context.
                                                                 */
                                                                product.mContext = prodCtxt;

                                                                /*
                                                                 * Mark it with
                                                                 * same tags as
                                                                 * factory.
                                                                 */
                                                                final int[] factoryTags = factory.mTags;
                                                                if (factoryTags != null) {
                                                                    product.mTags = factoryTags;
                                                                    product.mTagsLen = factory.mTagsLen;
                                                                }

                                                                /*
                                                                 * Associate
                                                                 * product with
                                                                 * factory.
                                                                 */
                                                                if (factory.mNonSingleton) {
                                                                    product.mFlags |= Node.DO_NOT_REUSE;
                                                                    generic = false;
                                                                } else {
                                                                    factory.mProduct = product;
                                                                }
                                                            }

                                                            /*
                                                             * Mark factory as
                                                             * used at least
                                                             * once.
                                                             */
                                                            factory.mStatus = Factory.EXECUTED;

                                                            /*
                                                             * Append to end of
                                                             * node list to
                                                             * ensure it will be
                                                             * processed.
                                                             */
                                                            final Node last = lastNode;
                                                            product.mPrev = last;
                                                            product.mNext = null;
                                                            last.mNext = product;
                                                            lastNode = product;
                                                            nodesLen++;

                                                            /* Add as child */
                                                            if (cacheLen == childrenLen) {
                                                                childrenLen += ARRAY_INCREMENT_SIZE;
                                                                Node[] temp = new Node[childrenLen];
                                                                System.arraycopy(children, 0, temp,
                                                                        0, cacheLen);
                                                                children = temp;
                                                            }
                                                            children[cacheLen++] = product;
                                                        } catch (InstantiationException e) {
                                                            throw new GluingException(
                                                                    "Factory class "
                                                                            + factoryClazz
                                                                                    .getCanonicalName()
                                                                            + " is abstract");
                                                        } catch (IllegalAccessException e) {
                                                            throw new GluingException(
                                                                    "Could not access constructor of "
                                                                            + factoryClazz
                                                                                    .getCanonicalName());
                                                        } catch (InvocationTargetException e) {
                                                            throw new GluingException(
                                                                    "Uncaught exception during object creation",
                                                                    e.getCause());
                                                        } catch (SecurityException e) {
                                                            throw new GluingException(
                                                                    "Not allowed to access constructor of "
                                                                            + factoryClazz
                                                                                    .getCanonicalName());
                                                        } catch (NoSuchMethodException e) {
                                                            throw new GluingException(
                                                                    factoryClazz.getCanonicalName()
                                                                            + " has no constructor that takes zero arguments");
                                                        }
                                                    }
                                                }
                                            }
                                            factory = factory.mNext;
                                        }
                                    }

                                    /*
                                     * Look for compatible view in case such are
                                     * available.
                                     */
                                    Object obj = null;
                                    if (View.class.isAssignableFrom(clazz)
                                            && (id & 0xff000000) != 0) {

                                        /* Check in cache */
                                        Object o = idCache.get(id);
                                        if (clazz.isInstance(o)) {
                                            obj = o;
                                        } else {

                                            /* Check all views for ID */
                                            View v = null;
                                            for (int l = 0; l < viewsLen; l++) {
                                                View candidate = views[l].findViewById(id);
                                                if (clazz.isInstance(candidate)) {
                                                    v = candidate;
                                                }
                                            }

                                            /*
                                             * Check activity for view when
                                             * possible.
                                             */
                                            if (v == null && ctxt instanceof Activity) {
                                                v = ((Activity)ctxt).findViewById(id);
                                            }

                                            /* Attempt to inflate */
                                            if (v == null && ctxt != null) {
                                                try {
                                                    v = ((LayoutInflater)ctxt
                                                            .getSystemService(Service.LAYOUT_INFLATER_SERVICE))
                                                            .inflate(id, null);
                                                } catch (InflateException e) {
                                                    /* Ignore */
                                                } catch (NotFoundException e) {
                                                    /* Ignore */
                                                }
                                            }

                                            /*
                                             * Add view as node in when it is
                                             * compatible with the class we are
                                             * looking for.
                                             */
                                            if (clazz.isInstance(v)) {
                                                obj = v;

                                                /* Add to cache */
                                                idCache.put(id, obj);
                                            }
                                        }
                                    }

                                    /*
                                     * See if we can find something in the
                                     * context that can be injected.
                                     */
                                    if (ctxt != null && obj == null) {
                                        if (numTags == 0) {
                                            if (clazz == ApplicationInfo.class) {
                                                obj = ctxt.getApplicationInfo();
                                            } else if (clazz == AssetManager.class) {
                                                obj = ctxt.getAssets();
                                            } else if (clazz == ClassLoader.class) {
                                                obj = ctxt.getClassLoader();
                                            } else if (clazz == ContentResolver.class) {
                                                obj = ctxt.getContentResolver();
                                            } else if (clazz == Looper.class) {
                                                obj = ctxt.getMainLooper();
                                            } else if (clazz == PackageManager.class) {
                                                obj = ctxt.getPackageManager();
                                            } else if (clazz == Resources.class) {
                                                obj = ctxt.getResources();
                                            } else if (clazz == AccessibilityManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.ACCESSIBILITY_SERVICE);
                                            } else if (clazz == AccountManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.ACCOUNT_SERVICE);
                                            } else if (clazz == ActivityManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.ACTIVITY_SERVICE);
                                            } else if (clazz == AlarmManager.class) {
                                                obj = ctxt.getSystemService(Context.ALARM_SERVICE);
                                            } else if (clazz == AudioManager.class) {
                                                obj = ctxt.getSystemService(Context.AUDIO_SERVICE);
                                            } else if (clazz == ConnectivityManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                                            } else if (clazz == DevicePolicyManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.DEVICE_POLICY_SERVICE);
                                            } else if (clazz == DropBoxManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.DROPBOX_SERVICE);
                                            } else if (clazz == InputMethodManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                                            } else if (clazz == KeyguardManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.KEYGUARD_SERVICE);
                                            } else if (clazz == LayoutInflater.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            } else if (clazz == LocationManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.LOCATION_SERVICE);
                                            } else if (clazz == NotificationManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.NOTIFICATION_SERVICE);
                                            } else if (clazz == PowerManager.class) {
                                                obj = ctxt.getSystemService(Context.POWER_SERVICE);
                                            } else if (clazz == SearchManager.class) {
                                                obj = ctxt.getSystemService(Context.SEARCH_SERVICE);
                                            } else if (clazz == ClipboardManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                            } else if (clazz == SensorManager.class) {
                                                obj = ctxt.getSystemService(Context.SENSOR_SERVICE);
                                            } else if (clazz == TelephonyManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.TELEPHONY_SERVICE);
                                            } else if (clazz == UiModeManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.UI_MODE_SERVICE);
                                            } else if (clazz == Vibrator.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.VIBRATOR_SERVICE);
                                            } else if (clazz == WifiManager.class) {
                                                obj = ctxt.getSystemService(Context.WIFI_SERVICE);
                                            } else if (clazz == WindowManager.class) {
                                                obj = ctxt.getSystemService(Context.WINDOW_SERVICE);
                                            } else if (clazz == WallpaperManager.class) {
                                                obj = ctxt
                                                        .getSystemService(Context.WALLPAPER_SERVICE);
                                            }
                                        } else if (res != null && (id & 0xff000000) != 0) {

                                            /* Check in cache */
                                            Object o = idCache.get(id);
                                            if (clazz.isInstance(o)) {
                                                obj = o;
                                            } else {
                                                try {
                                                    if (clazz == Integer.class) {
                                                        obj = res.getColor(id);
                                                    } else if (clazz == Boolean.class) {
                                                        obj = res.getBoolean(id);
                                                    } else if (clazz == Float.class) {
                                                        obj = res.getDimension(id);
                                                    } else if (clazz == String.class) {
                                                        obj = res.getString(id);
                                                    } else if (clazz == Drawable.class) {
                                                        obj = res.getDrawable(id);
                                                    } else if (clazz == ColorStateList.class) {
                                                        obj = res.getColorStateList(id);
                                                    } else if (clazz == XmlResourceParser.class
                                                            || clazz == XmlPullParser.class) {
                                                        obj = res.getXml(id);
                                                    }
                                                } catch (NotFoundException e1) {
                                                    try {
                                                        if (!directArray && array != null) {
                                                            if (clazz == String.class) {
                                                                obj = res.getStringArray(id);
                                                                directArray = true;
                                                            } else if (clazz == Integer.class) {
                                                                obj = res.getIntArray(id);
                                                                directArray = true;
                                                            }
                                                        }
                                                    } catch (NotFoundException e2) {
                                                        /* Ignore */
                                                    }
                                                }

                                                /* Add to cache */
                                                if (obj != null) {
                                                    idCache.put(id, obj);
                                                }
                                            }
                                        }
                                    }

                                    /* Add as child */
                                    /*
                                     * The resource is not added as a node since
                                     * it was not included by the user, and we
                                     * would also get into some trouble figuring
                                     * out if an object was already added.
                                     */
                                    if (obj != null) {
                                        final Node product = new Node(obj);
                                        if (cacheLen == childrenLen) {
                                            childrenLen += ARRAY_INCREMENT_SIZE;
                                            Node[] temp = new Node[childrenLen];
                                            System.arraycopy(children, 0, temp, 0, cacheLen);
                                            children = temp;
                                        }
                                        children[cacheLen++] = product;
                                    }

                                    /* Convert to primitive array when necessary */
                                    if (array != null && array.getComponentType().isPrimitive()
                                            && !directArray) {
                                        Object target = null;
                                        if (array == int[].class) {
                                            int[] temp = new int[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Integer)children[i].mObject).intValue();
                                            }
                                            target = temp;
                                        } else if (array == float[].class) {
                                            float[] temp = new float[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Float)children[i].mObject).floatValue();
                                            }
                                            target = temp;
                                        } else if (array == boolean[].class) {
                                            boolean[] temp = new boolean[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Boolean)children[i].mObject)
                                                        .booleanValue();
                                            }
                                            target = temp;
                                        } else if (array == long[].class) {
                                            long[] temp = new long[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Long)children[i].mObject).longValue();
                                            }
                                            target = temp;
                                        } else if (array == double[].class) {
                                            double[] temp = new double[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Double)children[i].mObject)
                                                        .doubleValue();
                                            }
                                            target = temp;
                                        } else if (array == char[].class) {
                                            char[] temp = new char[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Character)children[i].mObject)
                                                        .charValue();
                                            }
                                            target = temp;
                                        } else if (array == short[].class) {
                                            short[] temp = new short[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Short)children[i].mObject).shortValue();
                                            }
                                            target = temp;
                                        } else if (array == byte[].class) {
                                            byte[] temp = new byte[cacheLen];
                                            for (int i = 0; i < cacheLen; i++) {
                                                temp[i] = ((Byte)children[i].mObject).byteValue();
                                            }
                                            target = temp;
                                        }
                                        Node arr = new Node(target);
                                        children[0] = arr;
                                        cacheLen = 1;
                                        directArray = true;
                                    }

                                    /* Convert cache */
                                    /*
                                     * NOTE: This is the actual array that will
                                     * be injected later.
                                     */
                                    if (cacheLen > 0) {
                                        final Object[] typed;
                                        if (directArray) {
                                            clazz = array;
                                        }
                                        typed = (Object[])Array.newInstance(clazz, cacheLen);
                                        for (int k = 0; k < cacheLen; k++) {
                                            typed[k] = children[k].mObject;
                                        }
                                        justCached = true;
                                        cached = new Implementors(typed, children, cacheLen,
                                                directArray, generic);
                                        implCache.put(key, cached);
                                        key = null;
                                    }
                                }

                                try {
                                    if (cached != null) {

                                        /* Mark for sorting */
                                        Object[] objs = cached.mObjects;
                                        if (sort && !toBeSorted.contains(objs)) {
                                            toBeSorted.add(objs);
                                            final Node[] children = cached.mNodes;
                                            final int numChildren = cached.mNumNodes;
                                            for (int i = 0; i < numChildren; i++) {
                                                children[i].mFlags |= Node.WILL_BE_SORTED;
                                            }
                                        }

                                        /* Perform injection */
                                        if (array != null) {
                                            if (cached.mDirectArray) {
                                                field.set(instance, objs[0]);
                                                if (vital) {
                                                    node.addChild(cached.mNodes[0]);
                                                }
                                            } else {
                                                /*
                                                 * NOTE: When we just cached the
                                                 * array there is no need to do
                                                 * substitutions of
                                                 * non-singletons in it.
                                                 */
                                                if (cached.mGeneric || justCached) {
                                                    field.set(instance, objs);

                                                    /* Add dependencies to graph */
                                                    if (vital) {
                                                        node.addChildren(cached.mNodes,
                                                                cached.mNumNodes);
                                                    }
                                                } else {
                                                    try {
                                                        /*
                                                         * Create new instances
                                                         * of non-singletons in
                                                         * an array.
                                                         */
                                                        Object[] cacheObjs = objs;
                                                        final int cacheObjsLen = cacheObjs.length;
                                                        Object[] unique = (Object[])Array
                                                                .newInstance(clazz, cacheObjsLen);
                                                        System.arraycopy(cacheObjs, 0, unique, 0,
                                                                cacheObjsLen);
                                                        Node[] uniqueChildren = new Node[cacheObjsLen];
                                                        System.arraycopy(cached.mNodes, 0,
                                                                uniqueChildren, 0, cacheObjsLen);
                                                        final Node[] cacheNodes = cached.mNodes;
                                                        for (int i = 0; i < cacheObjsLen; i++) {
                                                            Node original = cacheNodes[i];
                                                            if ((original.mFlags & Node.DO_NOT_REUSE) != 0) {
                                                                Class<?> factoryClazz = original.mObject
                                                                        .getClass();
                                                                Constructor<?> constructor = factoryClazz
                                                                        .getDeclaredConstructor();
                                                                constructor.setAccessible(true);

                                                                /*
                                                                 * Include
                                                                 * object.
                                                                 */
                                                                Object obj = constructor
                                                                        .newInstance();
                                                                Node product = new Node(obj);

                                                                /*
                                                                 * Inherit from
                                                                 * original
                                                                 */
                                                                product.mContext = original.mContext;
                                                                product.mTags = original.mTags;
                                                                product.mTagsLen = original.mTagsLen;
                                                                product.mFlags |= original.mFlags;

                                                                /*
                                                                 * Append to end
                                                                 * of node list.
                                                                 */
                                                                final Node last = lastNode;
                                                                product.mPrev = last;
                                                                product.mNext = null;
                                                                last.mNext = product;
                                                                lastNode = product;
                                                                nodesLen++;

                                                                /*
                                                                 * Alter
                                                                 * reference
                                                                 * list copy.
                                                                 */
                                                                unique[i] = obj;
                                                            }
                                                        }

                                                        /* Mark for sorting */
                                                        if (sort) {
                                                            toBeSorted.add(unique);
                                                        }

                                                        /*
                                                         * Inject the unique
                                                         * reference list.
                                                         */
                                                        field.set(instance, unique);

                                                        /*
                                                         * Add dependencies to
                                                         * graph.
                                                         */
                                                        if (vital) {
                                                            node.addChildren(uniqueChildren,
                                                                    cacheObjsLen);
                                                        }
                                                    } catch (InstantiationException e) {
                                                        internalError(e);
                                                    } catch (IllegalAccessException e) {
                                                        internalError(e);
                                                    } catch (SecurityException e) {
                                                        internalError(e);
                                                    } catch (NoSuchMethodException e) {
                                                        internalError(e);
                                                    } catch (InvocationTargetException e) {
                                                        throw new GluingException(
                                                                "Uncaught exception during object creation",
                                                                e.getCause());
                                                    }
                                                }
                                            }
                                        } else {
                                            final int fieldDataLength = cached.mObjects.length;
                                            if (fieldDataLength != 1) {
                                                throw new GluingException(
                                                        "Several objects available for "
                                                                + instance.getClass()
                                                                        .getCanonicalName() + "."
                                                                + field.getName());
                                            }
                                            field.set(instance, cached.mObjects[0]);

                                            /* Add dependency to graph */
                                            if (vital) {
                                                node.addChild(cached.mNodes[0]);
                                            }
                                        }
                                    } else if (vital) {
                                        throw new GluingException("Nothing to inject into "
                                                + instance.getClass().getCanonicalName() + "."
                                                + field.getName());
                                    } else {
                                        field.set(instance, null);
                                    }
                                } catch (IllegalArgumentException e) {
                                    internalError(e);
                                } catch (IllegalAccessException e) {
                                    internalError(e);
                                }
                            }
                        }

                        /* Walk up hierarchy */
                        current = current.getSuperclass();
                    }
                }

                /* Iterate over all objects */
                node = node.mNext;
            }

            /* Calculate dependency sequence */
            Node[] path = new Node[nodesLen];
            Object[] depSeq = new Object[nodesLen];
            int idx = 0;
            node = firstNode;
            while (node != null) {
                if ((node.mFlags & (Node.WILL_BE_SORTED | Node.MERGED | Node.VISITED)) == Node.WILL_BE_SORTED) {
                    int pathLen = 0;
                    start: while (true) {
                        if ((node.mFlags & Node.MERGED) == 0) {
                            path[pathLen] = node;
                            node.mFlags |= Node.VISITED;
                            for (int j = node.mLastVisit; j < node.mChildrenLen; j++) {

                                /* Check if child is already in path */
                                /*
                                 * NOTE: When this part of the graph has been
                                 * detected as a cycle, we simply merge all the
                                 * cyclic nodes into one single node. This new
                                 * node contains references to all instances in
                                 * the previous nodes, and all of their child
                                 * references. We do not bother to remove child
                                 * references to nodes that are removed since
                                 * those will be skipped in all further
                                 * processing anyway.
                                 */
                                Node child = node.mChildren[j];
                                if (child != node && (child.mFlags & Node.MERGED) == 0) {
                                    for (int k = 0; k < pathLen; k++) {
                                        if (path[k] == child) {
                                            /*
                                             * Merge all cyclic nodes with this
                                             * node.
                                             */
                                            for (int l = k; l < pathLen; l++) {
                                                Node segment = path[l];
                                                if ((segment.mFlags & Node.MERGED) == 0) {
                                                    segment.mFlags = (segment.mFlags & ~Node.MERGED_HEAD)
                                                            | Node.MERGED;
                                                    node.mFlags |= Node.MERGED_HEAD;
                                                    Node next = node.mNextMerged;
                                                    if (next != null) {
                                                        segment.mNextMerged = next;
                                                    }
                                                    node.mNextMerged = segment;
                                                    node.addChildren(segment.mChildren,
                                                            segment.mChildrenLen);
                                                }
                                            }
                                            child = null;
                                            break;
                                        }
                                    }

                                    /* Visit child when not cyclic */
                                    if (child != null && (child.mFlags & Node.VISITED) == 0) {
                                        node.mLastVisit = j + 1;
                                        pathLen++;
                                        node = child;
                                        continue start;
                                    }
                                }
                            }
                        }

                        /* Reset first visiting index */
                        node.mLastVisit = 0;

                        /* Add to dependency sequence */
                        /*
                         * NOTE: Nodes with several instances starts those
                         * instances in random order, since they have circular
                         * dependencies and it is impossible to start them in a
                         * "correct" order.
                         */
                        if ((node.mFlags & Node.MERGED) == 0) {
                            if ((node.mFlags & Node.MERGED_HEAD) != 0) {
                                Node n = node;
                                while (n != null) {
                                    depSeq[idx++] = n.mObject;
                                    n = n.mNextMerged;
                                }
                            } else {
                                depSeq[idx++] = node.mObject;
                            }
                        }

                        /* Pop stack when not empty and resume visit of children */
                        if (pathLen > 0) {
                            pathLen--;
                            node = path[pathLen];
                            continue start;
                        } else {
                            break;
                        }
                    }
                }
                node = node.mNext;
            }

            /* Sort all entries in injected arrays by dependency order */
            if (implCache != null) {
                for (Object[] injected : toBeSorted) {
                    /*
                     * NOTE: The array is sorted by iterating the calculated
                     * dependency sequence, copying only entries also present in
                     * the injected array to a temporary array.
                     */
                    idx = 0;
                    int injectedLen = injected.length;
                    Object sorted[] = new Object[injectedLen];
                    for (int i = 0; i < depSeq.length; i++) {
                        for (int j = 0; j < injectedLen; j++) {
                            if (injected[j] == depSeq[i]) {
                                sorted[idx++] = injected[j];
                                break;
                            }
                        }
                    }

                    /*
                     * NOTE: The sorted array is copied back to the injected
                     * array to automatically apply the changes across all
                     * affected objects (since they already reference that
                     * array).
                     */
                    System.arraycopy(sorted, 0, injected, 0, sorted.length);
                }
            }

            /* Forget factory products */
            /*
             * NOTE: Since the factories have references to their product, they
             * will not be lost.
             */
            mLastNode.mNext = null;
        }

        /* Gluing completed */
        mRobust = true;
        mProcessed = true;
    }

    /**
     * Creates a short text describing the current status.
     * 
     * @return A single line status text.
     * @see #toString()
     * @see #toDotString()
     */
    public final String toShortString() {
        Factory factory = mFactories;
        int numProducers = 0;
        while (factory != null) {
            if (factory.mStatus == Factory.EXECUTED) {
                numProducers++;
            }
            factory = factory.mNext;
        }
        return "DuctTape[robust=" + mRobust + ", objects=" + mNumNodes + " + " + numProducers
                + " product" + (numProducers == 1 ? "" : "s") + ", factories=" + mNumFactories
                + "]";
    }

    /**
     * Creates a graph representation of the current object relations. The graph
     * is described using the <a
     * href="http://en.wikipedia.org/wiki/DOT_language">DOT language</a>.
     * <p>
     * Note that the output will always reflect the current contents of the
     * object fields and does not necessarily correspond to a robust object mesh
     * (is case the mesh is not robust because of operations on the
     * {@link DuctTape} instance this fact is added as a warning message to the
     * graph).
     * <p>
     * In case the output is to be printed somewhere where newlines are not
     * allowed or appropriate you can use the following snippet to get the
     * output as a single compact line:
     * 
     * <pre>
     * String compact = dt.toDot.replaceAll(&quot;\t|\n| &quot;, &quot;&quot;);
     * </pre>
     * 
     * @return An indented multi-line representation of the current object
     *         relations as a graph.
     * @see #toShortString()
     * @see #toString()
     */
    public final String toDotString() {
        StringBuilder dump = new StringBuilder("digraph {");

        /* Set up graphical look and feel */
        dump.append("\n\tnode [shape=component, fontsize=15, "
                + "fillcolor=\"#fff080\", style=filled, fontname=\"sans\"];\n"
                + "\tedge [fontsize=10, fontname=\"sans italic\", "
                + "color=\"#008000\", arrowhead=vee, arrowsize=0.5];\n");
        if (!mRobust) {
            dump.append("\tgraph [fontsize=10, fontname=\"sans bold\", "
                    + "fontcolor=red, label=\"Warning! The object mesh is not robust!\"];\n");
        }

        /* Visit all objects */
        String prefix = null;
        int prefixEnd = 0;
        ArrayList<Object> all = new ArrayList<Object>();
        Node node = mFirstNode;
        while (node != null) {
            all.add(node.mObject);
            node = node.mNext;
        }
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < all.size(); i++) {
                final Object obj = all.get(i);
                Class<?> clazz = obj.getClass();
                if (pass == 0) {
                    prefix = commonPrefix(prefix, obj.getClass().getCanonicalName());
                }

                /* Visit injected fields */
                if (obj instanceof Gluey) {
                    if (pass == 1 && i >= mNumNodes) {
                        dump.append("\t\"");
                        dump.append(simpleName(prefixEnd, obj, all).replace("\"", "\\\""));
                        dump.append("\" [fillcolor=\"#e6f0c2\"];\n");
                    }
                    while (Gluey.class.isAssignableFrom(clazz)) {
                        final Field[] fields = clazz.getDeclaredFields();
                        final int fieldsLen = fields.length;
                        if (fieldsLen != 0) {
                            for (int k = 0; k < fieldsLen; k++) {
                                Field field = fields[k];
                                Glue injection = getInjection(field);
                                if (injection != null) {
                                    try {
                                        field.setAccessible(true);
                                        Object fieldVal = field.get(obj);
                                        if (fieldVal != null) {
                                            Object refs[];
                                            Class<?> fieldClazz = field.getType();
                                            if (fieldClazz.isArray()) {
                                                fieldClazz = fieldClazz.getComponentType();
                                                if (fieldClazz.isPrimitive()) {
                                                    /*
                                                     * Find out if it is a
                                                     * temporary array.
                                                     */
                                                    Node o = mFirstNode;
                                                    while (o != null && o.mObject != fieldVal) {
                                                        o = o.mNext;
                                                    }
                                                    if (o == null) {
                                                        final int arrLen = Array
                                                                .getLength(fieldVal);
                                                        refs = new Object[arrLen];
                                                        for (int j = 0; j < arrLen; j++) {
                                                            Array.set(refs, j,
                                                                    Array.get(fieldVal, j));
                                                        }
                                                    } else {
                                                        refs = new Object[] {
                                                            fieldVal
                                                        };
                                                    }
                                                } else {
                                                    refs = (Object[])fieldVal;
                                                }
                                            } else {
                                                refs = new Object[] {
                                                    fieldVal
                                                };
                                            }
                                            final int[] tags = injection.value();
                                            final int tagsLen = tags.length;
                                            boolean optional = false;
                                            boolean sorted = false;
                                            for (int m = 0; m < tagsLen; m++) {
                                                if (tags[m] == OPTIONAL) {
                                                    optional = true;
                                                }
                                                if (tags[m] == ORDERED) {
                                                    sorted = true;
                                                }
                                            }
                                            final int refLen = refs.length;
                                            for (int l = 0; l < refLen; l++) {
                                                Object ref = refs[l];
                                                if (pass == 0) {
                                                    prefix = commonPrefix(prefix, ref.getClass()
                                                            .getCanonicalName());
                                                    prefix = commonPrefix(prefix,
                                                            fieldClazz.getCanonicalName());
                                                    if (!all.contains(ref)) {
                                                        all.add(ref);
                                                    }
                                                } else {
                                                    dump.append("\t\"");
                                                    String name = simpleName(prefixEnd, obj, all)
                                                            .replace("\"", "\\\"");
                                                    dump.append(name);
                                                    dump.append("\" -> \"");
                                                    dump.append(simpleName(prefixEnd, ref, all)
                                                            .replace("\"", "\\\""));
                                                    dump.append("\"");
                                                    final String label;
                                                    if (!isPrimitive(fieldClazz)
                                                            && fieldClazz != String.class) {
                                                        label = simpleName(prefixEnd, fieldClazz);
                                                    } else {
                                                        label = field.getName();
                                                    }
                                                    dump.append(" [label=\"");
                                                    dump.append(label);
                                                    if (sorted && refLen > 1) {
                                                        dump.append(" (");
                                                        dump.append(l + 1);
                                                        dump.append(")");
                                                    }
                                                    dump.append("\"");
                                                    if (optional) {
                                                        dump.append(", style=dashed, color=\"#000080\"");
                                                    }
                                                    dump.append("];\n");
                                                }
                                            }
                                        }
                                    } catch (IllegalAccessException e) {
                                        /* Ignore */
                                    }
                                }
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                }
            }

            /* Store package prefix */
            if (pass == 0 && prefix != null) {
                prefixEnd = prefix.lastIndexOf('.') + 1;
            }
        }

        /* Change appearance of objects that are not injection targets */
        for (Object obj : all) {
            if (!(obj instanceof Gluey)) {
                Class<?> clazz = obj.getClass();
                dump.append("\t\"");
                dump.append(simpleName(prefixEnd, obj, all).replace("\"", "\\\""));
                dump.append("\" [");
                if (clazz == Integer.class || clazz == Boolean.class || clazz == Float.class
                        || clazz == Double.class || clazz == Long.class || clazz == Character.class
                        || clazz == Byte.class || clazz == Short.class || clazz == String.class
                        || isPrimitive(clazz)) {
                    dump.append("fillcolor=white, shape=ellipse");
                } else {
                    dump.append("fillcolor=\"#c2e6f0\"");
                }
                dump.append("];\n");
            }
        }

        /* End of graph */
        dump.append("}");
        return dump.toString();
    }

    /**
     * Creates a plain-text representation of the current object relations. The
     * be somewhat compact, and easy to navigate and interpret, the output
     * assumes that the reader is somewhat familiar with the code from which it
     * originates (since it omits details about the injections that can easily
     * be seen in the code).
     * <p>
     * Note that the output will always reflect the current contents of the
     * object fields and does not necessarily correspond to a robust object mesh
     * (is case the mesh is not robust because of operations on the
     * {@link DuctTape} instance this fact is indicated in the beginning of the
     * output).
     * <p>
     * In case the output is to be printed somewhere where newlines are not
     * allowed or appropriate you can use the following snippet to get the
     * output as a single compact line:
     * 
     * <pre>
     * String compact = dt.toString().replaceAll(&quot;\t|\n| &quot;, &quot;&quot;);
     * </pre>
     * 
     * @return An indented multi-line representation of the current object
     *         relations as plain-text.
     * @see #toShortString()
     * @see #toDotString()
     */
    @Override
    public final String toString() {
        StringBuilder dump = new StringBuilder("DuctTape@");
        dump.append(Integer.toHexString(hashCode()));
        dump.append(" [\n\trobust = ");
        dump.append(mRobust);
        dump.append("\n");

        String prefix = null;
        int prefixEnd = 0;
        ArrayList<Object> all = new ArrayList<Object>();
        Node node = mFirstNode;
        while (node != null) {
            all.add(node.mObject);
            node = node.mNext;
        }
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < all.size(); i++) {
                final Object obj = all.get(i);
                Class<?> clazz = obj.getClass();
                if (pass == 0) {
                    prefix = commonPrefix(prefix, obj.getClass().getCanonicalName());
                }

                /* Visit injected fields */
                if (obj instanceof Gluey) {
                    if (pass == 1) {
                        dump.append("\t");
                        dump.append(simpleName(prefixEnd, obj, all));
                        if (i >= mNumNodes) {
                            dump.append(" (product)");
                        }
                        dump.append(" [\n");
                    }
                    while (Gluey.class.isAssignableFrom(clazz)) {
                        final Field[] fields = clazz.getDeclaredFields();
                        final int fieldsLen = fields.length;
                        if (fieldsLen != 0) {
                            for (int k = 0; k < fieldsLen; k++) {
                                Field field = fields[k];
                                Glue injection = getInjection(field);
                                if (injection != null) {
                                    try {
                                        field.setAccessible(true);
                                        Object fieldVal = field.get(obj);
                                        if (fieldVal != null) {
                                            Object refs[];
                                            Class<?> fieldClazz = field.getType();
                                            boolean array = fieldClazz.isArray();
                                            if (array
                                                    && !fieldClazz.getComponentType().isPrimitive()) {
                                                refs = (Object[])fieldVal;
                                            } else {
                                                array = false;
                                                refs = new Object[] {
                                                    fieldVal
                                                };
                                            }
                                            final int refLen = refs.length;
                                            if (pass == 1) {
                                                dump.append("\t\t");
                                                dump.append(field.getName());
                                                dump.append(" = ");
                                                if (array) {
                                                    dump.append("[");
                                                }
                                            }
                                            for (int l = 0; l < refLen; l++) {
                                                Object ref = refs[l];
                                                if (pass == 0) {
                                                    prefix = commonPrefix(prefix, ref.getClass()
                                                            .getCanonicalName());
                                                    prefix = commonPrefix(
                                                            prefix,
                                                            clazz.getCanonicalName() + "."
                                                                    + field.getName());
                                                    if (!all.contains(ref)) {
                                                        all.add(ref);
                                                    }
                                                } else {
                                                    if (l != 0) {
                                                        dump.append(", ");
                                                    }
                                                    dump.append(simpleName(prefixEnd, ref, all));
                                                }
                                            }
                                            if (pass == 1) {
                                                if (array) {
                                                    dump.append("]");
                                                }
                                                dump.append("\n");
                                            }
                                        }
                                    } catch (IllegalAccessException e) {
                                        /* Ignore */
                                    }
                                }
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                    if (pass == 1) {
                        dump.append("\t]\n");
                    }
                } else {
                    if (pass == 1) {
                        dump.append("\t");
                        dump.append(simpleName(prefixEnd, obj, all));
                        dump.append(" (untouched");
                        if (i >= mNumNodes) {
                            dump.append(" product");
                        }
                        dump.append(")\n");
                    }
                }
            }

            /* Store package prefix */
            if (pass == 0 && prefix != null) {
                prefixEnd = prefix.lastIndexOf('.') + 1;
                dump.append("\tpackage = ");
                dump.append(prefix.substring(0, prefixEnd - 1));
                dump.append("\n");
            }
        }
        dump.append("]");
        return dump.toString();
    }

}
