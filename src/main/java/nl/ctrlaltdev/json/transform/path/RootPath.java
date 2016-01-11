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


public class RootPath extends Path {

    protected RootPath() {
        super();
    }

    @Override
    public Object get(Object root) {
        return root;
    }

    @Override
    public Object getTop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIndex() {
        return false;
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public Object set(Object root, Object object) {
        return object;
    }

    @Override
    protected StringBuilder toStringBuilder() {
        return new StringBuilder();
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RootPath;
    }

}
