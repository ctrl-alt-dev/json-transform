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
package nl.cad.json.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import nl.cad.json.transform.mapping.builder.CompositeMappingBuilder;
import nl.cad.json.transform.mapping.builder.MappingBuilder;
import nl.cad.json.transform.mapping.builder.PropertyMappingBuilder;
import nl.cad.json.transform.mapping.builder.sub.SplitSourceBuilder;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.merge.MergeStrategy;
import nl.cad.json.transform.parse.JsonParser;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.transforms.Transform;

/**
 * Facade for the most important API's.
 */
public class JsonTransform extends MappingBuilder {

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
     * @return the builder.
     */
    public static final CompositeMappingBuilder parallel(MergeStrategy merge, Transform... transforms) {
        return CompositeMappingBuilder.parallel(merge, transforms);
    }

    /**
     * constructs a mapper that will apply transform sequentially to each other.
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
     */
    public static final Object parse(Reader input) throws IOException {
        return new JsonParser().parse(input);
    }

    /**
     * reads and parses a Json document into a Map/ArrayList/Value structure.
     * @param input the inputstream to read the json from (using UTF-8 encoding).
     * @return the Map/ArrayList/Value structure.
     */
    public static final Object parse(InputStream input) throws IOException {
        return new JsonParser().parse(input);
    }

}
