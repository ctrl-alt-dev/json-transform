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
package nl.cad.json.transform.java;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.cad.json.transform.java.pojo.PojoFromDocumentMapper;
import nl.cad.json.transform.java.pojo.PojoToDocumentMapper;
import nl.cad.json.transform.java.pojo.deserializers.DefaultDeserializer.UnsupportedCollectionTypeException;
import nl.cad.json.transform.util.NodeUtils;

import org.junit.Before;
import org.junit.Test;

public class PojoFromDocumentMapperTest {

    public static class SomePojo {
        private String a = "a";
        private String b = "b";

        @Override
        public String toString() {
            return a + "|" + b;
        }
    }
    
    public static class NestedObject {
        private SomePojo some;
        private List<SomePojo> pojos = new ArrayList<SomePojo>();
        private Set<NestedObject> nested = new HashSet<NestedObject>();

        @Override
        public String toString() {
            return some + "|" + pojos + "|" + nested;
        }
    }

    public static class UnknownCollection {
        @SuppressWarnings("unused")
        private Map<String, String> map;
    }

    private PojoFromDocumentMapper mapper;

    @Before
    public void init() {
        mapper = new PojoFromDocumentMapper();
    }

    @Test
    public void shouldMapObject() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("a", "test");
        obj.put("b", "test2");

        SomePojo pojo = mapper.toJava(SomePojo.class, obj);
        
        assertEquals("test", pojo.a);
        assertEquals("test2", pojo.b);
    }

    @Test
    public void shouldMapArray() {
        String[] array = new String[] { "a", "b", "c" };

        String[] result = mapper.toJava(String[].class, Arrays.asList(array));
        
        assertArrayEquals(array, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMapArrayList() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("a", "test");
        obj.put("b", "test2");
        List<SomePojo> result = (List<SomePojo>) mapper.toJava(List.class, SomePojo.class, Arrays.asList(obj));
        assertEquals("[test|test2]", result.toString());
    }

    @Test
    public void shouldMapNestedObject() {
        NestedObject nested = new NestedObject();
        nested.pojos.add(new SomePojo());
        nested.nested.add(new NestedObject());
        nested.some = new SomePojo();

        Object doc = new PojoToDocumentMapper().toDocument(nested);
        NestedObject result = mapper.toJava(NestedObject.class, doc);

        assertEquals("a|b|[a|b]|[null|[]|[]]", result.toString());
    }

    @Test(expected = UnsupportedCollectionTypeException.class)
    public void shouldFailUnknownCollection() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("map", NodeUtils.newArray());
        
        mapper.toJava(UnknownCollection.class, obj);
    }


}
