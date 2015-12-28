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

import static nl.cad.json.transform.mapping.builder.CompositeMappingBuilder.join;
import static nl.cad.json.transform.mapping.builder.CompositeMappingBuilder.parallel;
import static nl.cad.json.transform.mapping.builder.CompositeMappingBuilder.sequence;
import static nl.cad.json.transform.mapping.builder.CompositeMappingBuilder.split;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.copy;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.javaTransform;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.manyToMany;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.map;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.move;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.template;
import static nl.cad.json.transform.mapping.builder.MappingBuilder.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import nl.cad.json.transform.java.PojoToDocumentMapperTest;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.JavaSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.mapping.source.SplitSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.template.CallbackTemplate;
import nl.cad.json.transform.template.handler.SelectHandler;
import nl.cad.json.transform.transforms.FlattenCompositeTransform;
import nl.cad.json.transform.transforms.IdentityTransform;
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
    public void shouldCopy() {
        DocumentSource src = sequence(copy()).build();

        Map<String, Object> source = TestUtils.parseJson("/json/one.json");
        Object out = src.getDocument(new ValueSource(source));

        assertEquals("{one=1}", out.toString());
        assertFalse(out == source);
    }

    @Test
    public void shouldMove() {
        DocumentSource src = sequence(move(Path.root())).build();

        Map<String, Object> source = TestUtils.parseJson("/json/one.json");
        Object out = src.getDocument(new ValueSource(source));

        assertEquals("{one=1}", out.toString());
        assertFalse(out == source);
    }

    @Test
    public void shouldMap() {
        DocumentSource src = sequence(move(Path.fromString("somewhere.over.the.rainbow"), SelectBuilder.fromJsonPath("$..object"))).build();

        Object out = src.getDocument(new ValueSource(TestUtils.parseJson("/json/identity.json")));
        //
        assertEquals("{somewhere={over={the={rainbow={other=thing, some=thing}}}}}", out.toString());
    }

    @Test
    public void shouldMapSequence() {
        DocumentSource src = sequence(
                move(Path.fromString("down.under"), SelectBuilder.fromJsonPath("$..over")),
                move(Path.fromString("somewhere.over.the.rainbow"), SelectBuilder.fromJsonPath("$..object"))
                ).build();
        //
        Object out = src.getDocument(new ValueSource(TestUtils.parseJson("/json/identity.json")));
        //
        assertEquals("{down={under={the={rainbow={other=thing, some=thing}}}}}", out.toString());
    }

    @Test
    public void shouldConvertViaSelect() {
        DocumentSource mb = sequence(
                map(Path.fromString("one"), new ToStringValueConversion(), SelectBuilder.fromJsonPath("$..one"))
                ).build();
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
        DocumentSource mb = sequence(
                transform(Path.fromString("components"),
                        new FlattenCompositeTransform("components"),
                        SelectBuilder.fromJsonPath("$.components"))
                ).build();

        Map<String, Object> src = TestUtils.parseJson("/json/composite.json");
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(new ValueSource(src)));

        assertEquals("{components=[{a=b, b=c, c=d}, {a=b, b=c, d=e}, {a=b, f=g}, {g=h}]}", out.toString());

    }

    @Test
    public void shouldPar() {
        DocumentSource mb = parallel(MergeFactory.join(),
                copy(),
                move(Path.fromString("some.copy"))
                ).build();

        Map<String, Object> src = TestUtils.parseJson("/json/arrays.json");
        Map<String, Object> out = NodeUtils.toObject(mb.getDocument(new ValueSource(src)));

        assertEquals("{arrays=[1, 2, [3, 4], 5, 6, [7, 8]], some={copy={arrays=[1, 2, [3, 4], 5, 6, [7, 8]]}}}", out.toString());
    }

    @Test
    public void shouldJoinParSeqWithMultiSource() {

        DocumentSource mb = join(
                parallel(MergeFactory.join(), copy(), move(Path.fromString("some.copy"))).namedSource("left"),
                sequence(copy(), move(Path.fromString("some.too"))).namedSource("right")
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
        DocumentSource ds = sequence(move(Path.fromString("some")))
                .link(sequence(move(Path.fromString("where"))).build());

        Object document = ds.getDocument(new ValueSource(TestUtils.parseJson("/json/one.json")));

        assertEquals("{some={where={one=1}}}", document.toString());

    }

    @Test
    public void shouldTemplate() {
        Map<String, Object> source = TestUtils.parseJson("/json/template/source.json");
        Map<String, Object> temp = TestUtils.parseJson("/json/template/single-select-template.json");

        CallbackTemplate template = new CallbackTemplate(temp, new SelectHandler());

        DocumentSource ds = sequence(template(template)).build();

        assertEquals(
                "{results={a=some-value}, value=nasigoreng}",
                ds.getDocument(new ValueSource(source)).toString());

    }

    @Test
    public void shouldMapPojo() {
        DocumentSource move = sequence(move(Path.fromString("somewhere"))).build();

        String result = String.valueOf(move.getDocument(new JavaSource(new PojoToDocumentMapperTest.SomeType())));

        assertEquals("{somewhere={value=value}}", result);
    }

    @Test
    public void shouldTransformViaJava() {
        Map<String, Object> map = NodeUtils.newObject();
        map.put("a", "pindakaas");
        DocumentSource java = sequence(javaTransform(
                Path.root(),
                InputPojo.class, src -> new TransformedPojo(src),
                SelectBuilder.select().root().build()
                )).build();

        Map<String, Object> document = NodeUtils.toObject(java.getDocument(new ValueSource(map)));

        assertEquals("{b=pindakaas}", String.valueOf(document));
    }

    /**
     * selects multiple paths from the same document,
     * feeds them through the same transform,
     * and moves the joined results to two paths in the target document.
     * Note the use of the manyToMany() builder.
     */
    @Test
    public void shouldManyToManyMap() {
        DocumentSource mapping = sequence(manyToMany()
                .move(Path.fromString("left"), Path.fromString("right"))
                .transform(new IdentityTransform())
                .select(
                        SelectBuilder.select().property("list").build(),
                        SelectBuilder.select().property("listOfObjects").build()
                ).build()).build();
        //
        Map<String, Object> src = TestUtils.parseJson("/json/identity.json");
        //
        assertEquals("{left=[1, 2, 3, 4, {name=erik}, {name=fluffy}], right=[1, 2, 3, 4, {name=erik}, {name=fluffy}]}",
                String.valueOf(mapping.getDocument(new ValueSource(src))));
    }

    @Test
    public void shouldHaveNullResultWhenNoMatch() {
        DocumentSource mapping = sequence(
                transform(Path.fromString("value"), new IdentityTransform(), SelectBuilder.select().property("doesnotexist").build()))
                .build();

        Map<String, Object> src = TestUtils.parseJson("/json/identity.json");

        assertEquals("{value=null}", String.valueOf(mapping.getDocument(new ValueSource(src))));
    }

    @Test
    public void shouldSplitSource() {

        DocumentSource mapping = join(
                sequence(move(Path.fromString("left"))).namedSource("left"),
                sequence(move(Path.fromString("right"))).namedSource("right")
                );

        DocumentSource source = new ValueSource(TestUtils.parseJson("/json/identity.json"));
        SplitSource ss = split(source)
                .add("left", SelectBuilder.select().property("list").build())
                .add("right",SelectBuilder.select().property("listOfObjects").build())
                .build();
        
        assertEquals("{left=[1, 2, 3, 4], right=[{name=erik}, {name=fluffy}]}", String.valueOf(mapping.getDocument(ss)));
    }

}
