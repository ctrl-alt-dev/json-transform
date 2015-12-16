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
package nl.cad.json.transform.transforms;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.AbstractVisitor;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;

/**
 * copies the input to the output.
 */
public class IdentityTransform extends AbstractVisitor implements Transform {

    @Override
    public void apply(Path path, Object source, Map<String, Object> target) {
        visit(source, new Visitor() {

            @Override
            public void onValue(Path path, Object object) {
                path.set(target, object);
            }

            @Override
            public void onBeginObject(Path path, Map<String, Object> map) {
                if (!path.isRoot()) {
                    path.set(target, NodeUtils.newObject());
                }
            }

            @Override
            public void onEndObject(Path path, Map<String, Object> map) {
                // Nop
            }

            @Override
            public void onBeginArray(Path path, List<Object> list) {
                path.set(target, NodeUtils.newArray());
            }

            @Override
            public void onEndArray(Path path, List<Object> list) {
                // Nop
            }

        });
    }

}
