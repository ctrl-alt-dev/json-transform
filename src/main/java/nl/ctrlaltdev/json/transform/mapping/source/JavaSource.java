package nl.ctrlaltdev.json.transform.mapping.source;

import nl.ctrlaltdev.json.transform.java.JavaToDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.PojoToDocumentMapper;

/**
 * Allows the use of a Java Object as a document source.
 */
public class JavaSource implements DocumentSource {

    private final Object input;
    private JavaToDocumentMapper mapper;

    /**
     * initializes this JavaSource with the given input object.
     * This will be converted into a document when asked for by the {@link PojoToDocumentMapper}.
     * @param input the input object
     */
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
