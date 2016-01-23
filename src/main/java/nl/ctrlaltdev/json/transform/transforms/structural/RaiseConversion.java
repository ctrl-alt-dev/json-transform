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

public class RaiseConversion extends AbstractVisitor implements ValuePathTransform {

    private String[] excludes;

    public RaiseConversion() {
        this(new String[0]);
    }

    public RaiseConversion(String... excludes) {
        this.excludes = excludes;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        target.set(visit(source.get(), new ValuePathVisitorImpl() {
            private Map<String, Object> values = NodeUtils.newObject();

            @Override
            public boolean onBeginArray(ValuePath source, ValuePath target) {
                boolean included = !isExcluded(source);
                if (included) {
                    target.set(NodeUtils.newArray());
                }
                return included;
            }

            @Override
            public boolean onBeginObject(ValuePath source, ValuePath target) {
                boolean included = !isExcluded(source);
                if (included) {
                    target.set(NodeUtils.newObject());
                    boolean leaf = true;
                    Map<String, Object> obj = NodeUtils.toObject(source.get());
                    for (Map.Entry<String, Object> kv : obj.entrySet()) {
                        if (!isExcluded(source.enter(source.path().enter(kv.getKey()), kv.getValue()))) {
                            if (NodeUtils.isValue(kv.getValue()) || NodeUtils.isNull(kv.getValue())) {
                                values.put(kv.getKey(), kv.getValue());
                            } else {
                                leaf = false;
                            }
                        }
                    }
                    if (leaf) {
                        NodeUtils.toObject(target.get()).putAll(values);
                    }
                }
                return included;
            }
            
            @Override
            public void onEndObject(ValuePath source, ValuePath target) {
                boolean included = !isExcluded(source);
                if (included) {
                    Map<String, Object> obj = NodeUtils.toObject(source.get());
                    for (Map.Entry<String, Object> kv : obj.entrySet()) {
                        if (!isExcluded(source.enter(source.path().enter(kv.getKey()), kv.getValue()))) {
                            if (NodeUtils.isValue(kv.getValue()) || NodeUtils.isNull(kv.getValue())) {
                                values.remove(kv.getKey());
                            }
                        }
                    }
                }
            }
        }));
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
