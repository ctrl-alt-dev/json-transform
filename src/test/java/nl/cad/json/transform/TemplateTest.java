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

import java.util.Map;

import nl.cad.json.transform.template.CallbackTemplate;
import nl.cad.json.transform.template.handler.CompositeHandler;
import nl.cad.json.transform.template.handler.PathHandler;
import nl.cad.json.transform.template.handler.SelectHandler;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class TemplateTest {

    @Test
    public void shouldFillCallbackTemplateWithPaths() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/path-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new PathHandler());
        Map<String, Object> results = template.fill(source);

        assertEquals(
                "{results={a=some-value, b=[1, 2, 4, 8, 16, 32, 64, 128], c={name=value, number=42}, d=[{name=a}, {name=b}]}, value=nasigoreng}",
                results.toString());
    }

    @Test
    public void shouldFillCallbackTemplateWithSingleSelect() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/single-select-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new SelectHandler());
        Map<String, Object> results = template.fill(source);

        assertEquals(
                "{results={a=some-value}, value=nasigoreng}",
                results.toString());
    }

    @Test
    public void shouldFillCallbackTemplateWithMultiSelect() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/multi-select-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new SelectHandler());
        Map<String, Object> results = template.fill(source);

        assertEquals(
                "{results={a=[a, b, value]}, value=nasigoreng}",
                results.toString());
    }

    @Test
    public void shouldFillCallbackWithComposite() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/composite-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new CompositeHandler(new PathHandler(), new SelectHandler()));
        Map<String, Object> results = template.fill(source);

        assertEquals(
                "{results={a=[a, b, value], b={name=value, number=42}}, value=nasigoreng}",
                results.toString());
    }

}
