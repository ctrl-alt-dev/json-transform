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
package nl.ctrlaltdev.json.transform.path;

import java.util.List;

public class IndexPath extends Path {

    private int index;

    protected IndexPath() {
        super();
    }

    protected IndexPath(Path parent, int index) {
        super(parent);
        this.index = index;
    }

    @Override
    public Object getTop() {
        return Integer.valueOf(index);
    }

    @Override
    public boolean isIndex() {
        return true;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(Object root) {
        List<Object> obj = (List<Object>) parent().get(root);
        return (index < obj.size() ? obj.get(index) : null);
    }

    @SuppressWarnings("unchecked")
    public Object set(final Object root, Object x) {
        Object node = this.parent().get(root);
        List<Object> obj = (List<Object>) node;
        while (obj.size() <= index) {
            obj.add(null);
        }
        obj.set(index, x);
        return root;
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder result = parent().toStringBuilder();
        result.append("[");
        result.append(index);
        result.append("]");
        return result;
    }

    @Override
    public int hashCode() {
        return parent().hashCode() * 31 + index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexPath) {
            IndexPath pp = (IndexPath) obj;
            return index == pp.index && parent().equals(pp.parent());
        }
        return false;
    }
}
