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

import nl.ctrlaltdev.json.transform.select.jsonpath.JsonPathTokenizer;
import nl.ctrlaltdev.json.transform.select.jsonpath.NameType;
import nl.ctrlaltdev.json.transform.select.jsonpath.ValueType;
import nl.ctrlaltdev.json.transform.select.jsonpath.JsonPathTokenizer.JsonPathParseException;
import nl.ctrlaltdev.json.transform.select.jsonpath.JsonPathTokenizer.JsonPathToken;

import org.junit.Test;

public class JsonPathTokenizerTest {

    @Test
    public void shouldTokenizeRoot() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.");
        assertEquals(1, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
    }

    @Test
    public void shouldTokenizeRootArray() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$[1]");
        assertEquals(2, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.INDEX, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
    }

    @Test
    public void shouldTokenizeRootProperty() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.pindakaas");
        assertEquals(2, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
    }

    @Test
    public void shouldTokenizeNestedProperties() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.a.b");
        assertEquals(3, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizeMixed() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.a[1].b");
        assertEquals(4, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.INDEX, tokens.get(2).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(2).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(3).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(3).getValueType());
    }

    @Test
    public void shouldTokenizeAny() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.*.b");
        assertEquals(3, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.ANY_PROPERTY_OR_INDEX_NODE, tokens.get(1).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizeAnyArray() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$[*].b");
        assertEquals(3, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.ANY_INDEX, tokens.get(1).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizeSlice() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$[1:2:3]");
        assertEquals(2, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.SLICE, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
    }

    @Test
    public void shouldTokenizeExpression() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$.a[?(@.b)]");
        assertEquals(3, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.EXPRESSION, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizePropertyInBrackets() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$['test']");
        assertEquals(2, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
    }

    @Test
    public void shouldTokenizeNodesInbetween() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$..a");
        assertEquals(3, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.NESTED_NODES, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizeRelativeProperty() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("@.a");
        assertEquals(2, tokens.size());
        assertEquals(NameType.CURRENT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
    }

    @Test
    public void shouldTokenizeRelativeArrayIndex() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("@[2].a");
        assertEquals(3, tokens.size());
        assertEquals(NameType.CURRENT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.INDEX, tokens.get(1).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }

    @Test
    public void shouldTokenizeRelativeNested() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("@..a");
        assertEquals(3, tokens.size());
        assertEquals(NameType.CURRENT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.NESTED_NODES, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(2).getValueType());
    }


    @Test
    public void shouldTokenizeNestedWithSlice() {
        List<JsonPathToken> tokens = JsonPathTokenizer.tokenize("$..book[-1:]");
        assertEquals(4, tokens.size());
        assertEquals(NameType.ROOT_NODE, tokens.get(0).getNameType());
        assertEquals(ValueType.OBJECT_VALUE_NODE, tokens.get(0).getValueType());
        assertEquals(NameType.NESTED_NODES, tokens.get(1).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(1).getValueType());
        assertEquals(NameType.PROPERTY, tokens.get(2).getNameType());
        assertEquals(ValueType.ARRAY_VALUE_NODE, tokens.get(2).getValueType());
        assertEquals(NameType.SLICE, tokens.get(3).getNameType());
        assertEquals(ValueType.ANY_VALUE_NODE, tokens.get(3).getValueType());
    }

    @Test(expected = JsonPathParseException.class)
    public void shouldFailOnMissingBracket() {
        JsonPathTokenizer.tokenize("a[b");
    }

}
