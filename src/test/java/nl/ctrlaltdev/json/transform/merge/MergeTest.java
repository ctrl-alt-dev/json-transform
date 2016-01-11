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
package nl.ctrlaltdev.json.transform.merge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.merge.MergeFactory;
import nl.ctrlaltdev.json.transform.merge.MergeStrategy;
import nl.ctrlaltdev.json.transform.merge.MergeStrategyException;
import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.util.NodeUtils;
import nl.ctrlaltdev.json.transform.utils.TestUtils;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class MergeTest {

    @Test
    public void shouldArrayMerge() {
        MergeStrategy merge = MergeFactory.toArray(Path.root().enter("array"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertEquals(2, ((List<Object>) target.get("array")).size());
    }

    @Test
    public void shouldArrayMergeTwoLevelsDeep() {
        MergeStrategy merge = MergeFactory.toArray(Path.root().enter("array").enter("array"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertEquals(2, ((List<Object>) ((Map<String, Object>) target.get("array")).get("array")).size());
    }

    @Test(expected = MergeStrategyException.class)
    public void shouldFailArrayMergeAtRoot() {
        MergeStrategy merge = MergeFactory.toArray(Path.root());
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

    }

    @Test
    public void shouldOverwriteMerge() {
        MergeStrategy merge = MergeFactory.overwrite(Path.root().enter("merge"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertNull(((Map<String, Object>) target.get("merge")).get("one"));
        assertEquals(Integer.valueOf(2), ((Map<String, Object>) target.get("merge")).get("two"));
    }

    @Test
    public void shouldOverwriteMergeAtRoot() {
        MergeStrategy merge = MergeFactory.overwrite(Path.root());
        Map<String, Object> target = NodeUtils.newObject();

        target = NodeUtils.toObject(merge.merge(TestUtils.parseJson("/json/one.json"), target));
        target = NodeUtils.toObject(merge.merge(TestUtils.parseJson("/json/two.json"), target));

        assertEquals(Integer.valueOf(2), target.get("two"));
    }

    @Test
    public void shouldOverwriteMergeAtLevel2() {
        MergeStrategy merge = MergeFactory.overwrite(Path.root().enter("merge").enter("merge"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertNull(((Map<String, Object>) ((Map<String, Object>) target.get("merge")).get("merge")).get("one"));
        assertEquals(Integer.valueOf(2), ((Map<String, Object>) ((Map<String, Object>) target.get("merge")).get("merge")).get("two"));
    }

    @Test
    public void shouldJoinMerge() {
        MergeStrategy merge = MergeFactory.join(Path.root().enter("merge"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertEquals(Integer.valueOf(1), ((Map<String, Object>) target.get("merge")).get("one"));
        assertEquals(Integer.valueOf(2), ((Map<String, Object>) target.get("merge")).get("two"));
    }

    @Test
    public void shouldJoinMergeAtLevel2() {
        MergeStrategy merge = MergeFactory.join(Path.root().enter("merge").enter("merge"));
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertEquals(Integer.valueOf(1), ((Map<String, Object>) ((Map<String, Object>) target.get("merge")).get("merge")).get("one"));
        assertEquals(Integer.valueOf(2), ((Map<String, Object>) ((Map<String, Object>) target.get("merge")).get("merge")).get("two"));
    }

    @Test
    public void shouldJoinMergeAtRoot() {
        MergeStrategy merge = MergeFactory.join(Path.root());
        Map<String, Object> target = NodeUtils.newObject();

        merge.merge(TestUtils.parseJson("/json/one.json"), target);
        merge.merge(TestUtils.parseJson("/json/two.json"), target);

        assertEquals(Integer.valueOf(1), target.get("one"));
        assertEquals(Integer.valueOf(2), target.get("two"));
    }

}
