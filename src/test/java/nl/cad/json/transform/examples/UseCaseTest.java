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

import static nl.cad.json.transform.select.SelectBuilder.select;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.mapping.DetailMappingBuilder;
import nl.cad.json.transform.mapping.MappingBuilder;
import nl.cad.json.transform.mapping.TransformSelect;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.transforms.convert.TimestampToFormattedLocalDateTimeConversion;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class UseCaseTest {

    @Test
    public void shouldDoUseCase() {
        TimestampToFormattedLocalDateTimeConversion timestamp = new TimestampToFormattedLocalDateTimeConversion("yyyy-MM-dd HH:mm");

        List<TransformSelect> containerMapping = DetailMappingBuilder.map()
                .overwrite("type", "CONTAINER", "Split")
                .build();
        List<TransformSelect> splitTransform = DetailMappingBuilder.map()
                .objectMapping(containerMapping, "type", "CONTAINER")
                .build();

        Transform plainTextTransform = new Transform() {

            @Override
            public Object apply(Path path, Object source) {
                return NodeUtils.toObject(source).get("text");
            }
        };

        DocumentSource mapping = MappingBuilder.join(
                MappingBuilder.seq().copy().namedSource("authors"),
                MappingBuilder.seq().copy().namedSource("related"),
                MappingBuilder.par(MergeFactory.join())
                        .map(Path.fromString("publication"), timestamp, select().property("publicationDate").build())
                        .map(Path.fromString("update"), timestamp, select().property("lastUpdateDate").build())
                        .transform(Path.fromString("title"), plainTextTransform, select().objectPropertyValue("type", "TITLE").build())
                        .transform(Path.fromString("intro"), plainTextTransform, select().objectPropertyValue("type", "INTRO").build())
                        .exists(Path.fromString("twitter"), select().or(select()
                                .objectPropertyValue("type", "TWITTER")
                                .objectPropertyValue("type", "TIMELINE")
                                ).build())
                        .exists(Path.fromString("video"), select().objectPropertyValue("type", "VIDEO").build())
                        .move(Path.fromString("topComponent"), select().one().objectPropertyValue("type", "PHOTO").build())
                        .map(Path.fromString("components"), splitTransform,
                                select().root().property("rootContainer").any().property("components").build())
                        .namedSource("revision")
                );

        Map<String, Object> authors = TestUtils.parseJson("/json/usecase/source/authors.json");
        Map<String, Object> related = TestUtils.parseJson("/json/usecase/source/related.json");
        Map<String, Object> revision = TestUtils.parseJson("/json/usecase/source/revision.json");

        MultiSource source = new MultiSource();
        source.putDocument("authors", authors);
        source.putDocument("related", related);
        source.putDocument("revision", revision);

        Object results = mapping.getDocument(source);
        
        System.out.println(TestUtils.renderJson(results));
    }

}
