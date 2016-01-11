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
package nl.ctrlaltdev.json.transform.visitor;

import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

public class AbstractVisitor {

    public interface ValuePathVisitor {
        boolean onBeginArray(ValuePath source, ValuePath target);

        void onEndArray(ValuePath source, ValuePath target);

        boolean onBeginObject(ValuePath source, ValuePath target);

        void onEndObject(ValuePath source, ValuePath target);

        void onValue(ValuePath source, ValuePath target);
    }

    public abstract static class ValuePathVisitorImpl implements ValuePathVisitor {
        @Override
        public boolean onBeginArray(ValuePath source, ValuePath target) {
            return true;
        }

        @Override
        public void onEndArray(ValuePath source, ValuePath target) {
            // NOP
        }

        @Override
        public boolean onBeginObject(ValuePath source, ValuePath target) {
            return true;
        }

        @Override
        public void onEndObject(ValuePath source, ValuePath target) {
            // NOP
        }

        @Override
        public void onValue(ValuePath source, ValuePath target) {
            // NOP
        }
    }

    public Object visit(Object node, ValuePathVisitor v, Object targetNode) {
        ValuePath source = new ValuePath(node);
        ValuePath target = new ValuePath(targetNode);
        this.doVisit(v, source, target);
        return target.value();
    }

    public Object visit(Object node, ValuePathVisitor v) {
        return visit(node, v, null);
    }

    private void doVisit(ValuePathVisitor v, ValuePath source, ValuePath target) {
        if (NodeUtils.isObject(source.value())) {
            visitObject(v, source, target);
        } else if (NodeUtils.isArray(source.value())) {
            visitArray(v, source, target);
        } else {
            visitValue(v, source, target);
        }
    }

    private void visitValue(ValuePathVisitor v, ValuePath source, ValuePath target) {
        v.onValue(source, target);
    }

    private void visitArray(ValuePathVisitor v, ValuePath source, ValuePath target) {
        if (v.onBeginArray(source, target)) {
            List<Object> array = NodeUtils.toArray(source.value());
            for (Object value : array) {
                ValuePath nextSource = source.enter(source.path().enter(indexOf(array, value)), value);
                ValuePath nextTarget = target.enter(target.path().enter(indexOf(array, value)), null);
                doVisit(v, nextSource, nextTarget);
            }
        }
        v.onEndArray(source, target);
    }

    /**
     * we don't want the equals indexOf but the index of the same reference.
     * @param array the array to scan.
     * @param reference the reference to look for.
     * @return the index or -1 if not found
     */
    private int indexOf(List<Object> array, Object reference) {
        for (int t = 0; t < array.size(); t++) {
            if (array.get(t) == reference) {
                return t;
            }
        }
        return -1;
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
    
}
