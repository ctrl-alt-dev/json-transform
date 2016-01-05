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
package nl.cad.json.transform.mapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.JsonTransform;
import nl.cad.json.transform.mapping.builder.PropertyMappingBuilder;
import nl.cad.json.transform.path.relative.RelativePathBuilder;
import nl.cad.json.transform.transforms.MappingTransform;
import nl.cad.json.transform.transforms.convert.string.UppercaseConversion;
import nl.cad.json.transform.transforms.convert.time.FormatTimestampToLocalDateTimeConversion;
import nl.cad.json.transform.transforms.convert.time.ReformatDateTimeConversion;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class PropertyMappingTest {

    /*
     * {
     * "id":42,
     * "name":"Fortytwo",
     * "value":42.0
     * }
     */
    @Test
    public void shouldMapProperties() {
        
        MappingTransform mapping = PropertyMappingBuilder.map()
                .rename("id", "remoteId")
                .delete("value")
                .add("constant", Integer.valueOf(42))
                .reformat("name", new UppercaseConversion())
                .build();
        
        Map<String, Object> input = TestUtils.parseJson("/json/simple-object.json");
        
        Object output = mapping.apply(input);

        assertEquals("{constant=42, name=FORTYTWO, remoteId=42}", String.valueOf(output));
        
    }

    @Test
    public void shouldOverwriteAndRenameSameProperty() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .overwrite("one", "two")
                .rename("one", "two")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/one.json");

        Object output = mapping.apply(input);

        assertEquals("{two=two}", String.valueOf(output));
    }

    @Test
    public void shouldMapDateTimes() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .reformat("timestamp", new FormatTimestampToLocalDateTimeConversion("yyyy-MM-dd"))
                .reformat("date", new ReformatDateTimeConversion("yyyy-MM-dd", "dd-MM-yyyy"))
                .reformat("time", new ReformatDateTimeConversion("HH:mm", "hh:mm"))
                .reformat("datetime", new ReformatDateTimeConversion("yyyy-MM-dd HH:mm", "mm:hh dd-MM-yyyy"))
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/date-time.json");

        Object output = mapping.apply(input);

        assertEquals("{date=31-12-2015, datetime=00:12 31-12-2015, time=01:00, timestamp=2015-12-29}", String.valueOf(output));
    }

    @Test
    public void shouldMapRecursively() {

        List<TransformSelect> recursiveMappers = new ArrayList<TransformSelect>();

        MappingTransform itemMapping = PropertyMappingBuilder.map()
                .rename("from", "to")
                .rename("type", "class")
                .overwrite("overwrite", "some", "censored")
                .add("added", Boolean.TRUE)
                .build();

        MappingTransform nrMapping = PropertyMappingBuilder.map()
                .rename("number", "nr")
                .rename("type", "class")
                .delete("delete")
                .recurse("children", recursiveMappers)
                .build();

        MappingTransform mapping = PropertyMappingBuilder.map()
                .objectMapping(itemMapping, "type", "Item")
                .objectMapping(nrMapping, "type", "Number")
                .build();

        recursiveMappers.addAll(mapping.getTransformSelects());

        Map<String, Object> source = TestUtils.parseJson("/json/objectMapping.json");

        Object output = mapping.apply(source);

        assertEquals(
                "{list=[{added=true, class=Item, overwrite=censored, to=pindakaas}, {children=[{children=[{added=true, class=Item, to=wodkasju}], class=Number}], class=Number, nr=758}]}",
                String.valueOf(output));
    }

    @Test
    public void shouldMapMoveRelative() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .overwrite("one", "two")
                .move("one", RelativePathBuilder.relativePath().parent().property("spirited").property("away").build())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/one.json");

        Object output = mapping.apply(input);

        assertEquals("{spirited={away=two}}", String.valueOf(output));
    }

    @Test
    public void shouldMapMoveRelativeToo() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .move("one", RelativePathBuilder.relativePath().parent().property("spirited").index(2).build())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/one.json");

        Object output = mapping.apply(input);

        assertEquals("{spirited=[null, null, 1]}", String.valueOf(output));
    }

    @Test
    public void shouldMapMoveAbsolute() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .overwrite("one", "two")
                .move("one", JsonTransform.path("moved.somewhere"))
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/one.json");

        Object output = mapping.apply(input);

        assertEquals("{moved={somewhere=two}}", String.valueOf(output));
    }
}
