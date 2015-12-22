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
package nl.cad.json.transform.visitor;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.util.NodeUtils;

public class AbstractVisitor {

    public interface Visitor {
        void onBeginArray(Path path, List<Object> list);

        void onEndArray(Path path, List<Object> list);

        void onBeginObject(Path path, Map<String, Object> map);

        void onEndObject(Path path, Map<String, Object> map);

        void onValue(Path path, Object object);
    }

    public interface ValuePathVisitor {
        boolean onBeginArray(ValuePath source, ValuePath target);

        void onEndArray(ValuePath source, ValuePath target);

        boolean onBeginObject(ValuePath source, ValuePath target);

        void onEndObject(ValuePath source, ValuePath target);

        void onValue(ValuePath source, ValuePath target);
    }

    public abstract static class VisitorImpl implements Visitor {
        @Override
        public void onBeginArray(Path path, List<Object> list) {
        }

        @Override
        public void onBeginObject(Path path, Map<String, Object> map) {
        }

        @Override
        public void onEndArray(Path path, List<Object> list) {
        }

        @Override
        public void onEndObject(Path path, Map<String, Object> map) {
        }

        @Override
        public void onValue(Path path, Object object) {
        }

    }

    public void visit(Object node, Visitor v) {
        System.out.println(v + " : " + node);
        this.doVisit(v, Path.root(), node);
    }

    public Object visit(Object node, ValuePathVisitor v) {
        System.out.println(v + " vp: " + node);
        ValuePath source = new ValuePath(node);
        ValuePath target = new ValuePath(null);
        this.doVisit(v, source, target);
        System.out.println(v + " vp:-> " + target.value());
        return target.value();
    }

    private void doVisit(ValuePathVisitor v, ValuePath source, ValuePath target) {
        System.out.println(v + " " + source + " .. " + target);
        if (NodeUtils.isObject(source.value())) {
            visitObject(v, source, target);
        } else if (NodeUtils.isArray(source.value())) {
            visitArray(v, source, target);
        } else {
            visitValue(v, source, target);
        }
        System.out.println(v + " " + source + " -> " + target);
    }

    private void visitValue(ValuePathVisitor v, ValuePath source, ValuePath target) {
        v.onValue(source, target);
    }

    private void visitArray(ValuePathVisitor v, ValuePath source, ValuePath target) {
        if (v.onBeginArray(source, target)) {
            List<Object> array = NodeUtils.toArray(source.value());
            for (Object value : array) {
                ValuePath nextSource = source.enter(source.path().enter(array.indexOf(value)), value);
                ValuePath nextTarget = target.enter(target.path().enter(array.indexOf(value)), null);
                doVisit(v, nextSource, nextTarget);
            }
        }
        v.onEndArray(source, target);
    }

    private void visitObject(ValuePathVisitor v, ValuePath source, ValuePath target) {
        if (v.onBeginObject(source, target)) {
            for (Map.Entry<String, Object> elem : NodeUtils.toObject(source.value()).entrySet()) {
                ValuePath nextSource = source.enter(source.path().enter(elem.getKey()), elem.getValue());
                ValuePath nextTarget = target.enter(target.path().enter(elem.getKey()), null);
                doVisit(v, nextSource, nextTarget);
            }
        }
        v.onEndObject(source, target);
    }

    private void visit(Path path, Map<String, Object> node, Visitor v) {
        v.onBeginObject(path, node);
        for (Map.Entry<String, Object> elem : node.entrySet()) {
            Path next = path.enter(elem.getKey());
            Object value = elem.getValue();
            doVisit(v, next, value);
        }
        v.onEndObject(path, node);
    }

    private void visit(Path path, List<Object> node, Visitor v) {
        v.onBeginArray(path, node);
        for (Object value : node) {
            Path next = path.enter(node.indexOf(value));
            doVisit(v, next, value);
        }
        v.onEndArray(path, node);
    }

    @SuppressWarnings("unchecked")
    private void doVisit(Visitor v, Path next, Object value) {
        if (value instanceof Map) {
            this.visit(next, (Map<String, Object>) value, v);
        } else if (value instanceof List) {
            this.visit(next, (List<Object>) value, v);
        } else {
            v.onValue(next, value);
        }
    }
    
}
