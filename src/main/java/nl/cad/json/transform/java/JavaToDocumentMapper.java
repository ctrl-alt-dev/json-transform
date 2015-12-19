package nl.cad.json.transform.java;

/**
 * Transforms a Java object to a Map / List / Value document structure.
 */
public interface JavaToDocumentMapper {

    Object toDocument(Object java);

}
