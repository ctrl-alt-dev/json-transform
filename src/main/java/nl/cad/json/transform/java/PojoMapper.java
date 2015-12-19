package nl.cad.json.transform.java;

import java.util.List;

public interface PojoMapper {

    Object toDocument(List<Object> stack, Object java);

}
