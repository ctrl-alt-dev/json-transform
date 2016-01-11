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

import nl.ctrlaltdev.json.transform.java.DocumentToJavaMapper;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.DefaultDeserializer;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.Deserializer;

public class PojoFromDocumentMapper implements DocumentToJavaMapper, FromDocumentMapper {

    public static class NoDeserializerFoundException extends RuntimeException {
        public NoDeserializerFoundException(Class<?> type) {
            super(type.getName());
        }
    }

    private final Deserializer[] deserializers;

    public PojoFromDocumentMapper() {
        this(new DefaultDeserializer());
    }

    public PojoFromDocumentMapper(Deserializer... deserializers) {
        this.deserializers = deserializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> A toJava(Class<A> type, Object document) {
        return (A) this.toJava(type, null, document);
    }
    
    @Override
    public Object toJava(Class<?> type, Class<?> genericType, Object document) {
        for (Deserializer d : deserializers) {
            if (d.supports(type)) {
                return d.toObject(this, type, genericType, document);
            }
        }
        throw new NoDeserializerFoundException(type);
    }

}
