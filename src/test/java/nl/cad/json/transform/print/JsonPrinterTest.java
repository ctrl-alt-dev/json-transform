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
package nl.cad.json.transform.print;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.util.NodeUtils;

import org.junit.Test;

public class JsonPrinterTest {

    private JsonPrinter printer = new JsonPrinter();

    @Test
    public void shouldPrintValue() {
        assertEquals("42", printer.toString(Integer.valueOf(42)));
        assertEquals("42.0", printer.toString(Double.valueOf(42)));
        assertEquals("\"42\"", printer.toString("42"));
        assertEquals("\"42\\t43\\n\"", printer.toString("42\t43\n"));
    }

    @Test
    public void shouldPrintObject() {
        assertEquals("{}", printer.toString(NodeUtils.newObject()));
    }

    @Test
    public void shouldPrintArray() {
        assertEquals("[]", printer.toString(NodeUtils.newArray()));
    }

    @Test
    public void shouldPrintFilledArray() {
        List<Object> array = NodeUtils.newArray();
        array.add(Integer.valueOf(42));
        array.add(Integer.valueOf(43));
        assertEquals("[42,43]", printer.toString(array));
    }

    @Test
    public void shouldPrintNestedArray() {
        List<Object> array = NodeUtils.newArray();
        array.add(NodeUtils.newArray());
        array.add(NodeUtils.newArray());
        assertEquals("[[],[]]", printer.toString(array));
    }

    @Test
    public void shouldPrintNestedObject() {
        Map<String, Object> value = NodeUtils.newObject();
        value.put("a", NodeUtils.newObject());
        value.put("b", NodeUtils.newArray());
        value.put("c", NodeUtils.newObject());
        assertEquals("{\"a\":{},\"b\":[],\"c\":{}}", printer.toString(value));
    }

    @Test
    public void shouldPrintNestedFilledObject() {
        Map<String, Object> sub = NodeUtils.newObject();
        sub.put("a", "v");
        List<Object> arr = NodeUtils.newArray();
        arr.add(Integer.valueOf(42));
        arr.add(Integer.valueOf(43));
        Map<String, Object> value = NodeUtils.newObject();
        value.put("a", sub);
        value.put("b", arr);
        assertEquals("{\"a\":{\"a\":\"v\"},\"b\":[42,43]}", printer.toString(value));
    }

    @Test
    public void shouldPrintFilledObject() {
        Map<String, Object> value = NodeUtils.newObject();
        value.put("a", "b");
        value.put("c", Integer.valueOf(1));
        assertEquals("{\"a\":\"b\",\"c\":1}", printer.toString(value));
    }

    @Test
    public void shouldPrettyPrint() {
        String crlf = System.getProperty("line.separator");
        Map<String, Object> value = NodeUtils.newObject();
        value.put("a", "b");
        value.put("c", Integer.valueOf(1));
        assertEquals("{" + crlf + "  \"a\":\"b\"," + crlf + "  \"c\":1" + crlf + "}", printer.toPrettyString(value));
    }

}
