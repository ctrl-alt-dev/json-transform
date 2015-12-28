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
package nl.cad.json.transform.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.select.jsonpath.AnyNodesSelector;
import nl.cad.json.transform.select.selector.Selector;
import nl.cad.json.transform.visitor.AbstractVisitor;

public class SelectorChain extends AbstractVisitor implements Select {

    private final Selector[] selectors;

    public SelectorChain(Selector... selectors) {
        this.selectors = selectors;
    }

    public List<ValuePath> select(Object source) {
        final List<ValuePath> matches = new ArrayList<ValuePath>();
        visit(source, new ValuePathVisitorImpl() {
            
            @Override
            public void onValue(ValuePath source, ValuePath target) {
                if (isMatch(source)) {
                    matches.add(source);
                }
            }
            
            @Override
            public boolean onBeginObject(ValuePath source, ValuePath target) {
                if (isMatch(source)) {
                    matches.add(source);
                }
                return true;
            }
            
            @Override
            public boolean onBeginArray(ValuePath source, ValuePath target) {
                if (isMatch(source)) {
                    matches.add(source);
                }
                return true;
            }
        });
        return matches;
    }

    @Override
    public List<Path> selectPaths(Map<String, Object> source) {
        List<ValuePath> results = select(source);
        ArrayList<Path> tmp = new ArrayList<>();
        for (ValuePath vp:results) {
            tmp.add(vp.path());
        }
        return tmp;
    }

    @Override
    public ValuePath selectOne(Object source) {
        List<ValuePath> results = select(source);
        if (results.size() != 1) {
            throw new SelectionException(results.size());
        }
        return results.get(0);
    }

    @Override
    public Path selectOnePath(Map<String, Object> source) {
        return selectOne(source).path();
    }

    @Override
    public boolean isMatch(ValuePath path) {
        ValuePath current = path;
        int cnt = selectors.length;
        do {
            if (current == null) {
                return false;
            }
            cnt--;
            if (selectors[cnt] instanceof AnyNodesSelector) {
                cnt--;
                while (!selectors[cnt].matches(current)) {
                    current = current.parent();
                    if (current == null) {
                        return false;
                    }
                } 
            } else if (!selectors[cnt].matches(current)) {
                return false;
            }
            current = current.parent();
        } while (cnt > 0);
        return true;
    }
}
