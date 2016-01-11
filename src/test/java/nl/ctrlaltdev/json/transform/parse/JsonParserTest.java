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
package nl.ctrlaltdev.json.transform.parse;

import static org.junit.Assert.assertEquals;
import nl.ctrlaltdev.json.transform.parse.JsonParser;

import org.junit.Before;
import org.junit.Test;

public class JsonParserTest {

    private JsonParser parser;

    @Before
    public void init() {
        parser = new JsonParser();
    }

    @Test
    public void shouldParseValue() {
        Object results = parser.parse("true");
        assertEquals("true", String.valueOf(results));
    }

    @Test
    public void shouldParseInt() {
        Object results = parser.parse("42");
        assertEquals("42", String.valueOf(results));
    }

    @Test
    public void shouldParseDouble() {
        Object results = parser.parse("42.0");
        assertEquals("42.0", String.valueOf(results));
    }

    @Test
    public void shouldParseArray() {
        Object results = parser.parse("[1,2,3,4]");
        assertEquals("[1, 2, 3, 4]", String.valueOf(results));
    }

    @Test
    public void shouldParseNestedArray() {
        Object results = parser.parse("[1,2,[3,4]]");
        assertEquals("[1, 2, [3, 4]]", String.valueOf(results));
    }

    @Test
    public void shouldParseObject() {
        Object results = parser.parse("{\"a\":\"b\"}");
        assertEquals("{a=b}", String.valueOf(results));
    }

    @Test
    public void shouldParseNestedObject() {
        Object results = parser.parse("{\"a\": { \"b\":\"c\" }}");
        assertEquals("{a={b=c}}", String.valueOf(results));
    }

}
