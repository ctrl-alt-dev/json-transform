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
import java.util.TreeMap;

import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.merge.MergeStrategy;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.transforms.MoveTransform;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.util.NodeUtils;

/**
 * Move Transform Select, selects nodes from the source,
 * applies the transform and moves the result to the given path.
 */
public class MoveTransformSelect implements Transform {

    public static class MixedResultSetException extends RuntimeException {

    }

    public static class CantMergeMultipleValuesException extends RuntimeException {

    }

    private Map<Path, MoveTransform> moves = new TreeMap<Path, MoveTransform>();
    private Transform transform;
    private List<Select> selects = new ArrayList<Select>();
    private MergeStrategy merge;

    public MoveTransformSelect(Path path, Transform transform, Select select) {
        moves.put(path, new MoveTransform(path));
        this.transform = transform;
        this.selects.add(select);
        this.merge = MergeFactory.join();
    }

    public MoveTransformSelect(List<Path> moves, Transform transform, List<Select> selects) {
        this.merge = MergeFactory.join();
        this.transform = transform;
        for (Path move : moves) {
            this.moves.put(move, new MoveTransform(move));
        }
        this.selects.addAll(selects);
    }

    @Override
    public Object apply(Object source) {
        List<Object> results = new ArrayList<Object>();
        for (Select select : selects) {
            List<ValuePath> selection = select.select(source);
            for (ValuePath sel : selection) {
                results.add(transform.apply(sel.value()));
            }
        }
        if (results.isEmpty()) {
            results.add(transform.apply(null));
        }
        if (NodeUtils.isAllNull(results)) {
            return move(null);
        } else if (NodeUtils.isAllArray(results)) {
            return move(mergeArrays(results));
        } else if (NodeUtils.isAllObjects(results)) {
            return move(joinObjects(results));
        } else if (NodeUtils.isAllValues(results)) {
            return move(mergeValues(results));
        } else {
            throw new MixedResultSetException();
        }
    }

    private Object move(Object object) {
        Object result = null;
        for (Map.Entry<Path, MoveTransform> entry : moves.entrySet()) {
            result = merge.merge(entry.getValue().apply(object), result);
        }
        return result;
    }

    private Object mergeValues(List<Object> results) {
        if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new CantMergeMultipleValuesException();
        }
    }

    private Object joinObjects(List<Object> results) {
        Map<String, Object> tmp = NodeUtils.newObject();
        for (Object o : results) {
            merge.merge(o, tmp);
        }
        return tmp;
    }

    @SuppressWarnings("unchecked")
    private Object mergeArrays(List<Object> results) {
        List<Object> tmp = NodeUtils.newArray();
        for (Object o : results) {
            tmp.addAll((List<Object>) o);
        }
        return tmp;
    }

}
