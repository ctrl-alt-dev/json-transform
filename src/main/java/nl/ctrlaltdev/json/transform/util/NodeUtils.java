/*
 * Copyright 2015 E.Hooijmeijer / www.ctrl-alt-dev.nl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.ctrlaltdev.json.transform.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class NodeUtils {

    private NodeUtils() {
        // Util
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        if (obj instanceof List) {
            return ((List) obj).isEmpty();
        }
        return false;
    }

    public static boolean isArray(Object obj) {
        return obj instanceof List;
    }

    public static boolean isObject(Object obj) {
        return obj instanceof Map;
    }

    public static boolean isValue(Object obj) {
        return !isArray(obj) && !isObject(obj) && !isNull(obj);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toObject(Object obj) {
        if (isObject(obj)) {
            return (Map<String, Object>) obj;
        }
        throw new IllegalArgumentException(obj + " is not an object.");
    }

    @SuppressWarnings("unchecked")
    public static List<Object> toArray(Object obj) {
        if (isArray(obj)) {
            return (List<Object>) obj;
        }
        throw new IllegalArgumentException(obj + " is not an array.");
    }

    public static Map<String, Object> newObject() {
        return new TreeMap<String, Object>();
    }

    public static List<Object> newArray() {
        return new ArrayList<Object>();
    }

    public static Object newInstanceOf(Object source) {
        if (isArray(source)) {
            return newArray();
        } else if (isObject(source)) {
            return newObject();
        } else {
            return source;
        }
    }

    public static boolean isAllValues(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isValue(o) && !NodeUtils.isNull(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllNull(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isNull(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllArray(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isArray(o)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllObjects(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isObject(o)) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, Object> shallowCopy(Map<String, Object> object) {
        return new TreeMap<String, Object>(object);
    }

}
