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
package nl.cad.json.transform.path;

import java.util.Map;

public class PropertyPath extends Path {

    private String property;

    protected PropertyPath() {
        super();
    }

    protected PropertyPath(Path parent, String property) {
        super(parent);
        this.property = property;
    }

    @Override
    public Object getTop() {
        return property;
    }

    @Override
    public boolean isIndex() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(Object root) {
        Map<String, Object> obj = (Map<String, Object>) parent().get(root);
        if (obj == null) {
            return null;
        }
        return obj.get(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object set(final Object root, Object x) {
        Map<String, Object> obj = (Map<String, Object>) this.parent().get(root);
        obj.put(property, x);
        return root;
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder result = parent().toStringBuilder();
        if (result.length() > 0) {
            result.append(".");
        }
        result.append(property);
        return result;
    }

    @Override
    public int hashCode() {
        return parent().hashCode() * 31 + property.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyPath) {
            PropertyPath pp = (PropertyPath) obj;
            return property.equals(pp.property) && parent().equals(pp.parent());
        }
        return false;
    }

}
