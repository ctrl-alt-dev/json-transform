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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cad.json.transform.mapping.map.Mapper;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.merge.MergeStrategy;
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

    public static class MixedResultSetException extends RuntimeException {

    }

    public static class CantMergeMultipleValuesException extends RuntimeException {

    }

    private MoveTransform move;
    private Transform transform;
    private Select select;
    private MergeStrategy merge;
    private Path movePath;

    public MoveTransformSelect(Path path, Transform transform, Select select) {
        this.movePath = path;
        this.move = new MoveTransform(path);
        this.transform = transform;
        this.select = select;
        this.merge = MergeFactory.join();
    }

    @Override
    public Map<String, Object> map(Object source) {
        List<Object> results = new ArrayList<Object>();
        Map<Path, Object> selection = select.select(source);
        for (Map.Entry<Path, Object> sel : selection.entrySet()) {
            results.add(transform.apply(sel.getKey(), sel.getValue()));
        }
        if (selection.isEmpty()) {
            results.add(transform.apply(Path.root(), null));
        }
        if (isAllArray(results)) {
            return mergeArrays(results);
        } else if (isAllObjects(results)) {
            return joinObjects(results);
        } else if (isAllValues(results)) {
            return mergeValues(results);
        } else {
            throw new MixedResultSetException();
        }
    }

    private Map<String, Object> mergeValues(List<Object> results) {
        if (results.size() == 1) {
            Map<String, Object> tmp = NodeUtils.newObject();
            movePath.set(tmp, results.get(0));
            return tmp;
        } else {
            throw new CantMergeMultipleValuesException();
        }
    }

    private Map<String, Object> joinObjects(List<Object> results) {
        Map<String, Object> tmp = NodeUtils.newObject();
        for (Object o : results) {
            merge.merge(o, tmp);
        }
        return move.apply(Path.root(), tmp);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mergeArrays(List<Object> results) {
        List<Object> tmp = NodeUtils.newArray();
        for (Object o : results) {
            tmp.addAll((List<Object>) o);
        }
        Map<String, Object> target = NodeUtils.newObject();
        movePath.set(target, tmp);
        return target;
    }

    private boolean isAllValues(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isValue(o) && !NodeUtils.isNull(o)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllArray(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isArray(o)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllObjects(List<Object> results) {
        for (Object o : results) {
            if (!NodeUtils.isObject(o)) {
                return false;
            }
        }
        return true;
    }

}
