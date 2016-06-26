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

/**
 * Sometimes you need to convert a string to a java type (such as with dates) or an object into
 * a specific Java type that cannot be done using the default property mappings. This interface
 * allows you to write a custom mapping.
 */
public interface ValueDeserializer {

    /**
     * thrown if a value cannot be deserialized.
     */
    public static class ValueDeserializationException extends RuntimeException {
        public ValueDeserializationException(String msg) {
            super(msg);
        }
    }

    /**
     * @param owner the Java owner type.
     * @param type the Java property type.
     * @param property the name of the property.
     * @return true if it can be deserialized by this value deserializer.
     */
    boolean accept(Class<?> owner, Class<?> type, String property);

    /**
     * deserializes the object.
     * @param type the type to create.
     * @param value the value to deserialize from.
     * @return the deserialized object.
     */
    Object deserialize(Class<?> type, Object value);

}
