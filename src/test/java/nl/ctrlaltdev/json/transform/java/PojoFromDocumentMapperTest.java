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
package nl.ctrlaltdev.json.transform.java;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import nl.ctrlaltdev.json.transform.JsonTransform;
import nl.ctrlaltdev.json.transform.java.pojo.PojoFromDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.PojoToDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.DefaultDeserializer;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.DefaultDeserializer.DocumentMappingException;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.DefaultDeserializer.UnsupportedCollectionTypeException;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.PropertyTypeSolver;
import nl.ctrlaltdev.json.transform.java.pojo.deserializers.value.DateDeserializer;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

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

    public static class SubclassPojo extends SomePojo {
        private String c = "c";

        @Override
        public String toString() {
            return super.toString() + "|" + c;
        }
    }

    public static class UnknownCollection {
        @SuppressWarnings("unused")
        private Map<String, String> map;
    }

    public static class ValueTypes {
        private Integer a;
        private Long b;
        private Double c;
        private Boolean d;

        private int e;
        private long f;
        private double g;
        private boolean h;
    }

    public static class DateValueTypes {
        private Date date;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        private ZonedDateTime zonedDateTime;
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

    @Test(expected = DocumentMappingException.class)
    public void shouldFailUnknownPropertyInStrictMode() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("a", "test");
        obj.put("unknown", "test2");

        SomePojo pojo = JsonTransform.toJavaStrict(SomePojo.class, obj);

        assertEquals("test", pojo.a);
        assertEquals("test2", pojo.b);
    }

    @Test
    public void shouldNotFailUnknownPropertyInRelaxedMode() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("a", "test");
        obj.put("unknown", "test2");

        SomePojo pojo = JsonTransform.toJava(SomePojo.class, obj);

        assertEquals("test", pojo.a);
        assertEquals("b", pojo.b);
    }

    @Test
    public void shouldMapViaTypeSolver() {
        PropertyTypeSolver typeSolver = new PropertyTypeSolver();
        typeSolver.mapType(SomePojo.class, SubclassPojo.class, "type", "a");
        typeSolver.mapType(SomePojo.class, SomePojo.class, "type", "b");
        mapper = new PojoFromDocumentMapper(new DefaultDeserializer(true, typeSolver));

        SomePojo java = mapper.toJava(SomePojo.class, JsonTransform.parse("{ \"type\":\"a\", \"a\":\"z\", \"c\": \"y\" }"));
        assertTrue(java instanceof SubclassPojo);
        assertEquals("z", java.a);
        assertEquals("y", ((SubclassPojo) java).c);
    }

    @Test
    public void shouldMapStringValueToValueType() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("a", "1");
        obj.put("b", "2");
        obj.put("c", "4.2");
        obj.put("d", "true");

        obj.put("e", "1");
        obj.put("f", "2");
        obj.put("g", "4.2");
        obj.put("h", "true");

        mapper = new PojoFromDocumentMapper(new DefaultDeserializer(true));
        ValueTypes pojo = mapper.toJava(ValueTypes.class, obj);

        assertEquals(Integer.valueOf(1), pojo.a);
        assertEquals(Long.valueOf(2), pojo.b);
        assertEquals(Double.valueOf(4.2), pojo.c, 0.01);
        assertEquals(Boolean.TRUE, pojo.d);

        assertEquals(1, pojo.e);
        assertEquals(2L, pojo.f);
        assertEquals(4.2, pojo.g, 0.01);
        assertEquals(true, pojo.h);
    }

    @Test
    public void shouldMapStringValueToDateType() {
        Map<String, Object> obj = NodeUtils.newObject();
        obj.put("date", "200101010000Z");
        obj.put("localDate", "200101020000Z");
        obj.put("localDateTime", "200101030000Z");
        obj.put("zonedDateTime", "200101040000Z");

        mapper = new PojoFromDocumentMapper(new DefaultDeserializer(true, new DateDeserializer("yyyyMMddHHmmX")));
        DateValueTypes pojo = mapper.toJava(DateValueTypes.class, obj);

        assertEquals(new Date(101, 0, 1), pojo.date);
        assertEquals(LocalDate.of(2001, 1, 2), pojo.localDate);
        assertEquals(LocalDateTime.of(2001, 1, 3, 0, 0, 0), pojo.localDateTime);
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2001, 1, 4, 0, 0, 0), ZoneId.of("Z")), pojo.zonedDateTime);
    }

}
