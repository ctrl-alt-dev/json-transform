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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.PathCreationException;
import nl.cad.json.transform.util.NodeUtils;

import org.junit.Test;

public class PathTest {

    @Test
    public void shouldBuild() {
        Path p = Path.root().enter("a").enter(1).enter("b").enter("c");
        assertEquals("a[1].b.c", p.toString());
        Path copy = Path.fromString(p.toString());
        assertEquals(p, copy);
    }

    @Test
    public void shouldBuildToo() {
        Path p = Path.root().enter("a").enter(1).enter("b").enter("c");
        Path p2 = p.leave().enter("d");
        assertEquals("a[1].b.d", p2.toString());
    }

    @Test
    public void shouldGet() {
        Map<String, Object> src = NodeUtils.newObject();
        src.put("prop", "value");
        Path p = Path.root().enter("prop");        
        assertEquals("value", p.get(src));
    }

    @Test
    public void shouldSet() {
        Map<String, Object> src = NodeUtils.newObject();
        Path p = Path.root().enter("prop");
        p.set(src, "value");
        assertEquals("value", src.get("prop"));
    }

    @Test
    public void shouldGetNested() {
        Map<String, Object> src = NodeUtils.newObject();
        Map<String, Object> sub = NodeUtils.newObject();
        sub.put("prop", "value");
        List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(42));
        src.put("array", list);
        src.put("sub", sub);
        //
        assertEquals(Integer.valueOf(42), Path.fromString("array[0]").get(src));
        assertEquals("value", Path.fromString("sub.prop").get(src));
    }

    @Test
    public void shouldSetNested() {
        Map<String, Object> src = NodeUtils.newObject();
        Map<String, Object> sub = NodeUtils.newObject();
        List<Object> list = new ArrayList<>();
        list.add(Integer.valueOf(42));
        src.put("array", list);
        src.put("sub", sub);
        //
        Path.fromString("array[1]").set(src, Integer.valueOf(42));
        Path.fromString("sub.prop").set(src, "value");
        //
        assertEquals("value", sub.get("prop"));
        assertEquals(Integer.valueOf(42), list.get(1));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldCreatePath() {
        Map<String, Object> src = NodeUtils.newObject();
        //
        Path.fromString("prop1.prop2[3].prop4").create(src);
        //
        assertTrue(src.containsKey("prop1"));
        assertTrue(((Map) src.get("prop1")).containsKey("prop2"));
        assertNotNull(((List) ((Map) src.get("prop1")).get("prop2")).get(3));
    }

    @Test(expected = PathCreationException.class)
    public void shouldFailToOverwriteExistingPath() {
        Map<String, Object> src = NodeUtils.newObject();
        //
        Path.fromString("prop1.prop2.prop3").create(src);
        Path.fromString("prop1[2].prop2").create(src);
    }

    @Test
    public void shouldAdjustEmptyExistingPath() {
        Map<String, Object> src = NodeUtils.newObject();
        //
        Path.fromString("prop1").create(src);
        Path.fromString("prop1[0]").create(src);
        //
        assertEquals("{prop1=[null]}", src.toString());
        //
        Path.fromString("prop1[0]").set(src, "42");
        //
        assertEquals("{prop1=[42]}", src.toString());
    }

}
