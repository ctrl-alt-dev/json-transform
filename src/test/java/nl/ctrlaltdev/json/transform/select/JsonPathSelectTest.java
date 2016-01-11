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
package nl.ctrlaltdev.json.transform.select;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.select.Select;
import nl.ctrlaltdev.json.transform.select.SelectBuilder;
import nl.ctrlaltdev.json.transform.select.jsonpath.JsonPathTokenizer.JsonPathParseException;
import nl.ctrlaltdev.json.transform.util.NodeUtils;
import nl.ctrlaltdev.json.transform.utils.TestUtils;

import org.junit.Before;
import org.junit.Test;

public class JsonPathSelectTest {

    private Map<String, Object> store;

    @Before
    public void init() {
        store = TestUtils.parseJson("/json/path/store.json");
    }

    @Test
    public void shouldGetBookAuthors() {
        Select select = SelectBuilder.fromJsonPath("$.store.book[*].author");

        List<ValuePath> results = select.select(store);

        assertEquals(4, results.size());
    }

    @Test
    public void shouldGetAuthors() {
        Select select = SelectBuilder.fromJsonPath("$..author");

        List<ValuePath> results = select.select(store);

        assertEquals(4, results.size());
    }

    @Test
    public void shouldGetAllThings() {
        Select select = SelectBuilder.fromJsonPath("$.store.*");

        List<ValuePath> results = select.select(store);

        assertEquals(2, results.size());
    }

    @Test
    public void shouldGetAllPrices() {
        Select select = SelectBuilder.fromJsonPath("$.store..price");

        List<ValuePath> results = select.select(store);

        assertEquals(5, results.size());
    }

    @Test
    public void shouldGetThirdBook() {
        Select select = SelectBuilder.fromJsonPath("$..book[2]");

        List<ValuePath> results = select.select(store);

        assertEquals(1, results.size());
        assertEquals("Moby Dick", NodeUtils.toObject(results.get(0).value()).get("title"));
    }

    @Test
    public void shouldGetLastBook() {
        Select select = SelectBuilder.fromJsonPath("$..book[-1:]");

        List<ValuePath> results = select.select(store);

        assertEquals(1, results.size());
    }

    @Test
    public void shouldGetBooksWithIsbn() {
        Select select = SelectBuilder.fromJsonPath("$..book[?(@.isbn)]");

        List<ValuePath> results = select.select(store);

        assertEquals(2, results.size());
        assertEquals("Moby Dick", NodeUtils.toObject(results.get(0).value()).get("title"));
        assertEquals("The Lord of the Rings", NodeUtils.toObject(results.get(1).value()).get("title"));
    }

    @Test(expected = JsonPathParseException.class)
    public void shouldGetCheapBooks() {
        Select select = SelectBuilder.fromJsonPath("$..book[?(@.price<10)]");

        List<ValuePath> results = select.select(store);

        assertEquals(2, results.size());
    }

    @Test
    public void shouldGetAllNodes() {
        Select select = SelectBuilder.fromJsonPath("$..*");

        List<ValuePath> results = select.select(store);

        assertEquals(27, results.size());
    }

}
