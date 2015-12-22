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

import nl.cad.json.transform.merge.MergeStrategyException;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor.Visitor;

public final class MergeVisitor implements Visitor {

    private Path copyPath;
    private Map<String, Object> target;

    public MergeVisitor(Path targetPath, Map<String, Object> target) {
        this.copyPath = targetPath;
        this.target = target;
    }

    @Override
    public void onValue(Path path, Object object) {
        copyPath.enter(path).set(target, object);
    }

    @Override
    public void onEndObject(Path path, Map<String, Object> map) {
        copyPath = copyPath.leave();
    }

    @Override
    public void onEndArray(Path path, List<Object> list) {
        copyPath = copyPath.leave();
    }

    @Override
    public void onBeginObject(Path path, Map<String, Object> map) {
        copyPath = copyPath.enter(path);
        Object currentValue = copyPath.get(target);
        if (NodeUtils.isNull(currentValue)) {
            copyPath.set(target, NodeUtils.newObject());
        } else if (!NodeUtils.isObject(currentValue)) {
            throw new MergeStrategyException(path);
        }
    }

    @Override
    public void onBeginArray(Path path, List<Object> list) {
        copyPath = copyPath.enter(path);
        Object currentValue = copyPath.get(target);
        if (NodeUtils.isNull(currentValue)) {
            copyPath.set(target, NodeUtils.newArray());
        } else if (!NodeUtils.isArray(currentValue)) {
            throw new MergeStrategyException(path);
        }
    }
}