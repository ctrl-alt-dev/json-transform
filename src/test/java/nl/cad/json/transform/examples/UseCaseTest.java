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
package nl.cad.json.transform.examples;

import static nl.cad.json.transform.JsonTransform.join;
import static nl.cad.json.transform.JsonTransform.jsonPath;
import static nl.cad.json.transform.JsonTransform.mapProperties;
import static nl.cad.json.transform.JsonTransform.multiSource;
import static nl.cad.json.transform.JsonTransform.parallel;
import static nl.cad.json.transform.JsonTransform.path;
import static nl.cad.json.transform.JsonTransform.select;
import static nl.cad.json.transform.JsonTransform.sequence;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.copy;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.exists;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.map;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.move;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.transform;

import java.util.Map;

import nl.cad.json.transform.JsonTransform;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.transforms.MappingTransform;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.transforms.convert.time.FormatTimestampToLocalDateTimeConversion;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class UseCaseTest {

    @Test
    public void shouldDoUseCase() {
        FormatTimestampToLocalDateTimeConversion timestamp = new FormatTimestampToLocalDateTimeConversion("yyyy-MM-dd HH:mm");

        MappingTransform containerMapping = mapProperties()
                .overwrite("type", "CONTAINER", "Split")
                .build();
        MappingTransform splitTransform = mapProperties()
                .objectMapping(containerMapping, "type", "CONTAINER")
                .build();

        Transform plainTextTransform = new Transform() {

            @Override
            public Object apply(Object source) {
                return NodeUtils.toObject(source).get("text");
            }
        };

        DocumentSource mapping = join(
                sequence(copy()).namedSource("authors"),
                sequence(copy()).namedSource("related"),
                parallel(MergeFactory.join(),
                        map(path("publication"), timestamp, jsonPath("$..publicationDate")),
                        map(path("update"), timestamp, jsonPath("$..lastUpdateDate")),
                        transform(path("title"), plainTextTransform, select().objectPropertyValue("type", "TITLE").build()),
                        transform(path("intro"), plainTextTransform, select().objectPropertyValue("type", "INTRO").build()),
                        exists(path("twitter"), select().or(select()
                                .objectPropertyValue("type", "TWITTER")
                                .objectPropertyValue("type", "TIMELINE")
                                ).build()),
                        exists(path("video"), select().objectPropertyValue("type", "VIDEO").build()),
                        move(path("topComponent"), select().one().objectPropertyValue("type", "PHOTO").build()),
                        map(path("components"), splitTransform, jsonPath("$.rootContainer[*].components"))
                        ).namedSource("revision")
                );

        Map<String, Object> authors = TestUtils.parseJson("/json/usecase/source/authors.json");
        Map<String, Object> related = TestUtils.parseJson("/json/usecase/source/related.json");
        Map<String, Object> revision = TestUtils.parseJson("/json/usecase/source/revision.json");

        MultiSource source = multiSource();
        source.putDocument("authors", authors);
        source.putDocument("related", related);
        source.putDocument("revision", revision);

        Object results = mapping.getDocument(source);
        
        System.out.println(JsonTransform.print(results));
    }

}
