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
package nl.cad.json.transform.merge;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor;
import nl.cad.json.transform.visitor.impl.IdentityVisitor;

/**
 * constructs an array at the target node and inserts any source objects as elements.
 */
public class ArrayMergeStrategy extends AbstractVisitor implements MergeStrategy {

    private final Path targetPath;

    public ArrayMergeStrategy(Path target) {
        this.targetPath = target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object merge(Object source, Map<String, Object> target) {
        Object value = targetPath.get(target);
        if (NodeUtils.isNull(value)) {
            targetPath.create(target);
            targetPath.set(target, NodeUtils.newArray());
            value = targetPath.get(target);
        }
        if (NodeUtils.isArray(value)) {
            List<Object> array = (List<Object>) value;
            final Path destinationRoot = targetPath.enter(array.size()).create(target);
            Object result = visit(source, new IdentityVisitor());
            return destinationRoot.set(target, result);
        } else {
            throw new MergeStrategyException(targetPath);
        }
    }

}
