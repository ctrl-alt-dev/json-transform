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
import java.util.Map.Entry;
import java.util.TreeMap;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.selector.Selector;
import nl.cad.json.transform.visitor.AbstractVisitor;

public class SelectorChain extends AbstractVisitor implements Select {

    private final Selector[] selectors;

    public SelectorChain(Selector... selectors) {
        this.selectors = selectors;
    }

    public Map<Path, Object> select(Object source) {
        final Map<Path, Object> matches = new TreeMap<Path, Object>();
        visit(source, new Visitor() {

            final List<Object> pathValues=new ArrayList<Object>();

            @Override
            public void onValue(Path path, Object object) {
                pathValues.add(object);
                if (isMatch(path, pathValues)) {
                    matches.put(path, object);
                }
                pathValues.remove(pathValues.size() - 1);
            }

            @Override
            public void onBeginObject(Path path, Map<String, Object> map) {
                pathValues.add(map);
                if (isMatch(path, pathValues)) {
                    matches.put(path, map);
                }
            }

            @Override
            public void onEndObject(Path path, Map<String, Object> map) {
                pathValues.remove(pathValues.size() - 1);
            }

            @Override
            public void onBeginArray(Path path, List<Object> list) {
                pathValues.add(list);
                if (isMatch(path, pathValues)) {
                    matches.put(path, list);
                }
            }

            @Override
            public void onEndArray(Path path, List<Object> list) {
                pathValues.remove(pathValues.size() - 1);
            }

        });
        return matches;
    }

    @Override
    public List<Path> selectPaths(Map<String, Object> source) {
        Map<Path, Object> results = select(source);
        return new ArrayList<Path>(results.keySet());
    }

    @Override
    public Entry<Path, Object> selectOne(Map<String, Object> source) {
        Map<Path, Object> results = select(source);
        if (results.size() != 1) {
            throw new SelectionException();
        }
        return results.entrySet().iterator().next();
    }

    @Override
    public Path selectOnePath(Map<String, Object> source) {
        return selectOne(source).getKey();
    }

    protected boolean isMatch(Path path, List<Object> pathValues) {
        Path current = path;
        int cnt = 1;
        for (int t = selectors.length - 1; t >= 0; t--) {
            if (current == null) {
                return false;
            }
            if (!selectors[t].matches(current, pathValues.get(pathValues.size() - cnt))) {
                return false;
            }
            current = current.parent();
            cnt++;
        }
        return true;
    }

}
