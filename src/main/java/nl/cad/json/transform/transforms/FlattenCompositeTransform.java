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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor;

/**
 * Recursively flattens an array of objects containing a named property containing an array of objects.
 */
public class FlattenCompositeTransform extends AbstractVisitor implements Transform {

    private final String childPropertyName;

    public FlattenCompositeTransform(String childPropertyName) {
        this.childPropertyName = childPropertyName;
    }

    @Override
    public Map<String, Object> apply(Path path, Object source) {
        final Map<String, Object> target = NodeUtils.newObject();
        final List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        visit(source, new VisitorImpl() {

            private List<Map<String, Object>> aggregates = new ArrayList<Map<String, Object>>();

            @Override
            public void onBeginObject(Path path, Map<String, Object> map) {
                Map<String, Object> tmp = copyNonChildNodes(map);
                if (hasChildren(map)) {
                    aggregates.add(tmp);
                } else {
                    for (Map<String, Object> agg : aggregates) {
                        tmp.putAll(agg);
                    }
                    results.add(tmp);
                }
            }

            @Override
            public void onEndObject(Path path, Map<String, Object> map) {
                if (hasChildren(map)) {
                    aggregates.remove(aggregates.size() - 1);
                }
            }

        });
        path.set(target, results);
        return target;
    }

    private Map<String, Object> copyNonChildNodes(Map<String, Object> map) {
        Map<String, Object> tmp = NodeUtils.newObject();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!childPropertyName.equals(e.getKey())) {
                tmp.put(e.getKey(), e.getValue());
            }
        }
        return tmp;
    }

    private boolean hasChildren(Map<String, Object> map) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (childPropertyName.equals(e.getKey())) {
                return true;
            }
        }
        return false;
    }

}
