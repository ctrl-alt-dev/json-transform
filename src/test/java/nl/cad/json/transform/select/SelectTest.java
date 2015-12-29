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
package nl.cad.json.transform.select;

import static nl.cad.json.transform.select.SelectBuilder.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.selector.PropertySelector;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Before;
import org.junit.Test;

public class SelectTest {

    private static final int ALL_PATHS = 20;
    private Map<String, Object> source;

    @Before
    public void init() {
        source = TestUtils.parseJson("/json/identity.json");
    }

    @Test
    public void shouldSelectAll() {
        List<Path> selected = select().any().build().selectPaths(source);
        assertEquals(ALL_PATHS, selected.size());
    }

    @Test
    public void shouldSelectProperty() {
        List<Path> selected = select().property("name").build().selectPaths(source);
        assertEquals(2, selected.size());
    }

    @Test
    public void shouldSelectIndex() {
        assertNotNull(select().index(2).build().selectOnePath(source));
    }

    @Test(expected = SelectionException.class)
    public void shouldFailSelectOne() {
        select().any().build().selectOnePath(source);
    }

    @Test
    public void shouldSelectObjectProperty() {
        List<Path> selected = select().objectProperty("name").build().selectPaths(source);
        assertEquals(2, selected.size());
        assertTrue(selected.get(0).get(source) instanceof Map);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldSelectObjectPropertyValue() {
        List<Path> selected = select().objectPropertyValue("name", "fluffy").build().selectPaths(source);
        assertEquals(1, selected.size());
        assertTrue(selected.get(0).get(source) instanceof Map);
        assertEquals("fluffy", ((Map) selected.get(0).get(source)).get("name"));
    }

    @Test
    public void shouldSelectChained() {
        List<Path> selected = select().property("object").property("some").build().selectPaths(source);
        assertEquals(1, selected.size());
    }

    @Test
    public void shouldSelectOr() {
        List<Path> selected = select().or(select().property("string").property("number")).build().selectPaths(source);
        assertEquals(2, selected.size());
    }

    @Test
    public void shouldSelectAnd() {
        List<Path> selected = select().and(select().objectProperty("some").objectProperty("other")).build().selectPaths(source);
        assertEquals(1, selected.size());
    }

    @Test
    public void shouldSelectExclusiveOr() {
        List<Path> selected = select().exor(select().objectProperty("some").any()).build().selectPaths(source);
        assertEquals(ALL_PATHS - 1, selected.size());
    }

    @Test
    public void shouldSelectNot() {
        List<Path> selected = select().not(new PropertySelector("object")).build().selectPaths(source);
        assertEquals(ALL_PATHS - 1, selected.size());
    }

    @Test
    public void shouldSelectRoot() {
        assertEquals(source, select().root().build().selectOne(source).value());
    }

    @Test
    public void shouldSelectAnyValue() {
        assertEquals(13, select().anyValue().build().select(source).size());
    }

    @Test
    public void shouldSelectAnyArray() {
        assertEquals(2, select().anyArray().build().select(source).size());
    }

    @Test
    public void shouldSelectAnyObject() {
        assertEquals(4, select().anyObject().build().select(source).size());
    }

    @Test
    public void shouldSelectAnyValueOfType() {
        assertEquals(5, select().anyValueOfType(Integer.class).build().select(source).size());
        assertEquals(2, select().anyValueOfType(Double.class).build().select(source).size());
        assertEquals(1, select().anyValueOfType(Boolean.class).build().select(source).size());
        // Not values:
        assertEquals(0, select().anyValueOfType(List.class).build().select(source).size());
        assertEquals(0, select().anyValueOfType(Map.class).build().select(source).size());
    }

    @Test
    public void shouldSelectPropertyMatch() {
        assertEquals(2, select().propertyMatch("(number|string)").build().select(source).size());
    }

    @Test
    public void shouldSelectObjectPropertyMatch() {
        assertEquals(3, select().objectPropertyMatch("(name|some)").build().select(source).size());
    }

    @Test
    public void shouldNotEvaluateSelectTooDeep() {
        assertEquals(0, select().property("a").any().any().any().property("object").property("some").build().select(source).size());
    }

}
