package nl.cad.json.transform.java;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.java.pojo.PojoToDocumentMapper;
import nl.cad.json.transform.java.pojo.PojoToDocumentMapper.CircularReferenceException;
import nl.cad.json.transform.java.pojo.PojoToDocumentMapper.NoSerializerFoundException;
import nl.cad.json.transform.java.pojo.serializers.ValueSerializer;

import org.junit.Before;
import org.junit.Test;

public class PojoToDocumentMapperTest {

    @SuppressWarnings("unused")
    public static class SomeType {
        private static String ignored="ignored"; 
        private transient String ignoredToo = "ignored";
        private String value = "value";
    }
    
    @SuppressWarnings("unused")
    public static class AnotherType extends SomeType {
        private String anotherValue = "value";
        private int primitive = 1;
    }
    
    public static class RecursiveType extends SomeType {
        private RecursiveType parent;
    }

    private PojoToDocumentMapper mapper;

    @Before
    public void init() {
        mapper = new PojoToDocumentMapper();
    }

    @Test
    public void shouldMapValues() {
        assertEquals(Short.valueOf((short) 1), mapper.toDocument(Short.valueOf((short) 1)));
        assertEquals(Integer.valueOf(1), mapper.toDocument(Integer.valueOf(1)));
        assertEquals(Long.valueOf(1), mapper.toDocument(Long.valueOf(1)));
        assertEquals(Double.valueOf(4.2), mapper.toDocument(Double.valueOf(4.2)));
        assertEquals(Character.valueOf('c'), mapper.toDocument(Character.valueOf('c')));
        assertEquals("42", mapper.toDocument("42"));
        assertEquals(Boolean.valueOf(true), mapper.toDocument(Boolean.valueOf(true)));
    }

    @Test
    public void shouldMapCollectionsAsLists() {
        List<String> strings = Arrays.asList(new String[] { "a", "b", "c" });
        assertEquals("[a, b, c]", String.valueOf(mapper.toDocument(strings)));
    }

    @Test
    public void shouldArraysCollectionsAsLists() {
        assertEquals("[a, b, c]", String.valueOf(mapper.toDocument(new String[] { "a", "b", "c" })));
    }

    @Test
    public void shouldMapObjectsAsMaps() {
        assertEquals("{value=value}", String.valueOf(mapper.toDocument(new SomeType())));
    }

    @Test
    public void shouldMapInheritance() {
        assertEquals("{anotherValue=value, primitive=1, value=value}", String.valueOf(mapper.toDocument(new AnotherType())));
    }
    
    @Test
    public void shouldMapMapAsKeyValueList() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(new SomeType(), "pindakaas");
        assertEquals("[{key={value=value}, value=pindakaas}]", String.valueOf(mapper.toDocument(map)));
    }

    @Test(expected = NoSerializerFoundException.class)
    public void shouldFailSerialization() {
        new PojoToDocumentMapper(new ValueSerializer()).toDocument(null);
    }

    @Test(expected = CircularReferenceException.class)
    public void shouldFailRecursive() {
        RecursiveType rec = new RecursiveType();
        rec.parent = rec; // oops.
        mapper.toDocument(rec);
    }

    @Test
    public void shouldPassComposite() {
        RecursiveType rec = new RecursiveType();
        rec.parent = new RecursiveType();
        rec.parent.parent = new RecursiveType();
        assertEquals("{parent={parent={parent=null, value=value}, value=value}, value=value}", String.valueOf(mapper.toDocument(rec)));
    }

}
