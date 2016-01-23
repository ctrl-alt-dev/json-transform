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
package nl.ctrlaltdev.json.transform.mapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.JsonTransform;
import nl.ctrlaltdev.json.transform.mapping.builder.PropertyMappingBuilder;
import nl.ctrlaltdev.json.transform.path.relative.RelativePathBuilder;
import nl.ctrlaltdev.json.transform.select.SelectBuilder;
import nl.ctrlaltdev.json.transform.transforms.MappingTransform;
import nl.ctrlaltdev.json.transform.transforms.convert.string.UppercaseConversion;
import nl.ctrlaltdev.json.transform.transforms.convert.time.FormatTimestampToLocalDateTimeConversion;
import nl.ctrlaltdev.json.transform.transforms.convert.time.ReformatDateTimeConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.RaiseConversion;
import nl.ctrlaltdev.json.transform.utils.TestUtils;

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

    @Test
    public void shouldFlattenProperty() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .flatten("components")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/composite.json");

        Object output = mapping.apply(input);

        assertEquals("{a=b, b=c, c=d, d=e, f=g, g=h}", String.valueOf(output));
    }

    @Test
    public void shouldFlattenPropertyWithExclude() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .flatten("components", "components")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/composite.json");

        Object output = mapping.apply(input);

        assertEquals("{a=b, g=h}", String.valueOf(output));
    }

    @Test
    public void shouldMapifyArray() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .mapify("some", "key")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/mapify.json");

        Object output = mapping.apply(input);

        assertEquals("{some={a={key=a, value=b}, b={key=b, value=c}, c={key=c, value=d}}}", String.valueOf(output));
    }

    @Test
    public void shouldListifyObject() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .listify("some", "key")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/listify.json");

        Object output = mapping.apply(input);

        assertEquals("{some=[{d=e, key=a}, {e=f, key=b}, {key=c, value=d}]}", String.valueOf(output));
    }

    @Test
    public void shouldMapifyListify() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .mapify("some", "key")
                .build();
        MappingTransform reverse = PropertyMappingBuilder.map()
                .listify("some", "key")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/mapify.json");

        Object output = mapping.apply(input);
        Object result = reverse.apply(output);

        assertEquals("{some=[{key=a, value=b}, {key=b, value=c}, {key=c, value=d}]}", String.valueOf(result));
    }

    @Test
    public void shouldSkipProperty() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .skip("components")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/composite.json");

        Object output = mapping.apply(input);

        assertEquals("{}", String.valueOf(output));
    }

    @Test
    public void shouldCompute() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .compute("components", prop -> ((List<?>) prop.get()).size())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/composite.json");

        Object output = mapping.apply(input);

        assertEquals("{components=2}", String.valueOf(output));
        
    }

    @Test
    public void shouldComputeAndAdd() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .computeAndAdd("arrays", "size", prop -> ((List<?>) prop.get()).size())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/arrays.json");

        Object output = mapping.apply(input);

        assertEquals("{arrays=[1, 2, [3, 4], 5, 6, [7, 8]], size=6}", String.valueOf(output));

    }

    @Test
    public void shouldRaise() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .mapping(new RaiseConversion(), SelectBuilder.selectRoot())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/raise.json");

        Object output = mapping.apply(input);

        assertEquals("{c=[{a=b, d=e, j=k}, {g={a=b, e=f, h=i, j=k}}]}", String.valueOf(output));

    }

    @Test
    public void shouldRaiseWithExcludes() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .mapping(new RaiseConversion("g"), SelectBuilder.selectRoot())
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/raise.json");

        Object output = mapping.apply(input);

        assertEquals("{c=[{a=b, d=e, j=k}, {a=b, e=f, j=k}]}", String.valueOf(output));

    }

    /**
     * in this particular case the property c gets raised, however its an array
     * of objects so it does not have any values from itself. The first object
     * doesn't have any sub objects or arrays and is copied as is. The second
     * object has an object property g, so the properties of the second object
     * are moved to g. The root object is not changed.
     */
    @Test
    public void shouldRaiseProperty() {
        MappingTransform mapping = PropertyMappingBuilder.map()
                .raise("c")
                .build();

        Map<String, Object> input = TestUtils.parseJson("/json/raise.json");

        Object output = mapping.apply(input);

        assertEquals("{a=b, c=[{d=e}, {g={e=f, h=i}}], j=k}", String.valueOf(output));

    }

}
