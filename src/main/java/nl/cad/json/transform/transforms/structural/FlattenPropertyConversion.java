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
package nl.cad.json.transform.transforms.structural;

import java.util.Map;

import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.transforms.ValuePathTransform;
import nl.cad.json.transform.util.NodeUtils;
import nl.cad.json.transform.visitor.AbstractVisitor;

/**
 * Flattens the selected property into its parent object node,
 * so that all its child properties become part of the parent object.
 */
public class FlattenPropertyConversion extends AbstractVisitor implements ValuePathTransform {

    public FlattenPropertyConversion() {
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        Map<String, Object> object = NodeUtils.toObject(target.parent().get());
        visit(source.get(), new ValuePathVisitorImpl() {
            @Override
            public void onValue(ValuePath source, ValuePath target) {
                if (source.path().isProperty()) {
                    object.put(String.valueOf(source.path().getTop()), source.get());
                }
            }
        });
    }

}
