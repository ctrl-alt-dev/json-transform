package nl.cad.json.transform.java.serializers;

import java.util.List;

import nl.cad.json.transform.java.PojoMapper;

public class ValueSerializer implements Serializer {
    
    @Override
    public boolean supports(Object value) {
        return value instanceof Number ||
                value instanceof Boolean ||
                value instanceof String ||
                value instanceof Character;
    }
    
    @Override
    public Object toDocument(PojoMapper mapper, List<Object> stack, Object value) {
        return value;
    }

}
