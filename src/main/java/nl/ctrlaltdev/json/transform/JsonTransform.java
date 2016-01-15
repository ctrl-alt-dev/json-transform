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
package nl.ctrlaltdev.json.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import nl.ctrlaltdev.json.transform.java.DocumentToJavaMapper;
import nl.ctrlaltdev.json.transform.java.JavaToDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.PojoFromDocumentMapper;
import nl.ctrlaltdev.json.transform.java.pojo.PojoToDocumentMapper;
import nl.ctrlaltdev.json.transform.mapping.builder.CompositeMappingBuilder;
import nl.ctrlaltdev.json.transform.mapping.builder.MappingBuilder;
import nl.ctrlaltdev.json.transform.mapping.builder.PropertyMappingBuilder;
import nl.ctrlaltdev.json.transform.mapping.builder.sub.SplitSourceBuilder;
import nl.ctrlaltdev.json.transform.mapping.source.DocumentSource;
import nl.ctrlaltdev.json.transform.mapping.source.MultiSource;
import nl.ctrlaltdev.json.transform.mapping.source.ValueSource;
import nl.ctrlaltdev.json.transform.merge.MergeStrategy;
import nl.ctrlaltdev.json.transform.parse.JsonParser;
import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.print.JsonPrinter;
import nl.ctrlaltdev.json.transform.select.Select;
import nl.ctrlaltdev.json.transform.select.SelectBuilder;
import nl.ctrlaltdev.json.transform.transforms.Transform;

/**
 * Facade for the most important API's.
 */
public class JsonTransform extends MappingBuilder {

    private static final DocumentToJavaMapper RELAXED_JAVA_MAPPER = new PojoFromDocumentMapper(false);

    private static final DocumentToJavaMapper STRICT_JAVA_MAPPER = new PojoFromDocumentMapper(true);

    private static final JavaToDocumentMapper DOCUMENT_MAPPER = new PojoToDocumentMapper();

    /**
     * @return the root path.
     */
    public static final Path rootPath() {
        return Path.root();
    }

    /**
     * constructs an absolute path using javascript notation ('some.property.array[42].value')
     * @param path the path.
     * @return the path.
     */
    public static final Path path(String path) {
        return Path.fromString(path);
    }

    /**
     * @return a select that selects the root of a document.
     */
    public static final Select selectRoot() {
        return SelectBuilder.selectRoot();
    }

    /**
     * @return the select builder to create a custom select.
     */
    public static final SelectBuilder select() {
        return SelectBuilder.select();
    }

    /**
     * creates a select from a JsonPath string.
     * @param jsonPath the jsonPath string.
     * @return the select.
     */
    public static final Select jsonPath(String jsonPath) {
        return SelectBuilder.fromJsonPath(jsonPath);
    }

    /**
     * builds a property mapping.
     * @return the builder.
     */
    public static final PropertyMappingBuilder mapProperties() {
        return PropertyMappingBuilder.map();
    }

    /**
     * construct a mapper that will apply multiple transform to same input,
     * merging the results using the provided merge strategy.
     * @param merge the merge strategy to use.
     * @param transforms the transforms to execute in parallel.
     * @return the builder.
     */
    public static final CompositeMappingBuilder parallel(MergeStrategy merge, Transform... transforms) {
        return CompositeMappingBuilder.parallel(merge, transforms);
    }

    /**
     * constructs a mapper that will apply transform sequentially to each other.
     * @param transforms the transforms to execute in sequence.
     * @return the builder.
     */
    public static final CompositeMappingBuilder sequence(Transform... transforms) {
        return CompositeMappingBuilder.sequence(transforms);
    }

    /**
     * joins multiple document sources into one.
     * @param sources the sources.
     * @return the joined document source.
     */
    public static final DocumentSource join(DocumentSource... sources) {
        return CompositeMappingBuilder.join(sources);
    }

    /**
     * splits a single document source into multiple named ones.
     * @param source the source to split.
     * @return the builder to add named selects.
     */
    public static final SplitSourceBuilder split(DocumentSource source) {
        return CompositeMappingBuilder.split(source);
    }

    /**
     * constructs a new single value document source.
     * @param value the document.
     * @return the source.
     */
    public static final DocumentSource valueSource(Object value) {
        return new ValueSource(value);
    }

    /**
     * constructs a new multi value document source.
     * @return the source.
     */
    public static final MultiSource multiSource() {
        return new MultiSource();
    }

    /**
     * parses a Json document into a Map/ArrayList/Value structure.
     * @param document the document.
     * @return the Map/ArrayList/Value structure.
     */
    public static final Object parse(String document) {
        return new JsonParser().parse(document);
    }

    /**
     * reads and parses a Json document into a Map/ArrayList/Value structure.
     * @param input the reader to read the json from.
     * @return the Map/ArrayList/Value structure.
     * @throws IOException when reading fails.
     */
    public static final Object parse(Reader input) throws IOException {
        return new JsonParser().parse(input);
    }

    /**
     * reads and parses a Json document into a Map/ArrayList/Value structure.
     * @param input the inputstream to read the json from (using UTF-8 encoding).
     * @return the Map/ArrayList/Value structure.
     * @throws IOException when reading fails.
     */
    public static final Object parse(InputStream input) throws IOException {
        return new JsonParser().parse(input);
    }

    /**
     * serializes a Map/ArrayList/Value structure to json without formatting.
     * @param obj the object to serialize.
     * @return the resulting string.
     */
    public static final String print(Object obj) {
        return new JsonPrinter().toString(obj);
    }

    /**
     * serializes a Map/List/Value structure to with formatting.
     * @param obj the object to serialize.
     * @return the resulting string.
     */
    public static final String printPretty(Object obj) {
        return new JsonPrinter().toPrettyString(obj);
    }

    /**
     * serializes a Java object into a Map/List/Value structure.
     * @param src the source object.
     * @return the resulting Map/List/Value structure.
     */
    public static final Object fromJava(Object src) {
        return DOCUMENT_MAPPER.toDocument(src);
    }

    /**
     * deserializes a Map/List/Value structure into the given Java type.
     * @param type the type to serialize to.
     * @param document the document.
     * @return the resulting object.
     */
    public static final <A> A toJava(Class<A> type, Object document) {
        return RELAXED_JAVA_MAPPER.toJava(type, document);
    }

    /**
     * deserializes a Map/List/Value structure into the given Java type.
     * All properties present in the JSON must be present in the Java type.
     * @param type the type to serialize to.
     * @param document the document.
     * @return the resulting object.
     */
    public static final <A> A toJavaStrict(Class<A> type, Object document) {
        return STRICT_JAVA_MAPPER.toJava(type, document);
    }

}
