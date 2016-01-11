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
package nl.ctrlaltdev.json.transform.java.pojo;

import java.util.ArrayList;
import java.util.List;

import nl.ctrlaltdev.json.transform.java.JavaToDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.serializers.DefaultSerializer;
import nl.ctrlaltdev.json.transform.java.pojo.serializers.Serializer;
import nl.ctrlaltdev.json.transform.java.pojo.serializers.ValueSerializer;

public class PojoToDocumentMapper implements JavaToDocumentMapper, ToDocumentMapper {

    public static final class NoSerializerFoundException extends RuntimeException {
        public NoSerializerFoundException(Object object) {
            super(String.valueOf(object));
        }
    }

    public static final class CircularReferenceException extends RuntimeException {

    }

    private Serializer[] serializers;

    public PojoToDocumentMapper() {
        this(new ValueSerializer(), new DefaultSerializer());
    }

    public PojoToDocumentMapper(Serializer... serializers) {
        this.serializers = serializers;
    }

    @Override
    public Object toDocument(Object java) {
        return toDocument(new ArrayList<Object>(), java);
    }

    public Object toDocument(List<Object> stack, Object java) {
        for (Serializer ser : serializers) {
            if (ser.supports(java)) {
                if (stack.contains(java)) {
                    throw new CircularReferenceException();
                }
                stack.add(java);
                Object result = ser.toDocument(this, stack, java);
                stack.remove(stack.size() - 1);
                return result;
            }
        }
        throw new NoSerializerFoundException(java);
    }

}
