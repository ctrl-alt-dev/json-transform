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
package nl.ctrlaltdev.json.transform.java.pojo.deserializers.value;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultValueDeserializer implements ValueDeserializer {

    private Set<Class> supported = new HashSet<>();

    private Map<Class, Class> primitives = new HashMap<>();

    public DefaultValueDeserializer() {
        supported.add(Byte.class);
        supported.add(Short.class);
        supported.add(Integer.class);
        supported.add(Long.class);
        supported.add(Double.class);
        supported.add(Float.class);
        supported.add(Boolean.class);
        primitives.put(Byte.TYPE, Byte.class);
        primitives.put(Short.TYPE, Short.class);
        primitives.put(Integer.TYPE, Integer.class);
        primitives.put(Long.TYPE, Long.class);
        primitives.put(Double.TYPE, Double.class);
        primitives.put(Float.TYPE, Float.class);
        primitives.put(Boolean.TYPE, Boolean.class);
    }

    @Override
    public boolean accept(Class<?> owner, Class<?> type, String property) {
        return supported.contains(type) || primitives.containsKey(type);
    }

    @Override
    public Object deserialize(Class<?> type, Object value) {
        try {
            if (type.isPrimitive()) {
                type = primitives.get(type);
            }
            return type.getConstructor(String.class).newInstance(new Object[] { String.valueOf(value) });
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new ValueDeserializationException(e.getMessage());
        }
    }
}
