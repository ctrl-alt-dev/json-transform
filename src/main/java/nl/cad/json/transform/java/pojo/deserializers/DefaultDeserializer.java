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
package nl.cad.json.transform.java.pojo.deserializers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.cad.json.transform.java.pojo.FromDocumentMapper;

public class DefaultDeserializer implements Deserializer {

    public static final class UnsupportedCollectionTypeException extends RuntimeException {
        public UnsupportedCollectionTypeException(Class<?> type) {
            super(type.getName());
        }
    }
    
    public static final class DocumentMappingException extends RuntimeException {
        public DocumentMappingException(Throwable ex) {
            super(ex);
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object toObject(FromDocumentMapper mapper, Class<?> type, Class<?> genericType, Object document) {
        if (document == null) {
            return null;
        } else if (document instanceof Map) {
            return handleObject(mapper, type, (Map) document);
        } else if (document instanceof List) {
            return handleList(mapper, type, genericType, (List) document);
        } else {
            return document;
        }
    }

    private Object handleList(FromDocumentMapper mapper, Class<?> type, Class<?> genericType, List<Object> document) {
        try {
            if (type.isArray()) {
                Object[] result = (Object[]) Array.newInstance(type.getComponentType(), document.size());
                for (int t = 0; t < document.size(); t++) {
                    result[t] = mapper.toJava(type.getComponentType(), null, document.get(t));
                }
                return result;
            } else {
                Collection<Object> result = createCollectionInstance(type);
                for (Object o : document) {
                    result.add(mapper.toJava(genericType, null, o));
                }
                return result;
            }
        } catch (ReflectiveOperationException ex) {
            throw new DocumentMappingException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> createCollectionInstance(Class<?> type) throws InstantiationException, IllegalAccessException {
        Collection<Object> result = null;
        if (type.isInterface()) {
            if (List.class.isAssignableFrom(type)) {
                result = new ArrayList<Object>();
            } else if (Set.class.isAssignableFrom(type)) {
                result = new HashSet<Object>();
            } else {
                throw new UnsupportedCollectionTypeException(type);
            }
        } else {
            result = (Collection<Object>) type.newInstance();
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object handleObject(FromDocumentMapper mapper, Class<?> type, Map document) {
        try {
            Object target = type.newInstance();
            handleObject(mapper, type, (Map<String, Object>) document, target);
            return target;
        } catch (ReflectiveOperationException ex) {
            throw new DocumentMappingException(ex);
        }
    }

    private void handleObject(FromDocumentMapper mapper, Class<?> type, Map<String, Object> document, Object target) throws ReflectiveOperationException {
        if (!type.getSuperclass().equals(Object.class)) {
            handleObject(mapper, type.getSuperclass(), document, target);
        }
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            Field field = type.getDeclaredField(entry.getKey());
            Class<?> genericType = getGenericType(field.getGenericType());
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, mapper.toJava(field.getType(), genericType, entry.getValue()));
        }
    }

    private Class<?> getGenericType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return null;
    }

}
