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

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.transforms.IdentityTransform;
import nl.cad.json.transform.transforms.MoveTransform;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.utils.TestUtils;

import org.junit.Before;
import org.junit.Test;

public class TransformTest {

    private Map<String, Object> source;

    @Before
    public void init() {
        source = TestUtils.parseJson("/json/identity.json");
    }
    @Test
    public void shouldDoIdentityTransform() {
        Map<String, Object> dest = NodeUtils.newObject();
        new IdentityTransform().apply(Path.root(), source, dest);
        assertEquals(source, dest);
    }

    @Test
    public void shouldMoveTransform() {
        Map<String, Object> dest = NodeUtils.newObject();
        new MoveTransform(Path.root().enter("move")).apply(Path.root(), source, dest);
        assertEquals(source, dest.get("move"));
    }

}
