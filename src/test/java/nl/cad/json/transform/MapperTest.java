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
import static org.junit.Assert.assertTrue;

import java.util.Map;

import nl.cad.json.transform.java.PojoToDocumentMapperTest;
import nl.cad.json.transform.mapping.MappingBuilder;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.JavaSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.template.CallbackTemplate;
import nl.cad.json.transform.template.handler.SelectHandler;
import nl.cad.json.transform.transforms.FlattenCompositeTransform;
import nl.cad.json.transform.transforms.convert.ToStringValueConversion;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Test;

public class MapperTest {

    public static class InputPojo {
        private String a = "a";
    }

    public static class TransformedPojo {
        @SuppressWarnings("unused")
        private String b = "x";

        public TransformedPojo(InputPojo a) {
            b = a.a;
        }
    }

    @Test
    public void shouldMap() {
        DocumentSource src = MappingBuilder.seq()
                .move(Path.fromString("somewhere.over.the.rainbow"), SelectBuilder.fromString("property(\"object\")")).build();

        Object out = src.getDocument(new ValueSource(TestUtils.parseJson("/json/identity.json")));
        //
        assertEquals("{somewhere={over={the={rainbow={other=thing, some=thing}}}}}", out.toString());
    }

    @Test
    public void shouldMapSequence() {
        DocumentSource src = MappingBuilder.seq()
                .move(Path.fromString("down.under"), SelectBuilder.fromString("property(\"over\")"))
                .move(Path.fromString("somewhere.over.the.rainbow"), SelectBuilder.fromString("property(\"object\")")).build();
        //
        Object out = src.getDocument(new ValueSource(TestUtils.parseJson("/json/identity.json")));
        //
        assertEquals("{down={under={the={rainbow={other=thing, some=thing}}}}}", out.toString());
    }

    @Test
    public void shouldConvertViaSelect() {
        DocumentSource mb = MappingBuilder.seq()
                .map(Path.fromString("one"), new ToStringValueConversion(), SelectBuilder.fromString("property(\"one\")")).build();
        //
        Map<String, Object> src = TestUtils.parseJson("/json/one.json");
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(new ValueSource(src)));
        //
        assertTrue(src.get("one") instanceof Integer);
        assertEquals("{one=1}", out.toString());
        assertTrue(out.get("one") instanceof String);

    }
    
    @Test
    public void shouldFlattenComposite() {
        DocumentSource mb = MappingBuilder.seq()
                .transform(
                        new FlattenCompositeTransform("components"),
                        SelectBuilder.select().root().property("components").build()
                ).build();

        Map<String, Object> src = TestUtils.parseJson("/json/composite.json");
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(new ValueSource(src)));

        assertEquals("{components=[{a=b, b=c, c=d}, {a=b, b=c, d=e}, {a=b, f=g}, {g=h}]}", out.toString());

    }

    @Test
    public void shouldPar() {
        DocumentSource mb = MappingBuilder.par(MergeFactory.join())
                .copy()
                .move(Path.fromString("some.copy")).build();

        Map<String, Object> src = TestUtils.parseJson("/json/arrays.json");
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(new ValueSource(src)));

        assertEquals("{arrays=[1, 2, [3, 4], 5, 6, [7, 8]], some={copy={arrays=[1, 2, [3, 4], 5, 6, [7, 8]]}}}", out.toString());
    }

    @Test
    public void shouldJoinParSeqWithMultiSource() {

        DocumentSource mb = MappingBuilder.join(
                MappingBuilder.par(MergeFactory.join()).copy().move(Path.fromString("some.copy")).namedSource("left"),
                MappingBuilder.seq().copy().move(Path.fromString("some.too")).namedSource("right")
                );
        //
        MultiSource ms = new MultiSource();
        ms.putDocument("left", TestUtils.parseJson("/json/one.json"));
        ms.putDocument("right", TestUtils.parseJson("/json/two.json"));
        //
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(ms));
        //
        assertEquals("{one=1, some={copy={one=1}, too={two=2}}}", out.toString());
    }

    @Test
    public void shouldSeqSeq() {
        DocumentSource ds = MappingBuilder
                .seq().move(Path.fromString("some"))
                .link(MappingBuilder.seq().move(Path.fromString("where")).build());

        Object document = ds.getDocument(new ValueSource(TestUtils.parseJson("/json/one.json")));

        assertEquals("{some={where={one=1}}}", document.toString());

    }

    @Test
    public void shouldTemplate() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/single-select-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new SelectHandler());

        DocumentSource ds = MappingBuilder.seq().template(template).build();

        assertEquals(
                "{results={a=some-value}, value=nasigoreng}",
                ds.getDocument(new ValueSource(source)).toString());

    }

    @Test
    public void shouldMapPojo() {
        DocumentSource move = MappingBuilder.seq().move(Path.fromString("somewhere")).build();

        String result = String.valueOf(move.getDocument(new JavaSource(new PojoToDocumentMapperTest.SomeType())));

        assertEquals("{somewhere={value=value}}", result);
    }

    @Test
    public void shouldTransformViaJava() {
        Map<String, Object> map = NodeUtils.newObject();
        map.put("a", "pindakaas");
        DocumentSource java = MappingBuilder.seq().javaTransform(
                Path.root(),
                InputPojo.class, src -> new TransformedPojo(src),
                SelectBuilder.select().root().build()
                ).build();

        Map<String, Object> document = NodeUtils.toObject(java.getDocument(new ValueSource(map)));

        assertEquals("{b=pindakaas}", String.valueOf(document));
    }


}
