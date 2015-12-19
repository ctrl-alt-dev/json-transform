package nl.cad.json.transform.java.serializers;

import java.util.List;

import nl.cad.json.transform.java.PojoMapper;

public interface Serializer {

    boolean supports(Object value);

    Object toDocument(PojoMapper mapper, List<Object> stack, Object java);

}
