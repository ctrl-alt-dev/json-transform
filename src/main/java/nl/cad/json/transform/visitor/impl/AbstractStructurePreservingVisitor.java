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
package nl.cad.json.transform.visitor.impl;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor.VisitorImpl;

/**
 * duplicates the object structure but not the values.
 * useful if you want to keep the structure but adjust the values.
 */
public abstract class AbstractStructurePreservingVisitor extends VisitorImpl {

    private Object root;

    /**
     * @param root the root object (must be same type as source).
     */
    public AbstractStructurePreservingVisitor(Object root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    @Override
    public void onBeginObject(Path path, Map<String, Object> source) {
        Map<String, Object> target = NodeUtils.newObject();
        root = path.set(root, target);
    }

    @Override
    public void onBeginArray(Path path, List<Object> source) {
        List<Object> target = NodeUtils.newArray();
        root = path.set(root, target);
    }

    protected void setRoot(Object root) {
        this.root = root;
    }

}
