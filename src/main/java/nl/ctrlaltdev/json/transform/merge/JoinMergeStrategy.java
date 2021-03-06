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
package nl.ctrlaltdev.json.transform.merge;

import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.util.NodeUtils;
import nl.ctrlaltdev.json.transform.visitor.AbstractVisitor;
import nl.ctrlaltdev.json.transform.visitor.impl.MergeVisitor;

/**
 * attempts to join the various sources into the destination at the given path.
 */
public class JoinMergeStrategy extends AbstractVisitor implements MergeStrategy {

    private Path targetPath;

    public JoinMergeStrategy(Path target) {
        this.targetPath = target;
    }

    @Override
    public Object merge(Object source, Object target) {
        if (NodeUtils.isNull(targetPath.get(target))) {
            targetPath.create(target);
            Object result = visit(source, new MergeVisitor());
            return targetPath.set(target, result);
        } else {
            Object root = targetPath.get(target);
            Object result = visit(source, new MergeVisitor(), root);
            return targetPath.set(target, result);
        }
    }

}
