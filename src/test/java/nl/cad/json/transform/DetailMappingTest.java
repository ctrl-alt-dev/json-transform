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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.mapping.DetailMappingBuilder;
import nl.cad.json.transform.mapping.MappingBuilder;
import nl.cad.json.transform.mapping.TransformSelect;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class DetailMappingTest {

    @Test
    public void shouldBuild() {

        List<TransformSelect> mappers = new ArrayList<TransformSelect>();
        
        List<TransformSelect> itemMapping = DetailMappingBuilder.map()
                .rename("to", "from")
                .rename("class", "type")
                .overwrite("overwrite", "some", "censored")
                .build();
        List<TransformSelect> nrMapping = DetailMappingBuilder.map()
                .rename("nr", "number")
                .rename("class", "type")
                .delete("delete")
                .recurse("children", mappers)
                .build();
        List<TransformSelect> mapping = DetailMappingBuilder.map()
                .objectMapping(itemMapping, "type", "Item")
                .objectMapping(nrMapping, "type", "Number")
                .build();
        
        mappers.addAll(mapping);

        DocumentSource build = MappingBuilder.seq().map(Path.root(), mapping, SelectBuilder.select().property("list").build()).build();

        Map<String, Object> source = TestUtils.parseJson("/json/objectMapping.json");

        Object result = build.getDocument(new ValueSource(source));

        assertEquals(
                "[{class=Item, overwrite=censored, to=pindakaas}, {children=[{children=[{class=Item, to=wodkasju}], class=Number}], class=Number, nr=758}]",
                String.valueOf(result));
    }

}
