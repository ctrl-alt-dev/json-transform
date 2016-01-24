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
package nl.ctrlaltdev.json.transform.java.pojo.deserializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PropertyTypeSolver implements TypeSolver {

    private static class PropertyTypeSolverException extends RuntimeException {
        public PropertyTypeSolverException(String msg) {
            super(msg);
        }
    }
    
    private static class PropertyTypeMapping {
        private String property;
        private Object value;
        private Class<?> type;

        public PropertyTypeMapping(String property, Object value, Class<?> type) {
            this.property = property;
            this.value = value;
            this.type = type;
        }

        public boolean isApplicable(Map<String, Object> object) {
            return value.equals(object.get(property));
        }

        public Class<?> mapType(Set<String> handled) {
            handled.add(property);
            return type;
        }
    }

    private Map<Class<?>, List<PropertyTypeMapping>> mapping;

    public PropertyTypeSolver() {
        this.mapping = new HashMap<Class<?>, List<PropertyTypeMapping>>();
    }


    @SuppressWarnings("unchecked")
    @Override
    public <A> Class<? extends A> solveType(Class<A> declared, Map<String, Object> object, Set<String> handled) {
        if (mapping.containsKey(declared)) {
            for (PropertyTypeMapping map : mapping.get(declared)) {
                if (map.isApplicable(object)) {
                    return (Class<? extends A>) map.mapType(handled);
                }
            }
            throw new PropertyTypeSolverException("Missing type mapping for " + declared.getName());
        }
        return declared;
    }

    public <A> PropertyTypeSolver mapType(Class<A> declared, Class<? extends A> actual, String property, Object value) {
        List<PropertyTypeMapping> typeMappings = mapping.get(declared);
        if (typeMappings == null) {
            typeMappings = new ArrayList<PropertyTypeSolver.PropertyTypeMapping>();
            mapping.put(declared, typeMappings);
        }
        typeMappings.add(new PropertyTypeMapping(property, value, actual));
        return this;
    }

}
