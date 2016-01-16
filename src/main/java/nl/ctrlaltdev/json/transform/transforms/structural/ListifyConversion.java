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

import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.transforms.ValuePathTransform;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

/**
 * Converts an object of objects into an array, setting the key to the given property.
 */
public class ListifyConversion implements ValuePathTransform {

    private String keyProperty;

    public ListifyConversion(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        Map<String, Object> in = NodeUtils.toObject(source.get());
        List<Object> out = NodeUtils.newArray();
        for (Map.Entry<String, Object> value : in.entrySet()) {
            if (NodeUtils.isObject(value.getValue())) {
                Map<String, Object> ref = NodeUtils.toObject(value.getValue());
                ref.put(keyProperty, value.getKey());
                out.add(ref);
            } else {
                Map<String, Object> ref = NodeUtils.newObject();
                ref.put("value", value.getValue());
                ref.put(keyProperty, value.getKey());
                out.add(ref);
            }
        }
        target.set(out);
    }

}
