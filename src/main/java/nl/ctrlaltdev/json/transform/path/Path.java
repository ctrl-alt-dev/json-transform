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

import java.util.ArrayList;
import java.util.List;

import nl.ctrlaltdev.json.transform.util.NodeUtils;

public abstract class Path implements Comparable<Path> {

    public static final Path root() {
        return new RootPath();
    }

    public static final Path fromString(String str) {
        Path path = Path.root();
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < str.length(); t++) {
            char c = str.charAt(t);
            if (c == '.') {
                path = extendPath(path, sb);
            } else if (c == '[') {
                path = extendPath(path, sb);
            } else if (c == ']') {
                path = path.enter(Integer.parseInt(sb.toString()));
                sb.delete(0, sb.length());
            } else {
                sb.append(c);
            }
        }
        path = extendPath(path, sb);
        return path;
    }

    private static Path extendPath(Path path, StringBuilder sb) {
        if (sb.length() > 0) {
            path = path.enter(sb.toString());
            sb.delete(0, sb.length());
        }
        return path;
    }

    private Path parent;

    protected Path() {
    }

    protected Path(Path p) {
        this.parent = p;
    }

    public Path enter(String property) {
        return new PropertyPath(this, property);
    }

    public Path enter(int idx) {
        return new IndexPath(this, idx);
    }

    public List<Path> getPath() {
        if (parent() == null) {
            return new ArrayList<Path>();
        } else {
            List<Path> results = parent().getPath();
            results.add(this);
            return results;
        }
    }

    public Path parent() {
        return parent;
    }

    public Path leave() {
        return parent;
    }

    @Override
    public String toString() {
        return toStringBuilder().toString();
    }

    public abstract Object getTop();

    public abstract boolean isProperty();

    public abstract boolean isIndex();

    public abstract boolean isRoot();

    public abstract Object get(Object root);

    public abstract Object set(Object root, Object x);

    protected abstract StringBuilder toStringBuilder();

    public Path enter(Path path) {
        if (path.isIndex()) {
            return new IndexPath(this, (Integer) path.getTop());
        } else if (path.isProperty()) {
            return new PropertyPath(this, (String) path.getTop());
        } else if (path.isRoot()) {
            return this;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * ensures that the nodes in this path exist.
     * @param root the root.
     * @return this path.
     */
    public final Path create(Object root) {
        createPath(root, null);
        return this;
    }

    private void createPath(Object root, Path child) {
        if (parent() != null) {
            parent().createPath(root, this);
            Object currentValue = get(root);
            if (child == null) {
                set(root, null);
            } else if (child.isIndex()) {
                if (NodeUtils.isEmpty(currentValue)) {
                    set(root, NodeUtils.newArray());
                } else if (!NodeUtils.isArray(currentValue)) {
                    throw new PathCreationException(child);
                }
            } else if (child.isProperty()) {
                if (NodeUtils.isEmpty(currentValue)) {
                    set(root, NodeUtils.newObject());
                } else if (!NodeUtils.isObject(currentValue)) {
                    throw new PathCreationException(child);
                }
            }
        }
    }

    @Override
    public int compareTo(Path o) {
        return this.toString().compareTo(o.toString());
    }

}
