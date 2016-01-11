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
import nl.ctrlaltdev.json.transform.visitor.AbstractVisitor;
import nl.ctrlaltdev.json.transform.visitor.impl.IdentityVisitor;

/**
 * will copy the source onto the target path, erasing any existing nodes.
 * This strategy is only useful if you have only one selection or if you
 * want to keep the latest.
 */
public class OverwriteMergeStrategy extends AbstractVisitor implements MergeStrategy {

    private final Path targetPath;

    public OverwriteMergeStrategy(Path target) {
        this.targetPath = target;
    }

    @Override
    public Object merge(Object source, Object target) {
        targetPath.create(target);
        Object result = visit(source, new IdentityVisitor());
        return targetPath.set(target, result);
    }

}
