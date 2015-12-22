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

import nl.cad.json.transform.mapping.TransformSelect;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor.ValuePathVisitor;

public class MappingVisitor implements ValuePathVisitor {

    private List<TransformSelect> selects;

    public MappingVisitor(List<TransformSelect> selects) {
        this.selects = selects;
    }

    @Override
    public boolean onBeginArray(ValuePath source, ValuePath target) {
        return applyTransformSelects(source, target, NodeUtils.newArray());
    }

    @Override
    public void onEndArray(ValuePath source, ValuePath target) {
    }

    @Override
    public boolean onBeginObject(ValuePath source, ValuePath target) {
        return applyTransformSelects(source, target, NodeUtils.newObject());
    }

    @Override
    public void onEndObject(ValuePath source, ValuePath target) {
        applyPostTransformSelects(source, target);
    }

    @Override
    public void onValue(ValuePath source, ValuePath target) {
        applyTransformSelects(source, target, source.value());
    }

    private boolean applyTransformSelects(ValuePath source, ValuePath target, Object def) {
        for (TransformSelect pair : selects) {
            if (!pair.isPost() && pair.isMatch(source)) {
                pair.apply(source, target);
                return false;
            }
        }
        target.set(def);
        return true;
    }

    private void applyPostTransformSelects(ValuePath source, ValuePath target) {
        for (TransformSelect pair : selects) {
            if (pair.isPost() && pair.isMatch(source)) {
                pair.apply(source, target);
            }
        }
    }

}
