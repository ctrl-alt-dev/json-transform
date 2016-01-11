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
package nl.ctrlaltdev.json.transform.visitor.impl;

import nl.ctrlaltdev.json.transform.merge.MergeStrategyException;
import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.util.NodeUtils;
import nl.ctrlaltdev.json.transform.visitor.AbstractVisitor.ValuePathVisitor;

public final class MergeVisitor implements ValuePathVisitor {

    @Override
    public boolean onBeginArray(ValuePath source, ValuePath target) {
        Object currentValue = target.get();
        if (NodeUtils.isNull(currentValue)) {
            target.set(NodeUtils.newArray());
        } else if (!NodeUtils.isArray(currentValue)) {
            throw new MergeStrategyException(target.path());
        }
        return true;
    }

    @Override
    public void onEndArray(ValuePath source, ValuePath target) {
        // Nop
    }

    @Override
    public boolean onBeginObject(ValuePath source, ValuePath target) {
        Object currentValue = target.get();
        if (NodeUtils.isNull(currentValue)) {
            target.set(NodeUtils.newObject());
        } else if (!NodeUtils.isObject(currentValue)) {
            throw new MergeStrategyException(target.path());
        }
        return true;
    }

    @Override
    public void onEndObject(ValuePath source, ValuePath target) {
        // Nop
    }

    @Override
    public void onValue(ValuePath source, ValuePath target) {
        target.set(source.value());
    }

}