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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor.Visitor;

/**
 * Makes a copy of the visited object.
 * The root object can be an object, array or value.
 */
public class IdentityVisitor implements Visitor {

    private final List<Object> target;

    public IdentityVisitor() {
        this.target = new ArrayList<Object>();
    }

    @Override
    public void onValue(Path path, Object object) {
        if (path.isRoot()) {
            target.add(object);
        } else {
            path.set(target.get(0), object);
        }
    }

    @Override
    public void onBeginObject(Path path, Map<String, Object> map) {
        if (path.isRoot()) {
            target.add(NodeUtils.newObject());
        } else {
            path.set(target.get(0), NodeUtils.newObject());
        }
    }

    @Override
    public void onEndObject(Path path, Map<String, Object> map) {
        // Nop
    }

    @Override
    public void onBeginArray(Path path, List<Object> list) {
        if (path.isRoot()) {
            target.add(NodeUtils.newArray());
        } else {
            path.set(target.get(0), NodeUtils.newArray());
        }
    }

    @Override
    public void onEndArray(Path path, List<Object> list) {
        // Nop
    }

    public Object getTarget() {
        return target.isEmpty() ? null : target.get(0);
    }

}