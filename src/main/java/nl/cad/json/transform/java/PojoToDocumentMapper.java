package nl.cad.json.transform.java;

import java.util.ArrayList;
import java.util.List;

import nl.cad.json.transform.java.serializers.DefaultSerializer;
import nl.cad.json.transform.java.serializers.Serializer;
import nl.cad.json.transform.java.serializers.ValueSerializer;

public class PojoToDocumentMapper implements JavaToDocumentMapper, PojoMapper {

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
