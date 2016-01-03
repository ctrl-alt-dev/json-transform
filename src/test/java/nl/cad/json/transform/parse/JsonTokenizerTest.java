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
package nl.cad.json.transform.parse;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nl.cad.json.transform.parse.JsonTokenizer.JsonToken;

import org.junit.Before;
import org.junit.Test;

public class JsonTokenizerTest {

    private JsonTokenizer tokenizer;
    
    @Before
    public void init() {
        tokenizer = new JsonTokenizer();
    }
    
    @Test
    public void shouldParseBooleanTrueValue() {
        List<JsonToken> results = tokenizer.tokenize("true");
        assertEquals("[(VALUE:true)]", String.valueOf(results));
    }

    @Test
    public void shouldParseBooleanFalseValue() {
        List<JsonToken> results = tokenizer.tokenize("false");
        assertEquals("[(VALUE:false)]", String.valueOf(results));
    }

    @Test
    public void shouldParseNullValue() {
        List<JsonToken> results = tokenizer.tokenize("null");
        assertEquals("[(VALUE:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseStringValue() {
        List<JsonToken> results = tokenizer.tokenize("\"string\"");
        assertEquals("[(VALUE:string)]", String.valueOf(results));
    }

    @Test
    public void shouldParseEscapedStringValue() {
        List<JsonToken> results = tokenizer.tokenize("\"\n\\\"\u0020\"");
        assertEquals("[(VALUE:\n\" )]", String.valueOf(results));
    }

    @Test
    public void shouldParseNumberValue() {
        List<JsonToken> results = tokenizer.tokenize("42.0");
        assertEquals("[(VALUE:42.0)]", String.valueOf(results));
    }

    @Test
    public void shouldParseEmptyObject() {
        List<JsonToken> results = tokenizer.tokenize("{}");
        assertEquals("[(OBJECT_START:null), (OBJECT_END:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseObject() {
        List<JsonToken> results = tokenizer.tokenize("{ \"name\":\"value\" }");
        assertEquals("[(OBJECT_START:null), (NAME:name), (VALUE:value), (OBJECT_END:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseObjectWithMultipleProperties() {
        List<JsonToken> results = tokenizer.tokenize("{ \"name\":\"value\", \"name2\",\"value2\" }");
        assertEquals("[(OBJECT_START:null), (NAME:name), (VALUE:value), (NAME:name2), (NAME:value2), (OBJECT_END:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseNestedObject() {
        List<JsonToken> results = tokenizer.tokenize("{ \"name\": { \"name2\":\"value2\" }}");
        assertEquals(
                "[(OBJECT_START:null), (NAME:name), (OBJECT_START:null), (NAME:name2), (VALUE:value2), (OBJECT_END:null), (OBJECT_END:null)]",
                String.valueOf(results));
    }

    @Test
    public void shouldParseEmptyArray() {
        List<JsonToken> results = tokenizer.tokenize("[]");
        assertEquals("[(ARRAY_START:null), (ARRAY_END:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseArray() {
        List<JsonToken> results = tokenizer.tokenize("[1,2]");
        assertEquals("[(ARRAY_START:null), (VALUE:1), (VALUE:2), (ARRAY_END:null)]", String.valueOf(results));
    }

    @Test
    public void shouldParseNestedArray() {
        List<JsonToken> results = tokenizer.tokenize("[[1],[2]]");
        assertEquals(
                "[(ARRAY_START:null), (ARRAY_START:null), (VALUE:1), (ARRAY_END:null), (ARRAY_START:null), (VALUE:2), (ARRAY_END:null), (ARRAY_END:null)]",
                String.valueOf(results));
    }

}
