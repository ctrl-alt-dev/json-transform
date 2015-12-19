package nl.cad.json.transform.java.serializers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.java.PojoMapper;
import nl.cad.json.transform.util.NodeUtils;

public class DefaultSerializer implements Serializer {

    public static final class PojoMappingException extends RuntimeException {
        public PojoMappingException(Throwable ex) {
            super(ex);
        }
    }

    @Override
    public boolean supports(Object value) {
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object toDocument(PojoMapper mapper, List<Object> stack, Object java) {
        if (java == null) {
            return null;
        } else if (java instanceof Map) {
            return handleMap(mapper, stack, (Map) java);
        } else if (java instanceof Collection) {
            return handleCollection(mapper, stack, (Collection) java);
        } else if (java.getClass().isArray()) {
            return handleArray(mapper, stack, (Object[]) java);
        } else if (java.getClass().isEnum()) {
            return handleEnum(java);
        } else {
            return handleObject(mapper, stack, java);
        }
    }

    private Object handleObject(PojoMapper mapper, List<Object> stack, Object java) {
        Map<String, Object> results = NodeUtils.newObject();
        handleObject(mapper, stack, java.getClass(), java, results);
        return results;
    }

    private void handleObject(PojoMapper mapper, List<Object> stack, Class<?> type, Object java, Map<String, Object> results) {
        try {
            if (!type.getSuperclass().equals(Object.class)) {
                handleObject(mapper, stack, type.getSuperclass(), java, results);
            }
            for (Field f : type.getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                Object value = f.get(java);
                results.put(f.getName(), mapper.toDocument(stack, value));
            }
        } catch (ReflectiveOperationException ex) {
            throw new PojoMappingException(ex);
        }
    }

    private Object handleEnum(Object java) {
        return String.valueOf(java);
    }

    private Object handleArray(PojoMapper mapper, List<Object> stack, Object[] java) {
        List<Object> results = NodeUtils.newArray();
        for (int t = 0; t < java.length; t++) {
            results.add(mapper.toDocument(stack, java[t]));
        }
        return results;
    }

    private Object handleCollection(PojoMapper mapper, List<Object> stack, Collection<Object> java) {
        List<Object> results = NodeUtils.newArray();
        for (Object o : java) {
            results.add(mapper.toDocument(stack, o));
        }
        return results;
    }

    private Object handleMap(PojoMapper mapper, List<Object> stack, Map<Object, Object> java) {
        List<Object> results = NodeUtils.newArray();
        for (Map.Entry<Object, Object> entry : java.entrySet()) {
            Map<String, Object> e = NodeUtils.newObject();
            e.put("key", mapper.toDocument(stack, entry.getKey()));
            e.put("value", mapper.toDocument(stack, entry.getValue()));
            results.add(e);
        }
        return results;
    }
}
