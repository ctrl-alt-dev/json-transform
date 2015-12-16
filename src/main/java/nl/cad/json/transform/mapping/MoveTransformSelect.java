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
package nl.cad.json.transform.mapping;

import java.util.Map;

import nl.cad.json.transform.mapping.map.Mapper;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.transforms.MoveTransform;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.util.NodeUtils;

/**
 * Move Transform Select, selects nodes from the source,
 * applies the transform and moves the result to the given path.
 */
public class MoveTransformSelect implements Mapper {

    private MoveTransform move;
    private Transform transform;
    private Select select;

    public MoveTransformSelect(Path path, Transform transform, Select select) {
        this.move = new MoveTransform(path);
        this.transform = transform;
        this.select = select;
    }

    @Override
    public Map<String, Object> map(Object source) {
        return map(source, NodeUtils.newObject());
    }

    @Override
    public Map<String, Object> map(Object source, Map<String, Object> target) {
        Map<String, Object> tmp = NodeUtils.newObject();
        for (Map.Entry<Path, Object> sel : select.select(source).entrySet()) {
            transform.apply(sel.getKey(), sel.getValue(), tmp);
        }
        move.apply(Path.root(), tmp, target);
        return target;
    }

}
