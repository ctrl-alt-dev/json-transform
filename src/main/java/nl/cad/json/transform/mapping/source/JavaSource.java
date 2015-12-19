package nl.cad.json.transform.mapping.source;

import nl.cad.json.transform.java.JavaToDocumentMapper;
import nl.cad.json.transform.java.PojoToDocumentMapper;

public class JavaSource implements DocumentSource {

    private final Object input;
    private JavaToDocumentMapper mapper;

    public JavaSource(Object input) {
        this(new PojoToDocumentMapper(), input);
    }

    public JavaSource(JavaToDocumentMapper mapper, Object input) {
        this.mapper = mapper;
        this.input = input;
    }

    @Override
    public Object getDocument(DocumentSource src) {
        return mapper.toDocument(this.input);
    }

}
