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
package nl.cad.json.transform.transforms;

import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor;
import nl.cad.json.transform.visitor.impl.IdentityVisitor;

/**
 * moves the input to the targetPath in the output.
 */
public class MoveTransform extends AbstractVisitor implements Transform {

    private final Path targetPath;

    public MoveTransform(Path targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public Object apply(Path path, Object source) {
        if (!targetPath.isRoot()) {
            Map<String, Object> target = NodeUtils.newObject();
            targetPath.create(target);
            Object result = visit(source, new IdentityVisitor());
            targetPath.set(target, result);
            return target;
        } else {
            return visit(source, new IdentityVisitor());
        }
    }

}
