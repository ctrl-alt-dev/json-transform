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
package nl.ctrlaltdev.json.transform.transforms.structural;

import java.util.Map;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.transforms.ValuePathTransform;
import nl.ctrlaltdev.json.transform.util.NodeUtils;
import nl.ctrlaltdev.json.transform.visitor.AbstractVisitor;

/**
 * Flattens the selected property into its parent object node,
 * so that all its child properties become part of the parent object.
 */
public class FlattenPropertyConversion extends AbstractVisitor implements ValuePathTransform {

    private String[] excludes;

    public FlattenPropertyConversion() {
        this(new String[0]);
    }

    public FlattenPropertyConversion(String[] excludes) {
        this.excludes = excludes;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        Map<String, Object> object = NodeUtils.toObject(target.parent().get());
        visit(source.get(), new ValuePathVisitorImpl() {
            @Override
            public void onValue(ValuePath source, ValuePath target) {
                if (source.path().isProperty() && !isExcluded(source)) {
                    object.put(String.valueOf(source.path().getTop()), source.get());
                }
            }

            @Override
            public boolean onBeginArray(ValuePath source, ValuePath target) {
                return !isExcluded(source);
            }

            @Override
            public boolean onBeginObject(ValuePath source, ValuePath target) {
                return !isExcluded(source);
            }
        });
    }

    private boolean isExcluded(ValuePath prop) {
        if (!prop.isRoot()) {
            String name = String.valueOf(prop.path().getTop());
            for (String ex : excludes) {
                if (ex.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

}
